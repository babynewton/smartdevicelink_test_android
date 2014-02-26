package com.livio.sdltester.dialogs;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.livio.sdl.SdlConstants;
import com.livio.sdl.SdlImageItem;
import com.livio.sdl.SdlRequestFactory;
import com.livio.sdl.dialogs.BaseAlertDialog;
import com.livio.sdl.dialogs.BaseOkCancelDialog;
import com.livio.sdl.dialogs.ImageListDialog;
import com.livio.sdl.enums.SdlCommand;
import com.livio.sdl.menu.MenuItem;
import com.livio.sdl.utils.AndroidUtils;
import com.livio.sdltester.R;
import com.smartdevicelink.proxy.RPCRequest;

public class AddCommandDialog extends BaseOkCancelDialog{
	
	private static final SdlCommand SYNC_COMMAND = SdlCommand.ADD_COMMAND;
	private static final String DIALOG_TITLE = SYNC_COMMAND.toString();
	
	private EditText et_newCommand;
	private EditText et_voiceRecKeyword;
	private EditText et_imageName;
	
	private Spinner spin_addCommand_submenus;
	private CheckBox cb_image;
	
	private SdlImageItem selectedImage;
	
	public AddCommandDialog(Context context, List<MenuItem> availableBanks, List<SdlImageItem> images) {
		super(context, DIALOG_TITLE, R.layout.add_command);
		setupViews(availableBanks, images);
		setPositiveButton(okButtonListener);
		createDialog();
	}
	
	private void setupViews(List<MenuItem> availableSubmenus, final List<SdlImageItem> images){
		List<MenuItem> submenuList = new ArrayList<MenuItem>(availableSubmenus);
		submenuList.add(0, new MenuItem("Root-level menu", SdlConstants.AddCommandConstants.ROOT_PARENT_ID, true));
		spin_addCommand_submenus.setAdapter(AndroidUtils.createSpinnerAdapter(context, submenuList));
		
		if(images == null || images.size() <= 0){
			cb_image.setVisibility(View.GONE);
			et_imageName.setVisibility(View.GONE);
		}
		
		cb_image.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					if(images == null || images.size() <= 0){
						Toast.makeText(context, "No images have been added to the system yet.", Toast.LENGTH_LONG).show();
					}
					else{
						BaseAlertDialog selectImageDialog = new ImageListDialog(context, images);
						selectImageDialog.setListener(new BaseAlertDialog.Listener() {
							@Override
							public void onResult(Object resultData) {
								selectedImage = (SdlImageItem) resultData;
								et_imageName.setText(selectedImage.getImageName());
							}
						});
						selectImageDialog.show();
					}
				}
				else{
					et_imageName.setText("");
				}
			}
		});
	}

	@Override
	protected void findViews(View parent) {
		et_newCommand = (EditText) parent.findViewById(R.id.et_addCommand_commandName);
		et_voiceRecKeyword = (EditText) parent.findViewById(R.id.et_addCommand_voiceRecKeyword);
		et_imageName = (EditText) parent.findViewById(R.id.et_imageName);
		spin_addCommand_submenus = (Spinner) parent.findViewById(R.id.spin_addCommand_submenus);
		cb_image = (CheckBox) parent.findViewById(R.id.check_enable_image);
	}
	
	//dialog button listeners
	private final DialogInterface.OnClickListener okButtonListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			if(listener != null){
				// grab the data from the views
				final String commandName = et_newCommand.getText().toString();
				final int position = SdlConstants.AddCommandConstants.DEFAULT_POSITION;
				final String voiceRecKeyword   = et_voiceRecKeyword.getText().toString();
				final MenuItem parentBank = (MenuItem) spin_addCommand_submenus.getSelectedItem();
				final int parentId = (parentBank != null) ? parentBank.getId() : SdlConstants.AddCommandConstants.INVALID_PARENT_ID;
				final String imageName = (selectedImage != null) ? selectedImage.getImageName() : null;
				
				// all we really need is a valid name
				if(commandName.length() > 0){
					// if we have it, let's create our RPC object
					RPCRequest result = SdlRequestFactory.addCommand(commandName, position, parentId, voiceRecKeyword, imageName);
					notifyListener(result);
				}
				else{
					// if we don't have a valid name, inform the user.
					Toast.makeText(context, "Must enter command name.", Toast.LENGTH_LONG).show();
				}
			}
		}
	};
	
}

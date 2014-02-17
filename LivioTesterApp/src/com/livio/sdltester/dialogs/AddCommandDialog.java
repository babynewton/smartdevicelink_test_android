package com.livio.sdltester.dialogs;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.livio.sdl.SdlImageItem;
import com.livio.sdl.dialogs.BaseAlertDialog;
import com.livio.sdl.dialogs.BaseOkCancelDialog;
import com.livio.sdl.enums.SdlCommand;
import com.livio.sdl.menu.MenuItem;
import com.livio.sdl.utils.AndroidUtils;
import com.livio.sdltester.R;
import com.smartdevicelink.proxy.rpc.AddCommand;
import com.smartdevicelink.proxy.rpc.Image;
import com.smartdevicelink.proxy.rpc.MenuParams;
import com.smartdevicelink.proxy.rpc.enums.ImageType;

public class AddCommandDialog extends BaseOkCancelDialog implements OnCheckedChangeListener{
	
	private static final SdlCommand SYNC_COMMAND = SdlCommand.ADD_COMMAND;
	private static final String DIALOG_TITLE = SYNC_COMMAND.toString();
	
	private EditText et_newCommand;
	private EditText et_voiceRecKeyword;
	
	private Spinner spin_addCommand_submenus;
	private Button but_addCommand_selectImage;
	
	private SdlImageItem selectedImage;
	
	public AddCommandDialog(Context context, List<MenuItem> availableBanks, List<SdlImageItem> images) {
		super(context, DIALOG_TITLE, R.layout.add_command);
		setupViews(availableBanks, images);
		setPositiveButton(okButtonListener);
		createDialog();
	}
	
	private void setupViews(List<MenuItem> availableSubmenus, final List<SdlImageItem> images){
		List<MenuItem> submenuList = new ArrayList<MenuItem>(availableSubmenus);
		submenuList.add(0, new MenuItem("Root-level menu", 0, true));
		spin_addCommand_submenus.setAdapter(AndroidUtils.createSpinnerAdapter(context, submenuList));
		
		but_addCommand_selectImage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				BaseAlertDialog selectImageDialog = new ImageListDialog(context, images);
				selectImageDialog.setListener(new BaseAlertDialog.Listener() {
					@Override
					public void onResult(Object resultData) {
						selectedImage = (SdlImageItem) resultData;
					}
				});
				selectImageDialog.show();
			}
		});
	}

	@Override
	protected void findViews(View parent) {
		et_newCommand = (EditText) parent.findViewById(R.id.et_addCommand_commandName);
		et_voiceRecKeyword = (EditText) parent.findViewById(R.id.et_addCommand_voiceRecKeyword);
		
		spin_addCommand_submenus = (Spinner) parent.findViewById(R.id.spin_addCommand_submenus);
		
		but_addCommand_selectImage = (Button) parent.findViewById(R.id.but_addCommand_selectImage);
	}
	
	//dialog button listeners
	private final DialogInterface.OnClickListener okButtonListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			if(listener != null){
				final String commandName = et_newCommand.getText().toString();
				final String voiceRecKeyword   = et_voiceRecKeyword.getText().toString();
				final MenuItem parentBank = (MenuItem) spin_addCommand_submenus.getSelectedItem();
				
				if(commandName.length() > 0){
					AddCommand result = new AddCommand();
					MenuParams menuParams = new MenuParams();
					menuParams.setMenuName(commandName);
					menuParams.setPosition(0); // TODO - get number of items in the list??? should I input function banks instead of menu buttons?
					
					// if we're adding to the root-level menu (id = 0), we don't need to set any id here
					if(parentBank != null && parentBank.getId() != 0){
						menuParams.setParentID(parentBank.getId());
					}
					result.setMenuParams(menuParams);
					
					if(voiceRecKeyword.length() > 0){
						Vector<String> vrCommands = new Vector<String>(1);
						vrCommands.add(voiceRecKeyword);
						result.setVrCommands(vrCommands);
					}
					
					if(selectedImage != null){
						Image image = new Image();
						image.setImageType(ImageType.DYNAMIC);
						image.setValue(selectedImage.getImageName());
						result.setCmdIcon(image);
					}
					
					notifyListener(result);
				}
				else{
					Toast.makeText(context, "Must enter command name.", Toast.LENGTH_LONG).show();
				}
			}
		}
	};

	/*
	 * Checkbox click listener methods
	 */
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO - remove this code once images are supported.
		if(isChecked){
			Toast.makeText(context, context.getResources().getString(R.string.not_implemented), Toast.LENGTH_LONG).show();
		}
		
		// TODO - bring this code back in once images are supported.
//		enableIconViews(isChecked);
	}
	
}

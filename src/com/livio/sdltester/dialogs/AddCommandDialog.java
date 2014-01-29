package com.livio.sdltester.dialogs;

import java.util.List;
import java.util.Vector;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.livio.sdl.SdlFunctionBank;
import com.livio.sdl.enums.SdlCommand;
import com.livio.sdl.enums.SdlImageType;
import com.livio.sdltester.R;
import com.smartdevicelink.proxy.rpc.AddCommand;
import com.smartdevicelink.proxy.rpc.MenuParams;

public class AddCommandDialog extends BaseAlertDialog implements OnCheckedChangeListener{

	// TODO - add ability to select an image without manually entering the text
	// TODO - add ? icon next to static/dynamic and explain what the differences between the two are and what that means for devs
	
	private static final SdlCommand SYNC_COMMAND = SdlCommand.ADD_COMMAND;
	private static final String DIALOG_TITLE = SYNC_COMMAND.toString();
	
	private ArrayAdapter<SdlFunctionBank> functionBankAdapter;
	private ArrayAdapter<SdlImageType> imageTypeAdapter;
	
	private EditText et_newCommand;
	private EditText et_voiceRecKeyword;
	
	private ImageView iv_image;
	private TextView tv_imageType;
	
	private Spinner spin_addCommand_submenus;
	private Spinner spin_addCommand_iconType;
	
	private CheckBox check_addCommand_useIcon;
	
	public AddCommandDialog(Context context, List<SdlFunctionBank> availableBanks) {
		super(context, DIALOG_TITLE, R.layout.add_command);
		createAndSetAdapters(availableBanks);
		setPositiveButton(okButtonListener);
		createDialog();
	}
	
	private void createAndSetAdapters(List<SdlFunctionBank> availableBanks){
		functionBankAdapter = new ArrayAdapter<SdlFunctionBank>(context, android.R.layout.select_dialog_item);
		functionBankAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		for(SdlFunctionBank functionBank : availableBanks){
			functionBankAdapter.add(functionBank);
		}
		spin_addCommand_submenus.setAdapter(functionBankAdapter);
		
		imageTypeAdapter = new ArrayAdapter<SdlImageType>(context, android.R.layout.select_dialog_item, SdlImageType.values());
		imageTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spin_addCommand_iconType.setAdapter(imageTypeAdapter);
	}

	@Override
	protected void findViews(View parent) {
		et_newCommand = (EditText) view.findViewById(R.id.et_addCommand_commandName);
		et_voiceRecKeyword = (EditText) view.findViewById(R.id.et_addCommand_voiceRecKeyword);
		
		iv_image = (ImageView) view.findViewById(R.id.iv_addCommand_image);
		
		tv_imageType = (TextView) view.findViewById(R.id.tv_imageType);
		
		spin_addCommand_submenus = (Spinner) view.findViewById(R.id.spin_addCommand_submenus);
		spin_addCommand_iconType = (Spinner) view.findViewById(R.id.spin_addCommand_iconType);
		
		check_addCommand_useIcon = (CheckBox) view.findViewById(R.id.check_addCommand_useIcon);
		check_addCommand_useIcon.setOnCheckedChangeListener(this);
		enableIconViews(check_addCommand_useIcon.isChecked());
	}
	
	private void enableIconViews(boolean enable){
		int visibility = (enable) ? View.VISIBLE : View.GONE;
		spin_addCommand_iconType.setVisibility(visibility);
		iv_image.setVisibility(visibility);
		tv_imageType.setVisibility(visibility);
	}
	
	//dialog button listeners
	private final DialogInterface.OnClickListener okButtonListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			if(listener != null){
				final String commandName = et_newCommand.getText().toString();
				final String voiceRecKeyword   = et_voiceRecKeyword.getText().toString();
				final SdlFunctionBank parentBank = (SdlFunctionBank) spin_addCommand_submenus.getSelectedItem();
				Bitmap image = null;
				SdlImageType imageType = null;
				
				if(commandName.length() > 0){
					if(check_addCommand_useIcon.isChecked()){
						image = null; // TODO - return the image that was selected
						imageType = SdlImageType.lookupByReadableName(spin_addCommand_iconType.getSelectedItem().toString());
					}
					
					AddCommand result = new AddCommand();
					MenuParams menuParams = new MenuParams();
					menuParams.setMenuName(commandName);
					menuParams.setParentID(parentBank.getId());
					result.setMenuParams(menuParams);
					
					Vector<String> vrCommands = new Vector<String>(1);
					vrCommands.add(voiceRecKeyword);
					result.setVrCommands(vrCommands);
					
					if(image != null){
						// TODO - add "Image" into the returned result
					}
					
					notifyListener(result);
				}
				else{
					notifyListener(null);
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

package com.livio.sdltester.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.livio.sdl.dialogs.BaseOkCancelDialog;
import com.livio.sdl.enums.SdlCommand;
import com.livio.sdltester.R;
import com.smartdevicelink.proxy.rpc.AddSubMenu;

public class AddSubMenuDialog extends BaseOkCancelDialog{

	private static final SdlCommand SYNC_COMMAND = SdlCommand.ADD_SUBMENU;
	private static final String DIALOG_TITLE = SYNC_COMMAND.toString();
	
	private EditText et_submenuName;
	
	public AddSubMenuDialog(Context context) {
		super(context, DIALOG_TITLE, R.layout.add_submenu);
		setPositiveButton(okButtonListener);
		createDialog();
	}

	@Override
	protected void findViews(View parent) {
		et_submenuName = (EditText) view.findViewById(R.id.et_addSubMenu_subMenuName);
	}
	
	//dialog button click listeners
	private final DialogInterface.OnClickListener okButtonListener = new DialogInterface.OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			final String submenuName = et_submenuName.getText().toString();
			
			// set applicable data & return it to whoever's listening 
			if(submenuName.length() > 0){
				AddSubMenu rpcCommand = new AddSubMenu();
				rpcCommand.setMenuName(submenuName);
				notifyListener(rpcCommand);
			}
			else{
				Toast.makeText(context, "Must enter a submenu name.", Toast.LENGTH_LONG).show();
			}
		}
	};
	
}

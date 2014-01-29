package com.livio.sdltester.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.ArrayAdapter;

import com.livio.sdl.enums.EnumClickListener;
import com.livio.sdl.enums.SdlCommand;
import com.livio.sdltester.R;

public class SendMessageDialog{
	
	private static String DIALOG_TITLE;
	
	private Dialog dialog = null;
	private ArrayAdapter<SdlCommand> adapter;
	private EnumClickListener listener;
	
	public SendMessageDialog(Context context, EnumClickListener listener) {
		DIALOG_TITLE = context.getResources().getString(R.string.sdl_command_dialog_title);
		this.listener = listener;
		createSendMessageDialog(context);
	}
	
	//private methods
	private void createSendMessageDialog(Context context){
		SdlCommand[] syncCommands = SdlCommand.getSortedArray();
		adapter = new ArrayAdapter<SdlCommand>(context, android.R.layout.simple_list_item_1, syncCommands);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(DIALOG_TITLE)
		       .setAdapter(adapter, wrapperListener);
		
		dialog = builder.create();
	}
	
	//public methods
	public void show(){
		dialog.show();
	}
	
	public void dismiss(){
		if(dialog != null && dialog.isShowing()){
			dialog.dismiss();
		}
	}

	private final DialogInterface.OnClickListener wrapperListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			listener.OnEnumItemClicked(adapter.getItem(which));
		}
	};
}

package com.livio.sdltester.dialogs;


import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.EditText;

import com.livio.sdl.IpAddress;
import com.livio.sdl.dialogs.BaseOkCancelDialog;
import com.livio.sdltester.R;

public class SdlConnectionDialog extends BaseOkCancelDialog {

	private static final String DIALOG_TITLE = "SDL Connection";
	
	private EditText et_ipAddress, et_ipPort;
	
	public SdlConnectionDialog(Context context) {
		super(context, DIALOG_TITLE, R.layout.sdl_connection);
		setPositiveButton(okButtonListener);
		setNegativeButton(cancelListener);
		createDialog();
	}
	
	public SdlConnectionDialog(Context context, String initIpAddress, String initPort){
		this(context);
		setEditTextStrings(initIpAddress, initPort);
	}
	
	private void setEditTextStrings(String ipAddress, String ipPort){
		et_ipAddress.setText(ipAddress);
		et_ipPort.setText(ipPort);
	}

	@Override
	protected void findViews(View parent) {
		et_ipAddress = (EditText) parent.findViewById(R.id.et_ipAddress);
		et_ipPort = (EditText) parent.findViewById(R.id.et_ipPort);
	}
	
	//dialog button listeners
	private final DialogInterface.OnClickListener okButtonListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			final String ipAddress = et_ipAddress.getText().toString();
			final String ipPort = et_ipPort.getText().toString();
			
			IpAddress result = new IpAddress(ipAddress, ipPort);
			notifyListener(result);
		}
	};
	
	private final DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			notifyListener(null);
		}
	};

}

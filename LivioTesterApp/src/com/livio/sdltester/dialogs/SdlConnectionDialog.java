package com.livio.sdltester.dialogs;


import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.livio.sdl.IpAddress;
import com.livio.sdl.dialogs.BaseOkCancelDialog;
import com.livio.sdltester.LivioSdlTesterPreferences;
import com.livio.sdltester.R;

public class SdlConnectionDialog extends BaseOkCancelDialog {

	private static final String DIALOG_TITLE = "SDL Connection";
	
	private EditText et_ipAddress, et_ipPort;
	private RadioGroup radio_transportGroup;
	private RadioButton radio_wifi, radio_bt;
	
	public SdlConnectionDialog(Context context) {
		super(context, DIALOG_TITLE, R.layout.sdl_connection);
		setPositiveButton(okButtonListener);
		setNegativeButton(cancelListener);
		createDialog();
	}
	
	public SdlConnectionDialog(Context context, int transportType, String initIpAddress, String initPort){
		this(context);
		RadioButton initButton = (transportType == LivioSdlTesterPreferences.PREF_TRANSPORT_BLUETOOTH) ? radio_bt : radio_wifi;
		radio_transportGroup.check(initButton.getId());
		initButton.performClick();
		setEditTextStrings(initIpAddress, initPort);
	}
	
	private void setEditTextStrings(String ipAddress, String ipPort){
		et_ipAddress.setText(ipAddress);
		et_ipPort.setText(ipPort);
	}
	
	private void setEditTextVisibility(int visibility){
		et_ipAddress.setVisibility(visibility);
		et_ipPort.setVisibility(visibility);
	}

	@Override
	protected void findViews(View parent) {
		et_ipAddress = (EditText) parent.findViewById(R.id.et_ipAddress);
		et_ipPort = (EditText) parent.findViewById(R.id.et_ipPort);
		
		radio_transportGroup = (RadioGroup) parent.findViewById(R.id.radio_transportGroup);
		radio_wifi = (RadioButton) parent.findViewById(R.id.radio_wifi);
		radio_wifi.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setEditTextVisibility(View.VISIBLE);
			}
		});
		radio_bt = (RadioButton) parent.findViewById(R.id.radio_bt);
		radio_bt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setEditTextVisibility(View.GONE);
			}
		});
	}
	
	//dialog button listeners
	private final DialogInterface.OnClickListener okButtonListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			int selectedId = radio_transportGroup.getCheckedRadioButtonId();
			String ipAddress = null, ipPort = null;
			
			if(selectedId == R.id.radio_bt){
				ipAddress = null;
				ipPort = null;
			}
			else if(selectedId == R.id.radio_wifi){
				ipAddress = et_ipAddress.getText().toString();
				ipPort = et_ipPort.getText().toString();
			}
			
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

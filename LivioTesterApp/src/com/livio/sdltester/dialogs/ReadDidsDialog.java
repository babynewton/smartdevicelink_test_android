package com.livio.sdltester.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.text.InputFilter;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.livio.sdl.SdlConstants;
import com.livio.sdl.SdlRequestFactory;
import com.livio.sdl.dialogs.BaseOkCancelDialog;
import com.livio.sdl.enums.SdlCommand;
import com.livio.sdl.viewhelpers.MinMaxInputFilter;
import com.livio.sdltester.R;
import com.smartdevicelink.proxy.RPCRequest;

public class ReadDidsDialog extends BaseOkCancelDialog {

	// TODO didLocation should be comma-separated.
	
	private static final SdlCommand SYNC_COMMAND = SdlCommand.READ_DIDS;
	private static final String DIALOG_TITLE = SYNC_COMMAND.toString();
	
	private static final int MIN_ECU_NUMBER = SdlConstants.ReadDidsConstants.MINIMUM_ECU_ID;
	private static final int MAX_ECU_NUMBER = SdlConstants.ReadDidsConstants.MAXIMUM_ECU_ID;
	
	private EditText et_ecuName, et_didLocation;
	
	public ReadDidsDialog(Context context) {
		super(context, DIALOG_TITLE, R.layout.read_dids);
		setPositiveButton(okButtonListener);
		createDialog();
	}

	@Override
	protected void findViews(View parent) {
		et_ecuName = (EditText) parent.findViewById(R.id.et_readDids_ecuName);
		et_ecuName.setFilters(new InputFilter[]{new MinMaxInputFilter(MIN_ECU_NUMBER, MAX_ECU_NUMBER)});
		et_didLocation = (EditText) parent.findViewById(R.id.et_readDids_didLocation);
		et_didLocation.setFilters(new InputFilter[]{new MinMaxInputFilter(MIN_ECU_NUMBER, MAX_ECU_NUMBER)});
	}
	
	//dialog button listeners
	private final DialogInterface.OnClickListener okButtonListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			final String ecuName = et_ecuName.getText().toString();
			final String didLocation = et_didLocation.getText().toString();
			if(ecuName.length() > 0 && didLocation.length() > 0){
				RPCRequest result = SdlRequestFactory.readDid(Integer.parseInt(ecuName), Integer.parseInt(didLocation));
				notifyListener(result);
			}
			else{
				Toast.makeText(context, "Must enter ECU name & DID location.", Toast.LENGTH_LONG).show();
			}
		}
	};

}

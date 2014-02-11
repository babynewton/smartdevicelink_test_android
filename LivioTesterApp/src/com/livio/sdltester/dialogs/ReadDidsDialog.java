package com.livio.sdltester.dialogs;

import java.util.Vector;

import android.content.Context;
import android.content.DialogInterface;
import android.text.InputFilter;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.livio.sdl.datatypes.MinMaxInputFilter;
import com.livio.sdl.dialogs.BaseOkCancelDialog;
import com.livio.sdl.enums.SdlCommand;
import com.livio.sdltester.R;
import com.smartdevicelink.proxy.rpc.ReadDID;

public class ReadDidsDialog extends BaseOkCancelDialog {

	private static final SdlCommand SYNC_COMMAND = SdlCommand.READ_DIDS;
	private static final String DIALOG_TITLE = SYNC_COMMAND.toString();
	
	private static final int MIN_ECU_NUMBER = 0;
	private static final int MAX_ECU_NUMBER = 65535;
	
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
				Vector<Integer> didIds = new Vector<Integer>(); // TODO - make this dynamic so users can enter multiple DID locations.
				didIds.add(Integer.parseInt(didLocation));
				
				ReadDID readDid = new ReadDID();
				readDid.setEcuName(Integer.parseInt(ecuName));
				readDid.setDidLocation(didIds);
				notifyListener(readDid);
			}
			else{
				Toast.makeText(context, "Must enter ECU name & DID location.", Toast.LENGTH_LONG).show();
			}
		}
	};

}

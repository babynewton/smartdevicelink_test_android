package com.livio.sdltester.dialogs;

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
import com.smartdevicelink.proxy.rpc.GetDTCs;

public class GetDtcsDialog extends BaseOkCancelDialog {

	private static final SdlCommand SYNC_COMMAND = SdlCommand.GET_DTCS;
	private static final String DIALOG_TITLE = SYNC_COMMAND.toString();
	
	private static final int MIN_ECU_NUMBER = 0;
	private static final int MAX_ECU_NUMBER = 65535;
	
	private EditText et_ecuName;
	
	public GetDtcsDialog(Context context) {
		super(context, DIALOG_TITLE, R.layout.get_dtcs);
		setPositiveButton(okButtonListener);
		createDialog();
	}

	@Override
	protected void findViews(View parent) {
		et_ecuName = (EditText) parent.findViewById(R.id.et_getDtcs_ecuName);
		et_ecuName.setFilters(new InputFilter[]{new MinMaxInputFilter(MIN_ECU_NUMBER, MAX_ECU_NUMBER)});
	}
	
	//dialog button listeners
	private final DialogInterface.OnClickListener okButtonListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			final String ecuName = et_ecuName.getText().toString();
			if(ecuName.length() > 0){
				GetDTCs getDtcs = new GetDTCs();
				getDtcs.setEcuName(Integer.parseInt(ecuName));
				notifyListener(getDtcs);
			}
			else{
				Toast.makeText(context, "Must enter an ECU name.", Toast.LENGTH_LONG).show();
			}
		}
	};

}

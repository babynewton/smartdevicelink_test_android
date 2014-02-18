package com.livio.sdltester.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.livio.sdl.SdlRequestFactory;
import com.livio.sdl.dialogs.BaseOkCancelDialog;
import com.livio.sdl.enums.SdlCommand;
import com.livio.sdl.enums.SdlLanguage;
import com.livio.sdl.utils.AndroidUtils;
import com.livio.sdltester.R;
import com.smartdevicelink.proxy.RPCRequest;
import com.smartdevicelink.proxy.rpc.enums.Language;

public class ChangeRegistrationDialog extends BaseOkCancelDialog{

	private static final SdlCommand SYNC_COMMAND = SdlCommand.CHANGE_REGISTRATION;
	private static final String DIALOG_TITLE = SYNC_COMMAND.toString();
	
	private Spinner spin_changeRegistration_language;
	private Spinner spin_changeRegistration_hmiLanguage;
	
	public ChangeRegistrationDialog(Context context) {
		super(context, DIALOG_TITLE, R.layout.change_registration);
		setPositiveButton(okButtonListener);
		createDialog();
	}

	@Override
	protected void findViews(View parent) {
		ArrayAdapter<SdlLanguage> adapter = AndroidUtils.createSpinnerAdapter(context, SdlLanguage.values());
		
		spin_changeRegistration_language = (Spinner) view.findViewById(R.id.spin_changeRegistration_language);
		spin_changeRegistration_language.setAdapter(adapter);
		spin_changeRegistration_hmiLanguage = (Spinner) view.findViewById(R.id.spin_changeRegistration_hmiLanguage);
		spin_changeRegistration_hmiLanguage.setAdapter(adapter);
	}
	
	//dialog button listeners
	private final DialogInterface.OnClickListener okButtonListener = new DialogInterface.OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			final String language = spin_changeRegistration_language.getSelectedItem().toString();
			final String hmiLanguage = spin_changeRegistration_hmiLanguage.getSelectedItem().toString();
			
			RPCRequest result = SdlRequestFactory.changeRegistration(Language.valueForString(language), Language.valueForString(hmiLanguage));
			notifyListener(result);
		}
	};
	
}

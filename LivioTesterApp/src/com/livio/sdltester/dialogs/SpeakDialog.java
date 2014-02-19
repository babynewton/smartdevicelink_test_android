package com.livio.sdltester.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.livio.sdl.SdlRequestFactory;
import com.livio.sdl.dialogs.BaseOkCancelDialog;
import com.livio.sdl.enums.SdlCommand;
import com.livio.sdl.enums.SdlSpeechCapability;
import com.livio.sdl.utils.AndroidUtils;
import com.livio.sdltester.R;
import com.smartdevicelink.proxy.RPCRequest;
import com.smartdevicelink.proxy.rpc.enums.SpeechCapabilities;

public class SpeakDialog extends BaseOkCancelDialog{

	private static final SdlCommand SYNC_COMMAND = SdlCommand.SPEAK;
	private static final String DIALOG_TITLE = SYNC_COMMAND.toString();

	private EditText et_textToSpeak;
	private Spinner spin_speechCapabilities;
	
	public SpeakDialog(Context context){
		super(context, DIALOG_TITLE, R.layout.speak);
		setPositiveButton(okButtonListener);
		createDialog();
	}
	
	@Override
	protected void findViews(View view){
		et_textToSpeak = (EditText) view.findViewById(R.id.et_textToSpeak);
		spin_speechCapabilities = (Spinner) view.findViewById(R.id.spin_speechCapabilities);
		spin_speechCapabilities.setAdapter(AndroidUtils.createSpinnerAdapter(context, SdlSpeechCapability.values()));
	}
	
	//dialog button listeners
	private final DialogInterface.OnClickListener okButtonListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			final String ttsText = et_textToSpeak.getText().toString();
			final SdlSpeechCapability sdlSpeechCapabilities = (SdlSpeechCapability) spin_speechCapabilities.getSelectedItem();
			final SpeechCapabilities speechCapabilities = SdlSpeechCapability.translateToLegacy(sdlSpeechCapabilities);
			
			if(speechCapabilities != SpeechCapabilities.SILENCE){
				if(ttsText.length() > 0){
					RPCRequest result = SdlRequestFactory.speak(ttsText, speechCapabilities);
					notifyListener(result);
				}
				else{
					Toast.makeText(context, "Must enter text to speak.", Toast.LENGTH_LONG).show();
				}
			}
			else{
				RPCRequest result = SdlRequestFactory.speak(ttsText, speechCapabilities);
				notifyListener(result);
			}
			
			
			
		}
	};
	
}

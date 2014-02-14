package com.livio.sdltester.dialogs;

import java.util.Vector;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.livio.sdl.dialogs.BaseOkCancelDialog;
import com.livio.sdl.enums.SdlCommand;
import com.livio.sdl.enums.SdlSpeechCapability;
import com.livio.sdl.utils.AndroidUtils;
import com.livio.sdltester.R;
import com.smartdevicelink.proxy.TTSChunkFactory;
import com.smartdevicelink.proxy.rpc.Speak;
import com.smartdevicelink.proxy.rpc.TTSChunk;
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
			
			if(ttsText.length() > 0){
				Speak speak = new Speak();
				Vector<TTSChunk> ttsChunks = new Vector<TTSChunk>(1);
				ttsChunks.add(TTSChunkFactory.createChunk(speechCapabilities, ttsText));
				speak.setTtsChunks(ttsChunks);
				
				notifyListener(speak);
			}
			else{
				Toast.makeText(context, "Must enter text to speak.", Toast.LENGTH_LONG).show();
			}
		}
	};
	
}

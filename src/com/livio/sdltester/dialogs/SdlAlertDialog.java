package com.livio.sdltester.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.livio.sdl.enums.SdlCommand;
import com.livio.sdltester.R;
import com.smartdevicelink.proxy.TTSChunkFactory;
import com.smartdevicelink.proxy.rpc.Alert;

public class SdlAlertDialog extends BaseAlertDialog implements OnCheckedChangeListener, OnSeekBarChangeListener{

	private static final SdlCommand SYNC_COMMAND = SdlCommand.ALERT;
	private static final String DIALOG_TITLE = SYNC_COMMAND.toString();
	
	//set up your min & max time allowed here.  divide by 10 for actual time in seconds.
	private static final int MINIMUM_ALERT_TONE_TIME = 0; // 0.0 seconds
	private static final int MAXIMUM_ALERT_TONE_TIME = 50;// 5.0 seconds
	
	//seekbar can only start at 0, so we have to do all these stupid adjustments everywhere...
	private static final int MAX_SEEKBAR_PROGRESS = MAXIMUM_ALERT_TONE_TIME - MINIMUM_ALERT_TONE_TIME;

	//this is your default selection for tone duration.  again, divide by 10 for the actual time in seconds.
	private static final int DEFAULT_TONE_DURATION = 10;  // 1.0 second
	
	//another stupid adjustment.  your default selection must be offset by the minimum value since seekbars can only start at 0
	private static final int ADJUSTED_DEFAULT_TONE_DURATION = DEFAULT_TONE_DURATION - MINIMUM_ALERT_TONE_TIME;
	
	// a seekbar cannot do decimal points, so it currently ranges 0-50, which is then
	// divided by 10.0f to give us a number of seconds, rounded to 1/10 of a second.
	private static final float TENS_PLACE_DENOMINATOR = 10.0f;
	
	//multiplier to convert progress of seek bar (0-50) to milliseconds
	private static final int PROGRESS_TO_MILLISEC_MULTIPLIER = 100;
	
	private EditText et_alert_textToSpeak;
	private EditText et_alert_line1;
	private EditText et_alert_line2;
	private EditText et_alert_line3;
	
	private TextView tv_alert_toneDuration, tv_alert_toneDurationHeader;
	
	private CheckBox check_alert_playTone;
	
	private SeekBar seek_alert_toneDuration;
	
	//TODO - leaving these out for now because I don't know what they do yet... - MRB
	//private CheckBox check_alert_includeSoftButtons;
	//private Button but_alert_includeSoftButtons;
	
	public SdlAlertDialog(Context context) {
		super(context, DIALOG_TITLE, R.layout.alert);
		setPositiveButton(okButtonListener);
		createDialog();
	}

	@Override
	protected void findViews(View parent) {
		et_alert_textToSpeak = (EditText) view.findViewById(R.id.et_alert_textToSpeak);
		et_alert_line1 = (EditText) view.findViewById(R.id.et_alert_line1);
		et_alert_line2 = (EditText) view.findViewById(R.id.et_alert_line2);
		et_alert_line3 = (EditText) view.findViewById(R.id.et_alert_line3);
		
		tv_alert_toneDuration = (TextView) view.findViewById(R.id.tv_alert_toneDuration);
		tv_alert_toneDurationHeader = (TextView) view.findViewById(R.id.tv_alert_toneDurationHeader);
		
		seek_alert_toneDuration = (SeekBar) view.findViewById(R.id.seek_alert_toneDuration);
		seek_alert_toneDuration.setMax(MAX_SEEKBAR_PROGRESS);
		seek_alert_toneDuration.setProgress(ADJUSTED_DEFAULT_TONE_DURATION);
		seek_alert_toneDuration.setOnSeekBarChangeListener(this);
		
		check_alert_playTone = (CheckBox) view.findViewById(R.id.check_alert_playTone);
		check_alert_playTone.setOnCheckedChangeListener(this);
		
		//make initial updates to the UI using default values
		updateProgressText(progressToFloat(ADJUSTED_DEFAULT_TONE_DURATION));
		enableDuration(check_alert_playTone.isChecked());
		
		//TODO - leaving these out for now because I don't know what they do yet... - MRB
		//check_alert_includeSoftButtons = (CheckBox) view.findViewById(R.id.check_alert_includeSoftButtons);
		//
		//but_alert_includeSoftButtons = (Button) view.findViewById(R.id.but_alert_includeSoftButtons);
		//TODO - add on click listener to this button.
	}

	private void updateProgressText(float progress){
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(progress);
		strBuilder.append(context.getResources().getString(R.string.units_seconds));
		tv_alert_toneDuration.setText(strBuilder.toString());
	}
	
	private void enableDuration(boolean enabled){
		int visibility = (enabled) ? View.VISIBLE : View.GONE;
		tv_alert_toneDurationHeader.setVisibility(visibility);
		tv_alert_toneDuration.setVisibility(visibility);
		seek_alert_toneDuration.setVisibility(visibility);
	}
	
	//static methods
	private static int progressInMs(int progress){
		return adjustedProgress(progress)  * PROGRESS_TO_MILLISEC_MULTIPLIER;
	}
	
	private static float progressToFloat(int progress){
		return adjustedProgress(progress) / TENS_PLACE_DENOMINATOR;
	}
	
	private static int adjustedProgress(int progress){
		return progress + MINIMUM_ALERT_TONE_TIME;
	}
	
	// dialog button listsners
	private final DialogInterface.OnClickListener okButtonListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			final String textToSpeak = et_alert_textToSpeak.getText().toString();
			final String line1   = et_alert_line1.getText().toString();
			final String line2   = et_alert_line2.getText().toString();
			final String line3   = et_alert_line3.getText().toString();
			final int toneDurationInMs = progressInMs(seek_alert_toneDuration.getProgress());
			
			Alert alert = new Alert();
			if(textToSpeak.length() > 0){
				alert.setTtsChunks(TTSChunkFactory.createSimpleTTSChunks(textToSpeak));
			}
			alert.setAlertText1(line1);
			alert.setAlertText2(line2);
			alert.setAlertText3(line3);
			
			if(check_alert_playTone.isChecked()){
				alert.setDuration(toneDurationInMs);
			}
			
			notifyListener(alert);
		}
	};

	/*
	 * On Check Changed Listener Methods
	 */
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		enableDuration(isChecked);
	}

	/*
	 * On Seek Bar Changed Listener Methods
	 */
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		updateProgressText(progressToFloat(progress));
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// do nothing (required method for SeekBar listener)
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// do nothing (required method for SeekBar listener)
		
	}
	
}

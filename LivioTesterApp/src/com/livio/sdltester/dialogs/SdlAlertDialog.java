package com.livio.sdltester.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.livio.sdl.SdlConstants;
import com.livio.sdl.SdlRequestFactory;
import com.livio.sdl.dialogs.BaseOkCancelDialog;
import com.livio.sdl.enums.SdlCommand;
import com.livio.sdltester.R;
import com.smartdevicelink.proxy.RPCRequest;

public class SdlAlertDialog extends BaseOkCancelDialog implements OnSeekBarChangeListener{

	private static final SdlCommand SYNC_COMMAND = SdlCommand.ALERT;
	private static final String DIALOG_TITLE = SYNC_COMMAND.toString();

	// TODO clean up the math surrounding the seekbar
	
	// a seekbar cannot do decimal points, so it currently ranges 0-50, which is then
	// divided by 10.0f to give us a number of seconds, rounded to 1/10 of a second.
	private static final float TENS_PLACE_DENOMINATOR = 10.0f;
	
	//set up your min & max time allowed here.  divide by 10 for actual time in seconds.
	private static final int MINIMUM_ALERT_TONE_TIME = (int) (SdlConstants.AlertConstants.ALERT_TIME_MINIMUM * TENS_PLACE_DENOMINATOR);
	private static final int MAXIMUM_ALERT_TONE_TIME = (int) (SdlConstants.AlertConstants.ALERT_TIME_MAXIMUM * TENS_PLACE_DENOMINATOR);
	
	//seekbar can only start at 0, so we have to do all these stupid adjustments everywhere...
	private static final int MAX_SEEKBAR_PROGRESS = MAXIMUM_ALERT_TONE_TIME - MINIMUM_ALERT_TONE_TIME;

	//this is your default selection for tone duration.  again, divide by 10 for the actual time in seconds.
	private static final int DEFAULT_TONE_DURATION = 50;  // 5.0 seconds
	
	//another stupid adjustment.  your default selection must be offset by the minimum value since seekbars can only start at 0
	private static final int ADJUSTED_DEFAULT_TONE_DURATION = DEFAULT_TONE_DURATION - MINIMUM_ALERT_TONE_TIME;
	
	//multiplier to convert progress of seek bar (0-50) to milliseconds
	private static final int PROGRESS_TO_MILLISEC_MULTIPLIER = 100;
	
	private EditText et_alert_textToSpeak;
	private EditText et_alert_line1;
	private EditText et_alert_line2;
	private EditText et_alert_line3;
	
	private TextView tv_alert_toneDuration;
	
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
		
		seek_alert_toneDuration = (SeekBar) view.findViewById(R.id.seek_alert_toneDuration);
		seek_alert_toneDuration.setMax(MAX_SEEKBAR_PROGRESS);
		seek_alert_toneDuration.setProgress(ADJUSTED_DEFAULT_TONE_DURATION);
		seek_alert_toneDuration.setOnSeekBarChangeListener(this);
		
		check_alert_playTone = (CheckBox) view.findViewById(R.id.check_alert_playTone);
		
		//make initial updates to the UI using default values
		updateProgressText(progressToFloat(ADJUSTED_DEFAULT_TONE_DURATION));
		
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
			String textToSpeak = et_alert_textToSpeak.getText().toString();
			String line1   = et_alert_line1.getText().toString();
			String line2   = et_alert_line2.getText().toString();
			String line3   = et_alert_line3.getText().toString();
			int toneDurationInMs = progressInMs(seek_alert_toneDuration.getProgress());
			boolean playTone = check_alert_playTone.isChecked();
			
			if(textToSpeak.equals("")){
				textToSpeak = " ";
			}
			if(line1.equals("")){
				line1 = " ";
			}
			if(line2.equals("")){
				line2 = " ";
			}
			if(line3.equals("")){
				line3 = " ";
			}
			
			RPCRequest result = SdlRequestFactory.alert(textToSpeak, line1, line2, line3, playTone, toneDurationInMs);
			notifyListener(result);
		}
	};

	/*
	 * On Seek Bar Changed Listener Methods
	 */
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		updateProgressText(progressToFloat(progress));
	}

	@Override public void onStartTrackingTouch(SeekBar seekBar) {}
	@Override public void onStopTrackingTouch(SeekBar seekBar) {}
}

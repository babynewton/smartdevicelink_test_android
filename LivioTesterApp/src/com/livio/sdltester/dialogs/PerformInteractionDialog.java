package com.livio.sdltester.dialogs;

import java.util.List;
import java.util.Vector;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.livio.sdl.dialogs.BaseAlertDialog;
import com.livio.sdl.dialogs.BaseOkCancelDialog;
import com.livio.sdl.dialogs.MultipleListViewDialog;
import com.livio.sdl.enums.SdlCommand;
import com.livio.sdl.enums.SdlInteractionMode;
import com.livio.sdl.menu.MenuItem;
import com.livio.sdltester.R;
import com.smartdevicelink.proxy.TTSChunkFactory;
import com.smartdevicelink.proxy.rpc.PerformInteraction;
import com.smartdevicelink.proxy.rpc.enums.InteractionMode;

public class PerformInteractionDialog extends BaseOkCancelDialog implements OnCheckedChangeListener, OnSeekBarChangeListener{

	private static final SdlCommand SYNC_COMMAND = SdlCommand.ALERT;
	private static final String DIALOG_TITLE = SYNC_COMMAND.toString();
	
	//set up your min & max time allowed here.
	private static final int MINIMUM_ALERT_TONE_TIME = 5; // 5.0 seconds
	private static final int MAXIMUM_ALERT_TONE_TIME = 100;// 100.0 seconds
	
	//seekbar can only start at 0, so we have to do all these stupid adjustments everywhere...
	@SuppressWarnings("unused")
	private static final int MAX_SEEKBAR_PROGRESS = MAXIMUM_ALERT_TONE_TIME - MINIMUM_ALERT_TONE_TIME;

	//this is your default selection for tone duration.  again, divide by 10 for the actual time in seconds.
	private static final int DEFAULT_TONE_DURATION = 30;  // 30.0 seconds
	
	//another stupid adjustment.  your default selection must be offset by the minimum value since seekbars can only start at 0
	private static final int ADJUSTED_DEFAULT_TONE_DURATION = DEFAULT_TONE_DURATION - MINIMUM_ALERT_TONE_TIME;
	
	// a seekbar cannot do decimal points, so it currently ranges 5-100, which is then
	// divided by 1.0f to give us a number of seconds, rounded to 1.0 seconds.
	private static final float TENS_PLACE_DENOMINATOR = 1.0f;
	
	//multiplier to convert progress of seek bar (0-50) to milliseconds
	private static final int PROGRESS_TO_MILLISEC_MULTIPLIER = 1000;
	
	private EditText et_title, et_voicePrompt;
	private Button but_choiceSet;
	private Spinner spin_interactionMode;
	private TextView tv_interactionTimeout, tv_interactionTimeoutDuration;
	private CheckBox check_timeoutEnabled;
	private SeekBar seek_timeoutDuration;
	
	private List<MenuItem> selectedChoiceSets;
	
	//TODO - leaving these out for now because I don't know what they do yet... - MRB
	//private CheckBox check_alert_includeSoftButtons;
	//private Button but_alert_includeSoftButtons;
	
	public PerformInteractionDialog(Context context, List<MenuItem> interactionSets) {
		super(context, DIALOG_TITLE, R.layout.perform_interaction);
		setPositiveButton(okButtonListener);
		setupViews(interactionSets);
		createDialog();
	}
	
	private void setupViews(final List<MenuItem> interactionSets){
		// setup the button click event, which shows another dialog to select which choice sets to show
		but_choiceSet.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MultipleListViewDialog<MenuItem> dialog = new MultipleListViewDialog<MenuItem>(context, "Select Choice Sets to show", interactionSets);
				dialog.setListener(new BaseAlertDialog.Listener() {
					@SuppressWarnings("unchecked")
					@Override
					public void onResult(Object resultData) {
						selectedChoiceSets = (List<MenuItem>) resultData;
					}
				});
				dialog.show();
			}
		});
		
		// setup the spinner
		ArrayAdapter<SdlInteractionMode> adapter = new ArrayAdapter<SdlInteractionMode>(context, android.R.layout.select_dialog_item, SdlInteractionMode.values());
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spin_interactionMode.setAdapter(adapter);
		
		check_timeoutEnabled.setOnCheckedChangeListener(this);
		
		seek_timeoutDuration.setOnSeekBarChangeListener(this);
		seek_timeoutDuration.setProgress(DEFAULT_TONE_DURATION - MINIMUM_ALERT_TONE_TIME);
	}

	@Override
	protected void findViews(View parent) {
		et_title = (EditText) parent.findViewById(R.id.et_performInteraction_title);
		et_voicePrompt = (EditText) parent.findViewById(R.id.et_performInteraction_voicePrompt);
		tv_interactionTimeout = (TextView) parent.findViewById(R.id.tv_performInteraction_timeoutTitle);
		tv_interactionTimeoutDuration = (TextView) parent.findViewById(R.id.tv_performInteraction_timeoutDuration);
		
		but_choiceSet = (Button) parent.findViewById(R.id.but_performInteraction_selectChoiceSets);
		spin_interactionMode = (Spinner) parent.findViewById(R.id.spin_performInteraction_interactionMode);
		check_timeoutEnabled = (CheckBox) parent.findViewById(R.id.check_performInteraction_timeoutEnabled);
		seek_timeoutDuration = (SeekBar) parent.findViewById(R.id.seek_performInteraction_timeoutDuration);
		
		//make initial updates to the UI using default values
		updateProgressText(progressToFloat(ADJUSTED_DEFAULT_TONE_DURATION));
		enableDuration(check_timeoutEnabled.isChecked());
	}

	private void updateProgressText(float progress){
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(progress);
		strBuilder.append(context.getResources().getString(R.string.units_seconds));
		tv_interactionTimeoutDuration.setText(strBuilder.toString());
	}
	
	private void enableDuration(boolean enabled){
		int visibility = (enabled) ? View.VISIBLE : View.GONE;
		tv_interactionTimeout.setVisibility(visibility);
		tv_interactionTimeoutDuration.setVisibility(visibility);
		seek_timeoutDuration.setVisibility(visibility);
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
	
	// dialog button listeners
	private final DialogInterface.OnClickListener okButtonListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
//			private EditText et_title, et_voicePrompt;
//			private Button but_choiceSet;
//			private Spinner spin_interactionMode;
//			private TextView tv_interactionTimeout, tv_interactionTimeoutDuration;
//			private CheckBox check_timeoutEnabled;
//			private SeekBar seek_timeoutDuration;
//			
//			private List<MenuItem> selectedChoiceSets;
			
			if(selectedChoiceSets == null || selectedChoiceSets.size() == 0){
				Toast.makeText(context, "Must select an interaction set in order to perform an interaction", Toast.LENGTH_LONG).show();
			}
			else{
				String title = et_title.getText().toString();
				String voicePrompt = et_voicePrompt.getText().toString();
				boolean timeoutEnabled = check_timeoutEnabled.isChecked();
				SdlInteractionMode sdlInteractionMode = (SdlInteractionMode) spin_interactionMode.getAdapter().getItem(spin_interactionMode.getSelectedItemPosition());
				InteractionMode interactionMode = SdlInteractionMode.translateToLegacy(sdlInteractionMode);
				Vector<Integer> choiceSetIds = new Vector<Integer>(selectedChoiceSets.size());
				for(MenuItem item : selectedChoiceSets){
					choiceSetIds.add(item.getId());
				}
				
				if(title.length() <= 0){
					title = " ";
				}
				
				if(voicePrompt.length() <= 0){
					voicePrompt = " ";
				}
				
				PerformInteraction performInteraction = new PerformInteraction();
				performInteraction.setInitialText(title);
				performInteraction.setInitialPrompt(TTSChunkFactory.createSimpleTTSChunks(voicePrompt));
				performInteraction.setInteractionMode(interactionMode);
				performInteraction.setInteractionChoiceSetIDList(choiceSetIds);
				if(timeoutEnabled){
					int timeoutInMs = progressInMs(seek_timeoutDuration.getProgress());
					performInteraction.setTimeout(timeoutInMs);
				}
				
				notifyListener(performInteraction);
			}
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

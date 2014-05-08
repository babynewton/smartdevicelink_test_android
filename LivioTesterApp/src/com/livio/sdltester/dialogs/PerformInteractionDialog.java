package com.livio.sdltester.dialogs;

import java.util.List;
import java.util.Vector;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
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

import com.livio.sdl.SdlConstants;
import com.livio.sdl.SdlRequestFactory;
import com.livio.sdl.dialogs.BaseAlertDialog;
import com.livio.sdl.dialogs.BaseOkCancelDialog;
import com.livio.sdl.dialogs.MultipleListViewDialog;
import com.livio.sdl.enums.SdlCommand;
import com.livio.sdl.enums.SdlInteractionMode;
import com.livio.sdl.menu.MenuItem;
import com.livio.sdl.utils.AndroidUtils;
import com.livio.sdl.utils.MathUtils;
import com.livio.sdl.viewhelpers.SeekBarCalculator;
import com.livio.sdltester.R;
import com.smartdevicelink.proxy.RPCRequest;
import com.smartdevicelink.proxy.rpc.enums.InteractionMode;

public class PerformInteractionDialog extends BaseOkCancelDialog implements OnCheckedChangeListener, OnSeekBarChangeListener{

	private static final SdlCommand SYNC_COMMAND = SdlCommand.PERFORM_INTERACTION;
	private static final String DIALOG_TITLE = SYNC_COMMAND.toString();
	
	//set up your min & max time allowed here.
	private static final int MINIMUM_TIMEOUT = SdlConstants.PerformInteractionConstants.MINIMUM_TIMEOUT;
	private static final int MAXIMUM_TIMEOUT = SdlConstants.PerformInteractionConstants.MAXIMUM_TIMEOUT;

	//this is your default selection for tone duration.  again, divide by 10 for the actual time in seconds.
	private static final int DEFAULT_TIMEOUT = 30;  // 30.0 seconds
	
	private EditText et_title, et_voicePrompt;
	private Button but_choiceSet;
	private Spinner spin_interactionMode;
	private TextView tv_interactionTimeout, tv_interactionTimeoutDuration;
	private CheckBox check_timeoutEnabled;
	private SeekBar seek_timeoutDuration;
	private SeekBarCalculator progressCalculator;
	private String secondsStr;
	
	private List<MenuItem> selectedChoiceSets;
	
	//TODO - leaving these out for now - MRB
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
				MultipleListViewDialog<MenuItem> dialog = new MultipleListViewDialog<MenuItem>(context, "Select Choice Sets to show", interactionSets, selectedChoiceSets);
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
		spin_interactionMode.setAdapter(AndroidUtils.createSpinnerAdapter(context, SdlInteractionMode.values()));
		
		check_timeoutEnabled.setOnCheckedChangeListener(this);
		
		seek_timeoutDuration.setOnSeekBarChangeListener(this);
		seek_timeoutDuration.setProgress(progressCalculator.calculateProgress(DEFAULT_TIMEOUT));
	}

	@Override
	protected void findViews(View parent) {
		progressCalculator = new SeekBarCalculator(MINIMUM_TIMEOUT, MAXIMUM_TIMEOUT);
		secondsStr = context.getResources().getString(R.string.units_seconds);
		
		et_title = (EditText) parent.findViewById(R.id.et_performInteraction_title);
		et_voicePrompt = (EditText) parent.findViewById(R.id.et_performInteraction_voicePrompt);
		tv_interactionTimeout = (TextView) parent.findViewById(R.id.tv_performInteraction_timeoutTitle);
		tv_interactionTimeoutDuration = (TextView) parent.findViewById(R.id.tv_performInteraction_timeoutDuration);
		
		but_choiceSet = (Button) parent.findViewById(R.id.but_performInteraction_selectChoiceSets);
		spin_interactionMode = (Spinner) parent.findViewById(R.id.spin_performInteraction_interactionMode);
		check_timeoutEnabled = (CheckBox) parent.findViewById(R.id.check_performInteraction_timeoutEnabled);
		seek_timeoutDuration = (SeekBar) parent.findViewById(R.id.seek_performInteraction_timeoutDuration);
		
		//make initial updates to the UI using default values
		updateProgressText(DEFAULT_TIMEOUT);
		enableDuration(check_timeoutEnabled.isChecked());
	}

	private void updateProgressText(float progress){
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(progress);
		strBuilder.append(secondsStr);
		tv_interactionTimeoutDuration.setText(strBuilder.toString());
	}
	
	private void enableDuration(boolean enabled){
		int visibility = (enabled) ? View.VISIBLE : View.GONE;
		tv_interactionTimeout.setVisibility(visibility);
		tv_interactionTimeoutDuration.setVisibility(visibility);
		seek_timeoutDuration.setVisibility(visibility);
	}
	
	// dialog button listeners
	private final DialogInterface.OnClickListener okButtonListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			if(selectedChoiceSets == null || selectedChoiceSets.size() == 0){
				Toast.makeText(context, "Must select an interaction set in order to perform an interaction", Toast.LENGTH_LONG).show();
			}
			else{
				String title = et_title.getText().toString();
				String voicePrompt = et_voicePrompt.getText().toString();
				
				boolean timeoutEnabled = check_timeoutEnabled.isChecked();
				int timeout = SdlConstants.PerformInteractionConstants.INVALID_TIMEOUT;
				if(timeoutEnabled){
					timeout = progressCalculator.calculateProgress(seek_timeoutDuration.getProgress());
					timeout = MathUtils.convertSecsToMillisecs(timeout);
				}
				
				SdlInteractionMode sdlInteractionMode = (SdlInteractionMode) spin_interactionMode.getAdapter().getItem(spin_interactionMode.getSelectedItemPosition());
				InteractionMode interactionMode = SdlInteractionMode.translateToLegacy(sdlInteractionMode);
				
				Vector<Integer> choiceSetIds = new Vector<Integer>(selectedChoiceSets.size());
				for(MenuItem item : selectedChoiceSets){
					choiceSetIds.add(item.getId());
				}
				
				RPCRequest result = SdlRequestFactory.performInteraction(title, voicePrompt, choiceSetIds, interactionMode, timeout);
				notifyListener(result);
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
	@Override public void onStartTrackingTouch(SeekBar seekBar) {}
	@Override public void onStopTrackingTouch(SeekBar seekBar) {}
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		updateProgressText(progressCalculator.calculateValue(progress));
	}

}

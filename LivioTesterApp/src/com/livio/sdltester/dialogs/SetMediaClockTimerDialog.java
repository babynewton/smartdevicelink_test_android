package com.livio.sdltester.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.text.InputFilter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.livio.sdl.datatypes.MinMaxInputFilter;
import com.livio.sdl.dialogs.BaseOkCancelDialog;
import com.livio.sdl.enums.SdlCommand;
import com.livio.sdl.enums.SdlUpdateMode;
import com.livio.sdl.utils.AndroidUtils;
import com.livio.sdltester.R;
import com.smartdevicelink.proxy.rpc.SetMediaClockTimer;
import com.smartdevicelink.proxy.rpc.StartTime;
import com.smartdevicelink.proxy.rpc.enums.UpdateMode;

public class SetMediaClockTimerDialog extends BaseOkCancelDialog {

	private static final SdlCommand SYNC_COMMAND = SdlCommand.SET_MEDIA_CLOCK_TIMER;
	private static final String DIALOG_TITLE = SYNC_COMMAND.toString();
	
	private Spinner spin_mediaClock_type;
	private EditText et_mediaClock_hrs, et_mediaClock_mins, et_mediaClock_secs;
	private TextView tv_mediaClock_clock;
	private LinearLayout ll_mediaClock_clockContainer;
	
	public SetMediaClockTimerDialog(Context context) {
		super(context, DIALOG_TITLE, R.layout.set_media_clock_timer);
		setPositiveButton(okButtonListener);
		createDialog();
	}

	@Override
	protected void findViews(View parent) {
		spin_mediaClock_type = (Spinner) parent.findViewById(R.id.spin_mediaClock_updateMode);
		spin_mediaClock_type.setAdapter(AndroidUtils.createSpinnerAdapter(context, SdlUpdateMode.values()));
		spin_mediaClock_type.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override public void onNothingSelected(AdapterView<?> arg0) {}

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				SdlUpdateMode mode = (SdlUpdateMode) parent.getAdapter().getItem(position);
				onModeUpdated(mode);
			}
		});
		
		et_mediaClock_hrs = (EditText) parent.findViewById(R.id.et_mediaClockHours);
		et_mediaClock_hrs.setFilters(new InputFilter[]{new MinMaxInputFilter(0, 59)});
		et_mediaClock_mins = (EditText) parent.findViewById(R.id.et_mediaClockMins);
		et_mediaClock_mins.setFilters(new InputFilter[]{new MinMaxInputFilter(0, 59)});
		et_mediaClock_secs = (EditText) parent.findViewById(R.id.et_mediaClockSecs);
		et_mediaClock_secs.setFilters(new InputFilter[]{new MinMaxInputFilter(0, 59)});
		
		tv_mediaClock_clock = (TextView) parent.findViewById(R.id.tv_mediaClock_clock);
		
		ll_mediaClock_clockContainer = (LinearLayout) parent.findViewById(R.id.ll_clock);
	}
	
	private void onModeUpdated(SdlUpdateMode mode){
		enableClockView( (mode == SdlUpdateMode.COUNT_DOWN || mode == SdlUpdateMode.COUNT_UP) );
	}
	
	private void enableClockView(boolean enable){
		int visibility = (enable) ? View.VISIBLE : View.GONE;
		tv_mediaClock_clock.setVisibility(visibility);
		ll_mediaClock_clockContainer.setVisibility(visibility);
	}
	
	//dialog button listeners
	private final DialogInterface.OnClickListener okButtonListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			SdlUpdateMode mode = (SdlUpdateMode) spin_mediaClock_type.getSelectedItem();
			UpdateMode legacyMode = SdlUpdateMode.translateToLegacy(mode);
			SetMediaClockTimer result = new SetMediaClockTimer();
			result.setUpdateMode(legacyMode);
			
			if(mode == SdlUpdateMode.COUNT_UP || mode == SdlUpdateMode.COUNT_DOWN){
				String hours = et_mediaClock_hrs.getText().toString();
				String mins = et_mediaClock_mins.getText().toString();
				String secs = et_mediaClock_secs.getText().toString();

				// if user left a field blank, let's assume it's a 0
				hours = (hours.length() > 0) ? hours : "0";
				mins = (mins.length() > 0) ? mins : "0";
				secs = (secs.length() > 0) ? secs : "0";
				
				StartTime startTime = new StartTime();
				startTime.setHours(Integer.parseInt(hours));
				startTime.setMinutes(Integer.parseInt(mins));
				startTime.setSeconds(Integer.parseInt(secs));
				
				result.setStartTime(startTime);
			}
			
			notifyListener(result);
		}
	};

}
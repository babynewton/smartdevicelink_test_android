package com.livio.sdltester.dialogs;

import java.util.Vector;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.livio.sdl.dialogs.BaseOkCancelDialog;
import com.livio.sdl.enums.SdlCommand;
import com.livio.sdltester.R;
import com.smartdevicelink.proxy.rpc.Slider;

public class SliderDialog extends BaseOkCancelDialog {

	private static final SdlCommand SYNC_COMMAND = SdlCommand.SLIDER;
	private static final String DIALOG_TITLE = SYNC_COMMAND.toString();
	
	private static final int NUM_OF_TICKS_MIN = 2;
	private static final int START_POSITION_MIN = 1;
	private static final int TIMEOUT_MIN = 1;
	
	private static final int NUM_OF_TICKS_DEFAULT = 2;
	private static final int START_POSITION_DEFAULT = 1;
	private static final int TIMEOUT_DEFAULT = 1;
	
	private static final int S_TO_MS_MULTIPLIER = 1000;
	
	private EditText et_slider_title, et_slider_footer;
	private SeekBar seek_slider_numOfTicks, seek_slider_startPosition, seek_slider_timeout;
	private TextView tv_slider_numOfTicks, tv_slider_startPosition, tv_slider_timeout;
	
	private String numOfTicks, startPosition, timeout;
	
	public SliderDialog(Context context) {
		super(context, DIALOG_TITLE, R.layout.slider);
		setPositiveButton(okButtonListener);
		createDialog();
	}

	@Override
	protected void findViews(View parent) {
		Resources res = context.getResources();
		numOfTicks = res.getString(R.string.slider_ticks);
		startPosition= res.getString(R.string.slider_start_position);
		timeout = res.getString(R.string.slider_timeout);
		
		et_slider_title = (EditText) parent.findViewById(R.id.et_slider_title);
		et_slider_footer = (EditText) parent.findViewById(R.id.et_slider_footer);
		tv_slider_numOfTicks = (TextView) parent.findViewById(R.id.tv_slider_numOfTicks);
		tv_slider_startPosition = (TextView) parent.findViewById(R.id.tv_slider_startPosition);
		tv_slider_timeout = (TextView) parent.findViewById(R.id.tv_slider_timeout);

		updateTicks(NUM_OF_TICKS_DEFAULT);
		updateStartPosition(START_POSITION_DEFAULT);
		updateTimeout(TIMEOUT_DEFAULT);

		seek_slider_numOfTicks = (SeekBar) parent.findViewById(R.id.seek_slider_numOfTicks);
		seek_slider_numOfTicks.setProgress(NUM_OF_TICKS_DEFAULT - NUM_OF_TICKS_MIN);
		seek_slider_numOfTicks.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override public void onStopTrackingTouch(SeekBar seekBar) {}
			@Override public void onStartTrackingTouch(SeekBar seekBar) {}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				int adjustedProgress = progress + NUM_OF_TICKS_MIN;
				updateTicks(adjustedProgress);
				updateStartPositionMax(adjustedProgress);
			}
		});
		
		seek_slider_startPosition = (SeekBar) parent.findViewById(R.id.seek_slider_startPosition);
		seek_slider_startPosition.setProgress(START_POSITION_DEFAULT - START_POSITION_MIN);
		seek_slider_startPosition.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override public void onStopTrackingTouch(SeekBar seekBar) {}
			@Override public void onStartTrackingTouch(SeekBar seekBar) {}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				updateStartPosition(progress + START_POSITION_MIN);
			}
		});
		
		seek_slider_timeout = (SeekBar) parent.findViewById(R.id.seek_slider_timeout);
		seek_slider_timeout.setProgress(TIMEOUT_DEFAULT - TIMEOUT_MIN);
		seek_slider_timeout.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override public void onStopTrackingTouch(SeekBar seekBar) {}
			@Override public void onStartTrackingTouch(SeekBar seekBar) {}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				updateTimeout(progress + TIMEOUT_MIN);
			}
		});
		
		updateStartPositionMax(NUM_OF_TICKS_DEFAULT);
	}
	
	private void updateTicks(int ticks){
		tv_slider_numOfTicks.setText(new StringBuilder().append(numOfTicks).append(ticks).toString());
	}
	
	private void updateStartPosition(int start){
		tv_slider_startPosition.setText(new StringBuilder().append(startPosition).append(start).toString());
	}
	
	private void updateStartPositionMax(int numOfTicks){
		int adjustedNumOfTicks = numOfTicks - 1;
		seek_slider_startPosition.setMax(adjustedNumOfTicks);
	}
	
	private void updateTimeout(int newTimeout){
		tv_slider_timeout.setText(new StringBuilder().append(timeout).append(newTimeout).append(" s").toString());
	}
	
	//dialog button listeners
	private final DialogInterface.OnClickListener okButtonListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			String sliderTitle = et_slider_title.getText().toString();
			String sliderFooter = et_slider_footer.getText().toString();

			int numOfTicks = seek_slider_numOfTicks.getProgress() + NUM_OF_TICKS_MIN;
			int startPosition = seek_slider_startPosition.getProgress() + START_POSITION_MIN;
			
			int timeout = seek_slider_timeout.getProgress() + TIMEOUT_MIN;
			timeout *= S_TO_MS_MULTIPLIER; // convert timeout from s to ms
			
			if(sliderTitle.length() <= 0){
				sliderTitle = " ";
			}
			if(sliderFooter.length() <= 0){
				sliderFooter = " ";
			}
			
			Vector<String> sliderFooterVector = new Vector<String>(1);
			sliderFooterVector.add(sliderFooter);
			
			Slider slider = new Slider();
			slider.setNumTicks(numOfTicks);
			slider.setPosition(startPosition);
			slider.setTimeout(timeout);
			slider.setSliderHeader(sliderTitle);
			slider.setSliderFooter(sliderFooterVector);
			
			notifyListener(slider);
		}
	};

}

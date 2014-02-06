package com.livio.sdltester.dialogs;

import java.util.EnumSet;

import android.content.Context;
import android.content.DialogInterface;
import android.text.InputFilter;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;

import com.livio.sdl.MinMaxInputFilter;
import com.livio.sdl.enums.SdlCommand;
import com.livio.sdl.enums.SdlTextAlignment;
import com.livio.sdltester.R;
import com.smartdevicelink.proxy.rpc.Show;

public class ShowDialog extends BaseOkCancelDialog{

	private static final SdlCommand SYNC_COMMAND = SdlCommand.SHOW;
	private static final String DIALOG_TITLE = SYNC_COMMAND.toString();

	private CheckBox check_show1, check_show2, check_show3, check_show4, check_statusBar, check_mediaTrack, check_mediaClock;
	private EditText et_show1, et_show2, et_show3, et_show4, et_statusBar, et_mediaTrack, et_mediaClockMins, et_mediaClockSecs;
	private Spinner spin_textAlignment;
	
	public ShowDialog(Context context){
		super(context, DIALOG_TITLE, R.layout.show);
		setPositiveButton(okButtonListener);
		createDialog();
	}
	
	@Override
	protected void findViews(View view){
		check_show1 = (CheckBox) view.findViewById(R.id.check_show1);
		check_show1.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				et_show1.setEnabled(isChecked);
			}
		});
		check_show2 = (CheckBox) view.findViewById(R.id.check_show2);
		check_show2.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				et_show2.setEnabled(isChecked);
			}
		});
		check_show3 = (CheckBox) view.findViewById(R.id.check_show3);
		check_show3.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				et_show3.setEnabled(isChecked);
			}
		});
		check_show4 = (CheckBox) view.findViewById(R.id.check_show4);
		check_show4.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				et_show4.setEnabled(isChecked);
			}
		});
		check_statusBar = (CheckBox) view.findViewById(R.id.check_statusBar);
		check_statusBar.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				et_statusBar.setEnabled(isChecked);
			}
		});
		check_mediaTrack = (CheckBox) view.findViewById(R.id.check_mediaTrack);
		check_mediaTrack.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				et_mediaTrack.setEnabled(isChecked);
			}
		});
		check_mediaClock = (CheckBox) view.findViewById(R.id.check_mediaClock);
		check_mediaClock.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				et_mediaClockMins.setEnabled(isChecked);
				et_mediaClockSecs.setEnabled(isChecked);
			}
		});
		
		et_show1 = (EditText) view.findViewById(R.id.et_show1);
		et_show1.setEnabled(check_show1.isChecked());
		et_show2 = (EditText) view.findViewById(R.id.et_show2);
		et_show2.setEnabled(check_show2.isChecked());
		et_show3 = (EditText) view.findViewById(R.id.et_show3);
		et_show3.setEnabled(check_show3.isChecked());
		et_show4 = (EditText) view.findViewById(R.id.et_show4);
		et_show4.setEnabled(check_show4.isChecked());
		et_statusBar = (EditText) view.findViewById(R.id.et_statusBar);
		et_statusBar.setEnabled(check_statusBar.isChecked());
		et_mediaTrack = (EditText) view.findViewById(R.id.et_mediaTrack);
		et_mediaTrack.setEnabled(check_mediaTrack.isChecked());
		
		// media clock timers
		et_mediaClockMins = (EditText) view.findViewById(R.id.et_mediaClockMins);
		et_mediaClockMins.setEnabled(check_mediaClock.isChecked());
		et_mediaClockMins.setFilters(new InputFilter[]{new MinMaxInputFilter(0, 59)}); // text input filter - only allow 0-59 minutes
		et_mediaClockSecs = (EditText) view.findViewById(R.id.et_mediaClockSecs);
		et_mediaClockSecs.setEnabled(check_mediaClock.isChecked());
		et_mediaClockSecs.setFilters(new InputFilter[]{new MinMaxInputFilter(0, 59)}); // text input filter - only allow 0-59 seconds
		
		spin_textAlignment = (Spinner) view.findViewById(R.id.spin_textAlignment);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.select_dialog_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		adapter.add(context.getResources().getString(R.string.no_selection));
		for (SdlTextAlignment anEnum : EnumSet.allOf(SdlTextAlignment.class)) {
            adapter.add(anEnum.toString());
        }
		spin_textAlignment.setAdapter(adapter);
	}
	
	//dialog button listeners
	private final DialogInterface.OnClickListener okButtonListener = new DialogInterface.OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			Show show = new Show();
			
			if(et_show1.isEnabled()){
				String show1 = et_show1.getText().toString();
				show.setMainField1(show1);
			}
			
			if(et_show2.isEnabled()){
				String show2 = et_show2.getText().toString();
				show.setMainField2(show2);
			}
			
			if(et_show3.isEnabled()){
				String show3 = et_show3.getText().toString();
				show.setMainField3(show3);
			}
			
			if(et_show4.isEnabled()){
				String show4 = et_show4.getText().toString();
				show.setMainField4(show4);
			}
			
			if(et_statusBar.isEnabled()){
				String statusBar = et_statusBar.getText().toString();
				show.setStatusBar(statusBar);
			}
			
			if(et_mediaTrack.isEnabled()){
				String mediaTrack = et_mediaTrack.getText().toString();
				show.setMediaTrack(mediaTrack);
			}
			
			if(et_mediaClockMins.isEnabled() && et_mediaClockSecs.isEnabled()){
				StringBuilder builder = new StringBuilder();
				builder.append(et_mediaClockMins.getText().toString())
				       .append(":")
				       .append(et_mediaClockSecs.getText().toString());
				String mediaClock = builder.toString();
				show.setMediaClock(mediaClock);
			}
			
			if(spin_textAlignment.getSelectedItemPosition() != 0){
				String textAlignmentReadableName = spin_textAlignment.getSelectedItem().toString();
				show.setAlignment(SdlTextAlignment.lookupLegacyByReadableName(textAlignmentReadableName));
			}

			notifyListener(show);
		}
	};
	
}

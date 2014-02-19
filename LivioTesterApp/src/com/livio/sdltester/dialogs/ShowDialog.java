package com.livio.sdltester.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;

import com.livio.sdl.SdlRequestFactory;
import com.livio.sdl.dialogs.BaseOkCancelDialog;
import com.livio.sdl.enums.SdlCommand;
import com.livio.sdl.enums.SdlTextAlignment;
import com.livio.sdl.utils.AndroidUtils;
import com.livio.sdltester.R;
import com.smartdevicelink.proxy.RPCRequest;
import com.smartdevicelink.proxy.rpc.enums.TextAlignment;

public class ShowDialog extends BaseOkCancelDialog{

	private static final SdlCommand SYNC_COMMAND = SdlCommand.SHOW;
	private static final String DIALOG_TITLE = SYNC_COMMAND.toString();

	private CheckBox check_show1, check_show2, check_show3, check_show4, check_statusBar;
	private EditText et_show1, et_show2, et_show3, et_show4, et_statusBar;
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
		
		spin_textAlignment = (Spinner) view.findViewById(R.id.spin_textAlignment);
		spin_textAlignment.setAdapter(AndroidUtils.createSpinnerAdapter(context, SdlTextAlignment.values()));
	}
	
	//dialog button listeners
	private final DialogInterface.OnClickListener okButtonListener = new DialogInterface.OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			String line1 = null, line2 = null, line3 = null, line4 = null, statusBar = null;
			TextAlignment alignment = null;
			
			if(et_show1.isEnabled()){
				line1 = et_show1.getText().toString();
				if(line1.length() <= 0){
					line1 = " ";
				}
			}
			
			if(et_show2.isEnabled()){
				line2 = et_show2.getText().toString();
				if(line2.length() <= 0){
					line2 = " ";
				}
			}
			
			if(et_show3.isEnabled()){
				line3 = et_show3.getText().toString();
				if(line3.length() <= 0){
					line3 = " ";
				}
			}
			
			if(et_show4.isEnabled()){
				line4 = et_show4.getText().toString();
				if(line4.length() <= 0){
					line4 = " ";
				}
			}
			
			if(et_statusBar.isEnabled()){
				statusBar = et_statusBar.getText().toString();
				if(statusBar.length() <= 0){
					statusBar = " ";
				}
			}
			
			if(spin_textAlignment.getSelectedItemPosition() != 0){
				SdlTextAlignment sdlAlignment = (SdlTextAlignment) spin_textAlignment.getSelectedItem();
				alignment = SdlTextAlignment.translateToLegacy(sdlAlignment);
			}

			RPCRequest result = SdlRequestFactory.show(line1, line2, line3, line4, statusBar, alignment);
			notifyListener(result);
		}
	};
	
}

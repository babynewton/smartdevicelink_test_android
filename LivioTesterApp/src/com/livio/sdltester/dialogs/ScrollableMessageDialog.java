package com.livio.sdltester.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.livio.sdl.dialogs.BaseOkCancelDialog;
import com.livio.sdl.enums.SdlCommand;
import com.livio.sdltester.R;
import com.smartdevicelink.proxy.rpc.ScrollableMessage;

public class ScrollableMessageDialog extends BaseOkCancelDialog {

	// TODO - implement soft buttons.
	
	private static final SdlCommand SYNC_COMMAND = SdlCommand.SCROLLABLE_MESSAGE;
	private static final String DIALOG_TITLE = SYNC_COMMAND.toString();
	
	private static final int TIMEOUT_DEFAULT = 30;
	private static final int TIMEOUT_MIN = 1;
	private static final int S_TO_MS_MULTIPLIER = 1000;
	
	private EditText et_scrollableMessage_text;
	private TextView tv_timeout;
	private SeekBar seek_timeout;
	private String timeoutBaseStr;
	
	public ScrollableMessageDialog(Context context) {
		super(context, DIALOG_TITLE, R.layout.scrollable_message);
		setPositiveButton(okButtonListener);
		createDialog();
	}

	@Override
	protected void findViews(View parent) {
		timeoutBaseStr = context.getResources().getString(R.string.timeout);
		
		et_scrollableMessage_text = (EditText) parent.findViewById(R.id.et_scrollableMessage_text);
		
		Button clearButton = (Button) parent.findViewById(R.id.but_scrollableMessage_clear);
		clearButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				et_scrollableMessage_text.setText("");
			}
		});
		
		tv_timeout = (TextView) parent.findViewById(R.id.tv_scrollableMessage_timeout);
		updateTimeoutText(TIMEOUT_DEFAULT);
		
		seek_timeout = (SeekBar) parent.findViewById(R.id.seek_scrollableMessage_timeout);
		seek_timeout.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override public void onStopTrackingTouch(SeekBar seekBar) {}
			@Override public void onStartTrackingTouch(SeekBar seekBar) {}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				updateTimeoutText(progress + TIMEOUT_MIN);
			}
		});
		seek_timeout.setProgress(TIMEOUT_DEFAULT - TIMEOUT_MIN);
	}
	
	private void updateTimeoutText(int timeout){
		tv_timeout.setText(new StringBuilder().append(timeoutBaseStr).append(timeout).append(" s").toString());
	}
	
	//dialog button listeners
	private final DialogInterface.OnClickListener okButtonListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			String message = et_scrollableMessage_text.getText().toString();
			int timeout = seek_timeout.getProgress() + TIMEOUT_MIN;
			timeout *= S_TO_MS_MULTIPLIER; // convert s to ms
			
			if(message.length() <= 0){
				message = " ";
			}
			
			ScrollableMessage scrollableMessage = new ScrollableMessage();
			scrollableMessage.setScrollableMessageBody(message);
			scrollableMessage.setTimeout(timeout);
			
			notifyListener(scrollableMessage);
		}
	};

}

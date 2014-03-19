package com.livio.sdl.dialogs;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.livio.sdl.R;
import com.livio.sdl.SdlLogMessage;
import com.livio.sdl.utils.SdlUtils;

public class JsonFlipperDialog extends BaseAlertDialog {

	// TODO comments
	
	private List<SdlLogMessage> jsonMessages;
	private int currentPosition;
	
	private TextView text;
	private ImageButton leftButton, rightButton;
	
	public JsonFlipperDialog(Context context, List<SdlLogMessage> jsonMessages, int startPosition) {
		super(context, SdlUtils.makeJsonTitle(jsonMessages.get(startPosition).getCorrelationId()), R.layout.json_flipper_dialog);
		this.jsonMessages = jsonMessages;
		this.currentPosition = startPosition;
		createDialog();
		refresh();
	}

	@Override
	protected void findViews(View parent) {
		text = (TextView) parent.findViewById(R.id.textview);
		
		leftButton = (ImageButton) parent.findViewById(R.id.ib_moveLeft);
		leftButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(currentPosition > 0){
					currentPosition--;
					refresh();
				}
			}
		});
		
		rightButton = (ImageButton) parent.findViewById(R.id.ib_moveRight);
		rightButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(currentPosition < (jsonMessages.size()-1) ){
					currentPosition++;
					refresh();
				}
			}
		});
	}
	
	private void refresh(){
		refreshButtons();
		refreshText();
	}
	
	private void refreshButtons(){
		boolean atStart = (currentPosition == 0);
		boolean atEnd = (currentPosition == jsonMessages.size()-1);
		
		leftButton.setEnabled(!atStart);
		rightButton.setEnabled(!atEnd);
	}
	
	private void refreshText(){
		SdlLogMessage currentMessage = jsonMessages.get(currentPosition);
		dialog.setTitle(SdlUtils.makeJsonTitle(currentMessage.getCorrelationId()));
		text.setText(currentMessage.getJsonData());
	}

}

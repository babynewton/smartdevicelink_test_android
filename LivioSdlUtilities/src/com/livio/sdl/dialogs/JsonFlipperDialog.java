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

/**
 * This dialog shows a single JSON message, but allows the ability to flip back and forth between
 * all available messages via the arrow buttons at the bottom of the dialog.  This dialog will not
 * be updated when new messages are sent with this dialog open.  The dialog must be closed and re-opened
 * in order to refresh with new values.
 *
 * @author Mike Burke
 *
 */
public class JsonFlipperDialog extends BaseAlertDialog {
	
	private List<SdlLogMessage> jsonMessages;
	private int currentPosition;
	
	private TextView text;
	private ImageButton leftButton, rightButton;
	
	public JsonFlipperDialog(Context context, List<SdlLogMessage> jsonMessages, int startPosition) {
		super(context, SdlUtils.makeJsonTitle(jsonMessages.get(startPosition).getCorrelationId()), R.layout.json_flipper_dialog);
		this.jsonMessages = jsonMessages;
		this.currentPosition = startPosition;
		createDialog();
		
		// since refresh updates the dialog's title, this must be after createDialog() so the dialog isn't null
		refresh();
	}

	@Override
	protected void findViews(View parent) {
		text = (TextView) parent.findViewById(R.id.textview);
		
		// set up left button
		leftButton = (ImageButton) parent.findViewById(R.id.ib_moveLeft);
		leftButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// if we can move left, do it.  if not, do nothing
				if(currentPosition > 0){
					currentPosition--;
					refresh();
				}
			}
		});
		
		// set up right button
		rightButton = (ImageButton) parent.findViewById(R.id.ib_moveRight);
		rightButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// if we can move right, do it.  if not, do nothing
				if(currentPosition < (jsonMessages.size()-1) ){
					currentPosition++;
					refresh();
				}
			}
		});
	}
	
	// refresh the buttons & the text for this dialog
	private void refresh(){
		refreshButtons();
		refreshText();
	}
	
	// refreshes the buttons with new position.  disables the buttons when we're at the edges of the list.
	private void refreshButtons(){
		boolean atStart = (currentPosition == 0);
		boolean atEnd = (currentPosition == jsonMessages.size()-1);
		
		leftButton.setEnabled(!atStart);
		rightButton.setEnabled(!atEnd);
	}
	
	// refreshes the text of the dialog - both the title and the main text.
	private void refreshText(){
		SdlLogMessage currentMessage = jsonMessages.get(currentPosition);
		dialog.setTitle(SdlUtils.makeJsonTitle(currentMessage.getCorrelationId()));
		text.setText(currentMessage.getJsonData());
	}

}

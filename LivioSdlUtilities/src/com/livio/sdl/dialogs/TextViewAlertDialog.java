package com.livio.sdl.dialogs;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.livio.sdl.R;

/**
 * A simple dialog that contains a single, scrollable TextView.  The textview
 * is initialized with a the title and message in the constructor.
 *
 * @author Mike Burke
 *
 */
public class TextViewAlertDialog extends BaseAlertDialog {

	protected TextView tv;
	
	public TextViewAlertDialog(Context context, String dialogTitle, String message){
		super(context, dialogTitle, R.layout.textview);
		tv.setText(message);
		createDialog();
	}

	@Override
	protected void findViews(View parent) {
		tv = (TextView) parent.findViewById(R.id.textview);
	}

}

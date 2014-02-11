package com.livio.sdl.dialogs;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.livio.sdl.R;

public class BaseTextViewDialog extends BaseAlertDialog {

	protected TextView tv;
	
	public BaseTextViewDialog(Context context, String dialogTitle, String message){
		super(context, dialogTitle, R.layout.textview);
		tv.setText(message);
		createDialog();
	}

	@Override
	protected void findViews(View parent) {
		tv = (TextView) parent.findViewById(R.id.textview);
	}

}

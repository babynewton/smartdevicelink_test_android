package com.livio.sdl.dialogs;

import android.app.ProgressDialog;
import android.content.Context;

public class IndeterminateProgressDialog extends ProgressDialog{

	public IndeterminateProgressDialog(Context context, String title) {
		super(context);
		setCancelable(false);
		setMessage(title);
		setIndeterminate(true);
		setTitle(null);
	}

}

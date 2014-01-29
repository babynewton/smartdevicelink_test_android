package com.livio.sdltester.dialogs;

import android.app.ProgressDialog;
import android.content.Context;

public class ConnectingDialog extends ProgressDialog{

	public ConnectingDialog(Context context) {
		super(context);
		setCancelable(false);
		setMessage("Connecting");
		setIndeterminate(true);
		setTitle(null);
	}

}

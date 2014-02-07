package com.livio.sdltester.dialogs;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.livio.sdl.SdlLogMessage;
import com.livio.sdl.dialogs.BaseAlertDialog;
import com.livio.sdl.utils.SdlUtils;
import com.livio.sdltester.R;

public class JsonDialog extends BaseAlertDialog {

	private TextView tv;
	
	public JsonDialog(Context context, SdlLogMessage logMessage) {
		super(context, SdlUtils.makeJsonTitle(logMessage.getCorrelationId()), R.layout.textview);
		tv.setText(logMessage.getJsonData());
		createDialog();
	}

	@Override
	protected void findViews(View parent) {
		tv = (TextView) parent.findViewById(R.id.textview);
	}

}

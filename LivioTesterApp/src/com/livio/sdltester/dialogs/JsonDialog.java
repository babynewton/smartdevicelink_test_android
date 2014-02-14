package com.livio.sdltester.dialogs;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;

import com.livio.sdl.SdlLogMessage;
import com.livio.sdl.dialogs.BaseTextViewDialog;
import com.livio.sdl.utils.SdlUtils;

public class JsonDialog extends BaseTextViewDialog {
	public JsonDialog(Context context, SdlLogMessage logMessage) {
		super(context, SdlUtils.makeJsonTitle(logMessage.getCorrelationId()), logMessage.getJsonData());
		
		// click listener to close dialog when the text is clicked
		tv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
	}

}

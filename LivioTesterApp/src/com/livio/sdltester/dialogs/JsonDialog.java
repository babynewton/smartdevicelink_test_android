package com.livio.sdltester.dialogs;

import android.content.Context;

import com.livio.sdl.SdlLogMessage;
import com.livio.sdl.dialogs.BaseTextViewDialog;
import com.livio.sdl.utils.SdlUtils;

public class JsonDialog extends BaseTextViewDialog {
	public JsonDialog(Context context, SdlLogMessage logMessage) {
		super(context, SdlUtils.makeJsonTitle(logMessage.getCorrelationId()), logMessage.getJsonData());
	}

}

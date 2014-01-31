package com.livio.sdl;

import android.graphics.Bitmap;

public class SdlFunctionButton extends SdlBaseButton {

	public SdlFunctionButton(String name, int id) {
		super(name, id, false);
	}

	public SdlFunctionButton(String name, int id, Bitmap image) {
		super(name, id, false, image);
	}

	public SdlFunctionButton(String name, int id, OnClickListener listener) {
		super(name, id, false, listener);
	}

	public SdlFunctionButton(String name, int id, Bitmap image, OnClickListener listener) {
		super(name, id, false, image, listener);
		// TODO Auto-generated constructor stub
	}

}

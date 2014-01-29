package com.livio.sdl;

import android.graphics.Bitmap;

public class SdlFunctionButton extends SdlBaseButton {

	public SdlFunctionButton(String name, int id, boolean isMenuButton) {
		super(name, id, false);
	}

	public SdlFunctionButton(String name, int id, boolean isMenuButton, Bitmap image) {
		super(name, id, false, image);
	}

	public SdlFunctionButton(String name, int id, boolean isMenuButton, OnClickListener listener) {
		super(name, id, false, listener);
	}

	public SdlFunctionButton(String name, int id, boolean isMenuButton, Bitmap image, OnClickListener listener) {
		super(name, id, false, image, listener);
		// TODO Auto-generated constructor stub
	}

}

package com.livio.sdl;

import android.graphics.Bitmap;

public class SdlMenuButton extends SdlBaseButton{

	public SdlMenuButton(String name, int id, boolean isMenuButton, Bitmap image, OnClickListener listener) {
		super(name, id, true, image, listener);
	}

	public SdlMenuButton(String name, int id, boolean isMenuButton, Bitmap image) {
		super(name, id, true, image);
	}

	public SdlMenuButton(String name, int id, boolean isMenuButton,
			OnClickListener listener) {
		super(name, id, true, listener);
	}

	public SdlMenuButton(String name, int id, boolean isMenuButton) {
		super(name, id, isMenuButton);
	}

}

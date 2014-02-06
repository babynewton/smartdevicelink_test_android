package com.livio.sdl;

import android.graphics.Bitmap;

public class SdlMenuButton extends SdlBaseButton{

	public SdlMenuButton(String name, int id, Bitmap image, OnClickListener listener) {
		super(name, id, true, image, listener);
	}

	public SdlMenuButton(String name, int id, Bitmap image) {
		super(name, id, true, image);
	}

	public SdlMenuButton(String name, int id, OnClickListener listener) {
		super(name, id, true, listener);
	}

	public SdlMenuButton(String name, int id) {
		super(name, id, true);
	}

}

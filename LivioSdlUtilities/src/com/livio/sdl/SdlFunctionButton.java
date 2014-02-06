package com.livio.sdl;

import android.graphics.Bitmap;

public class SdlFunctionButton extends SdlBaseButton {

	private int parentId;
	
	public SdlFunctionButton(String name, int id, int parent) {
		super(name, id, false);
		parentId = parent;
	}

	public SdlFunctionButton(String name, int id, int parent, Bitmap image) {
		super(name, id, false, image);
		parentId = parent;
	}

	public SdlFunctionButton(String name, int id, int parent, OnClickListener listener) {
		super(name, id, false, listener);
		parentId = parent;
	}

	public SdlFunctionButton(String name, int id, int parent, Bitmap image, OnClickListener listener) {
		super(name, id, false, image, listener);
		parentId = parent;
	}

	public int getParentId() {
		return parentId;
	}

}

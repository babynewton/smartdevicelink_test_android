package com.livio.sdl.dialogs;

import java.util.List;

import android.content.Context;
import android.widget.ListView;

import com.livio.sdl.R;
import com.livio.sdl.SdlImageItem;
import com.livio.sdl.adapters.SdlImageAdapter;

public abstract class BaseImageListDialog extends BaseAlertDialog {

	protected ListView listview;
	
	public BaseImageListDialog(Context context, String title, List<SdlImageItem> imagesNotAdded) {
		super(context, title, R.layout.listview);
		SdlImageAdapter adapter = new SdlImageAdapter(context, imagesNotAdded);
		listview.setAdapter(adapter);
	}
	
}

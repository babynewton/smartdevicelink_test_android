package com.livio.sdl.dialogs;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.livio.sdl.R;
import com.livio.sdl.SdlImageItem;
import com.livio.sdl.adapters.SdlImageAdapter;

public class ImageListDialog extends BaseImageListDialog {

	public ImageListDialog(Context context, List<SdlImageItem> imagesNotAdded) {
		super(context, "Select an Image", imagesNotAdded);
		createDialog();
	}
	
	@Override
	protected void findViews(View parent) {
		listview = (ListView) parent.findViewById(R.id.listView);
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				SdlImageItem selectedItem = ((SdlImageAdapter) parent.getAdapter()).getItem(position);
				notifyListener(selectedItem);
				dismiss();
			}
		});
	}

}

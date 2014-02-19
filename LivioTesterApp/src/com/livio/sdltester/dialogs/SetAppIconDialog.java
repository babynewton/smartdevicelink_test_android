package com.livio.sdltester.dialogs;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.livio.sdl.SdlImageItem;
import com.livio.sdl.SdlRequestFactory;
import com.livio.sdl.adapters.SdlImageAdapter;
import com.livio.sdl.dialogs.BaseImageListDialog;
import com.livio.sdl.enums.SdlCommand;
import com.livio.sdltester.R;
import com.smartdevicelink.proxy.RPCRequest;

public class SetAppIconDialog extends BaseImageListDialog {

	private static final SdlCommand SYNC_COMMAND = SdlCommand.SET_APP_ICON;
	private static final String DIALOG_TITLE = SYNC_COMMAND.toString();
	
	public SetAppIconDialog(Context context, List<SdlImageItem> imageList) {
		super(context, DIALOG_TITLE, imageList);
		createDialog();
	}
	
	@Override
	protected void findViews(View parent) {
		listview = (ListView) parent.findViewById(R.id.listView);
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				SdlImageItem selectedItem = ((SdlImageAdapter) parent.getAdapter()).getItem(position);
				
				RPCRequest result = SdlRequestFactory.setAppIcon(selectedItem.getImageName());
				notifyListener(result);

				dismiss();
			}
		});
	}

}

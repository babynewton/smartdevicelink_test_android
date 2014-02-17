package com.livio.sdltester.dialogs;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.livio.sdl.SdlImageItem;
import com.livio.sdl.adapters.SdlImageAdapter;
import com.livio.sdl.enums.SdlCommand;
import com.livio.sdltester.R;
import com.smartdevicelink.proxy.rpc.DeleteFile;

public class DeleteFileDialog extends BaseImageListDialog {

	private static final SdlCommand SYNC_COMMAND = SdlCommand.DELETE_FILE;
	private static final String DIALOG_TITLE = SYNC_COMMAND.toString();
	
	public DeleteFileDialog(Context context, List<SdlImageItem> imageList) {
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
				
				DeleteFile deleteFile = new DeleteFile();
				deleteFile.setSmartDeviceLinkFileName(selectedItem.getImageName());
				notifyListener(deleteFile);

				dismiss();
			}
		});
	}

}

package com.livio.sdltester.dialogs;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

public class DeleteFileDialog extends BaseImageListDialog {

	private static final SdlCommand SYNC_COMMAND = SdlCommand.DELETE_FILE;
	private static final String DIALOG_TITLE = SYNC_COMMAND.toString();
	
	private static final String DELETE_ALL = "Delete All";
	private List<SdlImageItem> imageList;
	
	public DeleteFileDialog(Context context, List<SdlImageItem> imageList) {
		super(context, DIALOG_TITLE, imageList);
		this.imageList = new ArrayList<SdlImageItem>(imageList);
		Bitmap deleteAllImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_close);
		((SdlImageAdapter)listview.getAdapter()).insert(new SdlImageItem(deleteAllImage, DELETE_ALL, null), 0);
		createDialog();
	}
	
	@Override
	protected void findViews(View parent) {
		listview = (ListView) parent.findViewById(R.id.listView);
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				List<RPCRequest> messages = new ArrayList<RPCRequest>(imageList.size());
				SdlImageItem selectedItem = ((SdlImageAdapter) parent.getAdapter()).getItem(position);
				String selectedName = selectedItem.getImageName();
				
				if(selectedName.equals(DELETE_ALL)){
					for(SdlImageItem item : imageList){
						RPCRequest request = SdlRequestFactory.deleteFile(item.getImageName());
						messages.add(request);
					}
				}
				else{
					RPCRequest request = SdlRequestFactory.deleteFile(selectedItem.getImageName());
					messages.add(request);
				}

				notifyListener(messages);
				dismiss();
			}
		});
	}

}

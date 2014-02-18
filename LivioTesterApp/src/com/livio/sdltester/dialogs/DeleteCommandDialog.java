package com.livio.sdltester.dialogs;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.livio.sdl.SdlRequestFactory;
import com.livio.sdl.dialogs.BaseAlertDialog;
import com.livio.sdl.enums.SdlCommand;
import com.livio.sdl.menu.MenuItem;
import com.livio.sdl.utils.AndroidUtils;
import com.livio.sdltester.R;
import com.smartdevicelink.proxy.RPCRequest;

public class DeleteCommandDialog extends BaseAlertDialog{

	private static final SdlCommand SYNC_COMMAND = SdlCommand.DELETE_COMMAND;
	private static final String DIALOG_TITLE = SYNC_COMMAND.toString();
	
	private ListView listView;
	
	public DeleteCommandDialog(Context context, List<MenuItem> commandList) {
		super(context, DIALOG_TITLE, R.layout.listview);
		listView.setAdapter(AndroidUtils.createListViewAdapter(context, commandList));
		setCancelable(true);
		createDialog();
	}

	@Override
	protected void findViews(View parent) {
		listView = (ListView) parent.findViewById(R.id.listView);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				@SuppressWarnings("unchecked")
				final MenuItem selectedButton = ((ArrayAdapter<MenuItem>) parent.getAdapter()).getItem(position);
				final int commandId = selectedButton.getId();
				
				RPCRequest result = SdlRequestFactory.deleteCommand(commandId);
				notifyListener(result);
				dismiss();
			}
		});
	}

}

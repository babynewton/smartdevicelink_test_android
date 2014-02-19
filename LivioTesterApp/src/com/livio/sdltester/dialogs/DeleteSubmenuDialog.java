package com.livio.sdltester.dialogs;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.livio.sdl.SdlRequestFactory;
import com.livio.sdl.dialogs.BaseSingleListViewDialog;
import com.livio.sdl.enums.SdlCommand;
import com.livio.sdl.menu.MenuItem;
import com.livio.sdltester.R;
import com.smartdevicelink.proxy.RPCRequest;

public class DeleteSubmenuDialog extends BaseSingleListViewDialog<MenuItem>{

	private static final SdlCommand SYNC_COMMAND = SdlCommand.DELETE_SUB_MENU;
	private static final String DIALOG_TITLE = SYNC_COMMAND.toString();
	
	public DeleteSubmenuDialog(Context context, List<MenuItem> submenuList) {
		super(context, DIALOG_TITLE, submenuList);
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
				final int menuId = selectedButton.getId();
				
				RPCRequest request = SdlRequestFactory.deleteSubmenu(menuId);
				notifyListener(request);
				
				// since this isn't an ok/cancel dialog, we must dismiss the dialog when an item is selected
				dismiss();
			}
		});
	}

}

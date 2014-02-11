package com.livio.sdltester.dialogs;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.livio.sdl.R;
import com.livio.sdl.dialogs.BaseSingleListViewDialog;
import com.livio.sdl.enums.SdlCommand;
import com.livio.sdl.menu.MenuItem;
import com.smartdevicelink.proxy.rpc.DeleteInteractionChoiceSet;

public class DeleteInteractionDialog extends BaseSingleListViewDialog<MenuItem> {

	private static final SdlCommand SYNC_COMMAND = SdlCommand.DELETE_INTERACTION_CHOICE_SET;
	private static final String DIALOG_TITLE = SYNC_COMMAND.toString();
	
	public DeleteInteractionDialog(Context context, List<MenuItem> items) {
		super(context, DIALOG_TITLE, items);
		createDialog();
	}

	@Override
	protected void findViews(View parent) {
		listView = (ListView) parent.findViewById(R.id.listView);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				selectedItem = ((ArrayAdapter<MenuItem>) parent.getAdapter()).getItem(position);
				
				DeleteInteractionChoiceSet deleteInteraction = new DeleteInteractionChoiceSet();
				deleteInteraction.setInteractionChoiceSetID(selectedItem.getId());
				
				notifyListener(deleteInteraction);
				dismiss();
			}
		});
	}
	
}

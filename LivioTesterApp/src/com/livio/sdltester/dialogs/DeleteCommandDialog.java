package com.livio.sdltester.dialogs;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.livio.sdl.SdlBaseButton;
import com.livio.sdl.enums.SdlCommand;
import com.livio.sdltester.R;
import com.smartdevicelink.proxy.rpc.DeleteCommand;

public class DeleteCommandDialog extends BaseAlertDialog{

	private static final SdlCommand SYNC_COMMAND = SdlCommand.DELETE_COMMAND;
	private static final String DIALOG_TITLE = SYNC_COMMAND.toString();
	
	private ListView listView;
	private List<SdlBaseButton> commandList;
	private ArrayAdapter<SdlBaseButton> adapter;
	
	public DeleteCommandDialog(Context context, List<SdlBaseButton> commandList) {
		super(context, DIALOG_TITLE, R.layout.listview);
		this.commandList = commandList;
		populateAdapter();
		setCancelable(true);
		createDialog();
	}
	
	private void populateAdapter(){
		for(SdlBaseButton button : commandList){
			adapter.add(button);
		}
		
		adapter.notifyDataSetChanged();
	}

	@Override
	protected void findViews(View parent) {
		listView = (ListView) parent.findViewById(R.id.listView);
		adapter = new ArrayAdapter<SdlBaseButton>(context, android.R.layout.simple_list_item_1);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				final SdlBaseButton selectedButton = commandList.get(position);
				final int commandId = selectedButton.getId();
				
				DeleteCommand deleteCommand = new DeleteCommand();
				deleteCommand.setCmdID(commandId);
				notifyListener(deleteCommand);
				dismiss();
			}
		});
	}

}

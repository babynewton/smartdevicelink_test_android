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
import com.smartdevicelink.proxy.rpc.DeleteSubMenu;

public class DeleteSubmenuDialog extends BaseAlertDialog{

	private static final SdlCommand SYNC_COMMAND = SdlCommand.DELETE_SUB_MENU;
	private static final String DIALOG_TITLE = SYNC_COMMAND.toString();
	
	private ListView listView;
	private List<SdlBaseButton> submenuList;
	private ArrayAdapter<SdlBaseButton> adapter;
	
	public DeleteSubmenuDialog(Context context, List<SdlBaseButton> submenuList) {
		super(context, DIALOG_TITLE, R.layout.listview);
		this.submenuList = submenuList;
		populateAdapter();
		setCancelable(true);
		createDialog();
	}
	
	private void populateAdapter(){
		for(SdlBaseButton button : submenuList){
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
				final SdlBaseButton selectedButton = submenuList.get(position);
				final int menuId = selectedButton.getId();
				
				DeleteSubMenu deleteCommand = new DeleteSubMenu();
				deleteCommand.setMenuID(menuId);
				notifyListener(deleteCommand);
				dismiss();
			}
		});
	}

}

package com.livio.sdltester.dialogs;

import java.util.ArrayList;
import java.util.Arrays;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.livio.sdl.dialogs.BaseOkCancelDialog;
import com.livio.sdl.enums.EnumComparator;
import com.livio.sdl.enums.SdlButton;
import com.livio.sdl.enums.SdlCommand;
import com.livio.sdltester.R;

public class ButtonSubscriptionDialog extends BaseOkCancelDialog{

	private static final SdlCommand SYNC_COMMAND = SdlCommand.SUBSCRIBE_BUTTON;
	private static final String DIALOG_TITLE = SYNC_COMMAND.toString();
	
	private ListView listView;
	private SdlButton[] listViewItems;
	private ArrayList<SdlButton> selectedItems = null;
	
	public ButtonSubscriptionDialog(Context context) {
		super(context, DIALOG_TITLE, R.layout.button_subscription);
		setPositiveButton(positiveButton);
		createDialog();
	}

	@Override
	protected void findViews(View parent) {
		if(listViewItems == null){
			listViewItems = createListViewItems();
		}
		
		listView = (ListView) parent.findViewById(R.id.listView);
		listView.setAdapter(new ArrayAdapter<SdlButton>(context, android.R.layout.simple_list_item_multiple_choice, listViewItems));
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				toggleItem(listViewItems[position]);
			}
		});
	}
	
	private void toggleItem(SdlButton button){
		//lazily instantiate the list of selected items when one is first selected
		if(selectedItems == null){
			selectedItems = new ArrayList<SdlButton>();
		}
		
		final boolean alreadyInList = selectedItems.contains(button);
		
		if(alreadyInList){
			selectedItems.remove(button);
		}
		else{
			selectedItems.add(button);
		}
	}
	
	//dialog button click listeners
	private final DialogInterface.OnClickListener positiveButton = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			if(selectedItems == null || selectedItems.size() == 0){
				notifyListener(null);
			}
			else{
				SdlButton[] contents = new SdlButton[selectedItems.size()];
				notifyListener(selectedItems.toArray(contents));
			}
		}
	};
	
	private static SdlButton[] createListViewItems(){
		SdlButton[] buttonValues = SdlButton.values();
		Arrays.sort(buttonValues, new EnumComparator<SdlButton>());
		return buttonValues;
	}

}

package com.livio.sdltester.dialogs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.livio.sdl.dialogs.BaseOkCancelDialog;
import com.livio.sdl.enums.EnumComparator;
import com.livio.sdl.enums.SdlButton;
import com.livio.sdl.enums.SdlCommand;
import com.livio.sdltester.R;
import com.smartdevicelink.proxy.RPCRequest;
import com.smartdevicelink.proxy.rpc.UnsubscribeButton;

public class ButtonUnsubscriptionDialog extends BaseOkCancelDialog {

	private static final SdlCommand SYNC_COMMAND = SdlCommand.UNSUBSCRIBE_BUTTON;
	private static final String DIALOG_TITLE = SYNC_COMMAND.toString();
	
	private ListView listView;
	private ArrayAdapter<SdlButton> listViewAdapter;
	private List<SdlButton> selectedItems = new ArrayList<SdlButton>();
	
	public ButtonUnsubscriptionDialog(Context context, List<SdlButton> buttonSubscriptions) {
		super(context, DIALOG_TITLE, R.layout.button_subscription);
		setPositiveButton(positiveButton);
		addubscribedButtons(buttonSubscriptions);
		createDialog();
	}
	
	private void addubscribedButtons(List<SdlButton> buttonSubscriptions){
		for(SdlButton button : buttonSubscriptions){
			listViewAdapter.add(button);
		}
		listViewAdapter.sort(new EnumComparator<SdlButton>());
		((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();
	}

	@Override
	protected void findViews(View parent) {
		listView = (ListView) parent.findViewById(R.id.listView);
		listViewAdapter = new ArrayAdapter<SdlButton>(context, android.R.layout.simple_list_item_multiple_choice);
		listView.setAdapter(listViewAdapter);
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				toggleItem(((ArrayAdapter<SdlButton>) parent.getAdapter()).getItem(position));
			}
		});
	}
	
	private void toggleItem(SdlButton button){
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
				notifyListener(Collections.emptyList());
			}
			else{
				List<RPCRequest> buttonUnsubscribeMessages = new ArrayList<RPCRequest>(selectedItems.size());
				for(SdlButton button : selectedItems){
					UnsubscribeButton unsubscribeButton = new UnsubscribeButton();
					unsubscribeButton.setButtonName(SdlButton.translateToLegacy(button));
					buttonUnsubscribeMessages.add(unsubscribeButton);
				}
				notifyListener(buttonUnsubscribeMessages);
			}
		}
	};

}

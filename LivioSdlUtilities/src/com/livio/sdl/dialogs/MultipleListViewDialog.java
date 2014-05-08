package com.livio.sdl.dialogs;

import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.ListAdapter;
/**
 * A generic class representing a ListView dialog that allows the user to select
 * multiple listview items.  Selected items are stored in a protected List<E> object
 * called selectedItems, which can be used by subclasses of this class.
 *
 * @author Mike Burke
 *
 * @param <E>
 */
public class MultipleListViewDialog<E> extends BaseMultipleListViewDialog<E> {

	public MultipleListViewDialog(Context context, String title, List<E> items) {
		super(context, title, items);
		setPositiveButton(positiveButton);
		createDialog();
	}

	public MultipleListViewDialog(Context context, String title, List<E> items, List<E> checkedItems) {
		super(context, title, items);
		setPositiveButton(positiveButton);
		if(checkedItems != null && checkedItems.size() > 0){
			selectedItems.addAll(checkedItems);
		}
		
		if(selectedItems != null && selectedItems.size() > 0){
			// TODO: make this more efficient, but should be good enough for small data sets
			ListAdapter adapter = listView.getAdapter();
			int adapterLen = adapter.getCount();
			int selectedLen = selectedItems.size();
			for(int i=0; i<adapterLen; i++){
				for(int j=0; j<selectedLen; j++){
					if(adapter.getItem(i).equals(selectedItems.get(j))){
						listView.setItemChecked(i, true);
					}
				}
			}
		}
		
		createDialog();
	}
	
	//dialog button listeners
	private final DialogInterface.OnClickListener positiveButton = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			notifyListener(selectedItems);
		}
	};

}

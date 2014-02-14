package com.livio.sdl.dialogs;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.livio.sdl.R;
import com.livio.sdl.utils.AndroidUtils;

/**
 * A generic, abstract class representing a ListView dialog that allows the user to select
 * multiple listview items.  Selected items are stored in a protected List<E> object
 * called selectedItems, which can be used by subclasses of this class.
 *
 * @author Mike Burke
 *
 * @param <E>
 */
public abstract class BaseMultipleListViewDialog<E> extends BaseOkCancelDialog {

	protected ListView listView;
	protected List<E> selectedItems = new ArrayList<E>();
	
	public BaseMultipleListViewDialog(Context context, String title, List<E> items) {
		super(context, title, R.layout.listview);
		listView.setAdapter(AndroidUtils.createMultipleListViewAdapter(context, items));
	}

	@Override
	protected void findViews(View parent) {
		listView = (ListView) parent.findViewById(R.id.listView);
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				toggleItem(((ArrayAdapter<E>) parent.getAdapter()).getItem(position));
			}
		});
	}
	
	/**
	 * Toggles the input item in the list of selected items.  So, if the current item
	 * was not selected, it is added to the list; if the current item was selected,
	 * it is removed from the list.
	 * 
	 * @param item
	 */
	protected void toggleItem(E item){
		final boolean alreadyInList = selectedItems.contains(item);
		
		if(alreadyInList){
			selectedItems.remove(item);
		}
		else{
			selectedItems.add(item);
		}
	}

}

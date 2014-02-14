package com.livio.sdl.dialogs;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.livio.sdl.R;

/**
 * A generic, abstract class representing a ListView dialog that allows the user to select
 * a single listview item.  The selected item is returned to the listener as a generic object.
 *
 * @author Mike Burke
 *
 * @param <E>
 */
public abstract class BaseSingleListViewDialog<E> extends BaseAlertDialog {

	// since this class is abstract, we'll keep these variables protected so subclasses can access them directly
	protected ListView listView;
	protected ArrayAdapter<E> adapter;
	protected E selectedItem;
	
	public BaseSingleListViewDialog(Context context, String title, List<E> items) {
		super(context, title, R.layout.listview);
		adapter = new ArrayAdapter<E>(context, android.R.layout.simple_list_item_1, items);
		listView.setAdapter(adapter);
	}

	@Override
	protected void findViews(View parent) {
		listView = (ListView) parent.findViewById(R.id.listView);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				selectedItem = ((ArrayAdapter<E>) parent.getAdapter()).getItem(position);
				notifyListener(selectedItem);
				dismiss();
			}
		});
	}

}

package com.livio.sdl.dialogs;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.livio.sdl.R;

public abstract class BaseSingleListViewDialog<E> extends BaseAlertDialog {

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
			}
		});
	}

}

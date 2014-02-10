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

public abstract class BaseMultipleListViewDialog<E> extends BaseOkCancelDialog {

	protected ListView listView;
	protected ArrayAdapter<E> adapter;
	protected List<E> selectedItems = new ArrayList<E>();
	
	public BaseMultipleListViewDialog(Context context, String title, List<E> items) {
		super(context, title, R.layout.listview);
		adapter = new ArrayAdapter<E>(context, android.R.layout.simple_list_item_multiple_choice, items);
		listView.setAdapter(adapter);
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

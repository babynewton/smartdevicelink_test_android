package com.livio.sdl.dialogs;

import java.util.List;

import android.content.Context;
import android.content.DialogInterface;

public class MultipleListViewDialog<E> extends BaseMultipleListViewDialog<E> {

	public MultipleListViewDialog(Context context, String title, List<E> items) {
		super(context, title, items);
		setPositiveButton(positiveButton);
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

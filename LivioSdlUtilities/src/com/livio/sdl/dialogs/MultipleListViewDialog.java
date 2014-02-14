package com.livio.sdl.dialogs;

import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
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
	
	//dialog button listeners
	private final DialogInterface.OnClickListener positiveButton = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			notifyListener(selectedItems);
		}
	};

}

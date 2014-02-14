package com.livio.sdl.dialogs;

import java.util.List;

import android.content.Context;
/**
 * A generic class representing a ListView dialog that allows the user to select
 * a single listview item.  The selected item is returned to the listener as a generic object.
 *
 * @author Mike Burke
 *
 * @param <E>
 */
public class ListViewDialog<E> extends BaseSingleListViewDialog<E> {

	public ListViewDialog(Context context, String title, List<E> items) {
		super(context, title, items);
		createDialog();
	}

}

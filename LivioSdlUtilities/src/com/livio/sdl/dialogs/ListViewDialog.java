package com.livio.sdl.dialogs;

import java.util.List;

import android.content.Context;

public class ListViewDialog<E> extends BaseSingleListViewDialog<E> {

	public ListViewDialog(Context context, String title, List<E> items) {
		super(context, title, items);
		createDialog();
	}

}

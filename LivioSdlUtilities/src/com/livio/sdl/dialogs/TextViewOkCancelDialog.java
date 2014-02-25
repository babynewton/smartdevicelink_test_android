package com.livio.sdl.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.TextView;

import com.livio.sdl.R;

/**
 * A simple dialog that contains a single, scrollable TextView.  The textview
 * is initialized with a the title and message in the constructor.
 *
 * @author Mike Burke
 *
 */
public class TextViewOkCancelDialog extends BaseOkCancelDialog {

	protected TextView tv;
	
	public TextViewOkCancelDialog(Context context, String dialogTitle, String message){
		super(context, dialogTitle, R.layout.textview);
		tv.setText(message);
		setPositiveButton(okListener);
		setNegativeButton(cancelListener);
		createDialog();
	}

	@Override
	protected void findViews(View parent) {
		tv = (TextView) parent.findViewById(R.id.textview);
	}
	
	private DialogInterface.OnClickListener okListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			notifyListener(true);
		}
	};
	
	private DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			notifyListener(false);
		}
	};

}

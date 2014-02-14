package com.livio.sdl.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.livio.sdl.R;

/**
 * An abstract class that represents a dialog with an OK button and a cancel button.  Subclasses
 * can extend this class to create different types of OK/Cancel dialogs.  Subclasses
 * must set positive / negative buttons in their constructor and call createDialog() as the last
 * line of the constructor.
 *
 * @author Mike Burke
 *
 */
public abstract class BaseOkCancelDialog extends BaseAlertDialog {

	protected DialogInterface.OnClickListener okButton, cancelButton;
	
	public BaseOkCancelDialog(Context context, String title, int resource) {
		super(context, title, resource);
	}

	@Override
	protected void createDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title)
		       .setView(view)
		       .setPositiveButton(context.getResources().getString(R.string.positive_button), okButton)
		       .setNegativeButton(context.getResources().getString(R.string.negative_button), cancelButton)
		       .setCancelable(cancelable);
		dialog = builder.create();
	}
	
	/**
	 * Sets the positive button click listener for the dialog.
	 * 
	 * @param okButton The button click listener for the positive button
	 */
	protected void setPositiveButton(DialogInterface.OnClickListener okButton){
		this.okButton = okButton;
		if(dialog != null){
			dialog.setButton(DialogInterface.BUTTON_POSITIVE, context.getResources().getString(R.string.positive_button), okButton);
		}
	}
	
	/**
	 * Sets the negative button click listener for the dialog.
	 * 
	 * @param cancelButton The button click listener for the negative button
	 */
	protected void setNegativeButton(DialogInterface.OnClickListener cancelButton){
		this.cancelButton = cancelButton;
		if(dialog != null){
			dialog.setButton(DialogInterface.BUTTON_NEGATIVE, context.getResources().getString(R.string.negative_button), cancelButton);
		}
	}

}

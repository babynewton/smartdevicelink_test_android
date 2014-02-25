package com.livio.sdl.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

/**
 * An abstract base class for custom alert dialogs.  The class is basically
 * just a wrapper using Android's AlertDialog.Builder class to create
 * relatively simple custom dialogs.
 *
 * @author Mike Burke
 *
 */
public abstract class BaseAlertDialog {
	
	/**
	 * Allows another class to listen for the result of the subclass dialog.  Results
	 * are returned as an object.
	 *
	 * @author Mike Burke
	 *
	 */
	public interface Listener{
		/**
		 * Should be called when data from the dialog is ready to return.
		 * 
		 * @param resultData The data results for the dialog
		 */
		void onResult(Object resultData);
	}

	/**
	 * Called after views have been inflated, allowing the subclass an opportunity
	 * to find their custom views using view.findViewById(id).
	 * 
	 * @param parent The parent view which was inflated from input resource id.
	 */
	protected abstract void findViews(View parent);
	
	// since this is an abstract class, we'll set all variables to protected scope in case subclasses want to use them directly
	protected Context context;
	protected AlertDialog dialog;
	protected Listener listener;
	protected String title;
	protected View view;
	protected boolean cancelable = true;
	
	/**
	 * Creates a BaseDialog object with context, title and resource id.
	 * 
	 * @param context The context of the dialog
	 * @param title The title of the dialog
	 * @param resource The resource id used for inflating the main view of the dialog
	 */
	public BaseAlertDialog(Context context, String title, int resource) {
		this.context = context;
		this.title = title;
		
		// inflate the view and allow the subclass to locate its views
		inflateView(resource);
		findViews(view);
	}
	
	/**
	 * Creates the dialog object using the input data.
	 */
	protected void createDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title)
		       .setView(view)
		       .setCancelable(cancelable);
		dialog = builder.create();
	}
	
	/**
	 * Creates the main dialog view from the input resource id.
	 * 
	 * @param resourceId The id of the XML view to inflate
	 */
	protected void inflateView(int resourceId){
		//grab the system inflater to build views from XML for us
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(resourceId, null);
	}
	
	//public methods
	/**
	 * Creates the dialog object if necessary and shows it.
	 */
	public void show(){
		if(dialog == null){
			createDialog();
		}
		
		if(!dialog.isShowing()){
			dialog.show();
		}
	}
	
	/**
	 * If the dialog exists and is currently showing, dismisses it.
	 */
	public void dismiss(){
		if(dialog != null && dialog.isShowing()){
			dialog.dismiss();
		}
	}
	
	/**
	 * Returns whether the dialog is currently showing or not.
	 * 
	 * @return True if the dialog is showing, false otherwise
	 */
	public boolean isShowing(){
		if(dialog == null){
			return false;
		}
		
		return dialog.isShowing();
	}
	
	/**
	 * Sets a listener for any subclasses of BaseDialog.
	 * 
	 * @param l The listener to set
	 */
	public void setListener(Listener l){
		listener = l;
	}
	
	/**
	 * Sets whether or not the dialog is allowed to be cancelled.
	 * 
	 * @param cancelable True if this dialog is cancelable, false if not
	 */
	public void setCancelable(boolean cancelable){
		this.cancelable = cancelable;
		if(dialog != null){
			dialog.setCancelable(cancelable);
		}
	}
	
	/**
	 * Notifies an existing dialog listener that results are ready.
	 * 
	 * @param resultData Object containing dialog results
	 */
	public void notifyListener(Object resultData){
		if(listener != null){
			listener.onResult(resultData);
		}
	}
	
}

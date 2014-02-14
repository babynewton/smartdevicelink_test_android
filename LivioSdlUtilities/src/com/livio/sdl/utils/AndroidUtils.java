package com.livio.sdl.utils;

import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.ArrayAdapter;

/**
 * Contains static methods that will help with typical Android tasks.  For example,
 * there are methods to determine internet connectivity, creating adapters for spinners
 * and lists, etc.
 *
 * @author Mike Burke
 *
 */
public abstract class AndroidUtils {

	private AndroidUtils(){} // don't allow instantiation of static classes
	
	/**
	 * Determines if the network is currently available or not.
	 * 
	 * @param service The service with which to access the system connectivity service
	 * @return True if the network is available, false if not
	 */
	public static boolean isNetworkAvailable(Service service){
		ConnectivityManager cm = (ConnectivityManager) service.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo network = cm.getActiveNetworkInfo();
		return ( (network != null) && (network.isConnected()) );
	}

	/**
	 * Determines if the network is currently available or not.
	 * 
	 * @param activity The activity with which to access the system connectivity service
	 * @return True if the network is available, false if not
	 */
	public static boolean isNetworkAvailable(Activity activity){
		ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo network = cm.getActiveNetworkInfo();
		return ( (network != null) && (network.isConnected()) );
	}
	
	/**
	 * Creates a standard Android spinner adapter.  Input items can be of any type.
	 * 
	 * @param context Context with which to create the adapter
	 * @param items List of items to populate the adapter with
	 * @return The created adapter
	 */
	public static <E> ArrayAdapter<E> createSpinnerAdapter(Context context, List<E> items){
		ArrayAdapter<E> adapter = createAdapter(context, android.R.layout.select_dialog_item, items);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		return adapter;
	}
	
	/**
	 * Creates a standard Android spinner adapter.  Input items can be of any type.
	 * 
	 * @param context Context with which to create the adapter
	 * @param items Array of items to populate the adapter with
	 * @return The created adapter
	 */
	public static <E> ArrayAdapter<E> createSpinnerAdapter(Context context, E[] items){
		return createSpinnerAdapter(context, Arrays.asList(items));
	}

	/**
	 * Creates a standard Android ListView adapter.  Input items can be of any type.
	 * 
	 * @param context Context with which to create the adapter
	 * @param items List of items to populate the adapter with
	 * @return The created adapter
	 */
	public static <E> ArrayAdapter<E> createListViewAdapter(Context context, List<E> items){
		return createAdapter(context, android.R.layout.simple_list_item_1, items);
	}

	/**
	 * Creates a standard Android ListView adapter.  Input items can be of any type.
	 * 
	 * @param context Context with which to create the adapter
	 * @param items Array of items to populate the adapter with
	 * @return The created adapter
	 */
	public static <E> ArrayAdapter<E> createListViewAdapter(Context context, E[] items){
		return createListViewAdapter(context, Arrays.asList(items));
	}

	/**
	 * Creates a standard Android ListView multiple-choice adapter.  Input items can be of any type.
	 * 
	 * @param context Context with which to create the adapter
	 * @param items List of items to populate the adapter with
	 * @return The created adapter
	 */
	public static <E> ArrayAdapter<E> createMultipleListViewAdapter(Context context, List<E> items){
		return createAdapter(context, android.R.layout.simple_list_item_multiple_choice, items);
	}

	/**
	 * Creates a standard Android ListView multiple-choice adapter.  Input items can be of any type.
	 * 
	 * @param context Context with which to create the adapter
	 * @param items Array of items to populate the adapter with
	 * @return The created adapter
	 */
	public static <E> ArrayAdapter<E> createMultipleListViewAdapter(Context context, E[] items){
		return createMultipleListViewAdapter(context, Arrays.asList(items));
	}

	/**
	 * Creates a standard Android adapter.  Input items can be of any type.
	 * 
	 * @param context Context with which to create the adapter
	 * @param layoutId Android resource id to be used for a list row
	 * @param items List of items to populate the adapter with
	 * @return The created adapter
	 */
	public static <E> ArrayAdapter<E> createAdapter(Context context, int layoutId, List<E> items){
		return new ArrayAdapter<E>(context, layoutId, items);
	}
	
}

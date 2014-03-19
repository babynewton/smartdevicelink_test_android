package com.livio.sdl.utils;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.widget.ArrayAdapter;

/**
 * Contains static methods that will help with typical Android tasks.  For example,
 * there are methods to determine internet connectivity, creating adapters for spinners
 * and lists, etc.
 *
 * @author Mike Burke
 *
 */
public final class AndroidUtils {

	private AndroidUtils(){} // don't allow instantiation of static classes
	
	/**
	 * Determines if the network is currently available or not.
	 * 
	 * @param context The context with which to access the system connectivity service
	 * @return True if the network is available, false if not
	 */
	public static boolean isNetworkAvailable(ContextWrapper context){
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
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
	
	/**
	 * Converts and Android bitmap file to an array of raw bytes that are ready to be sent over bluetooth,
	 * wifi, usb, etc.
	 * 
	 * @param bitmap The bitmap to translate
	 * @param format The format of the bitmap
	 * @return The raw byte representation of the bitmap
	 */
	public static byte[] bitmapToRawBytes(Bitmap bitmap, CompressFormat format){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(format, 100, baos);
		byte[] result = baos.toByteArray();
		return result;
	}
	
	/**
	 * Enables or disables wifi.  Requires CHANGE_WIFI_STATE permission.
	 * 
	 * @param context A context with which to access wifi system service
	 * @param enable True if wifi should be enabled, false if it should be disabled
	 */
	public static void enableWifi(ContextWrapper context, boolean enable){
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		wifiManager.setWifiEnabled(enable);
	}
	
	/**
	 * Determines if the device's wifi is currently enabled or not.
	 * 
	 * @param context A context with which to access wifi system service
	 * @return True if wifi is enabled or enabling, false otherwise
	 */
	public static boolean wifiIsEnabled(ContextWrapper context){
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		int wifiState = wifiManager.getWifiState();
		return (wifiState == WifiManager.WIFI_STATE_ENABLED || wifiState == WifiManager.WIFI_STATE_ENABLING);
	}
	
}

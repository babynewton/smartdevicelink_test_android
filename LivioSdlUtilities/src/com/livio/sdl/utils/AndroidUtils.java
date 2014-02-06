package com.livio.sdl.utils;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.view.inputmethod.InputMethodManager;

public class AndroidUtils {

	public static boolean isNetworkAvailable(Service service){
		ConnectivityManager cm = (ConnectivityManager) service.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo network = cm.getActiveNetworkInfo();
		return ( (network != null) && (network.isConnected()) );
	}

	public static boolean isNetworkAvailable(Activity activity){
		ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo network = cm.getActiveNetworkInfo();
		return ( (network != null) && (network.isConnected()) );
	}
	
	public static void hideSoftKeybaord(IBinder windowToken, Context context){
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(windowToken, 0);
	}
	
}

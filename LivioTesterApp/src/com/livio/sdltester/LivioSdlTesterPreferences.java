package com.livio.sdltester;

import android.content.Context;

import com.livio.sdl.utils.ApplicationPreferences;

/**
 * A static, uninstantiable class that allows saving application-specific information to
 * Android's SharedPreferences.  This class is a wrapper around ApplicationPreferences,
 * allowing specific method names for the various types of data that need to be saved
 * as part of this application.
 * 
 * @see ApplicationPreferences
 *
 * @author Mike Burke
 *
 */
public final class LivioSdlTesterPreferences {

	private LivioSdlTesterPreferences(){}

	public static final int PREF_TRANSPORT_INVALID = -1;
	public static final int PREF_TRANSPORT_WIFI = 0;
	public static final int PREF_TRANSPORT_BLUETOOTH = 1;
	
	private static final String FILENAME = "com.livio.sdltester";
	
	private static final class Keys{
		public static final String IP_ADDRESS = "ip_address";
		public static final String TCP_PORT = "tcp_port";
		public static final String TRANSPORT_CHOICE = "transport_choice";
	}
	
	/**
	 * Looks up the IP_ADDRESS key in Android SharedPreferences and returns the
	 * IP address if it's found.
	 * 
	 * @param context Context to use for SharedPreferences
	 * @return The IP address string if it was found, null otherwise
	 */
	public static String restoreIpAddress(Context context){
		if(ApplicationPreferences.exists(context, FILENAME, Keys.IP_ADDRESS)){
			return ApplicationPreferences.getString(context, FILENAME, Keys.IP_ADDRESS);
		}
		
		return null;
	}
	
	/**
	 * Saves the input IP address to Android SharedPreferences.
	 * 
	 * @param context Context to use for SharedPreferences
	 * @param value The IP address string to save
	 */
	public static void saveIpAddress(Context context, String value){
		ApplicationPreferences.putString(context, FILENAME, Keys.IP_ADDRESS, value);
	}

	/**
	 * Looks up the TCP_PORT key in Android SharedPreferences and returns the
	 * TCP port if it's found.
	 * 
	 * @param context Context to use for SharedPreferences
	 * @return The TCP port string if it was found, null otherwise
	 */
	public static String restoreTcpPort(Context context){
		if(ApplicationPreferences.exists(context, FILENAME, Keys.TCP_PORT)){
			return ApplicationPreferences.getString(context, FILENAME, Keys.TCP_PORT);
		}
		
		return null;
	}
	
	/**
	 * Saves the input TCP port to Android SharedPreferences.
	 * 
	 * @param context Context to use for SharedPreferences
	 * @param value The TCP port string to save
	 */
	public static void saveTcpPort(Context context, String value){
		ApplicationPreferences.putString(context, FILENAME, Keys.TCP_PORT, value);
	}
	
	/**
	 * Looks up the TRANSPORT_CHOICE key in Android SharedPreferences and returns
	 * the choice if it's found.
	 * 
	 * @param context Context to use for SharedPreferences
	 * @return The transport choice if found, -1 if not
	 */
	public static int restoreTransportChoice(Context context){
		if(ApplicationPreferences.exists(context, FILENAME, Keys.TRANSPORT_CHOICE)){
			return ApplicationPreferences.getInt(context, FILENAME, Keys.TRANSPORT_CHOICE);
		}
		
		return PREF_TRANSPORT_INVALID;
	}
	
	/**
	 * Saves the TRANSPORT_CHOICE key in Android SharedPreferences.
	 * 
	 * @param context Context to use for SharedPreferences
	 * @param value The transport choice to save
	 */
	public static void saveTransportChoice(Context context, int value){
		ApplicationPreferences.putInt(context, FILENAME, Keys.TRANSPORT_CHOICE, value);
	}
	
}

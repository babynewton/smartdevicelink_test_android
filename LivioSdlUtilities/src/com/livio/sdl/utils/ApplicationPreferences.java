package com.livio.sdl.utils;

import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Contains static methods for saving and restoring data from Android SharedPreferences.
 *
 * @author Mike Burke
 *
 */
public final class ApplicationPreferences {
	
	private ApplicationPreferences(){} // don't allow instantiation of static classes
	
	/**
	 * Determines if the input key exists as part of the input filename.
	 * 
	 * @param context Context with which to retrieve shared preferences
	 * @param fileName File name for shared preferences
	 * @param key Key of the object to look up
	 * @return True if the key exists, false otherwise
	 */
	public static boolean exists(Context context, String fileName, String key){
		SharedPreferences prefs = getSharedPreferences(context, fileName);
		if(prefs == null){
			return false;
		}
		
		Map<String, ?> mapping = prefs.getAll();
		if(mapping == null || mapping.size() == 0){
			return false;
		}
		
		return (mapping.get(key) != null);
	}
	
	/**
	 * Retrieves the string with the input key from the input filename.
	 * 
	 * @param context Context with which to retrieve shared preferences
	 * @param fileName File name for shared preferences
	 * @param key Key of the object to look up
	 * @return The string if it was found, null otherwise
	 */
	public static String getString(Context context, String fileName, String key){
		SharedPreferences prefs = getSharedPreferences(context, fileName);
		String result = prefs.getString(key, null);
		return result;
	}
	
	/**
	 * Retrieves the boolean with the input key from the input filename.
	 * 
	 * @param context Context with which to retrieve shared preferences
	 * @param fileName File name for shared preferences
	 * @param key Key of the object to look up
	 * @return The boolean if it was found, null otherwise
	 */
	public static boolean getBoolean(Context context, String fileName, String key){
		SharedPreferences prefs = getSharedPreferences(context, fileName);
		boolean result = prefs.getBoolean(key, false);
		return result;
	}

	/**
	 * Retrieves the integer with the input key from the input filename.
	 * 
	 * @param context Context with which to retrieve shared preferences
	 * @param fileName File name for shared preferences
	 * @param key Key of the object to look up
	 * @return The integer if it was found, null otherwise
	 */
	public static int getInt(Context context, String fileName, String key){
		SharedPreferences prefs = getSharedPreferences(context, fileName);
		int result = prefs.getInt(key, -1);
		return result;
	}

	/**
	 * Retrieves the float with the input key from the input filename.
	 * 
	 * @param context Context with which to retrieve shared preferences
	 * @param fileName File name for shared preferences
	 * @param key Key of the object to look up
	 * @return The float if it was found, null otherwise
	 */
	public static float getFloat(Context context, String fileName, String key){
		SharedPreferences prefs = getSharedPreferences(context, fileName);
		float result = prefs.getFloat(key, -1f);
		return result;
	}

	/**
	 * Retrieves the long with the input key from the input filename.
	 * 
	 * @param context Context with which to retrieve shared preferences
	 * @param fileName File name for shared preferences
	 * @param key Key of the object to look up
	 * @return The long if it was found, null otherwise
	 */
	public static long getLong(Context context, String fileName, String key){
		SharedPreferences prefs = getSharedPreferences(context, fileName);
		long result = prefs.getLong(key, -1);
		return result;
	}

	/**
	 * Saves the input value at the input key in the input filename.
	 * 
	 * @param context Context with which to retrieve shared preferences
	 * @param fileName File name for shared preferences
	 * @param key Key of the object to save
	 * @param value Value of the object to save
	 */
	public static void putString(Context context, String fileName, String key, String value){
		SharedPreferences.Editor editor = getEditor(context, fileName);
		editor.putString(key, value);
		editor.apply();
	}

	/**
	 * Saves the input value at the input key in the input filename.
	 * 
	 * @param context Context with which to retrieve shared preferences
	 * @param fileName File name for shared preferences
	 * @param key Key of the object to save
	 * @param value Value of the object to save
	 */
	public static void putBoolean(Context context, String fileName, String key, boolean value){
		SharedPreferences.Editor editor = getEditor(context, fileName);
		editor.putBoolean(key, value);
		editor.apply();
	}

	/**
	 * Saves the input value at the input key in the input filename.
	 * 
	 * @param context Context with which to retrieve shared preferences
	 * @param fileName File name for shared preferences
	 * @param key Key of the object to save
	 * @param value Value of the object to save
	 */
	public static void putInt(Context context, String fileName, String key, int value){
		SharedPreferences.Editor editor = getEditor(context, fileName);
		editor.putInt(key, value);
		editor.apply();
	}

	/**
	 * Saves the input value at the input key in the input filename.
	 * 
	 * @param context Context with which to retrieve shared preferences
	 * @param fileName File name for shared preferences
	 * @param key Key of the object to save
	 * @param value Value of the object to save
	 */
	public static void putFloat(Context context, String fileName, String key, float value){
		SharedPreferences.Editor editor = getEditor(context, fileName);
		editor.putFloat(key, value);
		editor.apply();
	}

	/**
	 * Saves the input value at the input key in the input filename.
	 * 
	 * @param context Context with which to retrieve shared preferences
	 * @param fileName File name for shared preferences
	 * @param key Key of the object to save
	 * @param value Value of the object to save
	 */
	public static void putLong(Context context, String fileName, String key, long value){
		SharedPreferences.Editor editor = getEditor(context, fileName);
		editor.putLong(key, value);
		editor.apply();
	}
	
	private static SharedPreferences getSharedPreferences(Context context, String fileName){
		return context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
	}
	
	private static SharedPreferences.Editor getEditor(Context context, String fileName){
		return getSharedPreferences(context, fileName).edit();
	}
	
}

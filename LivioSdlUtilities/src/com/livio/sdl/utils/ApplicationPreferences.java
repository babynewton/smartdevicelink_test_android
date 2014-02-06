package com.livio.sdl.utils;

import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;

public class ApplicationPreferences {
	
	protected ApplicationPreferences(){}
	
	public static boolean exists(Context context, String fileName, String key){
		SharedPreferences prefs = getSharedPreferences(context, fileName);
		Map<String, ?> mapping = prefs.getAll();
		return (mapping.get(key) != null);
	}
	
	public static String getString(Context context, String fileName, String key){
		SharedPreferences prefs = getSharedPreferences(context, fileName);
		String result = prefs.getString(key, null);
		return result;
	}
	
	public static boolean getBoolean(Context context, String fileName, String key){
		SharedPreferences prefs = getSharedPreferences(context, fileName);
		boolean result = prefs.getBoolean(key, false);
		return result;
	}
	
	public static int getInt(Context context, String fileName, String key){
		SharedPreferences prefs = getSharedPreferences(context, fileName);
		int result = prefs.getInt(key, -1);
		return result;
	}
	
	public static float getFloat(Context context, String fileName, String key){
		SharedPreferences prefs = getSharedPreferences(context, fileName);
		float result = prefs.getFloat(key, -1f);
		return result;
	}
	
	public static long getLong(Context context, String fileName, String key){
		SharedPreferences prefs = getSharedPreferences(context, fileName);
		long result = prefs.getLong(key, -1);
		return result;
	}
	
	public static void putString(Context context, String fileName, String key, String value){
		SharedPreferences.Editor editor = getEditor(context, fileName);
		editor.putString(key, value);
		editor.apply();
	}
	
	public static void putBoolean(Context context, String fileName, String key, boolean value){
		SharedPreferences.Editor editor = getEditor(context, fileName);
		editor.putBoolean(key, value);
		editor.apply();
	}
	
	public static void putInt(Context context, String fileName, String key, int value){
		SharedPreferences.Editor editor = getEditor(context, fileName);
		editor.putInt(key, value);
		editor.apply();
	}
	
	public static void putFloat(Context context, String fileName, String key, float value){
		SharedPreferences.Editor editor = getEditor(context, fileName);
		editor.putFloat(key, value);
		editor.apply();
	}
	
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

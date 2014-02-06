package com.livio.sdltester;

import android.content.Context;

import com.livio.sdl.utils.ApplicationPreferences;

public class MyApplicationPreferences {

	private MyApplicationPreferences(){}
	
	private static final String FILENAME = "com.livio.sdltester";
	
	private static final class Keys{
		public static final String IP_ADDRESS = "ip_address";
		public static final String TCP_PORT = "tcp_port";
	}
	
	public static String restoreIpAddress(Context context){
		if(ApplicationPreferences.exists(context, FILENAME, Keys.IP_ADDRESS)){
			return ApplicationPreferences.getString(context, FILENAME, Keys.IP_ADDRESS);
		}
		
		return null;
	}
	
	public static void saveIpAddress(Context context, String value){
		ApplicationPreferences.putString(context, FILENAME, Keys.IP_ADDRESS, value);
	}
	
	public static String restoreTcpPort(Context context){
		if(ApplicationPreferences.exists(context, FILENAME, Keys.TCP_PORT)){
			return ApplicationPreferences.getString(context, FILENAME, Keys.TCP_PORT);
		}
		
		return null;
	}
	
	public static void saveTcpPort(Context context, String value){
		ApplicationPreferences.putString(context, FILENAME, Keys.TCP_PORT, value);
	}
	
}

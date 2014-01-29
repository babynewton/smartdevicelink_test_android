package com.livio.sdl.enums;

import java.util.Arrays;

public enum SdlTransportType {
	BLUETOOTH("Bluetooth"),
	WIFI("WiFi"),
	USB("USB"),
	
	;
	
	private final String name;
	
	private SdlTransportType(String friendlyName){
		this.name = friendlyName;
	}
	
	/**
	 * Returns an array of the objects in this enum sorted in alphabetical order.
	 * 
	 * @return The sorted array
	 */
	public static SdlTransportType[] getSortedArray(){
		SdlTransportType[] result = values();
		Arrays.sort(result, new EnumComparator<SdlTransportType>());
		return result;
	}
	
	public String getFriendlyName(){
		return name;
	}
}

package com.livio.sdl.enums;


/**
 * Represents the different types of connections that are available through SmartDeviceLink.
 *
 * @author Mike Burke
 *
 */
public enum SdlTransportType {
	/**
	 * Represents a BlueTooth connection with SmartDeviceLink.
	 */
	BLUETOOTH("Bluetooth"),
	/**
	 * Represents a WiFi connection with SmartDeviceLink.
	 */
	WIFI("WiFi"),
	/**
	 * Represents a USB connection with SmartDeviceLink.
	 */
	USB("USB"),
	
	;
	
	private final String name;
	
	private SdlTransportType(String friendlyName){
		this.name = friendlyName;
	}
	
	@Override
	public String toString(){
		return name;
	}
}

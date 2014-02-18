package com.livio.sdl.utils;

public final class WifiUtils {

	private static final int IP_ADDRESS_MIN_LENGTH = 7;
	private static final int IP_ADDRESS_MAX_LENGTH = 15;
	
	private static final int TCP_PORT_MIN_VALUE = 0;
	private static final int TCP_PORT_MAX_VALUE = 65535;
	
	private WifiUtils(){}
	
	/**
	 * Validates the input IP address string.
	 * 
	 * @param address The IP address string to analyze
	 * @return True if this is a valid IP address, false if not
	 */
	public static boolean validateIpAddress(String address){
		// address should be in the form of x.x.x.x, so at least 7 characters
		// address maximum is 255.255.255.255, so at most 15 characters
		if(address == null || address.length() < IP_ADDRESS_MIN_LENGTH || address.length() > IP_ADDRESS_MAX_LENGTH){
			return false;
		}
		
		// split the string into pieces separated by a .
		String[] pieces = address.split("\\.");
		// must have 4 numbers separated by .
		if(pieces.length != 4){
			return false;
		}
		
		// check each piece
		for(String piece : pieces){
			if(!StringUtils.isInteger(piece)){
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Validates the input TCP port string.
	 * 
	 * @param tcpPort The TCP port string to analyze
	 * @return True if this is a valid TCP port, false if not
	 */
	public static boolean validateTcpPort(String tcpPort){
		try{
			int portNumber = Integer.parseInt(tcpPort);
			if(portNumber < TCP_PORT_MIN_VALUE || portNumber > TCP_PORT_MAX_VALUE){
				return false;
			}
		}catch(NumberFormatException e){
			return false;
		}
		
		return true;
	}
}

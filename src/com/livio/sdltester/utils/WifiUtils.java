package com.livio.sdltester.utils;

public class WifiUtils {

	
	public static boolean validateIpAddress(String address){
		// address should be in the form of x.x.x.x, so at least 7 characters
		if(address == null || address.length() <= 7){
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
	
	public static boolean validateTcpPort(String ipPort){
		try{
			int portNumber = Integer.parseInt(ipPort);
			if(portNumber < 0 || portNumber > 65535){
				return false;
			}
		}catch(NumberFormatException e){
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
}

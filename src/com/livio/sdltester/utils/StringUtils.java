package com.livio.sdltester.utils;

public class StringUtils {
	public static boolean isInteger(String input){
		try{
			Integer.parseInt(input);
		}catch(NumberFormatException e){
			// if the string can't be parsed as an integer, it isn't a number.
			return false;
		}
		
		return true;
	}
}

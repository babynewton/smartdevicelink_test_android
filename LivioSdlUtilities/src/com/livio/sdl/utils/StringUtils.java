package com.livio.sdl.utils;

/**
 * Contains static methods useful in dealing with String objects.
 *
 * @author Mike Burke
 *
 */
public abstract class StringUtils {
	
	private StringUtils(){}
	
	/**
	 * Determines if the input string is an integer or not.
	 * 
	 * @param input String to analyze
	 * @return True if the string is an integer, false if not
	 */
	public static boolean isInteger(String input){
		try{
			Integer.parseInt(input);
			return true;
		}catch(NumberFormatException e){
			// if the string can't be parsed as an integer, it isn't a number.
			return false;
		}
	}
}

package com.livio.sdl.utils;

import java.util.Vector;

/**
 * Contains static methods useful in dealing with String objects.
 *
 * @author Mike Burke
 *
 */
public final class StringUtils {
	
	private static final String DEFAULT_DELIMITER = ",";
	
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
	
	/**
	 * Splits up a CSV string into a vector of strings.
	 * 
	 * @param input The CSV string to process
	 * @return The created vector of strings
	 */
	public static Vector<String> toVector(String input){
		return toVector(input, DEFAULT_DELIMITER);
	}
	
	/**
	 * Splits up a string into a vector of strings based on the input delimiter.
	 * 
	 * @param input The raw string to process
	 * @param delim The delimiter on which to split the string
	 * @return The created vector of strings
	 */
	public static Vector<String> toVector(String input, String delim){
		if(input == null){
			throw new NullPointerException();
		}
		if(delim == null){
			delim = DEFAULT_DELIMITER;
		}
		String[] inputArray = input.split(delim);
		Vector<String> result = new Vector<String>(inputArray.length);
		
		for(String splitStr : inputArray){
			result.add(splitStr.trim());
		}
		
		return result;
	}
}

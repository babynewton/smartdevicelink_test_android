package com.livio.sdl.viewhelpers;

import android.text.InputFilter;
import android.text.Spanned;

/**
 * An input filter for number-based text inputs.  The filter allows the user
 * to enter any values between the min and max values used in the constructor,
 * but will not allow any letters or numbers outside that range to be entered into
 * the text field.  For best results, set inputType to number for any text inputs
 * using this input filter.
 *
 * @author Mike Burke
 *
 */
public class MinMaxInputFilter implements InputFilter {

	private int min, max;
	
	public MinMaxInputFilter(int min, int max){
		this.min = min;
		this.max = max;
	}
	
	public MinMaxInputFilter(String min, String max){
		this.min = Integer.parseInt(min);
		this.max = Integer.parseInt(max);
	}
	
	@Override
	public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
		try{
			int input = Integer.parseInt(dest.toString() + source.toString());
			if(isInRange(min, max, input)){
				return null;
			}
		}catch(NumberFormatException e){
			// do nothing
		}
		return "";
	}
	
	// determines if the input is in range or not
	private static boolean isInRange(int min, int max, int input){
		if(max > min){
			return (input >= min && input <= max);
		}
		else{
			return (input >= max && input <= min);
		}
	}

}

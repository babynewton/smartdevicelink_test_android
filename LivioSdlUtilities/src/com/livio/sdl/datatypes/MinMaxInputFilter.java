package com.livio.sdl.datatypes;

import android.text.InputFilter;
import android.text.Spanned;

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
	
	private static boolean isInRange(int min, int max, int input){
		if(max > min){
			return (input >= min && input <= max);
		}
		else{
			return (input >= max && input <= min);
		}
	}

}

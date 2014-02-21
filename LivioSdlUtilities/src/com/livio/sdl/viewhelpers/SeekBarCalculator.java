package com.livio.sdl.viewhelpers;

/**
 * Performs math operations commonly used with SeekBar views.  Performs calculations
 * to translate a progress value to a real-world value and vice-versa. 
 *
 * @author Mike Burke
 *
 */
public class SeekBarCalculator {

	private static final int PROGRESS_MIN = 0; // standard Android seekbars start from 0
	
	private int min, max;
	private float divisor;
	
	public SeekBarCalculator(int min, int max){
		this(min, max, 1.0f);
	}
	
	public SeekBarCalculator(int min, int max, float divisor) {
		this.min = min;
		this.max = max;
		this.divisor = divisor;
	}

	public int getMinValue() {
		return min;
	}
	
	public void setMinValue(int min){
		this.min = min;
	}

	public int getMaxValue() {
		return max;
	}
	
	public void setMaxValue(int max){
		this.max = max;
	}

	public float getDivisor() {
		return divisor;
	}
	
	public void setDivisor(float divisor){
		this.divisor = divisor;
	}
	
	/**
	 * Determines the maximum progress value based on the min and max values.
	 * 
	 * @return The max value of the progress bar
	 */
	public int getMaxProgress(){
		return (max - min);
	}
	
	/**
	 * Determines the minimum progress value.  A typical Android SeekBar, this value is always 0.
	 * 
	 * @return The min value of the progress bar
	 */
	public int getMinProgress(){
		return PROGRESS_MIN;
	}
	
	/**
	 * Calculates the SeekBar progress value for the input real-world value.
	 * 
	 * @param value Real-world value to calculate progress for
	 * @return The progress value of the input real-world value
	 */
	public int calculateProgress(float value){
		value *= divisor;
		
		if(value < getMinValue() || value > getMaxValue()){
			throw new IllegalArgumentException("Value out of seekbar range");
		}
		
		int result = (int) (value - getMinValue());
		
		return result;
	}
	
	/**
	 * Calculates the real-world value for the input progress value.
	 * 
	 * @param progress Progress value to calculate real-world value for
	 * @return The real-world value of the input progress value
	 */
	public float calculateValue(int progress){
		if(progress < getMinProgress() || progress > getMaxProgress()){
			throw new IllegalArgumentException("Progress out of seekbar range");
		}
		
		float result = (progress + getMinValue()) / divisor;
				
		return result;
	}
	

}

package com.livio.sdl.utils;

/**
 * Represents a simple up-counter.  The up-counter can be initialized with
 * a seed value to start counting from.  If initialized with no seed, the counter
 * will start from 0.
 *
 * @author Mike Burke
 *
 */
public class UpCounter extends Counter{
	
	public UpCounter(){
		super();
	}
	
	public UpCounter(int start){
		super(start);
	}
	
	@Override
	public int next(){
		return current++;
	}
	
}

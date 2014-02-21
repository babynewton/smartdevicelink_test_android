package com.livio.sdl.utils;

/**
 * Represents a simple down-counter.  The down-counter can be initialized with
 * a seed value to start counting from.  If initialized with no seed, the counter
 * will start from 0.
 *
 * @author Mike Burke
 *
 */
public class DownCounter extends Counter{

	public DownCounter() {
		super();
	}
	
	public DownCounter(int start){
		super(start);
	}

	@Override
	public int next() {
		return current--;
	}

}

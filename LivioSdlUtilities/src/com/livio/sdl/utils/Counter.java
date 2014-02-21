package com.livio.sdl.utils;

/**
 * Represents an abstract integer counter.
 *
 * @author Mike Burke
 *
 */
public abstract class Counter {

	/**
	 * Returns the current value of the counter and moves to the next one.
	 * 
	 * @return Current value of the counter
	 */
	abstract public int next();
	
	private final int START;
	protected int current;
	
	public Counter(){
		this.START = 0;
		this.current = START;
	}
	
	public Counter(int start){
		this.START = start;
		this.current = start;
	}
	
	/**
	 * Resets the counter to its original starting point.
	 */
	public void reset(){
		this.current = START;
	}
	
	/**
	 * Returns the current value of the counter.
	 * 
	 * @return The current value of the counter
	 */
	public int current(){
		return current;
	}

}

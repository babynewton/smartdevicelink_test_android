package com.livio.sdl.utils;

public class UpCounter {

	private final int START;
	private int current;
	
	public UpCounter(){
		this.START = 0;
		this.current = START;
	}
	
	public UpCounter(int start){
		this.START = start;
		this.current = start;
	}
	
	public void reset(){
		this.current = START;
	}
	
	public int next(){
		return current++;
	}
	
	public int current(){
		return current;
	}
	
	public int getMax(){
		return Integer.MAX_VALUE;
	}
	
}

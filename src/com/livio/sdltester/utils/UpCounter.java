package com.livio.sdltester.utils;

public class UpCounter {

	private int start = 0;
	private int current;
	
	public UpCounter(){
		this.current = start;
	}
	
	public UpCounter(int start){
		this.start = start;
		this.current = start;
	}
	
	public void reset(){
		this.current = start;
	}
	
	public int next(){
		return current++;
	}
	
}

package com.livio.sdl.utils;

public final class MathUtils {

	private MathUtils() {}
	
	public static final class Conversions{
		private Conversions(){}
		
		public static final int S_TO_MS = 1000;
	}
	
	public static int convertSecsToMillisecs(int seconds){
		return seconds * Conversions.S_TO_MS;
	}
	
	public static float convertSecsToMillisecs(float seconds){
		return seconds * Conversions.S_TO_MS;
	}

}

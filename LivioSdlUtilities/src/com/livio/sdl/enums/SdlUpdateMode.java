package com.livio.sdl.enums;

import com.smartdevicelink.proxy.rpc.enums.UpdateMode;

/**
 * Represents various different types of actions on the media clock
 * counter for media applications.
 * 
 * @see UpdateMode
 * @see SetMediaClockTimer
 *
 * @author Mike Burke
 *
 */
public enum SdlUpdateMode {
	/**
	 * Indicates that the timer should start counting up from the input time.
	 */
	COUNT_UP("Count up"),
	/**
	 * Indicates that the timer should start counting down from the input time.
	 */
	COUNT_DOWN("Count down"),
	/**
	 * Indicates that the timer should be paused.
	 */
	PAUSE("Pause"),
	/**
	 * Indicates that the timer should be resumed.
	 */
	RESUME("Resume"),
	/**
	 * Indicates that the timer should be cleared.
	 */
	CLEAR("Clear"),
	;
	
	private final String friendlyName;
	private SdlUpdateMode(String friendlyName){
		this.friendlyName = friendlyName;
	}
	
	@Override
	public String toString(){
		return this.friendlyName;
	}
	
	/**
	 * Translates the input SdlUpdateMode to the associated UpdateMode object.
	 * 
	 * @param input The SdlUpdateMode object to translate
	 * @return The translated UpdateMode object
	 */
	public static UpdateMode translateToLegacy(SdlUpdateMode input){
		switch(input){
		case COUNT_UP:
			return UpdateMode.COUNTUP;
		case COUNT_DOWN:
			return UpdateMode.COUNTDOWN;
		case PAUSE:
			return UpdateMode.PAUSE;
		case RESUME:
			return UpdateMode.RESUME;
		case CLEAR:
			return UpdateMode.CLEAR;
		default:
			return null;
		}
	}
	
	/**
	 * Translates the input UpdateMode to the associated SdlUpdateMode object.
	 * 
	 * @param input The UpdateMode object to translate
	 * @return The translated SdlUpdateMode object
	 */
	public static SdlUpdateMode translateFromLegacy(UpdateMode input){
		switch(input){
		case COUNTUP:
			return COUNT_UP;
		case COUNTDOWN:
			return COUNT_DOWN;
		case PAUSE:
			return PAUSE;
		case RESUME:
			return RESUME;
		case CLEAR:
			return CLEAR;
		default:
			return null;
		}
	}
}

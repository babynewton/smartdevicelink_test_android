package com.livio.sdl.enums;

import com.smartdevicelink.proxy.rpc.enums.UpdateMode;

public enum SdlUpdateMode {
	COUNT_UP("Count up"),
	COUNT_DOWN("Count down"),
	PAUSE("Pause"),
	RESUME("Resume"),
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

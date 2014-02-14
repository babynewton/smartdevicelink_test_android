package com.livio.sdl.enums;

import com.smartdevicelink.proxy.rpc.enums.InteractionMode;

/**
 * Represents different types of interaction modes that the user can utilize 
 * when performing an interaction on a choice set list.
 *
 * @see InteractionMode 
 *
 * @author Mike Burke
 *
 */
public enum SdlInteractionMode {

	/**
	 * Represents perform interactions that can only be controlled manually by button click events.
	 */
	MANUAL_ONLY("Click events"),
	/**
	 * Represents perform interactions that can only be controlled by voice-rec events.
	 */
	VOICE_REC_ONLY("Voice-rec events"),
	/**
	 * Represents perform interactions that can be controlled by click events and voice-rec events.
	 */
	BOTH("Click and voice-rec events"),
	;
	
	private final String friendlyName;
	
	private SdlInteractionMode(String str){
		this.friendlyName = str;
	}
	
	/**
	 * Translates an input SdlInteractionMode to its associated InteractionMode object.
	 * 
	 * @param from The SdlInteractionMode to translate
	 * @return The InteractionMode object associated with the input SdlInteractionMode object
	 */
	public static InteractionMode translateToLegacy(SdlInteractionMode from){
		switch(from){
		case MANUAL_ONLY:
			return InteractionMode.MANUAL_ONLY;
		case VOICE_REC_ONLY:
			return InteractionMode.VR_ONLY;
		case BOTH:
			return InteractionMode.BOTH;
		default:
			return null;
		}
	}
	
	/**
	 * Translates an input InteractionMode to its associated SdlInteractionMode object.
	 * 
	 * @param from The InteractionMode to translate
	 * @return The SdlInteractionMode object associated with the input InteractionMode object
	 */
	public static SdlInteractionMode translateFromLegacy(InteractionMode from){
		switch(from){
		case MANUAL_ONLY:
			return MANUAL_ONLY;
		case VR_ONLY:
			return VOICE_REC_ONLY;
		case BOTH:
			return BOTH;
		default:
			return null;
		}
	}
	
	@Override
	public String toString(){
		return friendlyName;
	}
}

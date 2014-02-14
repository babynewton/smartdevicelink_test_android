package com.livio.sdl.enums;

import com.smartdevicelink.proxy.rpc.enums.SpeechCapabilities;


/**
 * Specifies different types of text-to-speech capabilities available through SmartDeviceLink.  When
 * an application wants the vehicle to speak something to the user, the application can deliver
 * any of the input strings formatted according to the options in this enumerated class.
 * 
 * @see SpeechCapabilities
 * 
 * @author Mike Burke
 */
public enum SdlSpeechCapability {
	/**
	 * Represents a standard text-to-speech translation.
	 */
	TEXT("Text"),
	/**
	 * Represents a SAPI phoneme text string.
	 */
	SAPI_PHONEMES("SAPI Phonemes"),
	/**
	 * Represents a LHPLUS phoneme text string.
	 */
	LHPLUS_PHONEMES("LHPLUS Phonemes"),
	/**
	 * Represents a pre-recorded text entry stored exclusively on the head-unit.
	 */
	PRE_RECORDED("Pre-recorded"),
	/**
	 * Represents a period of silence, for example, a pause between sentences.
	 */
	SILENCE("Silence"),
    
    // future languages go here
    
    ;

    private final String READABLE_NAME;
    
    private SdlSpeechCapability(String readableName) {
        this.READABLE_NAME = readableName;
    }
    
    /**
     * Translates the input SpeechCapabilities object into an SdlSpeechCapability object.
     * 
     * @param input The SpeechCapabilities object to translate
     * @return The translated SdlSpeechCapability object
     */
    public static SdlSpeechCapability translateFromLegacy(SpeechCapabilities input){
    	switch(input){
    	case TEXT:
    		return TEXT;
    	case LHPLUS_PHONEMES:
    		return LHPLUS_PHONEMES;
    	case SAPI_PHONEMES:
    		return SAPI_PHONEMES;
    	case SILENCE:
    		return SILENCE;
    	case PRE_RECORDED:
    		return PRE_RECORDED;
    	default:
    		return null;
    	}
    }
    
    /**
     * Translates the input SdlSpeechCapabilities object into a SpeechCapability object.
     * 
     * @param input The SdlSpeechCapabilities object to translate
     * @return The translated SpeechCapability object
     */
    public static SpeechCapabilities translateToLegacy(SdlSpeechCapability input){
    	switch(input){
    	case TEXT:
    		return SpeechCapabilities.TEXT;
    	case LHPLUS_PHONEMES:
    		return SpeechCapabilities.LHPLUS_PHONEMES;
    	case SAPI_PHONEMES:
    		return SpeechCapabilities.SAPI_PHONEMES;
    	case SILENCE:
    		return SpeechCapabilities.SILENCE;
    	case PRE_RECORDED:
    		return SpeechCapabilities.PRE_RECORDED;
    	default:
    		return null;
    	}
    }
	
	@Override
	public String toString(){
		return this.READABLE_NAME;
	}
}

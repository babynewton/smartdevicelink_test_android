package com.livio.sdl.enums;

import java.util.Arrays;
import java.util.EnumSet;

import com.smartdevicelink.proxy.rpc.enums.SpeechCapabilities;

/**
 * Specifies the language to be used for TTS, VR, displayed messages/menus
 * <p>
 * 
 * @since AppLink 1.0
 *
 */
public enum SdlSpeechCapability {
	TEXT("Text"),
	SAPI_PHONEMES("SAPI Phonemes"), // TODO
	LHPLUS_PHONEMES("LHPLUS Phonemes"),
	PRE_RECORDED("Pre-recorded"),
	SILENCE("Silence"),
    
    // future languages go here
    
    ;

    private final String READABLE_NAME;
    
    private SdlSpeechCapability(String readableName) {
        this.READABLE_NAME = readableName;
    }
	
	//public member methods
	public String getReadableName(){
		return this.READABLE_NAME;
	}

    public static SdlSpeechCapability lookupByReadableName(String readableName) {       	
    	for (SdlSpeechCapability anEnum : EnumSet.allOf(SdlSpeechCapability.class)) {
            if (anEnum.getReadableName().equals(readableName)) {
                return anEnum;
            }
        }
        return null;
    }

    public static SpeechCapabilities lookupLegacyByReadableName(String readableName) {       	
    	for (SdlSpeechCapability anEnum : EnumSet.allOf(SdlSpeechCapability.class)) {
            if (anEnum.getReadableName().equals(readableName)) {
                if(anEnum == TEXT){
                	return SpeechCapabilities.TEXT;
                }
                else if(anEnum == SAPI_PHONEMES){
                	return SpeechCapabilities.SAPI_PHONEMES;
                }
                else if(anEnum == LHPLUS_PHONEMES){
                	return SpeechCapabilities.LHPLUS_PHONEMES;
                }
                else if(anEnum == PRE_RECORDED){
                	return SpeechCapabilities.PRE_RECORDED;
                }
                else if(anEnum == SILENCE){
                	return SpeechCapabilities.SILENCE;
                }
            }
        }
        return null;
    }
	
	/**
	 * Returns an array of the objects in this enum sorted in alphabetical order.
	 * 
	 * @return The sorted array
	 */
	public static SdlSpeechCapability[] getSortedArray(){
		SdlSpeechCapability[] result = values();
		Arrays.sort(result, new EnumComparator<SdlSpeechCapability>());
		return result;
	}
	
	@Override
	public String toString(){
		return this.READABLE_NAME;
	}
}

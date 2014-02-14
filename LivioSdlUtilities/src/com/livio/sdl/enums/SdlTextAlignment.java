package com.livio.sdl.enums;

import com.smartdevicelink.proxy.rpc.enums.TextAlignment;

/**
 * Used in the Show command, the text alignment input tells the head-unit how
 * to align the text that is being updated in the Show command.
 * 
 * @see TextAlignment
 *
 * @author Mike Burke
 *
 */
public enum SdlTextAlignment {
	/**
	 * No change in alignment.
	 */
	NO_SELECTION("No selection"),
	/**
	 * Text aligned left.
	 */
    LEFT_ALIGNED("Left-align"),
    /**
     * Text aligned right.
     */
    RIGHT_ALIGNED("Right-align"),
    /**
     * Text aligned centered.
     */
    CENTERED("Center"),
    
    ;
    
    private final String READABLE_NAME;
    private SdlTextAlignment(String readableName){
    	this.READABLE_NAME = readableName;
    }

    /**
     * Translates the input TextAlignment object into an SdlTextAlignment object.
     * 
     * @param input The TextAlignment object to translate
     * @return The associated SdlTextAlignment object
     */
    public static SdlTextAlignment translateFromLegacy(TextAlignment input){
    	switch(input){
    	case LEFT_ALIGNED:
    		return LEFT_ALIGNED;
    	case RIGHT_ALIGNED:
    		return RIGHT_ALIGNED;
    	case CENTERED:
    		return CENTERED;
    	default:
    		return null;
    	}
    }
    
    /**
     * Translates the input SdlTextAlignment object into a TextAlignment object.
     * 
     * @param input The SdlTextAlignment object to translate
     * @return The associated TextAlignment object
     */
    public static TextAlignment translateToLegacy(SdlTextAlignment input){
    	switch(input){
    	case LEFT_ALIGNED:
    		return TextAlignment.LEFT_ALIGNED;
    	case RIGHT_ALIGNED:
    		return TextAlignment.RIGHT_ALIGNED;
    	case CENTERED:
    		return TextAlignment.CENTERED;
    	default:
    		return null;
    	}
    }
    
    @Override
    public String toString(){
    	return this.READABLE_NAME;
    }
}

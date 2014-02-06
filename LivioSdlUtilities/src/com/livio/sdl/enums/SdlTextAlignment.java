package com.livio.sdl.enums;

import java.util.Arrays;
import java.util.EnumSet;

import com.smartdevicelink.proxy.rpc.enums.TextAlignment;

public enum SdlTextAlignment {
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
    
    public String getReadableName(){
    	return this.READABLE_NAME;
    }

    public static SdlTextAlignment lookupByReadableName(String readableName) {       	
    	for (SdlTextAlignment anEnum : EnumSet.allOf(SdlTextAlignment.class)) {
            if (anEnum.getReadableName().equals(readableName)) {
                return anEnum;
            }
        }
        return null;
    }
    
    public static TextAlignment lookupLegacyByReadableName(String readableName){
    	for (SdlTextAlignment anEnum : EnumSet.allOf(SdlTextAlignment.class)) {
            if (anEnum.getReadableName().equals(readableName)) {
                if(anEnum == LEFT_ALIGNED){
                	return TextAlignment.LEFT_ALIGNED;
                }
                else if(anEnum == RIGHT_ALIGNED){
                	return TextAlignment.RIGHT_ALIGNED;
                }
                else if(anEnum == CENTERED){
                	return TextAlignment.CENTERED;
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
	public static SdlTextAlignment[] getSortedArray(){
		SdlTextAlignment[] result = values();
		Arrays.sort(result, new EnumComparator<SdlTextAlignment>());
		return result;
	}
    
    @Override
    public String toString(){
    	return this.READABLE_NAME;
    }
}

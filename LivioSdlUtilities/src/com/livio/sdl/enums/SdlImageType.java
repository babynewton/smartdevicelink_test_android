package com.livio.sdl.enums;

import java.util.Arrays;
import java.util.EnumSet;

public enum SdlImageType {

	STATIC("Static"),
	DYNAMIC("Dynamic"),
	
	;
	
	private final String READABLE_NAME;
	private SdlImageType(String name){
		this.READABLE_NAME = name;
	}
	
	public String getReadableName(){
		return this.READABLE_NAME;
	}

    public static SdlImageType lookupByReadableName(String readableName) {       	
    	for (SdlImageType anEnum : EnumSet.allOf(SdlImageType.class)) {
            if (anEnum.getReadableName().equals(readableName)) {
                return anEnum;
            }
        }
        return null;
    }
	
	/**
	 * Returns an array of the objects in this enum sorted in alphabetical order.
	 * 
	 * @return The sorted array
	 */
	public static SdlImageType[] getSortedArray(){
		SdlImageType[] result = values();
		Arrays.sort(result, new EnumComparator<SdlImageType>());
		return result;
	}
	
	@Override
	public String toString(){
		return this.READABLE_NAME;
	}
}

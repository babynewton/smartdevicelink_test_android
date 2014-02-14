package com.livio.sdl.enums;

import java.util.EnumSet;

import com.smartdevicelink.proxy.rpc.enums.ImageType;


/**
 * An enumerated class representing image types on the head-unit.  Images can be either
 * static or dynamic.  Static images are contained solely on the head-unit and cannot be
 * updated or deleted.  Dynamic images are contained in the application and sent to the
 * head-unit through the PutFile command.
 *
 * @see ImageType
 *
 * @author Mike Burke
 *
 */
public enum SdlImageType {

	/**
	 * Represents a dynamic image.  Dynamic images are sent from the application to the head-unit
	 * through the PutFile command and can be referenced by the name sent with PutFile.
	 */
	DYNAMIC("Dynamic"),
	/**
	 * Represents a static image.  Static images are stored exclusively on the head-unit and cannot
	 * be changed or deleted by applications.
	 */
	STATIC("Static"),
	
	;
	
	private final String READABLE_NAME;
	private SdlImageType(String name){
		this.READABLE_NAME = name;
	}
	
	/**
	 * Translates an SdlImageType object to it's associated ImageType object.
	 * 
	 * @param input The SdlImageType to convert
	 * @return The associated ImageType
	 */
	public static ImageType translateToLegacy(SdlImageType input){
		switch(input){
		case STATIC:
			return ImageType.STATIC;
		case DYNAMIC:
			return ImageType.DYNAMIC;
		default:
			return null;
		}
	}
	
	/**
	 * Translates an SdlImageType object to it's associated ImageType object.
	 * 
	 * @param input The SdlImageType to convert
	 * @return The associated ImageType
	 */
	public static SdlImageType translateFromLegacy(ImageType input){
		switch(input){
		case STATIC:
			return STATIC;
		case DYNAMIC:
			return DYNAMIC;
		default:
			return null;
		}
	}
	
	/**
	 * Allows a reverse-lookup based on the items readable name.
	 * 
	 * @param readableName The item's readable name
	 * @return The item with the input readable name if found, null otherwise
	 */
	public static SdlImageType lookupByReadableName(String readableName) {       	
    	for (SdlImageType anEnum : EnumSet.allOf(SdlImageType.class)) {
            if (anEnum.toString().equals(readableName)) {
                return anEnum;
            }
        }
        return null;
    }
	
	@Override
	public String toString(){
		return this.READABLE_NAME;
	}
}

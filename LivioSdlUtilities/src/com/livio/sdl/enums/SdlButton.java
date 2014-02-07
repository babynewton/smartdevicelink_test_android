package com.livio.sdl.enums;

import java.util.Arrays;
import java.util.HashMap;

import com.smartdevicelink.proxy.rpc.enums.ButtonName;

/**
 * <p>
 * Defines logical buttons which, on a given SYNC unit, would correspond to
 * either physical or soft (touchscreen) buttons. These logical buttons present
 * a standard functional abstraction which the developer can rely upon,
 * independent of the SYNC unit. For example, the developer can rely upon the OK
 * button having the same meaning to the user across SYNC platforms.
 * </p>
 * <p>
 * The preset buttons (0-9) can typically be interpreted by the application as
 * corresponding to some user-configured choices, though the application is free
 * to interpret these button presses as it sees fit.
 * </p>
 * <p>
 * The application can discover which buttons a given SYNC unit implements by
 * interrogating the ButtonCapabilities parameter of the
 * RegisterAppInterface response.
 * </p>
 * 
 * @since AppLink 1.0
 */
public enum SdlButton {
	
	//TODO - Map the names of the buttons to whatever value is sent through the SDL protocol.
	
	/**
	 * Represents the button usually labeled "OK". A typical use of this button
	 * is for the user to press it to make a selection.
	 * 
	 * @since AppLink 1.0
	 */
	OK ("Ok"),
	/**
	 * Represents the seek-left button. A typical use of this button is for the
	 * user to scroll to the left through menu choices one menu item per press.
	 * 
	 * @since AppLink 1.0
	 */
	SEEK_LEFT ("Seek Left"),
	/**
	 * Represents the seek-right button. A typical use of this button is for the
	 * user to scroll to the right through menu choices one menu item per press.
	 * 
	 * @since AppLink 1.0
	 */
	SEEK_RIGHT ("Seek Right"),
	/**
	 * Represents a turn of the tuner knob in the clockwise direction one tick.
	 * 
	 * @since AppLink 1.0
	 */
	TUNE_UP ("Tune Up"),
	/**
	 * Represents a turn of the tuner knob in the counter-clockwise direction
	 * one tick.
	 * 
	 * @since AppLink 1.0
	 */
	TUNE_DOWN ("Tune Down"),
	/**
	 * Represents the preset 0 button.
	 * 
	 * @since AppLink 1.0
	 */
	PRESET_0 ("Preset #0"),
	/**
	 * Represents the preset 1 button.
	 * 
	 * @since AppLink 1.0
	 */
	PRESET_1 ("Preset #1"),
	/**
	 * Represents the preset 2 button.
	 * 
	 * @since AppLink 1.0
	 */
	PRESET_2 ("Preset #2"),
	/**
	 * Represents the preset 3 button.
	 * 
	 * @since AppLink 1.0
	 */
	PRESET_3 ("Preset #3"),
	/**
	 * Represents the preset 4 button.
	 * 
	 * @since AppLink 1.0
	 */
	PRESET_4 ("Preset #4"),
	/**
	 * Represents the preset 5 button.
	 * 
	 * @since AppLink 1.0
	 */
	PRESET_5 ("Preset #5"),
	/**
	 * Represents the preset 6 button.
	 * 
	 * @since AppLink 1.0
	 */
	PRESET_6 ("Preset #6"),
	/**
	 * Represents the preset 7 button.
	 * 
	 * @since AppLink 1.0
	 */
	PRESET_7 ("Preset #7"),
	/**
	 * Represents the preset 8 button.
	 * 
	 * @since AppLink 1.0
	 */
	PRESET_8 ("Preset #8"),
	/**
	 * Represents the preset 9 button.
	 * 
	 * @since AppLink 1.0
	 */
	PRESET_9 ("Preset #9"),
	
	;
	
	private final String READABLE_NAME;
	
	//This HashMap allows an easy reverse look-up to get the SyncCommand enum value for a given readable name.
	// notice that it's static because we don't want to create an entire hashmap for every single enum value.
	// static means this is only done once and stored in memory indefinitely.
	private static HashMap<String, SdlButton> reverseLookupMap;
	
	private SdlButton(String readableName){
		this.READABLE_NAME = readableName;
	}
	
	/**
	 * Returns the readable name for the instance.
	 * @return A string containing the readable name.
	 */
	public String getReadableName(){
		return this.READABLE_NAME;
	}
	
	//public static methods
	/**
	 * Uses the SyncCommand static HashMap to perform a reverse look-up of the input
	 * readable name.  If no value is found, it will return null.
	 * 
	 * @param name A SyncCommand readable name to look-up
	 * @return The associated SyncCommand
	 */
	public static SdlButton lookupByReadableName(String name){
		if(reverseLookupMap == null){
			createReverseLookupMap();
		}
		
		return reverseLookupMap.get(name);
	}
	
	private static void createReverseLookupMap(){
		reverseLookupMap = new HashMap<String, SdlButton>(values().length);
		
		for(SdlButton enumValue : SdlButton.values()){
			//map the readable name back to the value, so we have an easy reverse look-up
			reverseLookupMap.put(enumValue.getReadableName(), enumValue);
		}
	}
	
	/**
	 * Returns an array of the objects in this enum sorted in alphabetical order.
	 * 
	 * @return The sorted array
	 */
	public static SdlButton[] getSortedArray(){
		SdlButton[] result = values();
		Arrays.sort(result, new EnumComparator<SdlButton>());
		return result;
	}
	
	/**
	 * Translates a legacy button (ButtonName) to this type of button (SdlButton).
	 * 
	 * @param legacyButton The legacy button to translate
	 * @return The appropriate SdlButton for the input
	 */
	public static SdlButton translateFromLegacy(ButtonName legacyButton){
		if(legacyButton == ButtonName.OK){
			return OK;
		}
		else if(legacyButton == ButtonName.SEEKLEFT){
			return SEEK_LEFT;
		}
		else if(legacyButton == ButtonName.SEEKRIGHT){
			return SEEK_RIGHT;
		}
		else if(legacyButton == ButtonName.TUNEDOWN){
			return TUNE_DOWN;
		}
		else if(legacyButton == ButtonName.TUNEUP){
			return TUNE_UP;
		}
		else if(legacyButton == ButtonName.PRESET_0){
			return PRESET_0;
		}
		else if(legacyButton == ButtonName.PRESET_1){
			return PRESET_1;
		}
		else if(legacyButton == ButtonName.PRESET_2){
			return PRESET_2;
		}
		else if(legacyButton == ButtonName.PRESET_3){
			return PRESET_3;
		}
		else if(legacyButton == ButtonName.PRESET_4){
			return PRESET_4;
		}
		else if(legacyButton == ButtonName.PRESET_5){
			return PRESET_5;
		}
		else if(legacyButton == ButtonName.PRESET_6){
			return PRESET_6;
		}
		else if(legacyButton == ButtonName.PRESET_7){
			return PRESET_7;
		}
		else if(legacyButton == ButtonName.PRESET_8){
			return PRESET_8;
		}
		else if(legacyButton == ButtonName.PRESET_9){
			return PRESET_9;
		}
		
		return null;
	}
	
	/**
	 * Translates this type of button (SdlButton) to a legacy button (ButtonName).
	 * 
	 * @param sdlButton The new button to translate
	 * @return The appropriate legacy button for the input
	 */
	public static ButtonName translateToLegacy(SdlButton sdlButton){
		if(sdlButton == OK){
			return ButtonName.OK;
		}
		else if(sdlButton == SEEK_LEFT){
			return ButtonName.SEEKLEFT;
		}
		else if(sdlButton == SEEK_RIGHT){
			return ButtonName.SEEKRIGHT;
		}
		else if(sdlButton == TUNE_DOWN){
			return ButtonName.TUNEDOWN;
		}
		else if(sdlButton == TUNE_UP){
			return ButtonName.TUNEUP;
		}
		else if(sdlButton == PRESET_0){
			return ButtonName.PRESET_0;
		}
		else if(sdlButton == PRESET_1){
			return ButtonName.PRESET_1;
		}
		else if(sdlButton == PRESET_2){
			return ButtonName.PRESET_2;
		}
		else if(sdlButton == PRESET_3){
			return ButtonName.PRESET_3;
		}
		else if(sdlButton == PRESET_4){
			return ButtonName.PRESET_4;
		}
		else if(sdlButton == PRESET_5){
			return ButtonName.PRESET_5;
		}
		else if(sdlButton == PRESET_6){
			return ButtonName.PRESET_6;
		}
		else if(sdlButton == PRESET_7){
			return ButtonName.PRESET_7;
		}
		else if(sdlButton == PRESET_8){
			return ButtonName.PRESET_8;
		}
		else if(sdlButton == PRESET_9){
			return ButtonName.PRESET_9;
		}
		
		return null;
	}
	
	@Override
	public String toString(){
		return this.READABLE_NAME;
	}
}

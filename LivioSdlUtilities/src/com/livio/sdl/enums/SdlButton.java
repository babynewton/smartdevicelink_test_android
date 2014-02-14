package com.livio.sdl.enums;

import java.util.Arrays;

import com.smartdevicelink.proxy.rpc.enums.ButtonName;

/**
 * <p>
 * Defines logical buttons which, on a given SDL unit, would correspond to
 * either physical or soft (touchscreen) buttons. These logical buttons present
 * a standard functional abstraction which the developer can rely upon,
 * independent of the SYNC unit. For example, the developer can rely upon the OK
 * button having the same meaning to the user across SDL platforms.
 * </p>
 * <p>
 * The preset buttons (0-9) can typically be interpreted by the application as
 * corresponding to some user-configured choices, though the application is free
 * to interpret these button presses as it sees fit.
 * </p>
 * <p>
 * The application can discover which buttons a given SDL unit implements by
 * interrogating the ButtonCapabilities parameter of the
 * RegisterAppInterface response.
 * </p>
 * 
 * @since SmartDeviceLink 1.0
 */
public enum SdlButton {
	
	/**
	 * Represents the button usually labeled "OK". A typical use of this button
	 * is for the user to press it to make a selection.
	 * 
	 * @since SmartDeviceLink 1.0
	 */
	OK ("Ok"),
	/**
	 * Represents the seek-left button. A typical use of this button is for the
	 * user to scroll to the left through menu choices one menu item per press.
	 * 
	 * @since SmartDeviceLink 1.0
	 */
	SEEK_LEFT ("Seek Left"),
	/**
	 * Represents the seek-right button. A typical use of this button is for the
	 * user to scroll to the right through menu choices one menu item per press.
	 * 
	 * @since SmartDeviceLink 1.0
	 */
	SEEK_RIGHT ("Seek Right"),
	/**
	 * Represents a turn of the tuner knob in the clockwise direction one tick.
	 * 
	 * @since SmartDeviceLink 1.0
	 */
	TUNE_UP ("Tune Up"),
	/**
	 * Represents a turn of the tuner knob in the counter-clockwise direction
	 * one tick.
	 * 
	 * @since SmartDeviceLink 1.0
	 */
	TUNE_DOWN ("Tune Down"),
	/**
	 * Represents the preset 0 button.
	 * 
	 * @since SmartDeviceLink 1.0
	 */
	PRESET_0 ("Preset #0"),
	/**
	 * Represents the preset 1 button.
	 * 
	 * @since SmartDeviceLink 1.0
	 */
	PRESET_1 ("Preset #1"),
	/**
	 * Represents the preset 2 button.
	 * 
	 * @since SmartDeviceLink 1.0
	 */
	PRESET_2 ("Preset #2"),
	/**
	 * Represents the preset 3 button.
	 * 
	 * @since SmartDeviceLink 1.0
	 */
	PRESET_3 ("Preset #3"),
	/**
	 * Represents the preset 4 button.
	 * 
	 * @since SmartDeviceLink 1.0
	 */
	PRESET_4 ("Preset #4"),
	/**
	 * Represents the preset 5 button.
	 * 
	 * @since SmartDeviceLink 1.0
	 */
	PRESET_5 ("Preset #5"),
	/**
	 * Represents the preset 6 button.
	 * 
	 * @since SmartDeviceLink 1.0
	 */
	PRESET_6 ("Preset #6"),
	/**
	 * Represents the preset 7 button.
	 * 
	 * @since SmartDeviceLink 1.0
	 */
	PRESET_7 ("Preset #7"),
	/**
	 * Represents the preset 8 button.
	 * 
	 * @since SmartDeviceLink 1.0
	 */
	PRESET_8 ("Preset #8"),
	/**
	 * Represents the preset 9 button.
	 * 
	 * @since SmartDeviceLink 1.0
	 */
	PRESET_9 ("Preset #9"),
	
	;
	
	private final String READABLE_NAME;
	
	private SdlButton(String readableName){
		this.READABLE_NAME = readableName;
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
		switch(legacyButton){
		case OK:
			return OK;
		case SEEKLEFT:
			return SEEK_LEFT;
		case SEEKRIGHT:
			return SEEK_RIGHT;
		case TUNEDOWN:
			return TUNE_DOWN;
		case TUNEUP:
			return TUNE_UP;
		case PRESET_0:
			return PRESET_0;
		case PRESET_1:
			return PRESET_1;
		case PRESET_2:
			return PRESET_2;
		case PRESET_3:
			return PRESET_3;
		case PRESET_4:
			return PRESET_4;
		case PRESET_5:
			return PRESET_5;
		case PRESET_6:
			return PRESET_6;
		case PRESET_7:
			return PRESET_7;
		case PRESET_8:
			return PRESET_8;
		case PRESET_9:
			return PRESET_9;
		default:
			return null;
		}
	}
	
	/**
	 * Translates this type of button (SdlButton) to a legacy button (ButtonName).
	 * 
	 * @param sdlButton The new button to translate
	 * @return The appropriate legacy button for the input
	 */
	public static ButtonName translateToLegacy(SdlButton sdlButton){
		switch(sdlButton){
		case OK:
			return ButtonName.OK;
		case SEEK_LEFT:
			return ButtonName.SEEKLEFT;
		case SEEK_RIGHT:
			return ButtonName.SEEKRIGHT;
		case TUNE_DOWN:
			return ButtonName.TUNEDOWN;
		case TUNE_UP:
			return ButtonName.TUNEUP;
		case PRESET_0:
			return ButtonName.PRESET_0;
		case PRESET_1:
			return ButtonName.PRESET_1;
		case PRESET_2:
			return ButtonName.PRESET_2;
		case PRESET_3:
			return ButtonName.PRESET_3;
		case PRESET_4:
			return ButtonName.PRESET_4;
		case PRESET_5:
			return ButtonName.PRESET_5;
		case PRESET_6:
			return ButtonName.PRESET_6;
		case PRESET_7:
			return ButtonName.PRESET_7;
		case PRESET_8:
			return ButtonName.PRESET_8;
		case PRESET_9:
			return ButtonName.PRESET_9;
		default:
			return null;
		}
	}
	
	@Override
	public String toString(){
		return this.READABLE_NAME;
	}
}

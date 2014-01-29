package com.livio.sdl.enums;

import java.util.Arrays;
import java.util.HashMap;

/**
 * This is an enumerated list of SYNC commands.  Each command should be built with a constructor
 * containing a world-readable name.  
 * 
 * The readable can be queried like this:
 * 
 * <code>
 *  SyncCommand.ALERT.getReadableName(); //returns "Alert" - Best Practice instead of toString(), which returns "ALERT"
 * </code>
 * 
 * Getting a list of names is better this way because it allows you to maintain coding standards for enums (all caps),
 * but still provide a world-readable string that can be used in menus or logs.  It also allows you to change the 
 * readable text in the future without causing errors in your users code by changing the enum values.
 * 
 * Since it will be useful to get a given SyncCommand from its readable name, this class also implements a HashMap
 * for reverse lookup.  This is particularly useful when a user selects an item from a list and the string from that
 * list is returned to you.  With that string, you can find the given enum value directly instead of having to 
 * iterate through every single enum searching for that string.  Using the HashMap is much more efficient, even if
 * it takes up a little bit of memory.  
 * 
 * If you have the name of a command, you can get its enum value like this:
 * 
 * <code>
 *  String readableName = SyncCommand.ALERT.getReadableName();
 *  SyncCommand alertCommand = SyncCommand.lookupByReadableName(readableName); //returns SyncCommand.ALERT
 * </code>
 * 
 * @author Mike Burke
 *
 */
public enum SdlCommand{
	// TODO - add Javadoc comments to these enum values
	// TODO - only show commands that are currently working through the SDL protocol
	ALERT ("Alert"),
	SPEAK ("Speak"),
	SHOW ("Show"),
	SUBSCRIBE_BUTTON ("Subscribe to Buttons"),
	ADD_COMMAND ("Add a Command"),
	DELETE_COMMAND ("Delete a Command"),
	ADD_SUBMENU ("Add a Submenu"),
	DELETE_SUB_MENU ("Delete a Submenu"),
	SET_GLOBAL_PROPERTIES ("Set Global Properties"),
	RESET_GLOBAL_PROPERTIES ("Reset Global Properties"),
	SET_MEDIA_CLOCK_TIMER ("Set Media Clock Timer"),
	CREATE_INTERACTION_CHOICE_SET ("Create Interaction Choice Set"),
	DELETE_INTERACTION_CHOICE_SET ("Delete Interaction Choice Set"),
	PERFORM_INTERACTION ("Perform Interaction"),
	ENCODED_SYNC_PDATA ("Encoded SYNC Pdata"),
	SLIDER ("Slider"),
	SCROLLABLE_MESSAGE ("Scrollable Message"),
	CHANGE_REGISTRATION ("Change Registration"),
	PUT_FILE ("Put File"),
	DELETE_FILE ("Delete File"),
	LIST_FILES ("List Files"),
	SET_APP_ICON ("Set App Icon"),
	PERFORM_AUDIO_PASSTHRU ("Perform Audio Pass-through"),
	END_AUDIO_PASSTHRU ("End Audio Pass-through"),
	SUBSCRIBE_VEHICLE_DATA ("Subscribe to Vehicle Data"),
	UNSUBSCRIBE_VEHICLE_DATA ("Unsubscribe to Vehicle Data"),
	GET_VEHICLE_DATA ("Get Vehicle Data"),
	READ_DIDS ("Read DIDs"),
	GET_DTCS ("Get DTCs"),
	SHOW_CONSTANT_TBT ("Show Constant TBT"),
	ALERT_MANEUVER ("Alert Maneuver"), //TODO - this command doesn't work on SDL core as of 1/23/2014
	UPDATE_TURN_LIST ("Update Turn List"),
	DIAL_NUMBER ("Dial Number"), //TODO - this command doesn't work on SDL core as of 1/23/2014
	
	//Future commands go here.
	
	;
	
	// THIS IS AN ENUM, SO BASICALLY EVERYTHING SHOULD BE FINAL.
	
	//methods and anything that is not part of the enum can go down here.  Doesn't need to be
	// in a different class or anything.  It's best to keep related items in the same class like this.
	
	private static final String COMMAND_ACTION_PREFIX = "com.ford.sync.action.command.";
	
	//This HashMap allows an easy reverse look-up to get the SyncCommand enum value for a given readable name.
	// notice that it's static because we don't want to create an entire hashmap for every single enum value.
	// we take a slight memory hit for storing this in memory, but performance of this will be drastically better
	// than iterating through the enum values every time we want to reverse look-up (which will happen often).
	private static HashMap<String, SdlCommand> reverseLookupMap;
	
	private final String READABLE_NAME;
	
	//constructor
	private SdlCommand(String readableName){
		this.READABLE_NAME = readableName;
	}
	
	@Override
	public String toString(){
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
	public static final SdlCommand lookupByReadableName(String name){
		if(reverseLookupMap == null){
			createReverseLookupMap();
		}
		
		return reverseLookupMap.get(name);
	}
	
	private static void createReverseLookupMap(){
		reverseLookupMap = new HashMap<String, SdlCommand>(values().length);
		for(SdlCommand enumValue : SdlCommand.values()){
			//map the readable name back to the value, so we have an easy reverse look-up
			reverseLookupMap.put(enumValue.toString(), enumValue);
		}
	}
	
	/**
	 * Builds a string that represents an intent action for the input SYNC command.
	 * 
	 * @param command The SYNC command for which to build an action string
	 * @return An action string to be used for intents
	 */
	public static final String syncCommandActionString(SdlCommand command){
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(COMMAND_ACTION_PREFIX);
		strBuilder.append(command.toString());
		return strBuilder.toString();
	}
	
	/**
	 * Returns an array of the objects in this enum sorted in alphabetical order.
	 * 
	 * @return The sorted array
	 */
	public static SdlCommand[] getSortedArray(){
		SdlCommand[] result = values();
		Arrays.sort(result, new EnumComparator<SdlCommand>());
		return result;
	}
	
	
	
}


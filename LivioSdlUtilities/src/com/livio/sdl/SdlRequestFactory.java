package com.livio.sdl;

import java.util.Vector;

import com.livio.sdl.utils.MathUtils;
import com.livio.sdl.utils.SdlUtils;
import com.smartdevicelink.proxy.RPCRequest;
import com.smartdevicelink.proxy.TTSChunkFactory;
import com.smartdevicelink.proxy.rpc.AddCommand;
import com.smartdevicelink.proxy.rpc.AddSubMenu;
import com.smartdevicelink.proxy.rpc.Alert;
import com.smartdevicelink.proxy.rpc.ChangeRegistration;
import com.smartdevicelink.proxy.rpc.Choice;
import com.smartdevicelink.proxy.rpc.CreateInteractionChoiceSet;
import com.smartdevicelink.proxy.rpc.DeleteCommand;
import com.smartdevicelink.proxy.rpc.DeleteFile;
import com.smartdevicelink.proxy.rpc.DeleteInteractionChoiceSet;
import com.smartdevicelink.proxy.rpc.DeleteSubMenu;
import com.smartdevicelink.proxy.rpc.GetDTCs;
import com.smartdevicelink.proxy.rpc.Image;
import com.smartdevicelink.proxy.rpc.PerformInteraction;
import com.smartdevicelink.proxy.rpc.PutFile;
import com.smartdevicelink.proxy.rpc.ReadDID;
import com.smartdevicelink.proxy.rpc.ScrollableMessage;
import com.smartdevicelink.proxy.rpc.SetAppIcon;
import com.smartdevicelink.proxy.rpc.SetMediaClockTimer;
import com.smartdevicelink.proxy.rpc.Show;
import com.smartdevicelink.proxy.rpc.Slider;
import com.smartdevicelink.proxy.rpc.Speak;
import com.smartdevicelink.proxy.rpc.StartTime;
import com.smartdevicelink.proxy.rpc.SubscribeButton;
import com.smartdevicelink.proxy.rpc.TTSChunk;
import com.smartdevicelink.proxy.rpc.UnsubscribeButton;
import com.smartdevicelink.proxy.rpc.enums.ButtonName;
import com.smartdevicelink.proxy.rpc.enums.FileType;
import com.smartdevicelink.proxy.rpc.enums.InteractionMode;
import com.smartdevicelink.proxy.rpc.enums.Language;
import com.smartdevicelink.proxy.rpc.enums.SpeechCapabilities;
import com.smartdevicelink.proxy.rpc.enums.TextAlignment;
import com.smartdevicelink.proxy.rpc.enums.UpdateMode;

/**
 * A static factory class that instantiates various types of RPCRequest subclasses based on the
 * input parameters.
 *
 * @author Mike Burke
 *
 */
public final class SdlRequestFactory {
	
	private SdlRequestFactory() {}

	/**
	 * Creates an AddCommand RPCRequest.
	 * 
	 * @param name The name of the command.  Cannot be null or empty string
	 * @param position The position in the menu list
	 * @param parentId The command's parent id
	 * @param vrCommands A CSV string input that is parsed into voice-rec strings
	 * @param imageName The image name of any image associated with the command
	 * @return The created AddCommand request
	 */
	public static RPCRequest addCommand(String name, int position, int parentId, String vrCommands, String imageName){
		if(name == null){
			throw new NullPointerException();
		}
		if(name.length() <= 0){
			throw new IllegalArgumentException();
		}
		
		AddCommand result = new AddCommand();
		result.setMenuParams(SdlUtils.menuParams(name, position, parentId));
		if(vrCommands != null && vrCommands.length() > 0){
			result.setVrCommands(SdlUtils.voiceRecognitionVector(vrCommands));
		}
		if(imageName != null){
			result.setCmdIcon(SdlUtils.dynamicImage(imageName));
		}
		return result;
	}
	
	/**
	 * Creates an AddSubmenu RPCRequest.
	 * 
	 * @param submenuName The name of the submenu.  Cannot be null or empty string
	 * @param position The position in the menu list
	 * @return The created AddSubMenu request
	 */
	public static RPCRequest addSubmenu(String submenuName, int position){
		if(submenuName == null){
			throw new NullPointerException();
		}
		if(submenuName.length() <= 0){
			throw new IllegalArgumentException();
		}
		
		AddSubMenu result = new AddSubMenu();
		result.setMenuName(submenuName);
		result.setPosition(position);
		return result;
	}
	
	/**
	 * Creates a SubscribeButton RPCRequest.
	 * 
	 * @param name The ButtonName that is being subscribed to
	 * @return The created SubscribeButton request
	 */
	public static RPCRequest subscribeButton(ButtonName name){
		if(name == null){
			throw new NullPointerException();
		}
		
		SubscribeButton result = new SubscribeButton();
		result.setButtonName(name);
		return result;
	}
	
	/**
	 * Creates a UnsubscribeButton RPCRequest
	 * 
	 * @param name The ButtonName that is being unsubscribed from
	 * @return The created UnsubscribeButton request
	 */
	public static RPCRequest unsubscribeButton(ButtonName name){
		if(name == null){
			throw new NullPointerException();
		}
		
		UnsubscribeButton result = new UnsubscribeButton();
		result.setButtonName(name);
		return result;
	}
	
	/**
	 * Creates a ChangeRegistration request
	 * 
	 * @param mainLang Main language to set.  Cannot be null
	 * @param hmiLang HMI language to set.  Cannot be null
	 * @return The ChangeRegistration request
	 */
	public static RPCRequest changeRegistration(Language mainLang, Language hmiLang){
		if(mainLang == null || hmiLang == null){
			throw new NullPointerException();
		}
		
		ChangeRegistration result = new ChangeRegistration();
		result.setLanguage(mainLang);
		result.setHmiDisplayLanguage(hmiLang);
		return result;
	}
	
	/**
	 * Creates a CreateInteractionChoiceSet request.
	 * 
	 * @param choiceSet The choices contained within the CreateInteractionChoiceSet.  Cannot be null or empty vector.
	 * @return The created CreateInteractionChoiceSet request
	 */
	public static RPCRequest createInteractionChoiceSet(Vector<Choice> choiceSet){
		if(choiceSet == null){
			throw new NullPointerException();
		}
		if(choiceSet.size() <= 0){
			throw new IllegalArgumentException();
		}
		
		CreateInteractionChoiceSet result = new CreateInteractionChoiceSet();
		result.setChoiceSet(choiceSet);
		return result;
	}
	
	/**
	 * Creates a DeleteCommand request.
	 * 
	 * @param commandId The id of the command to delete.  Must be a valid ID value.
	 * @return The created DeleteCommand request
	 */
	public static RPCRequest deleteCommand(int commandId){
		if(commandId < SdlConstants.AddCommandConstants.MINIMUM_COMMAND_ID ||
		   commandId > SdlConstants.AddCommandConstants.MAXIMUM_COMMAND_ID){
			throw new IllegalArgumentException();
		}
		
		DeleteCommand result = new DeleteCommand();
		result.setCmdID(commandId);
		return result;
	}
	
	/**
	 * Creates a DeleteSubMenu request.
	 * 
	 * @param menuId The id of the command to delete.  Must be a valid ID value.
	 * @return The created DeleteSubMenu request
	 */
	public static RPCRequest deleteSubmenu(int menuId){
		if(menuId < SdlConstants.AddCommandConstants.MINIMUM_COMMAND_ID ||
			menuId > SdlConstants.AddCommandConstants.MAXIMUM_COMMAND_ID){
			throw new IllegalArgumentException();
		}
				
		DeleteSubMenu result = new DeleteSubMenu();
		result.setMenuID(menuId);
		return result;
	}
	
	/**
	 * Creates a DeleteFile request.
	 * 
	 * @param fileName The file name of the image to delete.  Cannot be null.
	 * @return The created DeleteFile request
	 */
	public static RPCRequest deleteFile(String fileName){
		if(fileName == null){
			throw new NullPointerException();
		}
		
		DeleteFile result = new DeleteFile();
		result.setSmartDeviceLinkFileName(fileName);
		return result;
	}
	
	/**
	 * Creates a DeleteInteracionChoiceSet request.
	 * 
	 * @param id The id of the choice set to delete.  Must be a valid id value.
	 * @return
	 */
	public static RPCRequest deleteInteractionChoiceSet(int id){
		if(id < SdlConstants.InteractionChoiceSetConstants.MINIMUM_CHOICE_SET_ID || id > SdlConstants.InteractionChoiceSetConstants.MAXIMUM_CHOICE_SET_ID){
			throw new IllegalArgumentException();
		}
		
		DeleteInteractionChoiceSet result = new DeleteInteractionChoiceSet();
		result.setInteractionChoiceSetID(id);
		return result;
	}
	
	
	/**
	 * Creates a GetDTCs request.
	 * 
	 * @param ecuId The ECU id to get DTCs for.  Must be a valid ECU id value.
	 * @return The created GetDTCs request
	 */
	public static RPCRequest getDtcs(int ecuId){
		if(ecuId < SdlConstants.GetDtcsConstants.MINIMUM_ECU_ID || ecuId > SdlConstants.GetDtcsConstants.MAXIMUM_ECU_ID){
			throw new IllegalArgumentException();
		}
		
		GetDTCs result = new GetDTCs();
		result.setEcuName(ecuId);
		return result;
	}
	
	/**
	 * Creates a GetDTCs request.
	 * 
	 * @param ecuId
	 * @return
	 */
	public static RPCRequest getDtcs(String ecuId){
		try{
			return getDtcs(Integer.parseInt(ecuId));
		}
		catch(NumberFormatException e){
			throw new IllegalArgumentException();
		}
	}
	
	/**
	 * Creates a PerformInteraction request.
	 * 
	 * @param title The title of the interaction
	 * @param voicePrompt The CSV string representing the vector of voice-rec inputs
	 * @param choiceIds The ids of the choices to present to the user.  Cannot be null or empty.
	 * @param mode The interaction mode for this interaction
	 * @param timeout The timeout for the interaction.  Must be within timeout range.
	 * @return The created PerformInteraction request
	 */
	public static RPCRequest performInteraction(String title, String voicePrompt, Vector<Integer> choiceIds, InteractionMode mode, int timeout){
		if(choiceIds == null || choiceIds.size() <= 0){
			throw new IllegalArgumentException();
		}
		
		if( (timeout < MathUtils.convertSecsToMillisecs(SdlConstants.PerformInteractionConstants.MINIMUM_TIMEOUT) || 
			 timeout > MathUtils.convertSecsToMillisecs(SdlConstants.PerformInteractionConstants.MAXIMUM_TIMEOUT) ) &&
			(timeout != SdlConstants.PerformInteractionConstants.INVALID_TIMEOUT) ){
			throw new IllegalArgumentException();
		}
		
		PerformInteraction result = new PerformInteraction();
		
		// set the title
		if(title == null || title.length() <= 0){
			title = " ";
		}
		result.setInitialText(title);
		
		// set the voice prompt
		if(voicePrompt == null || voicePrompt.length() <= 0){
			voicePrompt = " ";
		}
		Vector<TTSChunk> ttsChunks = TTSChunkFactory.createSimpleTTSChunks(voicePrompt);
		result.setInitialPrompt(ttsChunks);
		
		// set the interaction mode
		result.setInteractionMode(mode);
		
		// set the choice set ids
		result.setInteractionChoiceSetIDList(choiceIds);
		
		// set the timeout
		if(timeout != SdlConstants.PerformInteractionConstants.INVALID_TIMEOUT){
			result.setTimeout(timeout);
		}
		
		return result;
	}
	
	/**
	 * Creates a PerformInteraction request.
	 * 
	 * @param title The title of the interaction
	 * @param voicePrompt The CSV string representing the vector of voice-rec inputs
	 * @param choiceIds The ids of the choices to present to the user.  Cannot be null or empty.
	 * @param mode The interaction mode for this interaction
	 * @return The created PerformInteraction request
	 */
	public static RPCRequest performInteraction(String title, String voicePrompt, Vector<Integer> choiceIds, InteractionMode mode){
		return performInteraction(title, voicePrompt, choiceIds, mode, SdlConstants.PerformInteractionConstants.INVALID_TIMEOUT);
	}
	
	/**
	 * Creates a PutFile request.
	 * 
	 * @param fileName The file name for the file.  Cannot be null or empty.
	 * @param type Type of file this request will contain.  Cannot be null.
	 * @param persistent Whether or not the file is persistent on the head-unit
	 * @param rawBytes The raw bytes of the file to send.  Cannot be null or empty.
	 * @return The created PutFile request
	 */
	public static RPCRequest putFile(String fileName, FileType type, boolean persistent, byte[] rawBytes){
		if(fileName == null || type == null || rawBytes == null){
			throw new NullPointerException();
		}
		if(fileName.length() <= 0 || rawBytes.length <= 0){
			throw new IllegalArgumentException();
		}
		
		PutFile result = new PutFile();
		result.setSmartDeviceLinkFileName(fileName);
		result.setBulkData(rawBytes);
		result.setPersistentFile(persistent);
		result.setFileType(type);
		return result;
	}
	
	/**
	 * Creates a ReadDID request
	 * 
	 * @param ecu The ECU id.  Must be a valid ECU id value.
	 * @param did The DID id.  Must be a valid DID id value.
	 * @return The created ReadDid request
	 */
	public static RPCRequest readDid(int ecu, int did){
		Vector<Integer> dids = new Vector<Integer>(1);
		dids.add(did);
		return readDid(ecu, dids);
	}
	
	/**
	 * Creates a ReadDID request.
	 * 
	 * @param ecu The ECU id.  Must be a valid ECU id value.
	 * @param dids A vector of DID ids.  Cannot be null or empty and all values must be valid DID id values.
	 * @return The created ReadDID request
	 */
	public static RPCRequest readDid(int ecu, Vector<Integer> dids){
		if(dids == null){
			throw new NullPointerException();
		}
		
		if(ecu < SdlConstants.ReadDidsConstants.MINIMUM_ECU_ID || ecu > SdlConstants.ReadDidsConstants.MAXIMUM_ECU_ID  ||
		   dids.size() <= 0){
			throw new IllegalArgumentException();
		}
		
		for(Integer did : dids){
			if(did < SdlConstants.ReadDidsConstants.MINIMUM_DID_LOCATION || did > SdlConstants.ReadDidsConstants.MAXIMUM_DID_LOCATION){
				throw new IllegalArgumentException();
			}
		}
		
		ReadDID result = new ReadDID();
		result.setEcuName(ecu);
		result.setDidLocation(dids);
		return result;
	}
	
	/**
	 * Creates a ScrollableMessage request
	 * 
	 * @param msg The message to show on the head-unit.  Cannot be null and max length is 500 chars.
	 * @param timeoutInMs The timeout for the request in milliseconds.  Must be within timeout range for ScrollableMessage.
	 * @return The created ScrollableMessage request
	 */
	public static RPCRequest scrollableMessage(String msg, int timeoutInMs){
		if(msg == null){
			throw new NullPointerException();
		}
		if(msg.length() > SdlConstants.ScrollableMessageConstants.MESSAGE_LENGTH_MAX ||
		   timeoutInMs < MathUtils.convertSecsToMillisecs(SdlConstants.ScrollableMessageConstants.TIMEOUT_MINIMUM) || 
		   timeoutInMs > MathUtils.convertSecsToMillisecs(SdlConstants.ScrollableMessageConstants.TIMEOUT_MAXIMUM) ){
			throw new IllegalArgumentException();
		}
		
		ScrollableMessage result = new ScrollableMessage();
		result.setScrollableMessageBody(msg);
		result.setTimeout(timeoutInMs);
		return result;
	}
	
	/**
	 * Creates an Alert request
	 * 
	 * @param textToSpeak The text that should be spoken on the vehicle sound system
	 * @param line1 The first line in the alert
	 * @param line2 The second line in the alert
	 * @param line3 The third line in the alert
	 * @param playTone Whether or not to play a tone
	 * @param toneDuration The length time to show the alert.  Must be within timeout range for Alert.
	 * @return The created Alert request
	 */
	public static RPCRequest alert(String textToSpeak, String line1, String line2, String line3, boolean playTone, int toneDuration){
		if(toneDuration < MathUtils.convertSecsToMillisecs(SdlConstants.AlertConstants.ALERT_TIME_MINIMUM) ||
		    toneDuration > MathUtils.convertSecsToMillisecs(SdlConstants.AlertConstants.ALERT_TIME_MAXIMUM) ){
			throw new IllegalArgumentException();
		}
		
		Alert result = new Alert();
		if(textToSpeak != null && textToSpeak.length() > 0){
			result.setTtsChunks(SdlUtils.createTextToSpeechVector(textToSpeak));
		}
		if(line1 != null && line1.length() > 0){
			result.setAlertText1(line1);
		}
		if(line2 != null && line2.length() > 0){
			result.setAlertText2(line2);
		}
		if(line3 != null && line3.length() > 0){
			result.setAlertText3(line3);
		}
		
		result.setPlayTone(playTone);
		result.setDuration(toneDuration);
		
		return result;
	}
	
	/**
	 * Creates a SetAppIcon request.
	 * 
	 * @param iconName The name of the image to show as the app icon.  Cannot be null or empty.
	 * @return The created SetAppIcon request
	 */
	public static RPCRequest setAppIcon(String iconName){
		if(iconName == null){
			throw new NullPointerException();
		}
		if(iconName.length() <= 0){
			throw new IllegalArgumentException();
		}
		
		SetAppIcon result = new SetAppIcon();
		result.setSmartDeviceLinkFileName(iconName);
		return result;
	}
	
	/**
	 * Creates a SetMediaClockTimer request.
	 * 
	 * @param mode The UpdateMode for the clock timer.  Cannot be null.
	 * @param startTime The start time for the clock timer
	 * @return The created SetMediaClockTimer request
	 */
	public static RPCRequest setMediaClockTimer(UpdateMode mode, StartTime startTime){
		if(mode == null){
			throw new NullPointerException();
		}
		
		SetMediaClockTimer result = new SetMediaClockTimer();
		result.setUpdateMode(mode);
		if(startTime != null){
			result.setStartTime(startTime);
		}
		return result;
	}
	
	
	/**
	 * Creates a SetMediaClockTimer request.
	 * 
	 * @param mode The UpdateMode for the clock timer.  Cannot be null.
	 * @param hours The hours place of the clock
	 * @param minutes The minutes place of the clock
	 * @param seconds The seconds place of the clock
	 * @return The created SetMediaClockTimer request
	 */
	public static RPCRequest setMediaClockTimer(UpdateMode mode, int hours, int minutes, int seconds){
		return setMediaClockTimer(mode, SdlUtils.createStartTime(hours, minutes, seconds));
	}
	
	/**
	 * Creates a SetMediaClockTimer request.
	 * 
	 * @param mode The UpdateMode for the clock timer.  Cannot be null.
	 * @return
	 */
	public static RPCRequest setMediaClockTimer(UpdateMode mode){
		return setMediaClockTimer(mode, null);
	}
	
	/**
	 * Creates a Show request.
	 * 
	 * @param line1 The first line of text on the HMI
	 * @param line2 The second line of text on the HMI
	 * @param line3 The third line of text on the HMI
	 * @param line4 The fourth line of text on the HMI (not available for media apps)
	 * @param statusBar The text of the status bar
	 * @param alignment The TextAlignment of the text
	 * @param imageName The name of the image to show on the HMI
	 * @return The created Show request
	 */
	public static RPCRequest show(String line1, String line2, String line3, String line4, String statusBar, TextAlignment alignment, String imageName){
		Show result = new Show();
		if(line1 != null && line1.length() > 0){
			result.setMainField1(line1);
		}
		if(line2 != null && line2.length() > 0){
			result.setMainField2(line2);
		}
		if(line3 != null && line3.length() > 0){
			result.setMainField3(line3);
		}
		if(line4 != null && line4.length() > 0){
			result.setMainField4(line4);
		}
		if(statusBar != null && statusBar.length() > 0){
			result.setStatusBar(statusBar);
		}
		if(alignment != null){
			result.setAlignment(alignment);
		}
		if(imageName != null){
			Image image = SdlUtils.dynamicImage(imageName);
			result.setGraphic(image);
		}
		
		return result;
	}
	
	/**
	 * Creates a Slider request.
	 * 
	 * @param header The header/title text for the slider.  Cannot be null.
	 * @param footer The footer text for the slider
	 * @param numOfTicks The total number of ticks available in the slider.  Must be a valid ticks value.
	 * @param startPosition The initial position of the slider.  Must be a valid start position value.
	 * @param timeout The timeout of the slider.  Must be a valid slider timeout value.
	 * @return The created Slider request
	 */
	public static RPCRequest slider(String header, String footer, int numOfTicks, int startPosition, int timeout){
		if(header == null){
			throw new NullPointerException();
		}
		if(numOfTicks < SdlConstants.SliderConstants.NUM_OF_TICKS_MIN || numOfTicks > SdlConstants.SliderConstants.NUM_OF_TICKS_MAX ||
		   startPosition < SdlConstants.SliderConstants.START_POSITION_MIN || startPosition > numOfTicks ||
		   timeout < MathUtils.convertSecsToMillisecs(SdlConstants.SliderConstants.TIMEOUT_MIN) ||
		   timeout > MathUtils.convertSecsToMillisecs(SdlConstants.SliderConstants.TIMEOUT_MAX) ){
			throw new IllegalArgumentException();
		}
		
		Slider result = new Slider();
		result.setSliderHeader(header);
		result.setSliderFooter(SdlUtils.voiceRecognitionVector(footer));
		result.setNumTicks(numOfTicks);
		result.setPosition(startPosition);
		result.setTimeout(timeout);
		return result;
	}
	
	/**
	 * Creates a Speak request.
	 * 
	 * @param text The text to speak.  Cannot be null.
	 * @param speechCapabilities The type of input - either text, phoneme type or silence.  Cannot be null.
	 * @return The created Speak request
	 */
	public static RPCRequest speak(String text, SpeechCapabilities speechCapabilities){
		if(text == null || speechCapabilities == null){
			throw new NullPointerException();
		}
		
		Speak result = new Speak();
		result.setTtsChunks(SdlUtils.createTextToSpeechVector(text, speechCapabilities));
		return result;
	}
}

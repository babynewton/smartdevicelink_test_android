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

public final class SdlRequestFactory {
	
	public SdlRequestFactory() {}
	// TODO - comments

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
	
	public static RPCRequest addSubmenu(String submenuName, int position){
		if(submenuName == null){
			throw new NullPointerException();
		}
		
		AddSubMenu result = new AddSubMenu();
		result.setMenuName(submenuName);
		result.setPosition(position);
		return result;
	}
	
	public static RPCRequest subscribeButton(ButtonName name){
		if(name == null){
			throw new NullPointerException();
		}
		
		SubscribeButton result = new SubscribeButton();
		result.setButtonName(name);
		return result;
	}
	
	public static RPCRequest unsubscribeButton(ButtonName name){
		if(name == null){
			throw new NullPointerException();
		}
		
		UnsubscribeButton result = new UnsubscribeButton();
		result.setButtonName(name);
		return result;
	}
	
	public static RPCRequest changeRegistration(Language mainLang, Language hmiLang){
		if(mainLang == null){
			throw new NullPointerException();
		}
		if(hmiLang == null){
			throw new NullPointerException();
		}
		
		ChangeRegistration result = new ChangeRegistration();
		result.setLanguage(mainLang);
		result.setHmiDisplayLanguage(hmiLang);
		return result;
	}
	
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
	
	public static RPCRequest deleteCommand(int commandId){
		if(commandId < SdlConstants.AddCommandConstants.MINIMUM_COMMAND_ID ||
		   commandId > SdlConstants.AddCommandConstants.MAXIMUM_COMMAND_ID){
			throw new IllegalArgumentException();
		}
		
		DeleteCommand result = new DeleteCommand();
		result.setCmdID(commandId);
		return result;
	}
	
	public static RPCRequest deleteSubmenu(int menuId){
		DeleteSubMenu result = new DeleteSubMenu();
		result.setMenuID(menuId);
		return result;
	}
	
	public static RPCRequest deleteFile(String fileName){
		if(fileName == null){
			throw new NullPointerException();
		}
		
		DeleteFile result = new DeleteFile();
		result.setSmartDeviceLinkFileName(fileName);
		return result;
	}
	
	public static RPCRequest deleteInteractionChoiceSet(int id){
		if(id < SdlConstants.InteractionChoiceSetConstants.MINIMUM_CHOICE_SET_ID || id > SdlConstants.InteractionChoiceSetConstants.MAXIMUM_CHOICE_SET_ID){
			throw new IllegalArgumentException();
		}
		
		DeleteInteractionChoiceSet result = new DeleteInteractionChoiceSet();
		result.setInteractionChoiceSetID(id);
		return result;
	}
	
	public static RPCRequest getDtcs(int ecuId){
		if(ecuId < SdlConstants.GetDtcsConstants.MINIMUM_ECU_ID || ecuId > SdlConstants.GetDtcsConstants.MAXIMUM_ECU_ID){
			throw new IllegalArgumentException();
		}
		
		GetDTCs result = new GetDTCs();
		result.setEcuName(ecuId);
		return result;
	}
	
	public static RPCRequest getDtcs(String ecuId){
		try{
			return getDtcs(Integer.parseInt(ecuId));
		}
		catch(NumberFormatException e){
			throw new IllegalArgumentException();
		}
	}
	
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
	
	public static RPCRequest performInteraction(String title, String voicePrompt, Vector<Integer> choiceIds, InteractionMode mode){
		return performInteraction(title, voicePrompt, choiceIds, mode, SdlConstants.PerformInteractionConstants.INVALID_TIMEOUT);
	}
	
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
	
	public static RPCRequest readDid(int ecu, int did){
		Vector<Integer> dids = new Vector<Integer>(1);
		dids.add(did);
		return readDid(ecu, dids);
	}
	
	public static RPCRequest readDid(int ecu, Vector<Integer> dids){
		if(ecu < SdlConstants.ReadDidsConstants.MINIMUM_ECU_ID || ecu > SdlConstants.ReadDidsConstants.MAXIMUM_ECU_ID){
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
	
	public static RPCRequest setMediaClockTimer(UpdateMode mode, int hours, int minutes, int seconds){
		return setMediaClockTimer(mode, SdlUtils.createStartTime(hours, minutes, seconds));
	}
	
	public static RPCRequest setMediaClockTimer(UpdateMode mode){
		return setMediaClockTimer(mode, null);
	}
	
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
	
	public static RPCRequest speak(String text, SpeechCapabilities speechCapabilities){
		if(text == null || speechCapabilities == null){
			throw new NullPointerException();
		}
		
		Speak result = new Speak();
		result.setTtsChunks(SdlUtils.createTextToSpeechVector(text, speechCapabilities));
		return result;
	}
}

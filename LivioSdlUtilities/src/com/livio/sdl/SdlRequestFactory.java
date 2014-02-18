package com.livio.sdl;

import java.util.Vector;

import com.smartdevicelink.proxy.RPCRequest;
import com.smartdevicelink.proxy.rpc.AddCommand;
import com.smartdevicelink.proxy.rpc.AddSubMenu;
import com.smartdevicelink.proxy.rpc.ChangeRegistration;
import com.smartdevicelink.proxy.rpc.Choice;
import com.smartdevicelink.proxy.rpc.CreateInteractionChoiceSet;
import com.smartdevicelink.proxy.rpc.DeleteCommand;
import com.smartdevicelink.proxy.rpc.DeleteFile;
import com.smartdevicelink.proxy.rpc.DeleteInteractionChoiceSet;
import com.smartdevicelink.proxy.rpc.Image;
import com.smartdevicelink.proxy.rpc.MenuParams;
import com.smartdevicelink.proxy.rpc.SubscribeButton;
import com.smartdevicelink.proxy.rpc.UnsubscribeButton;
import com.smartdevicelink.proxy.rpc.enums.ButtonName;
import com.smartdevicelink.proxy.rpc.enums.ImageType;
import com.smartdevicelink.proxy.rpc.enums.Language;

public final class SdlRequestFactory {
	
	public SdlRequestFactory() {}
	
	// TODO - write factory methods to create different types of commands.  Most everything should return RPCRequest type
	// TODO - comments

	public static RPCRequest addCommand(String name, int position, int parentId, String vrCommands, String imageName){
		if(name == null){
			throw new NullPointerException();
		}
		if(name.length() <= 0){
			throw new IllegalArgumentException();
		}
		
		AddCommand result = new AddCommand();
		result.setMenuParams(menuParams(name, position, parentId));
		if(vrCommands != null && vrCommands.length() > 0){
			result.setVrCommands(voiceRecognitionVector(vrCommands));
		}
		if(imageName != null){
			result.setCmdIcon(dynamicImage(imageName));
		}
		return result;
	}
	
	public static MenuParams menuParams(String name, int position, int parentId){
		if(name == null){
			throw new NullPointerException();
		}
		if(name.length() <= 0){
			throw new IllegalArgumentException();
		}
		
		MenuParams result = new MenuParams();
		result.setMenuName(name);
		result.setPosition(position);
		
		if(parentId != SdlConstants.AddCommand.INVALID_PARENT_ID && parentId != SdlConstants.AddCommand.ROOT_PARENT_ID){
			result.setParentID(parentId);
		}
		
		return result;
	}
	
	public static Vector<String> voiceRecognitionVector(String input){
		// TODO parse the input string for multiple values
		Vector<String> result = new Vector<String>(1);
		result.add(input);
		return result;
	}
	
	public static Image dynamicImage(String imageName){
		if(imageName == null){
			throw new NullPointerException();
		}
		
		Image result = new Image();
		result.setImageType(ImageType.DYNAMIC);
		result.setValue(imageName);
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
	
	public static Choice choice(String name, String vrCommands, String imageName){
		if(name == null){
			throw new NullPointerException();
		}
		
		Choice choice = new Choice();
		choice.setMenuName(name);
		
		if(vrCommands != null && vrCommands.length() > 0){
			choice.setVrCommands(voiceRecognitionVector(vrCommands));
		}
		
		if(imageName != null){
			choice.setImage(dynamicImage(imageName));
		}
		
		return choice;
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
		if(commandId < SdlConstants.AddCommand.MINIMUM_COMMAND_ID){
			throw new IllegalArgumentException();
		}
		
		DeleteCommand result = new DeleteCommand();
		result.setCmdID(commandId);
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
		if(id < SdlConstants.InteractionChoiceSet.MINIMUM_CHOICE_SET_ID || id > SdlConstants.InteractionChoiceSet.MAXIMUM_CHOICE_SET_ID){
			throw new IllegalArgumentException();
		}
		
		DeleteInteractionChoiceSet result = new DeleteInteractionChoiceSet();
		result.setInteractionChoiceSetID(id);
		return result;
	}
}

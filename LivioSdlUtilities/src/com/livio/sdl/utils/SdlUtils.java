package com.livio.sdl.utils;

import java.util.Vector;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;

import com.livio.sdl.IpAddress;
import com.livio.sdl.R;
import com.livio.sdl.SdlConstants;
import com.smartdevicelink.exception.SmartDeviceLinkException;
import com.smartdevicelink.proxy.RPCMessage;
import com.smartdevicelink.proxy.SmartDeviceLinkProxyALM;
import com.smartdevicelink.proxy.TTSChunkFactory;
import com.smartdevicelink.proxy.interfaces.IProxyListenerALM;
import com.smartdevicelink.proxy.rpc.Choice;
import com.smartdevicelink.proxy.rpc.Image;
import com.smartdevicelink.proxy.rpc.MenuParams;
import com.smartdevicelink.proxy.rpc.StartTime;
import com.smartdevicelink.proxy.rpc.TTSChunk;
import com.smartdevicelink.proxy.rpc.enums.FileType;
import com.smartdevicelink.proxy.rpc.enums.ImageType;
import com.smartdevicelink.proxy.rpc.enums.SpeechCapabilities;
import com.smartdevicelink.transport.TCPTransportConfig;

/**
 * Contains static methods that are useful in working with SmartDeviceLink.
 *
 * @author Mike Burke
 *
 */
public final class SdlUtils {
	private static final int NUMBER_OF_INDENTS = 4;
	
	private SdlUtils(){}

	/**
	 * Creates and returns the raw JSON string associated with the
	 * input RPC message object.
	 * 
	 * @param msg The message to retrieve raw JSON for
	 * @return The raw JSON string
	 */
	public static String getJsonString(RPCMessage msg){
		return getJsonString(msg, NUMBER_OF_INDENTS);
	}
	
	/**
	 * Creates and returns the raw JSON string associated with the input
	 * RPC message object.  Allows a custom number of indent spaces.
	 * 
	 * @param msg The message to retrieve raw JSON for
	 * @param numOfIndents Number of indents to be used in raw JSON
	 * @return The raw JSON string
	 */
	public static String getJsonString(RPCMessage msg, int numOfIndents){
		String result = "";
		try {
			JSONObject json = msg.serializeJSON();
			result = json.toString(numOfIndents);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * Creates a JSON title that can be used in JSON dialogs.  Input correlation ID
	 * is allowed to be null or -1 if there is no correlation ID associated with the
	 * particular message.
	 * 
	 * @param correlationId The associated correlation ID for the message
	 * @return The JSON title with associated correlation ID.
	 */
	public static String makeJsonTitle(Integer correlationId){
		if(correlationId == null || correlationId == -1){
			return "Raw JSON";
		}
		
		return new StringBuilder().append("Raw JSON (").append(correlationId).append(")").toString();
	}
	
	/**
	 * Converts an SDL file type to its associated CompressFormat.
	 * 
	 * @param type The file type to convert
	 * @return The associated CompressFormat
	 */
	public static CompressFormat convertImageTypeToCompressFormat(FileType type){
		switch(type){
		case GRAPHIC_JPEG:
			return CompressFormat.JPEG;
		case GRAPHIC_PNG:
			return CompressFormat.PNG;
		case GRAPHIC_BMP:
			return null; // TODO what's the compression format for a bitmap object?
		default:
			return null;
		}
	}
	
	/**
	 * Converts the input bitmap to a byte array based on the input File Type.
	 * 
	 * @param image The image to convert
	 * @param type The file type of the image
	 * @return The byte array of the input bitmap
	 */
	public static byte[] bitmapToByteArray(Bitmap image, FileType type){
		CompressFormat format = convertImageTypeToCompressFormat(type);
		byte[] bitmapData = AndroidUtils.bitmapToRawBytes(image, format);
		return bitmapData;
	}

	/**
	 * Creates a Choice object to be used in Choice Interaction Sets.
	 * 
	 * @param name The name of the choice
	 * @param vrCommands CSV list of voice-rec options
	 * @param imageName Image name of any associated image
	 * @return The created Choice object
	 */
	public static Choice createChoice(String name, String vrCommands, String imageName){
		if(name == null){
			throw new NullPointerException();
		}
		
		Choice choice = new Choice();
		choice.setMenuName(name);
		
		if(vrCommands != null){
			choice.setVrCommands(voiceRecognitionVector(vrCommands));
		}
		
		if(imageName != null){
			choice.setImage(dynamicImage(imageName));
		}
		
		return choice;
	}
	
	/**
	 * Creates a vector of voice-rec commands based on a CSV input string.
	 * 
	 * @param input CSV list of voice-rec options
	 * @return A vector of voice-rec strings
	 */
	public static Vector<String> voiceRecognitionVector(String input){
		// TODO support CSV input string
		Vector<String> result = new Vector<String>(1);
		result.add(input);
		return result;
	}
	
	/**
	 * Creates a dynamic image object with the input image name.
	 * 
	 * @param imageName The name of the image this object represents
	 * @return The created Image object
	 */
	public static Image dynamicImage(String imageName){
		if(imageName == null){
			throw new NullPointerException();
		}
		
		Image result = new Image();
		result.setImageType(ImageType.DYNAMIC);
		result.setValue(imageName);
		return result;
	}
	
	/**
	 * Creates a MenuParams object that can be used in other SDL requests, such as AddCommand.
	 * If there is no parent id, send -1 as the parent id.
	 * 
	 * @param name The name of the menu item
	 * @param position The position of the menu item
	 * @param parentId The item's parent id
	 * @return The created MenuParams object
	 */
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
		
		if(parentId != SdlConstants.AddCommandConstants.INVALID_PARENT_ID &&
		   parentId != SdlConstants.AddCommandConstants.ROOT_PARENT_ID){
			result.setParentID(parentId);
		}
		
		return result;
	}
	
	/**
	 * Creates a vector of text-to-speech "chunks" that will be used to speak the input
	 * message through the vehicle's speaker system.  This method assumes the input format
	 * is a normal text string (as opposed to some type of phonemes).
	 * 
	 * @param input The text to create "chunks" from
	 * @return The created vector of TTSChunk objects
	 */
	public static Vector<TTSChunk> createTextToSpeechVector(String input){
		return TTSChunkFactory.createSimpleTTSChunks(input); // TODO support CSV input string
	}
	
	/**
	 * Creates a vector of text-to-speech "chunks" that will be used to speak the input
	 * message through the vehicle's speaker system.  This method requires an input parameter
	 * detailing the format of the input string.
	 * 
	 * @param input The text to create "chunks" from
	 * @param speechCapabilities The format of the input string
	 * @return The created vector of TTSChunk objects
	 */
	public static Vector<TTSChunk> createTextToSpeechVector(String input, SpeechCapabilities speechCapabilities){
		Vector<TTSChunk> result = new Vector<TTSChunk>(1); // TODO support CSV input string
		result.add(TTSChunkFactory.createChunk(speechCapabilities, input));
		return result;
	}
	
	/**
	 * Creates a StartTime object from the input time in hours, minutes & seconds.
	 * 
	 * @param hours Hours value for StartTime object
	 * @param minutes Minutes value for StartTime object
	 * @param seconds Seconds value for the StartTime object
	 * @return The created StartTime object
	 */
	public static StartTime createStartTime(int hours, int minutes, int seconds){
		if(hours < SdlConstants.SetMediaClockTimerConstants.HOURS_MINIMUM || hours > SdlConstants.SetMediaClockTimerConstants.HOURS_MAXIMUM ||
		   minutes < SdlConstants.SetMediaClockTimerConstants.MINUTES_MINIMUM || minutes > SdlConstants.SetMediaClockTimerConstants.MINUTES_MAXIMUM ||
		   seconds < SdlConstants.SetMediaClockTimerConstants.SECONDS_MINIMUM || seconds > SdlConstants.SetMediaClockTimerConstants.SECONDS_MAXIMUM ){
			throw new IllegalArgumentException();
		}
		
		StartTime result = new StartTime();
		result.setHours(hours);
		result.setMinutes(minutes);
		result.setSeconds(seconds);
		return result;
	}

}

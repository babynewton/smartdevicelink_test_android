package com.livio.sdl.utils;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap.CompressFormat;

import com.smartdevicelink.proxy.RPCMessage;
import com.smartdevicelink.proxy.rpc.enums.FileType;

/**
 * Contains static methods that are useful in working with SmartDeviceLink.
 *
 * @author Mike Burke
 *
 */
public abstract class SdlUtils {
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

}

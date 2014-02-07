package com.livio.sdl.utils;

import org.json.JSONException;
import org.json.JSONObject;

import com.smartdevicelink.proxy.RPCMessage;

public class SdlUtils {
	private static final int NUMBER_OF_INDENTS = 4;

	public static String getJsonString(RPCMessage msg){
		return getJsonString(msg, NUMBER_OF_INDENTS);
	}
	
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
	
	public static String makeJsonTitle(Integer correlationId){
		if(correlationId == null || correlationId == -1){
			return "Raw JSON";
		}
		
		return new StringBuilder().append("Raw JSON (").append(correlationId).append(")").toString();
	}

}

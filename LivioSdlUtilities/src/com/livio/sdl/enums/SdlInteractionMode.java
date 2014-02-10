package com.livio.sdl.enums;

import com.smartdevicelink.proxy.rpc.enums.InteractionMode;

public enum SdlInteractionMode {

	MANUAL_ONLY("Click events"),
	VOICE_REC_ONLY("Voice-rec events"),
	BOTH("Both"),
	;
	
	private final String friendlyName;
	
	private SdlInteractionMode(String str){
		this.friendlyName = str;
	}
	
	@Override
	public String toString(){
		return friendlyName;
	}
	
	public static InteractionMode translateToLegacy(SdlInteractionMode from){
		InteractionMode to = null;
		
		if(from == MANUAL_ONLY){
			to = InteractionMode.MANUAL_ONLY;
		}
		else if(from == VOICE_REC_ONLY){
			to = InteractionMode.VR_ONLY;
		}
		else if(from == BOTH){
			to = InteractionMode.BOTH;
		}
		
		return to;
	}
	
	public static SdlInteractionMode translateFromLegacy(InteractionMode from){
		SdlInteractionMode to = null;
		
		if(from == InteractionMode.MANUAL_ONLY){
			to = MANUAL_ONLY;
		}
		else if(from == InteractionMode.VR_ONLY){
			to = VOICE_REC_ONLY;
		}
		else if(from == InteractionMode.BOTH){
			to = BOTH;
		}
		
		return to;
	}
}

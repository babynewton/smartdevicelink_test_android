package com.livio.sdl;

import android.graphics.Bitmap;

import com.livio.sdl.enums.SdlImageType;

/**
 * Represents a selection in an "Interaction Choice Set" command.  This object
 * can be created with simply a name and a voice-rec keyword, or a more complex
 * command including an image & image type.
 *
 * @author Mike Burke
 *
 */
public class SdlChoiceSetItem {

	private String name, voiceRec;
	private SdlImageType imageType;
	private Bitmap image;
	
	/**
	 * Creates a simple SdlChoiceSetItem with a name & voice-rec keyword. 
	 * @param name The name of the selection.
	 * @param voiceRec A voice-rec keyword that will select the item when a user speaks it.
	 */
	public SdlChoiceSetItem(String name, String voiceRec){
		this.name = name;
		this.voiceRec = voiceRec;
	}
	
	/**
	 * Creates a complex SdlChoiceSetItem with a name, voice-rec keyword & image.
	 * @param name The name of the selection.
	 * @param voiceRec A voice-rec keyword that will select the item when a user speaks it.
	 * @param imageType Enum value representing either a static or dynamic image.
	 * @param image The image to show along with the command's name in the menu.
	 */
	public SdlChoiceSetItem(String name, String voiceRec, SdlImageType imageType, Bitmap image){
		this(name, voiceRec);
		this.imageType = imageType;
		this.image = image;
	}

	public String getName() {
		return name;
	}

	public String getVoiceRec() {
		return voiceRec;
	}

	public SdlImageType getImageType() {
		return imageType;
	}

	public Bitmap getImage() {
		return image;
	}
	
}

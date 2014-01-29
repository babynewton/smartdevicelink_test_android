package com.livio.sdl;

import android.graphics.Bitmap;



public class SdlShow {

	// TODO - add more stuff here
	private String line1, line2, line3;
	private Bitmap image;
	
	@SuppressWarnings("unused")
	private SdlShow(){}
	
	/**
	 * Creates a simple SdlShow object with 2 lines of metadata.
	 * @param line1 Metadata line 1
	 * @param line2 Metadata line 2
	 */
	public SdlShow(String line1, String line2){
		this.line1 = line1;
		this.line2 = line2;
	}
	
	/**
	 * Creates an SdlShow object with 3 lines of metadata.
	 * @param line1 Metadata line 1
	 * @param line2 Metadata line 2
	 * @param line3 Metadata line 3
	 */
	public SdlShow(String line1, String line2, String line3){
		this.line1 = line1;
		this.line2 = line2;
		this.line3 = line3;
	}
	
	/**
	 * Creates an SdlShow object with 2 lines of metadata and a primary artwork image.
	 * @param line1 Metadata line 1
	 * @param line2 Metadata line 2
	 * @param image The image to show as primary artwork
	 */
	public SdlShow(String line1, String line2, Bitmap image){
		this(line1, line2);
		this.image = image;
	}

	public String getLine1() {
		return line1;
	}

	public String getLine2() {
		return line2;
	}
	
	public String getLine3(){
		return line3;
	}
	
	public Bitmap getImage(){
		return image;
	}
	
	/**
	 * Determines if this SdlShow object contains an image or not.
	 * @return True if an image is part of this object, false otherwise
	 */
	public boolean hasImage(){
		return (image != null);
	}
	
	@Override
	public String toString(){
		return new StringBuilder().append("Line1: ").append(line1).append("\n")
								  .append("Line2: ").append(line2)
								  .toString();
	}
	
}

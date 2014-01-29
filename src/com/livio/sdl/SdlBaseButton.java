package com.livio.sdl;

import android.graphics.Bitmap;

/**
 * Represents a function button - either a menu or a command - that can be shown and selected
 * on the head-unit through SDL.
 *
 * @author Mike Burke
 *
 */
public class SdlBaseButton {

	/**
	 * An interface representing an on-click event callback for SdlFunctionButton clicks.
	 *
	 * @author Mike Burke
	 *
	 */
	public interface OnClickListener{
		void onClick(int parent, int id);
	}
	
	private String name;
	private int id;
	private boolean isMenuButton;
	private Bitmap image;
	private OnClickListener clickListener;
	
	/**
	 * Creates a simple SdlFunctionButton object with name & id.
	 * @param name The name of the button
	 * @param id The id of the button
	 * @param isMenuButton Whether the button is a menu or not
	 */
	public SdlBaseButton(String name, int id, boolean isMenuButton){
		this.name = name;
		this.id = id;
		this.isMenuButton = isMenuButton;
	}
	
	/**
	 * Creates an SdlFunctionButton object with name, id & image.
	 * @param name The name of the button
	 * @param id The id of the button
	 * @param isMenuButton Whether the button is a menu or not
	 * @param image The image to show on the button
	 */
	public SdlBaseButton(String name, int id, boolean isMenuButton, Bitmap image){
		this(name, id, isMenuButton);
		this.image = image;
	}
	
	/**
	 * Creates an SdlFunctionButton object with name, id & click event listener.
	 * @param name The name of the button
	 * @param id The id of the button
	 * @param isMenuButton Whether the button is a menu or not
	 * @param listener A listener that should be called when the button is clicked
	 */
	public SdlBaseButton(String name, int id, boolean isMenuButton, OnClickListener listener){
		this(name, id, isMenuButton);
		this.clickListener = listener;
	}
	
	/**
	 * Creates an SdlFunctionButton object with name, id, image & click event listener.
	 * @param name The name of the button
	 * @param id The id of the button
	 * @param isMenuButton Whether the button is a menu or not
	 * @param image The image to show on the button
	 * @param listener A listener that should be called when the button is clicked
	 */
	public SdlBaseButton(String name, int id, boolean isMenuButton, Bitmap image, OnClickListener listener){
		this(name, id, isMenuButton, image);
		this.clickListener = listener;
	}
	
	/**
	 * Dispatches a click event to the listener for this button.
	 * @param parent The id of the function bank that is holding this button
	 * @param id The id of this button
	 */
	public void click(int parent, int id){
		if(clickListener != null){
			clickListener.onClick(parent, id);
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public Bitmap getImage() {
		return image;
	}

	public void setImage(Bitmap image) {
		this.image = image;
	}

	public void setOnClickListener(OnClickListener onClick) {
		this.clickListener = onClick;
	}

	public boolean isMenuButton() {
		return isMenuButton;
	}

	public void setMenuButton(boolean isMenuButton) {
		this.isMenuButton = isMenuButton;
	}

	@Override
	public String toString(){
		return new StringBuilder().append(name)
				                  .append(" (").append(id).append(")")
				                  .toString();
	}
	
}

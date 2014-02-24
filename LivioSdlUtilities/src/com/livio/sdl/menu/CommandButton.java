package com.livio.sdl.menu;

/**
 * Represents a command button that can be clicked on the SDL-connected head-unit.  A command
 * button contains all the fields contained in the MenuItem parent class, as well as a parent id,
 * a string representing an image on the head-unit and an OnClickListener.
 *
 * @author Mike Burke
 *
 */
public class CommandButton extends MenuItem {

	/**
	 * An interface that defines a click event listener for a command button.
	 *
	 * @author Mike Burke
	 *
	 */
	public interface OnClickListener{
		void onClick(CommandButton button);
	}
	
	private int parentId = -1;
	private OnClickListener listener;
	private String imageName;
	
	public CommandButton(CommandButton copy){
		super(copy.getName(), copy.getId(), false);
		this.parentId = copy.getParentId();
		this.listener = copy.getOnClickListener();
		this.imageName = copy.getImageName();
	}
	
	public CommandButton(String name, int id) {
		super(name, id, false);
	}
	
	public CommandButton(String name, int id, int parentId) {
		super(name, id, false);
		this.parentId = parentId;
	}
	
	public CommandButton(String name, int id, OnClickListener listener) {
		super(name, id, false);
		this.listener = listener;
	}
	
	public CommandButton(String name, int id, int parentId, OnClickListener listener) {
		this(name, id, parentId, null, listener);
	}
	
	public CommandButton(String name, int id, String imageName) {
		super(name, id, false);
		this.imageName = imageName;
	}
	
	public CommandButton(String name, int id, int parentId, String imageName) {
		this(name, id, parentId, imageName, null);
	}
	
	public CommandButton(String name, int id, int parentId, String imageName, OnClickListener listener) {
		super(name, id, false);
		this.parentId = parentId;
		this.imageName = imageName;
		this.listener = listener;
	}

	public int getParentId() {
		return parentId;
	}

	public OnClickListener getOnClickListener() {
		return listener;
	}

	public String getImageName() {
		return imageName;
	}
	
	/**
	 * Executes the code in the click listener, if it exists.
	 */
	public void dispatchClickEvent(){
		if(listener != null){
			listener.onClick(this);
		}
	}

}

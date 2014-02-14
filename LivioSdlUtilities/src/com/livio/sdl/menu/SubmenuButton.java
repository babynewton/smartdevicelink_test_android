package com.livio.sdl.menu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a submenu button that can be clicked on the SDL-connected head-unit.  A submenu
 * contains all the fields contained in the MenuItem parent class, as well as maintaining a list
 * of children commands.
 *
 * @author Mike Burke
 *
 */
public class SubmenuButton extends MenuItem {

	private List<MenuItem> children;
	
	public SubmenuButton(SubmenuButton copy){
		super(copy.getName(), copy.getId(), true);
		copyChildren(copy.getChildren());
	}
	
	public SubmenuButton(String name, int id) {
		super(name, id, true);
	}
	
	/**
	 * Creates a copy of the children to ensure there are no leaked references to the
	 * children of the object we're copying.
	 * 
	 * @param children
	 */
	private void copyChildren(List<MenuItem> children){
		if(children == null || children.size() <= 0){
			return;
		}
		
		if(this.children == null){
			this.children = new ArrayList<MenuItem>(children.size());
		}
		
		for(MenuItem child : children){
			if(child.isMenu()){
				this.children.add(new SubmenuButton((SubmenuButton) child));
			}
			else{
				this.children.add(new CommandButton((CommandButton) child));
			}
		}
	}
	
	/**
	 * Returns a copy of the list of all children associated with this menu item.  If there
	 * are no children associated with this menu item, this method will return an empty list.
	 * 
	 * @return A copy of the list of children
	 */
	public List<MenuItem> getChildren(){
		if(children == null || children.size() <= 0){
			return Collections.emptyList();
		}
		
		return new ArrayList<MenuItem>(children);
	}
	
	/**
	 * Adds a child to this submenu object.
	 * 
	 * @param item The item to add
	 */
	public void addChild(MenuItem item){
		if(children == null){
			children = new ArrayList<MenuItem>();
		}
		
		children.add(item);
	}
	
	/**
	 * Removes a child from this submenu object.
	 * 
	 * @param childId The id of the child to remove
	 */
	public void removeChild(int childId){
		if(children == null || children.size() <= 0){
			return;
		}
		
		for(MenuItem child : children){
			if(childId == child.getId()){
				children.remove(child);
				return;
			}
		}
	}
	
	/**
	 * Removes all children from this submenu object.
	 */
	public void removeAllChildren(){
		if(children == null || children.size() <= 0){
			return;
		}
		
		children.clear();
	}

}

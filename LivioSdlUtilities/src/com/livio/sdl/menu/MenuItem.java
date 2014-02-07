package com.livio.sdl.menu;

import java.util.Comparator;

/**
 * Represents a generic menu item - either a submenu or a command.  All menu items
 * have similar fields, for example, a name and an id.
 * 
 * @see CommandButton, SubmenuButton
 *
 * @author Mike Burke
 *
 */
public class MenuItem {
	/**
	 * Comparator for sorting MenuItem objects based on their id.
	 *
	 * @author Mike Burke
	 *
	 */
	public static class IdComparator implements Comparator<MenuItem>{
		@Override
		public int compare(MenuItem lhs, MenuItem rhs) {
			final int lId = lhs.getId();
			final int rId = rhs.getId();
			
			if(lId > rId){
				return 1;
			}
			else if(lId < rId){
				return -1;
			}
			else{
				return 0;
			}
		}
	}
	
	/**
	 * Comparator for sorting MenuItem objects based on their name.
	 *
	 * @author Mike Burke
	 *
	 */
	public static class NameComparator implements Comparator<MenuItem> {
		@Override
		public int compare(MenuItem lhs, MenuItem rhs) {
			return lhs.getName().compareTo(rhs.getName());
		}
	}
	
	private String name;
	private int id;
	private boolean isMenu;
	
	public MenuItem(MenuItem copy){
		this.name = copy.getName();
		this.id = copy.getId();
		this.isMenu = copy.isMenu();
	}
	
	public MenuItem(String name, int id, boolean isMenu) {
		this.name = name;
		this.id = id;
		this.isMenu = isMenu;
	}

	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}

	public boolean isMenu() {
		return isMenu;
	}
	
	@Override
	public String toString(){
		return new StringBuilder().append(name).append(" (").append(id).append(")").toString();
	}

}

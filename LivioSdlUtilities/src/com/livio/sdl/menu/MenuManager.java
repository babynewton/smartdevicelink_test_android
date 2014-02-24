package com.livio.sdl.menu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import android.util.Log;
import android.util.SparseArray;

/**
 * Manages SDL menu items (commands and submenus), keeping an up-to-date list of available
 * menu items.
 *
 * @author Mike Burke
 *
 */
public class MenuManager {
	/**
	 * Allows iterating over the items in a menu manager object.
	 *
	 * @author Mike Burke
	 *
	 */
	public static class MenuIterator implements Iterator<MenuItem>{

		private final MenuManager items;
		private int currentIndex = 0;
		
		private MenuIterator(MenuManager items){
			this.items = items;
		}
		
		@Override
		public boolean hasNext() {
			return (currentIndex < (items.size()));
		}

		@Override
		public MenuItem next() {
			return items.getItemAt(currentIndex++);
		}

		@Override
		public void remove() {
			// don't allow removing items through the iterator
			throw new UnsupportedOperationException();
		}
	}

	private static boolean debug = false;
	private SparseArray<MenuItem> menuItems;
	
	// constructors
	public MenuManager() {
		menuItems = new SparseArray<MenuItem>();
	}
	
	public MenuManager(int startSize){
		menuItems = new SparseArray<MenuItem>(startSize);
	}
	
	/**
	 * Adds the input menu item to the list.  If the input item is the child of a submenu,
	 * also adds the item to the parent's list of children.
	 * 
	 * @param item The item to add
	 */
	public void addItem(MenuItem item){
		log(new StringBuilder().append("Adding item: ").append(item.toString()).toString());
		menuItems.put(item.getId(), item);
		
		// if not a top-level menu, let's check if it needs to be added to a submenu as well
		if(!item.isMenu()){
			CommandButton button = (CommandButton) item;
			int parentId = button.getParentId();
			if(parentId != -1){
				// button has a valid parent.  let's find it and add it to the list
				addCommandToParent(button, parentId);
			}
		}
	}
	
	/**
	 * Adds the input command button to the parent represented by the parent id.
	 * 
	 * @param button The button to add
	 * @param parentId The id of the button's parent
	 */
	private void addCommandToParent(CommandButton button, int parentId){
		MenuItem parent = menuItems.get(parentId);
		log(new StringBuilder().append("Adding command: ").append(button.toString()).append(" to parent: ").append(parent.toString()).toString());
		
		if(parent.isMenu()){
			SubmenuButton parentButton = (SubmenuButton) parent;
			parentButton.addChild(button);
		}
	}
	
	/**
	 * Removes the item with the input id.  If the item to remove is a submenu which contains children,
	 * this method removes the children commands from the menu manager as well.  If the item to remove is
	 * a command which belongs to a registered submenu, this method will also remove the child from its
	 * parent's list of children.
	 * 
	 * @param id The id of the item to remove
	 */
	public void removeItem(int id){
		MenuItem itemToRemove = menuItems.get(id);
		if(itemToRemove == null){
			return;
		}

		log(new StringBuilder().append("Removing item: ").append(itemToRemove.toString()).toString());
		
		if(!itemToRemove.isMenu()){
			// command button
			final int parentId = ((CommandButton)itemToRemove).getParentId(); 
			if(parentId != -1){
				// we have a command button with a valid parent - let's remove it from the parent list
				SubmenuButton parent = (SubmenuButton) menuItems.get(parentId);
				if(parent != null){
					log(new StringBuilder().append("Removing child: ").append(itemToRemove.toString()).append(" from parent: ").append(parent.toString()).toString());
					parent.removeChild(itemToRemove.getId());
				}
			}
		}
		else{
			// submenu button is being deleted - remove all children as well
			removeChildren((SubmenuButton) itemToRemove);
		}
		menuItems.remove(id);
	}
	
	/**
	 * Removes any children that belong to the input submenu.
	 * 
	 * @param parent The parent whose children should be removed
	 */
	private void removeChildren(SubmenuButton parent){
		List<MenuItem> children = parent.getChildren();
		if(children != null && children.size() > 0){
			for(MenuItem child : children){
				removeItem(child.getId());
			}
		}
	}
	
	/**
	 * Makes a copy of all registered submenus and returns it.
	 * 
	 * @return A list of all registered submenus
	 */
	public List<MenuItem> getSubmenus(){
		if(size() == 0){
			return Collections.emptyList();
		}
		
		log("Making a copy of all submenus");
		
		List<MenuItem> result = new ArrayList<MenuItem>();
		
		// iterate through all items
		Iterator<MenuItem> iterator = iterator();
		while(iterator.hasNext()){
			MenuItem current = iterator.next();
			if(current.isMenu()){
				// if this item is a submenu, make a copy of the item and add it to the result list
				result.add(new SubmenuButton((SubmenuButton) current));
			}
		}
		
		return result;
	}

	/**
	 * Makes a copy of all registered commands and returns it.
	 * 
	 * @return A list of all registered commands
	 */
	public List<MenuItem> getCommands(){
		if(size() == 0){
			return Collections.emptyList();
		}
		
		log("Making a copy of all commands");
		
		List<MenuItem> result = new ArrayList<MenuItem>();
		
		// iterate through all items
		Iterator<MenuItem> iterator = iterator();
		while(iterator.hasNext()){
			MenuItem current = iterator.next();
			if(!current.isMenu()){
				// if this item is a command, make a copy of the item and add it to the result list
				result.add(new CommandButton((CommandButton) current));
			}
		}
		
		return result;
	}
	
	/**
	 * Makes a copy of all items and returns it.
	 * 
	 * @return A list of all registered menu items
	 */
	public List<MenuItem> getAllItems(){
		if(size() == 0){
			return Collections.emptyList();
		}
		
		log("Making a copy of all menu items");
		
		List<MenuItem> result = new ArrayList<MenuItem>(size());
		
		// iterate through all items
		Iterator<MenuItem> iterator = iterator();
		while(iterator.hasNext()){
			MenuItem current = iterator.next();

			// make a copy of the item and add it to the list
			if(current.isMenu()){
				result.add(new SubmenuButton((SubmenuButton) current));
			}
			else{
				result.add(new CommandButton((CommandButton) current));
			}
		}
		
		return result;
	}
	
	/**
	 * Returns the item with the given id.
	 * 
	 * @param id The id of the item to return
	 * @return The item with the input id, or null if the id doesn't exist
	 */
	public MenuItem get(int id){
		return menuItems.get(id);
	}
	
	/**
	 * Returns the item at the input index.
	 * 
	 * @param index The index of the item to return
	 * @return The item at the given index
	 */
	public MenuItem getItemAt(int index){
		return menuItems.valueAt(index);
	}
	
	/**
	 * Returns the item with the given name.
	 * 
	 * @param name The name of the item to return
	 * @return The item with the given name, or null if the name doesn't exist
	 */
	public MenuItem get(String name){
		// iterate through all items
		Iterator<MenuItem> iterator = iterator();
		while(iterator.hasNext()){
			MenuItem current = iterator.next();

			// if the current item matches the input name, return it
			if(name.equals(current.getName())){
				return current;
			}
		}
		
		return null;
	}
	
	/**
	 * Returns the number of items being managed by the menu manager.
	 * 
	 * @return The number of items being managed by the menu manager
	 */
	public int size(){
		return menuItems.size();
	}
	
	/**
	 * Removes all menu items from the menu manager.
	 */
	public void clear(){
		menuItems.clear();
	}
	
	/**
	 * Dispatches a click event to the button with the input id.  If the button with the
	 * input id isn't found in the menu manager, this method does nothing.
	 * 
	 * @param buttonId The id of the button that was clicked
	 */
	public void dispatchClick(int buttonId){
		MenuItem item = menuItems.get(buttonId);
		
		if(item == null || item.isMenu()){
			log("Button with selected ID hasn't been added to this menu manager.");
		}
		else{
			CommandButton itemCmd = (CommandButton) item;
			itemCmd.dispatchClickEvent();
		}
	}
	
	/**
	 * Creates a new iterator object for this MenuManager object.
	 * 
	 * @return The new iterator instance
	 */
	public Iterator<MenuItem> iterator(){
		log("Creating new iterator object");
		Iterator<MenuItem> iterator = new MenuIterator(this);
		return iterator;
	}

	/**
	 * Enables or disables debug mode for log messages.
	 * 
	 * @param enable True to enable debug logs, false to disable
	 */
	public static void setDebug(boolean enable){
		debug = enable;
	}
	
	private static void log(String msg){
		if(debug){
			Log.d("MenuManager", msg);
		}
	}
}

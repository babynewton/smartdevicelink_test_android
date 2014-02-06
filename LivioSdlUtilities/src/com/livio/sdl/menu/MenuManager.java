package com.livio.sdl.menu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import android.util.Log;
import android.util.SparseArray;

public class MenuManager {
	
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
	
	public MenuManager() {
		menuItems = new SparseArray<MenuItem>();
	}
	
	public MenuManager(int startSize){
		menuItems = new SparseArray<MenuItem>(startSize);
	}
	
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
	
	private void addCommandToParent(CommandButton button, int parentId){
		MenuItem parent = menuItems.get(parentId);
		log(new StringBuilder().append("Adding command: ").append(button.toString()).append(" to parent: ").append(parent.toString()).toString());
		if(parent.isMenu()){
			SubmenuButton parentButton = (SubmenuButton) parent;
			parentButton.addChild(button);
		}
	}
	
	public void removeItemAt(int index){
		if(index < 0 || index >= menuItems.size()){
			throw new ArrayIndexOutOfBoundsException();
		}
		
		int i=0;
		Iterator<MenuItem> iterator = iterator();
		while(iterator.hasNext()){
			MenuItem current = iterator.next();
			if(i == index){
				removeItem(current.getId());
				return;
			}
			i++;
		}
	}
	
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
	
	private void removeChildren(SubmenuButton parent){
		List<MenuItem> children = parent.getChildren();
		if(children != null && children.size() > 0){
			for(MenuItem child : children){
				removeItem(child.getId());
			}
		}
	}
	
	public List<MenuItem> getSubmenus(){
		if(size() == 0){
			return Collections.emptyList();
		}
		
		log("Making a copy of all submenus");
		
		List<MenuItem> result = new ArrayList<MenuItem>();
		Iterator<MenuItem> iterator = iterator();
		while(iterator.hasNext()){
			MenuItem current = iterator.next();
			if(current.isMenu()){
				result.add(new SubmenuButton((SubmenuButton) current));
			}
		}
		
		return result;
	}
	
	public List<MenuItem> getCommands(){
		if(size() == 0){
			return Collections.emptyList();
		}
		
		log("Making a copy of all commands");
		
		List<MenuItem> result = new ArrayList<MenuItem>();
		Iterator<MenuItem> iterator = iterator();
		while(iterator.hasNext()){
			MenuItem current = iterator.next();
			if(!current.isMenu()){
				result.add(new CommandButton((CommandButton) current));
			}
		}
		
		return result;
	}
	
	public List<MenuItem> getAllItems(){
		if(size() == 0){
			return Collections.emptyList();
		}
		
		log("Making a copy of all menu items");
		
		List<MenuItem> result = new ArrayList<MenuItem>(size());
		Iterator<MenuItem> iterator = iterator();
		while(iterator.hasNext()){
			MenuItem current = iterator.next();
			if(current.isMenu()){
				result.add(new SubmenuButton((SubmenuButton) current));
			}
			else{
				result.add(new CommandButton((CommandButton) current));
			}
		}
		
		return result;
	}
	
	public MenuItem get(int id){
		return menuItems.get(id);
	}
	
	public MenuItem getItemAt(int index){
		return menuItems.valueAt(index);
	}
	
	public MenuItem get(String name){
		Iterator<MenuItem> iterator = iterator();
		while(iterator.hasNext()){
			MenuItem current = iterator.next();
			if(name.equals(current.getName())){
				return current;
			}
		}
		
		return null;
	}
	
	public int size(){
		return menuItems.size();
	}
	
	public Iterator<MenuItem> iterator(){
		log("Creating new iterator object");
		Iterator<MenuItem> iterator = new MenuIterator(this);
		return iterator;
	}

	public static void setDebug(boolean enable){
		debug = enable;
	}
	
	private static void log(String msg){
		if(debug){
			Log.d("MenuManager", msg);
		}
	}
}

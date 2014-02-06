package com.livio.sdl.menu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class SubmenuButton extends MenuItem {

	private List<MenuItem> children;
	
	public SubmenuButton(SubmenuButton copy){
		super(copy.getName(), copy.getId(), true);
		copyChildren(copy.getChildren());
	}
	
	public SubmenuButton(String name, int id) {
		super(name, id, true);
	}
	
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
	
	public List<MenuItem> getChildren(){
		if(children == null || children.size() <= 0){
			return Collections.emptyList();
		}
		
		return new ArrayList<MenuItem>(children);
	}
	
	public void addChild(MenuItem item){
		if(children == null){
			children = new ArrayList<MenuItem>();
		}
		
		children.add(item);
	}
	
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
	
	public void removeAllChildren(){
		if(children == null || children.size() <= 0){
			return;
		}
		
		children.clear();
	}

}

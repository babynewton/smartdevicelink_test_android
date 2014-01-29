package com.livio.sdl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import android.util.Log;

/**
 * Defines and manages a list of buttons that will be shown as a menu on the head-unit.
 *
 * @author Mike Burke
 *
 */
public class SdlFunctionBank implements List<SdlBaseButton>{

	/**
	 * Comparator for sorting SdlFunctionBank objects based on their id.
	 *
	 * @author Mike Burke
	 *
	 */
	public static class IdComparator implements Comparator<SdlFunctionBank>{
		@Override
		public int compare(SdlFunctionBank lhs, SdlFunctionBank rhs) {
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
	 * Comparator for sorting SdlFunctionBank objects based on their name.
	 *
	 * @author Mike Burke
	 *
	 */
	public static class NameComparator implements Comparator<SdlFunctionBank> {

		@Override
		public int compare(SdlFunctionBank lhs, SdlFunctionBank rhs) {
			return lhs.getName().compareTo(rhs.getName());
		}

	}
	
	private String name;
	private List<SdlBaseButton> bankItems;
	private int id;
	
	/**
	 * Creates an SdlFunctionBank item with a name & id.
	 * @param name The name of the item.
	 * @param id The id of the item.
	 */
	public SdlFunctionBank(String name, int id){
		this.name = name;
		this.id = id;
		bankItems = new ArrayList<SdlBaseButton>();
	}
	
	public String getName(){
		return this.name;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	/**
	 * Creates a copy of the underlying list containing the SdlFunctionBank objects and returns it.
	 * @return A copy of the underlying list of SdlFunctionBank objects.
	 */
	public List<SdlBaseButton> getBankItems() {
		List<SdlBaseButton> copy = new ArrayList<SdlBaseButton>(bankItems);
		return copy;
	}

	public int getId() {
		return id;
	}
	
	public void setId(int id){
		this.id = id;
	}

	/**
	 * Processes and dispatches a click event to the correct SdlFunctionButton contained in this SdlFunctionBank.
	 * @param parentId The id of the SdlFunctionBank that contains the button that was clicked.
	 * @param buttonId The id of the SdlFunctionButton that was clicked.
	 */
	public void processClick(int parentId, int buttonId){
		// TODO - this could be much more efficient, but I'm in a hurry for CES, so I'm doing the quick-and-dirty method
		for(SdlBaseButton bankItem : bankItems){
			if(bankItem.getId() == buttonId){
				bankItem.click(parentId, buttonId);
				return;
			}
		}
		
		log("No button with this id in the bank");
	}
	
	private static boolean debug = false;
	
	/**
	 * Enables or disables LogCat messages for this class.
	 * @param enable True to enable logs, false to disable.
	 */
	public static void setDebug(boolean enable){
		debug = enable;
	}
	
	private static void log(String msg){
		if(debug){
			Log.d("SdlFunctionBank", msg);
		}
	}
	
	@Override
	public String toString(){
		return new StringBuilder().append(this.name).append(" (").append(this.id).append(")").toString();
	}

	@Override
	public boolean add(SdlBaseButton object) {
		return bankItems.add(object);
	}

	@Override
	public void add(int location, SdlBaseButton object) {
		bankItems.add(location, object);
	}

	@Override
	public boolean addAll(Collection<? extends SdlBaseButton> arg0) {
		return bankItems.addAll(arg0);
	}

	@Override
	public boolean addAll(int arg0, Collection<? extends SdlBaseButton> arg1) {
		return bankItems.addAll(arg0, arg1);
	}

	@Override
	public void clear() {
		bankItems.clear();
	}

	@Override
	public boolean contains(Object object) {
		return bankItems.contains(object);
	}

	@Override
	public boolean containsAll(Collection<?> arg0) {
		return bankItems.containsAll(arg0);
	}

	@Override
	public SdlBaseButton get(int location) {
		return bankItems.get(location);
	}

	@Override
	public int indexOf(Object object) {
		return bankItems.indexOf(object);
	}

	@Override
	public boolean isEmpty() {
		return bankItems.isEmpty();
	}

	@Override
	public Iterator<SdlBaseButton> iterator() {
		return bankItems.iterator();
	}

	@Override
	public int lastIndexOf(Object object) {
		return bankItems.lastIndexOf(object);
	}

	@Override
	public ListIterator<SdlBaseButton> listIterator() {
		return bankItems.listIterator();
	}

	@Override
	public ListIterator<SdlBaseButton> listIterator(int location) {
		return bankItems.listIterator(location);
	}

	@Override
	public SdlBaseButton remove(int location) {
		return bankItems.remove(location);
	}

	@Override
	public boolean remove(Object object) {
		return bankItems.remove(object);
	}

	@Override
	public boolean removeAll(Collection<?> arg0) {
		return bankItems.removeAll(arg0);
	}

	@Override
	public boolean retainAll(Collection<?> arg0) {
		return bankItems.retainAll(arg0);
	}

	@Override
	public SdlBaseButton set(int location, SdlBaseButton object) {
		return bankItems.set(location, object);
	}

	@Override
	public int size() {
		return bankItems.size();
	}

	@Override
	public List<SdlBaseButton> subList(int start, int end) {
		return bankItems.subList(start, end);
	}

	@Override
	public Object[] toArray() {
		return bankItems.toArray();
	}

	@Override
	public <T> T[] toArray(T[] array) {
		return bankItems.toArray(array);
	}
	
}

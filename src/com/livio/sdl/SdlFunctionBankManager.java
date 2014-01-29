package com.livio.sdl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import android.util.Log;

/**
 * Manages all function banks available to be shown through SDL.
 * 
 * In order to create and show menu items through SDL, an SdlFunctionBank object
 * must be created and added to the SdlFunctionBankManager.
 * 
 * The current function bank represents the bank that is currently showing through
 * SDL (or the last-shown bank if no bank is currently showing).
 * 
 * Click events are also processed through the SdlFunctionBankManager based on the id
 * of the current bank and the id of the button that was clicked.
 *
 * @author Mike Burke
 *
 */
public class SdlFunctionBankManager {

	private static boolean debug = false;
	
	private static SdlFunctionBankManager instance = null;
	
	private HashMap<String, SdlFunctionBank> banks;
	private SdlFunctionBank currentBank;
	
	private SdlFunctionBankManager(){
		log("creating SDL function bank manager instance");
		banks = new HashMap<String, SdlFunctionBank>();
	}
	
	/**
	 * Obtain the single instance of the SdlFunctionBankManager.
	 * @return The single instance of the SdlFunctionBankManager
	 */
	public static SdlFunctionBankManager getInstance(){
		if(instance == null){
			instance = new SdlFunctionBankManager();
		}
		
		return instance;
	}
	
	/**
	 * Adds a bank to the function bank manager.
	 * @param name The name of the bank to add.
	 * @param bank The bank to add.
	 */
	public void addBank(String name, SdlFunctionBank bank){
		banks.put(name, bank);
		log(new StringBuilder().append("added / replaced a bank.  there are now ").append(size()).append(" banks").toString());
	}
	
	/**
	 * Returns the number of SdlFunctionBank objects being managed.
	 * @return The number of SdlFunctionBank objects being managed
	 */
	public int size(){
		return banks.size();
	}
	
	/**
	 * If the input bank exists, sets the input bank as the currently showing bank.
	 * @param name The name of the bank to set current
	 * @return True if the bank was set to current, false otherwise
	 */
	public SdlFunctionBank setCurrentBank(String name){
		SdlFunctionBank currBank = banks.get(name);
		if(currBank != null){
			log(new StringBuilder().append("changing current bank from ")
					.append( (currentBank == null) ? null : currentBank.getName()).append(" to ").append(currBank.getName()).toString());
			currentBank = currBank;
			return currentBank;
		}
		else{
			log("bank doesn't exist.  add it and try again");
			return null;
		}
	}
	
	/**
	 * Returns the most recently showing function bank object.
	 * @return The most recently showing function bank object
	 */
	public SdlFunctionBank getCurrentBank(){
		log(new StringBuilder().append("the current bank is ")
				.append( (currentBank == null) ? null : currentBank.getName()).toString());
		return currentBank;
	}
	
	/**
	 * Determines if the input bank name exists in the function bank manager.
	 * @param name The name of the bank to search for.
	 * @return True if the bank already exists in the function bank manager, false otherwise
	 */
	public boolean contains(String name){
		return (banks.get(name) != null);
	}
	
	/**
	 * Processes a click event to the correct function bank and function button.
	 * @param parentId The id of the function bank that contains the function button
	 * @param buttonId The id of the function button that was clicked
	 */
	public void processClick(int parentId, int buttonId){
		SdlFunctionBank bankClicked = getBank(parentId);
		if(bankClicked != null){
			log(new StringBuilder().append("found bank with id ").append(parentId).append(", dispatching click event").toString());
			bankClicked.processClick(parentId, buttonId);
		}
		else{
			log(new StringBuilder().append("no bank with id ").append(parentId).append(" found in function bank manager.").toString());
		}
	}
	
	/**
	 * Returns a function bank given the bank's name.
	 * @param name The name of the bank to search for
	 * @return The function bank object if it exists, null otherwise
	 */
	public SdlFunctionBank getBank(String name){
		SdlFunctionBank result = banks.get(name);
		
		if(result != null){
			log(new StringBuilder().append("found bank with name ").append(name).toString());
		}
		else{
			log(new StringBuilder().append("couldn't find bank with name ").append(name).toString());
		}
		
		return result;
	}
	
	/**
	 * Returns a function bank given the bank's id.
	 * @param id The id of the bank to search for
	 * @return The function bank object if it exists, null otherwise
	 */
	public SdlFunctionBank getBank(int id){
		if(banks == null || banks.size() == 0){
			return null;
		}
		log("searching for bank by id");
		
		Iterator<Entry<String, SdlFunctionBank>> iterator = banks.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<String, SdlFunctionBank> entry = iterator.next();
			if(entry.getValue().getId() == id){
				log(new StringBuilder().append("found bank with id ").append(id).toString());
				return entry.getValue();
			}
		}
		
		log("no bank with this id in the bank manager.");
		return null;
	}
	
	/**
	 * Returns an unsorted list of all bank items in the FunctionBankManager.
	 * @return The sorted list of banks
	 */
	public List<SdlFunctionBank> getAllBanks(){
		List<SdlFunctionBank> result = new ArrayList<SdlFunctionBank>(banks.values());
		return result;
	}

	/**
	 * Enables or disables LogCat messages for this class.
	 * @param enable True to enable logs, false to disable.
	 */
	public static void setDebug(boolean enable){
		debug = enable;
		SdlFunctionBank.setDebug(enable);
	}
	
	private static void log(String msg){
		if(debug){
			Log.d("SdlFunctionBankManager", msg);
		}
	}
}

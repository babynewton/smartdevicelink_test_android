package com.livio.sdl;

import android.util.SparseArray;

import com.livio.sdl.utils.Timeout;
import com.smartdevicelink.proxy.RPCRequest;

/**
 * Tracks outgoing requests to core and sets a timeout for when the response
 * should come back from core.  If a request isn't received by the time
 * the timeout expires, it should be assumed that SDL core disconnected.
 *
 * @author Mike Burke
 *
 */
public class SdlResponseTracker {

	/**
	 * Listener interface for SdlResponseTracker.  Contains callback method
	 * for when any request times out.
	 *
	 * @author Mike Burke
	 *
	 */
	public interface Listener{
		public void onRequestTimedOut();
	}
	
	/**
	 * Default timeout to receive a response for a typical request.
	 */
	public static final int DEFAULT_TIMEOUT = 5000; // 5 seconds
	
	private SparseArray<RPCRequest> requests = new SparseArray<RPCRequest>();
	private SparseArray<Timeout> timeouts = new SparseArray<Timeout>();
	
	private Listener listener;
	
	public SdlResponseTracker(Listener l) {
		this.listener = l;
	}
	
	/**
	 * Adds a request to the tracker with a default timeout.
	 * 
	 * @param request The request to track
	 */
	public void add(RPCRequest request){
		add(request, DEFAULT_TIMEOUT);
	}
	
	/**
	 * Adds a request to the tracker with a specified timeout.
	 * 
	 * @param request The request to track
	 * @param duration The timeout duration (in ms)
	 */
	public void add(RPCRequest request, int duration){
		int corrId = request.getCorrelationID();
		requests.put(corrId, request);
		
		Timeout timeout = new Timeout(duration, new Timeout.Listener() {
			@Override public void onTimeoutCancelled() {}
			
			@Override
			public void onTimeoutCompleted() {
				notifyRequestTimedOut();
			}
		});
		timeout.start();
		timeouts.put(corrId, timeout);
	}
	
	/**
	 * Removes the specified correlation id from the tracker.  Also automatically
	 * cancels the associated timer for the specified correlation id.
	 * 
	 * @param corrId The correlation id to stop tracking
	 * @return The request that was removed
	 */
	public RPCRequest remove(int corrId){
		timeouts.get(corrId).cancel();
		timeouts.remove(corrId);
		
		RPCRequest result = requests.get(corrId);
		requests.remove(corrId);
		return result;
	}
	
	/**
	 * Clears all requests being tracked and cancels their associated timeouts.
	 */
	public void clear(){
		if(requests != null){
			// loop through requests and remove each one
			for(int numItems = requests.size()-1; numItems >= 0; numItems--){
				RPCRequest request = requests.valueAt(numItems);
				remove(request.getCorrelationID());
			}
		}
	}
	
	private void notifyRequestTimedOut(){
		if(listener != null){
			listener.onRequestTimedOut();
		}
	}

}

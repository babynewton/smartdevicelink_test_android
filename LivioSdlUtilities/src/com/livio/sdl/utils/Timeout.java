package com.livio.sdl.utils;

import android.os.Handler;

/**
 * Runs a simple thread that sleeps for the duration set in the constructor.  When the thread
 * is completed or interrupted, it informs the listener that the timeout has been completed.
 *
 * @author Mike Burke
 *
 */
public class Timeout {

	/**
	 * Listener interface for the Timeout class.  Contains callbacks for timeout completed
	 * and timeout cancelled.
	 *
	 * @author Mike Burke
	 *
	 */
	public interface Listener{
		/**
		 * Called when the thread has successfully ran for the input time.
		 */
		public void onTimeoutCompleted();
		/**
		 * Called when the thread has been cancelled or interrupted.
		 */
		public void onTimeoutCancelled();
	}
	
	protected int timeout;
	protected Listener listener;
	protected Thread thread;
	protected Handler handler;
	
	/**
	 * Creates a Timeout object.
	 * 
	 * @param timeout The time to wait for (in ms)
	 * @param l A listener for when the thread completes
	 */
	public Timeout(int timeout, Listener l) {
		this.timeout = timeout;
		this.listener = l;
		handler = new Handler();
	}
	
	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public Listener getListener() {
		return listener;
	}

	public void setListener(Listener listener) {
		this.listener = listener;
	}

	/**
	 * Starts a new thread with the timeout that was set in the constructor.
	 */
	public void start(){
		thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(timeout);
					onTimeoutCompleted();
				} catch (InterruptedException e) {
					onTimeoutCancelled();
				}
			}
		});
		thread.start();
	}
	
	/**
	 * Cancels the currently running thread.
	 */
	public void cancel(){
		if(thread != null && thread.isAlive()){
			thread.interrupt();
		}
	}
	
	/**
	 * Called by the timeout thread to inform that it has successfully completed.
	 */
	protected void onTimeoutCompleted(){
		if(listener != null){
			handler.post(new Runnable() {
				@Override
				public void run() {
					listener.onTimeoutCompleted();
				}
			});
		}
	}
	
	/**
	 * Called by the timeout thread to inform that it has be cancelled or interrupted.
	 */
	protected void onTimeoutCancelled(){
		if(listener != null){
			handler.post(new Runnable() {
				@Override
				public void run() {
					listener.onTimeoutCancelled();
				}
			});
		}
	}

}

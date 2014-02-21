package com.livio.sdl.utils;

import android.os.Handler;

public class Timeout {

	public interface Listener{
		public void onTimeoutCompleted();
		public void onTimeoutCancelled();
	}
	
	protected int timeout;
	protected Listener listener;
	protected Thread thread;
	protected Handler handler;
	
	public Timeout(int timeout, Listener l) {
		this.timeout = timeout;
		this.listener = l;
	}
	
	public void start(){
		handler = new Handler();
		thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(timeout);
					onTimeoutCompleted();
				} catch (InterruptedException e) {
					onTimeoutCancelled();
				}
				dispose();
			}
		});
		thread.start();
	}
	
	public void cancel(){
		if(thread != null){
			thread.interrupt();
		}
	}
	
	protected void dispose(){
		handler = null;
		thread = null;
	}
	
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

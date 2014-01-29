package com.livio.sdl;

import android.os.Handler;
import android.os.Message;

import com.smartdevicelink.proxy.rpc.OnHMIStatus;
import com.smartdevicelink.proxy.rpc.enums.ButtonName;

/**
 * A wrapper class that translates handler messages into specific
 * callback methods for convenience.
 *
 * @author Mike Burke
 *
 */
public class SdlHandler extends Handler {

	/**
	 * Defines an interface of callback methods that are supported
	 * in this handler.
	 *
	 * @author Mike Burke
	 *
	 */
	public interface Listener{
		/**
		 * Called when SDL is connected to a head-unit.
		 */
		void onSdlConnected();
		/**
		 * Called when SDL is disconnected from the head-unit.
		 */
		void onSdlDisconnected();
		/**
		 * Called when the HMI state has changed.
		 * @param newLevel The new HMI status
		 */
		void onHmiChange(OnHMIStatus newLevel);
		/**
		 * Called when any command button is clicked.
		 * @param buttonId The id of the button that was clicked
		 */
		void onButtonClick(int buttonId);
		/**
		 * Called when a hardware media or preset button is clicked.
		 * @param button The enum value of the button that was clicked
		 */
		void onMediaButtonClick(ButtonName button);
		/**
		 * Called when a perform interaction event was canceled for whatever reason.
		 */
		void onPerformInteractionCanceled();
	}
	
	private class What{
		public static final int SDL_CONNECTED = 0;
		public static final int SDL_DISCONNECTED = 1;
		public static final int ON_HMI_CHANGED = 2;
		public static final int ON_BUTTON_CLICK = 3;
		public static final int ON_MEDIA_BUTTON_CLICK = 4;
		public static final int ON_PERFORM_INTERACTION_CANCELED = 5;
	}
	
	@SuppressWarnings("unused")
	// prevent handler from being used without a listener
	private SdlHandler(){}
	
	private Listener listener;
	
	/**
	 * Creates an SdlHandler object with a required listener.
	 * @param listener The listener that callbacks should be dispatched to
	 */
	public SdlHandler(Listener listener){
		if(listener == null){
			throw new NullPointerException();
		}
		
		this.listener = listener;
	}
	
	@Override
	public void handleMessage(Message msg) {
		switch(msg.what){
		case What.SDL_CONNECTED:
			listener.onSdlConnected();
			break;
		case What.SDL_DISCONNECTED:
			listener.onSdlDisconnected();
			break;
		case What.ON_HMI_CHANGED:
			listener.onHmiChange((OnHMIStatus)msg.obj);
			break;
		case What.ON_BUTTON_CLICK:
			listener.onButtonClick(msg.arg1);
			break;
		case What.ON_MEDIA_BUTTON_CLICK:
			listener.onMediaButtonClick((ButtonName) msg.obj);
			break;
		case What.ON_PERFORM_INTERACTION_CANCELED:
			listener.onPerformInteractionCanceled();
			break;
		default:
			break;
		}
		super.handleMessage(msg);
	}
	
	/**
	 * Obtains a Message from this handler's pool and sets the Message details to represent an OnConnected message. 
	 * @return The initialized message
	 */
	public Message createOnConnectMessage(){
		Message msg = this.obtainMessage();
		msg.what = What.SDL_CONNECTED;
		return msg;
	}

	/**
	 * Obtains a Message from this handler's pool and sets the Message details to represent an OnDisonnected message. 
	 * @return The initialized message
	 */
	public Message createOnDisconnectMessage(){
		Message msg = this.obtainMessage();
		msg.what = What.SDL_DISCONNECTED;
		return msg;
	}

	/**
	 * Obtains a Message from this handler's pool and sets the Message details to represent an onHmiChange message. 
	 * @return The initialized message
	 */
	public Message createOnHmiChangeMessage(OnHMIStatus status){
		Message msg = this.obtainMessage();
		msg.what = What.ON_HMI_CHANGED;
		msg.obj = status;
		return msg;
	}

	/**
	 * Obtains a Message from this handler's pool and sets the Message details to represent an OnButtonClick message. 
	 * @return The initialized message
	 */
	public Message createOnButtonClickMessage(int buttonId){
		Message msg = this.obtainMessage();
		msg.what = What.ON_BUTTON_CLICK;
		msg.arg1 = buttonId;
		return msg;
	}

	/**
	 * Obtains a Message from this handler's pool and sets the Message details to represent an OnMediaButtonClick message. 
	 * @return The initialized message
	 */
	public Message createOnMediaButtonClickMessage(ButtonName button){
		Message msg = this.obtainMessage();
		msg.what = What.ON_MEDIA_BUTTON_CLICK;
		msg.obj = button;
		return msg;
	}

}

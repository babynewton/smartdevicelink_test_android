package com.livio.sdl;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;

import com.livio.sdl.services.SdlService;

/**
 * Defines a Broadcast Receiver to receive events from the SDL mobile libraries,
 * including bluetooth events, media button events, audio events and others.
 *
 * @author Mike Burke
 *
 */
public class SdlReceiver extends BroadcastReceiver {

	private static boolean debug = false;

	private static class Actions{
		static final String BLUETOOTH_STATE_CHANGE = BluetoothAdapter.ACTION_STATE_CHANGED;
		static final String ACTION_MEDIA_BUTTON = Intent.ACTION_MEDIA_BUTTON;
		static final String AUDIO_BECOMING_NOISY = AudioManager.ACTION_AUDIO_BECOMING_NOISY;
		static final String FORD_BROADCAST = "com.ford.syncV4.broadcast";
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		
		if(action.equals(Actions.BLUETOOTH_STATE_CHANGE)){
			// TODO
			log("bluetooth state changed");
			
//			BluetoothAdapter.EXTRA_CONNECTION_STATE
			int bluetoothState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
			
			if( bluetoothState == BluetoothAdapter.STATE_TURNING_OFF ||
					bluetoothState == BluetoothAdapter.STATE_DISCONNECTED ){
				
				Intent syncService = new Intent(context, SdlService.class);
				syncService.putExtras(intent);
				context.stopService(syncService);
			}
			else if(bluetoothState == BluetoothAdapter.STATE_CONNECTED){
				Intent syncService = new Intent(context, SdlService.class);
				syncService.putExtras(intent);
				context.startService(syncService);
			}
		}
		else if(action.equals(Actions.ACTION_MEDIA_BUTTON)){
			// TODO
			log("media button pressed");
		}
		else if(action.equals(Actions.AUDIO_BECOMING_NOISY)){
			// TODO
			log("audio becoming noisy");
		}
		else if(action.equals(Actions.FORD_BROADCAST)){
			// TODO
			log("ford broadcast");
		}
		
	}

	/**
	 * Enables or disables LogCat messages for this class.
	 * @param enable True to enable logs, false to disable.
	 */
	public static void setDebug(boolean enable){
		debug = enable;
	}
	
	private static void log(String msg){
		if(debug){
			Log.d("SyncReceiver", msg);
		}
	}

}

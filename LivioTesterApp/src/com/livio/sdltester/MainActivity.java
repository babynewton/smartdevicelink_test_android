package com.livio.sdltester;


import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.livio.sdl.IpAddress;
import com.livio.sdl.SdlBaseButton;
import com.livio.sdl.enums.EnumClickListener;
import com.livio.sdl.enums.SdlCommand;
import com.livio.sdl.services.SdlService;
import com.livio.sdl.utils.WifiUtils;
import com.livio.sdltester.dialogs.AddCommandDialog;
import com.livio.sdltester.dialogs.AddSubMenuDialog;
import com.livio.sdltester.dialogs.BaseAlertDialog;
import com.livio.sdltester.dialogs.ButtonSubscriptionDialog;
import com.livio.sdltester.dialogs.ChangeRegistrationDialog;
import com.livio.sdltester.dialogs.ConnectingDialog;
import com.livio.sdltester.dialogs.CreateInteractionChoiceSetDialog;
import com.livio.sdltester.dialogs.DeleteCommandDialog;
import com.livio.sdltester.dialogs.DeleteSubmenuDialog;
import com.livio.sdltester.dialogs.SdlAlertDialog;
import com.livio.sdltester.dialogs.SdlConnectionDialog;
import com.livio.sdltester.dialogs.SendMessageDialog;
import com.livio.sdltester.dialogs.ShowDialog;
import com.livio.sdltester.dialogs.SpeakDialog;
import com.smartdevicelink.proxy.RPCMessage;
import com.smartdevicelink.proxy.RPCRequest;


public class MainActivity extends Activity{
	
	private static final class ResultCodes{
		private static final class SubmenuResult{
			private static final int ADD_COMMAND_DIALOG = 1;
			private static final int DELETE_SUBMENU_DIALOG = 2;
		}
		private static final class CommandResult{
			private static final int DELETE_COMMAND_DIALOG = 1;
		}
		
	}
	
	private static final int CONNECTING_DIALOG_TIMEOUT = 10000; // duration to attempt a connection (10s)
	private static final String OFFLINE_MODE_IP_ADDRESS = "0.0.0.0"; // ip address for offline mode
	private boolean offlineMode = false;
	
	private ListView commandList;
	
	/* Messenger for communicating with service. */
    private Messenger serviceMsgr = null;
    
    /* Flag indicating whether we have called bind on the service. */
    private boolean isBound = false, isConnected = false;

	private ConnectingDialog connectingDialog;
	private int connectionAttempts = 3;
	
	private ArrayAdapter<RPCMessage> listViewAdapter;
	
    /*
     * Target we publish for clients to send messages to IncomingHandler.
     */
    final Messenger mMessenger = new Messenger(new IncomingHandler());
    
	@SuppressLint("HandlerLeak")
	private class IncomingHandler extends Handler{
		@Override
		public void handleMessage(Message msg) {
			int resultCode;
			
			switch(msg.what){
			case SdlService.ClientMessages.SDL_CONNECTED:
				isConnected = true;
				if(connectingDialog != null && connectingDialog.isShowing()){
					connectingDialog.dismiss();
				}
				break;
			case SdlService.ClientMessages.SDL_DISCONNECTED:
				isConnected = false;
				break;
			case SdlService.ClientMessages.ON_APP_OPENED:
				//TODO - what should happen here?  Any messages sent before this point will not work correctly.
				break;
			case SdlService.ClientMessages.FOREGROUND_STATE_RECEIVED:
				onForegroundStateReceived( (Boolean) msg.obj);
				break;
			case SdlService.ClientMessages.ON_MESSAGE_RESULT:
				onMessageResponseReceived((RPCMessage) msg.obj);
				break;
			case SdlService.ClientMessages.SUBMENU_LIST_RECEIVED:
				@SuppressWarnings("unchecked")
				List<SdlBaseButton> submenuList = (List<SdlBaseButton>) msg.obj;
				resultCode = msg.arg1;
				switch(resultCode){
				case ResultCodes.SubmenuResult.ADD_COMMAND_DIALOG:
					createAddCommandDialog(submenuList);
					break;
				case ResultCodes.SubmenuResult.DELETE_SUBMENU_DIALOG:
					if(submenuList.size() > 0){
						createDeleteSubmenuDialog(submenuList);
					}
					else{
						Toast.makeText(MainActivity.this, getResources().getString(R.string.no_submenus_to_delete), Toast.LENGTH_SHORT).show();
					}
					break;
				default:
					break;
				}
				break;
			case SdlService.ClientMessages.COMMAND_LIST_RECEIVED:
				@SuppressWarnings("unchecked")
				List<SdlBaseButton> commandList = (List<SdlBaseButton>) msg.obj;
				resultCode = msg.arg1;
				switch(resultCode){
				case ResultCodes.CommandResult.DELETE_COMMAND_DIALOG:
					if(commandList.size() > 0){
						createDeleteCommandDialog(commandList);	
					}
					else{
						Toast.makeText(MainActivity.this, getResources().getString(R.string.no_commands_to_delete), Toast.LENGTH_SHORT).show();
					}
					break;
				default:
					break;
				}
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
    }
	
	private void sendMessageToService(Message msg){
		// only send messages to the service if we are NOT in offline mode
		if(!offlineMode){
			try {
				serviceMsgr.send(msg);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void sendSdlMessageToService(RPCRequest request){
		Message msg = Message.obtain(null, SdlService.ServiceMessages.SEND_MESSAGE);
		msg.obj = request;
		sendMessageToService(msg);
		logSdlMessageSent(request);
	}
	
	private void sendSubmenuListRequest(int reqCode){
		Message msg = Message.obtain(null, SdlService.ServiceMessages.REQUEST_SUBMENU_LIST);
		msg.replyTo = mMessenger;
		msg.arg1 = reqCode;
		sendMessageToService(msg);
	}
	
	private void sendCommandListRequest(int reqCode){
		Message msg = Message.obtain(null, SdlService.ServiceMessages.REQUEST_COMMAND_LIST);
		msg.replyTo = mMessenger;
		msg.arg1 = reqCode;
		sendMessageToService(msg);
	}
	
	private void logSdlMessageSent(RPCRequest request){
		listViewAdapter.add(request);
		listViewAdapter.notifyDataSetChanged();
	}
	
	private void logSdlMessageReceived(RPCMessage response){
		listViewAdapter.add(response);
		listViewAdapter.notifyDataSetChanged();
	}
    
    /*
     * Class for interacting with the main interface of the service.
     */
    private final ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            serviceMsgr = new Messenger(service);

            Message msg = Message.obtain(null, SdlService.ServiceMessages.REGISTER_CLIENT);
            msg.replyTo = mMessenger;
            sendMessageToService(msg);
        }

        public void onServiceDisconnected(ComponentName className) {
            // process crashed - make sure nobody can use messenger instance.
            serviceMsgr = null;
        }
    };
    
    void doBindService() {
    	if(!isBound){
	   		bindService(new Intent(MainActivity.this, SdlService.class), mConnection, Context.BIND_AUTO_CREATE);
	        isBound = true;
    	}
    }

    void doUnbindService() {
        if (isBound) {
            if (serviceMsgr != null) {
                Message msg = Message.obtain(null, SdlService.ServiceMessages.UNREGISTER_CLIENT);
                msg.replyTo = mMessenger;
                sendMessageToService(msg);
            }

            // Detach our existing connection.
            unbindService(mConnection);
            isBound = false;
        }
    }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		SdlService.setDebug(true);
		
		initViews();
		doBindService();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		if(!isConnected && !offlineMode){
			showSdlConnectionDialog();
		}
		super.onResume();
	}

	private void initViews(){		
		findViewById(R.id.btn_main_sendMessage).setOnClickListener(
			new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					new SendMessageDialog(MainActivity.this, new EnumClickListener() {
						@Override
						public <E extends Enum<E>> void OnEnumItemClicked(E selection) {
							showCommandDialog((SdlCommand) selection);
						}
				}).show();
			}
		});
		
		commandList = (ListView) findViewById(R.id.list_main_commandList);
		listViewAdapter = new ArrayAdapter<RPCMessage>(this, android.R.layout.simple_list_item_1);
		commandList.setAdapter(listViewAdapter);
	}

	@Override
	protected void onDestroy() {
		sendMessageToService(Message.obtain(null, SdlService.ServiceMessages.DISCONNECT));
		doUnbindService();
		super.onDestroy();
	}
	
	private void onForegroundStateReceived(boolean foregroundState){
		// TODO
	}
	
	private void onMessageResponseReceived(RPCMessage response){
		// TODO
		logSdlMessageReceived(response);
	}
	
	private void setOfflineMode(boolean enable){
		offlineMode = enable;
		String enableStr = (enable) ? "enabled" : "disabled";
		String toastMsg = new StringBuilder().append("Offline mode ").append(enableStr).toString();
		Toast.makeText(MainActivity.this, toastMsg, Toast.LENGTH_SHORT).show();
	}
	
	private void showSdlConnectionDialog(){
		String savedIpAddress = MyApplicationPreferences.restoreIpAddress(MainActivity.this);
		String savedTcpPort = MyApplicationPreferences.restoreTcpPort(MainActivity.this);
		
		BaseAlertDialog connectionDialog;
		if(savedIpAddress != null && savedTcpPort != null){
			connectionDialog = new SdlConnectionDialog(this, savedIpAddress, savedTcpPort);
		}
		else{
			connectionDialog = new SdlConnectionDialog(this);
		}
		
		connectionDialog.setCancelable(false);
		connectionDialog.setListener(new BaseAlertDialog.Listener() {
			@Override
			public void onResult(Object resultData) {
				IpAddress result = (IpAddress) resultData;
				if(result == null){
					setOfflineMode(true);
					return;
				}
				
				String addressString = result.getIpAddress();
				String portString = result.getTcpPort();
				
				boolean ipAddressValid = WifiUtils.validateIpAddress(addressString);
				boolean ipPortValid = WifiUtils.validateTcpPort(portString);
				
				if(ipAddressValid && ipPortValid){
					MyApplicationPreferences.saveIpAddress(MainActivity.this, addressString);
					MyApplicationPreferences.saveTcpPort(MainActivity.this, portString);
					
					if(addressString.equals(OFFLINE_MODE_IP_ADDRESS)){
						setOfflineMode(true);
					}
					else{
						connectingDialog = new ConnectingDialog(MainActivity.this);
						connectingDialog.show();
						new Thread(new Runnable() {
							@Override
							public void run() {
								Looper.prepare();
								try {
									Thread.sleep(CONNECTING_DIALOG_TIMEOUT);
									if(connectingDialog != null && connectingDialog.isShowing()){
										connectingDialog.dismiss();
										Toast.makeText(MainActivity.this, "Connection timed out", Toast.LENGTH_SHORT).show();
									}
								} catch (InterruptedException e) {
									// do nothing
								}
								Looper.loop();
							}
						}).start();
						
						Message msg = Message.obtain(null, SdlService.ServiceMessages.CONNECT);
	                    msg.obj = resultData;
	                	sendMessageToService(msg);
					}
				}
				else{
					// user input was invalid
					Toast.makeText(MainActivity.this, "Input was invalid - please try again", Toast.LENGTH_SHORT).show();
					showSdlConnectionDialog();
				}
			}
		});
		connectionDialog.show();
	}
	
	/*
	 * This really sucks...
	 * 
	 * Figure out which command we're looking at and launch the appropriate dialog.
	 */
	private void showCommandDialog(SdlCommand command){
		if(command == null){
			throw new IllegalArgumentException(getResources().getString(R.string.not_an_sdl_command));
		}
		
		switch(command){
		case ALERT:
			createAlertDialog();
			break;
		case SPEAK:
			createSpeakDialog();
			break;
		case SHOW:
			createShowDialog();
			break;
		case SUBSCRIBE_BUTTON:
			createButtonSubscribeDialog();
			break;
		case ADD_COMMAND:
			sendSubmenuListRequest(ResultCodes.SubmenuResult.ADD_COMMAND_DIALOG);
			break;
		case ADD_SUBMENU:
			createAddSubmenuDialog();
			break;
		case CREATE_INTERACTION_CHOICE_SET:
			createInteractionChoiceSetDialog();
			break;
		case CHANGE_REGISTRATION:
			createChangeRegistrationDialog();
			break;
		case DELETE_COMMAND:
			sendCommandListRequest(ResultCodes.CommandResult.DELETE_COMMAND_DIALOG);
			break;
		case DELETE_SUB_MENU:
			sendSubmenuListRequest(ResultCodes.SubmenuResult.DELETE_SUBMENU_DIALOG);
			break;
		case SET_GLOBAL_PROPERTIES:
		case RESET_GLOBAL_PROPERTIES:
		case SET_MEDIA_CLOCK_TIMER:
		case DELETE_INTERACTION_CHOICE_SET:
		case PERFORM_INTERACTION:
		case SLIDER:
		case SCROLLABLE_MESSAGE:
		case PUT_FILE:
		case DELETE_FILE:
		case LIST_FILES:
		case SET_APP_ICON:
		case PERFORM_AUDIO_PASSTHRU:
		case END_AUDIO_PASSTHRU:
		case SUBSCRIBE_VEHICLE_DATA:
		case UNSUBSCRIBE_VEHICLE_DATA:
		case GET_VEHICLE_DATA:
		case READ_DIDS:
		case GET_DTCS:
			Toast.makeText(this, getResources().getString(R.string.not_implemented), Toast.LENGTH_SHORT).show();
			break;
		default:
			break;
		}
	}
	
	private void createAlertDialog(){
		BaseAlertDialog alertDialog = new SdlAlertDialog(this);
		alertDialog.setListener(new BaseAlertDialog.Listener() {
			@Override
			public void onResult(Object resultData) {
				sendSdlMessageToService((RPCRequest) resultData);
			}
		});
		alertDialog.show();
	}
	
	private void createSpeakDialog(){
		BaseAlertDialog speakDialog = new SpeakDialog(this);
		speakDialog.setListener(new BaseAlertDialog.Listener() {
			@Override
			public void onResult(Object resultData) {
				sendSdlMessageToService((RPCRequest) resultData);
			}
		});
		speakDialog.show();
	}
	
	private void createShowDialog(){
		BaseAlertDialog showDialog = new ShowDialog(this);
		showDialog.setListener(new BaseAlertDialog.Listener() {
			@Override
			public void onResult(Object resultData) {
				sendSdlMessageToService((RPCRequest) resultData);
			}
		});
		showDialog.show();
	}
	
	private void createButtonSubscribeDialog(){
		BaseAlertDialog buttonSubscribeDialog = new ButtonSubscriptionDialog(this);
		buttonSubscribeDialog.setListener(new BaseAlertDialog.Listener() {
			@Override
			public void onResult(Object resultData) {
				sendSdlMessageToService((RPCRequest) resultData);
			}
		});
		buttonSubscribeDialog.show();
	}
	
	private void createAddCommandDialog(List<SdlBaseButton> allBanks){
		BaseAlertDialog addCommandDialog = new AddCommandDialog(this, allBanks);
		addCommandDialog.setListener(new BaseAlertDialog.Listener() {
			@Override
			public void onResult(Object resultData) {
				sendSdlMessageToService((RPCRequest) resultData);
			}
		});
		addCommandDialog.show();
	}
	
	private void createAddSubmenuDialog(){
		BaseAlertDialog submenuDialog = new AddSubMenuDialog(this);
		submenuDialog.setListener(new BaseAlertDialog.Listener() {
			@Override
			public void onResult(Object resultData) {
				sendSdlMessageToService((RPCRequest) resultData);
			}
		});
		submenuDialog.show();
	}
	
	private void createInteractionChoiceSetDialog(){
		BaseAlertDialog createInteractionChoiceSetDialog = new CreateInteractionChoiceSetDialog(this);
		createInteractionChoiceSetDialog.setListener(new BaseAlertDialog.Listener() {
			@Override
			public void onResult(Object resultData) {
				sendSdlMessageToService((RPCRequest) resultData);
			}
		});
		createInteractionChoiceSetDialog.show();
	}
	
	private void createChangeRegistrationDialog(){
		BaseAlertDialog changeRegistrationDialog = new ChangeRegistrationDialog(this);
		changeRegistrationDialog.setListener(new BaseAlertDialog.Listener() {
			@Override
			public void onResult(Object resultData) {
				sendSdlMessageToService((RPCRequest) resultData);
			}
		});
		changeRegistrationDialog.show();
	}
	
	private void createDeleteCommandDialog(List<SdlBaseButton> commandList){
		BaseAlertDialog deleteCommandDialog = new DeleteCommandDialog(this, commandList);
		deleteCommandDialog.setListener(new BaseAlertDialog.Listener() {
			
			@Override
			public void onResult(Object resultData) {
				sendSdlMessageToService((RPCRequest) resultData);
			}
		});
		deleteCommandDialog.show();
	}
	
	private void createDeleteSubmenuDialog(List<SdlBaseButton> submenuList){
		BaseAlertDialog deleteCommandDialog = new DeleteSubmenuDialog(this, submenuList);
		deleteCommandDialog.setListener(new BaseAlertDialog.Listener() {
			
			@Override
			public void onResult(Object resultData) {
				sendSdlMessageToService((RPCRequest) resultData);
			}
		});
		deleteCommandDialog.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int menuItemId = item.getItemId();
		switch(menuItemId){
		case R.id.menu_connect:
			offlineMode = false;
			showSdlConnectionDialog();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}

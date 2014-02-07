package com.livio.sdltester;


import java.util.Collections;
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
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.livio.sdl.SdlLogMessage;
import com.livio.sdl.SdlMessageAdapter;
import com.livio.sdl.datatypes.IpAddress;
import com.livio.sdl.dialogs.BaseAlertDialog;
import com.livio.sdl.enums.EnumClickListener;
import com.livio.sdl.enums.SdlCommand;
import com.livio.sdl.menu.MenuItem;
import com.livio.sdl.services.SdlService;
import com.livio.sdl.utils.WifiUtils;
import com.livio.sdltester.dialogs.AddCommandDialog;
import com.livio.sdltester.dialogs.AddSubMenuDialog;
import com.livio.sdltester.dialogs.ButtonSubscriptionDialog;
import com.livio.sdltester.dialogs.ChangeRegistrationDialog;
import com.livio.sdltester.dialogs.ConnectingDialog;
import com.livio.sdltester.dialogs.CreateInteractionChoiceSetDialog;
import com.livio.sdltester.dialogs.DeleteCommandDialog;
import com.livio.sdltester.dialogs.DeleteSubmenuDialog;
import com.livio.sdltester.dialogs.JsonDialog;
import com.livio.sdltester.dialogs.SdlAlertDialog;
import com.livio.sdltester.dialogs.SdlConnectionDialog;
import com.livio.sdltester.dialogs.SendMessageDialog;
import com.livio.sdltester.dialogs.ShowDialog;
import com.livio.sdltester.dialogs.SpeakDialog;
import com.smartdevicelink.proxy.RPCMessage;
import com.smartdevicelink.proxy.RPCRequest;


public class MainActivity extends Activity{
	
	/**
	 * Used when requesting information from the SDL service, these constants can be used
	 * to perform different tasks when the information is returned by the service.
	 *
	 * @author Mike Burke
	 *
	 */
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
	
	private boolean offlineMode = false;
	
	private ListView commandList;
	private SdlMessageAdapter listViewAdapter;
	
	/* Messenger for communicating with service. */
    private Messenger serviceMsgr = null;
    
    private boolean isBound = false, isConnected = false;

    private BaseAlertDialog connectionDialog;
	private ConnectingDialog connectingDialog;
	
    /*
     * Target we publish for clients to send messages to IncomingHandler.
     */
    final Messenger mMessenger = new Messenger(new IncomingHandler());
    
	@SuppressLint("HandlerLeak")
	private class IncomingHandler extends Handler{
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case SdlService.ClientMessages.SDL_CONNECTED:
				isConnected = true;
				setOfflineMode(false);
				if(connectingDialog != null && connectingDialog.isShowing()){
					connectingDialog.dismiss();
				}
				break;
			case SdlService.ClientMessages.SDL_DISCONNECTED:
				isConnected = false;
				Toast.makeText(MainActivity.this, getResources().getString(R.string.sdl_disconnected), Toast.LENGTH_LONG).show();
				break;
			case SdlService.ClientMessages.ON_APP_OPENED:
				break;
			case SdlService.ClientMessages.FOREGROUND_STATE_RECEIVED:
				onForegroundStateReceived( (Boolean) msg.obj);
				break;
			case SdlService.ClientMessages.ON_MESSAGE_RESULT:
				onMessageResponseReceived((RPCMessage) msg.obj);
				break;
			case SdlService.ClientMessages.SUBMENU_LIST_RECEIVED:
				onSubmenuListReceived((List<MenuItem>) msg.obj, msg.arg1);
				break;
			case SdlService.ClientMessages.COMMAND_LIST_RECEIVED:
				onCommandListReceived((List<MenuItem>) msg.obj, msg.arg1);
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
    }
	
	/**
	 * Sends the input message to the SDL service through the service messenger.
	 * 
	 * @param msg The message to send
	 */
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
	
	/**
	 * Sends an RPCRequest to the SDL service through the service messenger and adds the request to the list view.
	 * 
	 * @param request The request to send
	 */
	private void sendSdlMessageToService(RPCRequest request){
		Message msg = Message.obtain(null, SdlService.ServiceMessages.SEND_MESSAGE);
		msg.obj = request;
		sendMessageToService(msg);
		logSdlMessage(request);
	}
	
	/**
	 * Sends a request for the most up-to-date submenu list with a request code so this activity knows
	 * what to do when the response comes back.
	 * 
	 * @param reqCode The request code to associate with the request
	 */
	private void sendSubmenuListRequest(int reqCode){
		Message msg = Message.obtain(null, SdlService.ServiceMessages.REQUEST_SUBMENU_LIST);
		msg.replyTo = mMessenger;
		msg.arg1 = reqCode;
		sendMessageToService(msg);
	}
	
	/**
	 * Sends a request for the most up-to-date command list with a request code so this activity knows
	 * what to do when the response comes back.
	 * 
	 * @param reqCode The request code to associate with the request
	 */
	private void sendCommandListRequest(int reqCode){
		Message msg = Message.obtain(null, SdlService.ServiceMessages.REQUEST_COMMAND_LIST);
		msg.replyTo = mMessenger;
		msg.arg1 = reqCode;
		sendMessageToService(msg);
	}
	
	/**
	 * Adds the input RPCMessage to the list view.
	 * 
	 * @param request The message to log
	 */
	private void logSdlMessage(RPCMessage request){
		listViewAdapter.add(new SdlLogMessage(request));
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
    
    /**
     * Binds this activity to the SDL service, using the service connection as a messenger between the two.
     */
    private void doBindService() {
    	if(!isBound){
	   		bindService(new Intent(MainActivity.this, SdlService.class), mConnection, Context.BIND_AUTO_CREATE);
	        isBound = true;
    	}
    }

    /**
     * Unbinds this activity from the SDL service.
     */
    private void doUnbindService() {
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
	
    /* ********** Android Life-Cycle ********** */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		SdlService.setDebug(true);
		
		initViews();
		doBindService();
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
		listViewAdapter = new SdlMessageAdapter(this);
		commandList.setAdapter(listViewAdapter);
		commandList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				SdlLogMessage logMessage = listViewAdapter.getItem(position);
				String messageType = logMessage.getMessageType();
				if(!messageType.equals(SdlLogMessage.NOTIFICATION)){
					BaseAlertDialog jsonDialog = new JsonDialog(MainActivity.this, logMessage);
					jsonDialog.show();
				}
			}
		});
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		if(!isConnected && !offlineMode && (connectionDialog == null || !connectionDialog.isShowing()) ){
			showSdlConnectionDialog();
		}
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		sendMessageToService(Message.obtain(null, SdlService.ServiceMessages.DISCONNECT));
		doUnbindService();
		super.onDestroy();
	}
	
	/* ********** SDL Service request callbacks ********** */
	
	/**
	 * Called when the current foreground state has been updated.
	 * 
	 * @param foregroundState True if the app is in the foreground on the head-unit, false otherwise
	 */
	private void onForegroundStateReceived(boolean foregroundState){
		// TODO - change foreground state from a request-response model to an autoresponse model
	}
	
	/**
	 * Called when a message has been received from the head-unit.
	 * 
	 * @param response The response that was received
	 */
	private void onMessageResponseReceived(RPCMessage response){
		logSdlMessage(response);
	}
	
	/**
	 * Called when the up-to-date list of submenus is received.  The request code can be used
	 * to perform different operations based on the request code that is sent with the initial request.
	 * 
	 * @param submenuList The list of submenu items
	 * @param reqCode The request code that was sent with the request
	 */
	private void onSubmenuListReceived(List<MenuItem> submenuList, int reqCode){
		Collections.sort(submenuList, new MenuItem.NameComparator()); // sort submenu list by name.  you can also sort by id with the MenuItem.IdComparator object
		
		switch(reqCode){
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
	}
	
	/**
	 * Called when the up-to-date list of commands is received.  The request code can be used
	 * to perform different operations based on the request code that is sent with the initial request.
	 * 
	 * @param commandList The list of command items
	 * @param reqCode The request code that was sent with the request
	 */
	private void onCommandListReceived(List<MenuItem> commandList, int reqCode){
		Collections.sort(commandList, new MenuItem.NameComparator()); // sort command list by name
		switch(reqCode){
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
	}
	
	/**
	 * Sets offline mode so that messages are not sent to the SDL service.  This allows
	 * the app to run successfully without being connected to SDL core.
	 * 
	 * @param enable
	 */
	private void setOfflineMode(boolean enable){
		if(offlineMode != enable){
			offlineMode = enable;
			String enableStr = (enable) ? "enabled" : "disabled";
			String toastMsg = new StringBuilder().append("Offline mode ").append(enableStr).toString();
			Toast.makeText(MainActivity.this, toastMsg, Toast.LENGTH_SHORT).show();
			// TODO - implement offline mode in SDL service so we can still receive (fake) responses and still see some data being updated in offline mode
		}
	}
	
	/**
	 * Shows the SDL connection dialog, which allows the user to enter the IP address for the core component they would like to connect to.
	 */
	private void showSdlConnectionDialog(){
		// restore any old IP address from preferences
		String savedIpAddress = MyApplicationPreferences.restoreIpAddress(MainActivity.this);
		String savedTcpPort = MyApplicationPreferences.restoreTcpPort(MainActivity.this);
		
		if(savedIpAddress != null && savedTcpPort != null){
			// if there was an old IP stored in preferences, initialize the dialog with those values
			connectionDialog = new SdlConnectionDialog(this, savedIpAddress, savedTcpPort);
		}
		else{
			// if no IP address was in preferences, initialize the dialog with no input strings
			connectionDialog = new SdlConnectionDialog(this);
		}
		
		// set us up the dialog
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
					// if the user entered valid IP settings, save them to preferences so they don't have to re-enter them next time
					MyApplicationPreferences.saveIpAddress(MainActivity.this, addressString);
					MyApplicationPreferences.saveTcpPort(MainActivity.this, portString);
					
					// show an indeterminate connecting dialog
					connectingDialog = new ConnectingDialog(MainActivity.this);
					connectingDialog.show();
					
					// and start a timeout thread in case the connection isn't successful
					new Thread(new Runnable() {
						@Override
						public void run() {
							Looper.prepare();
							try {
								Thread.sleep(CONNECTING_DIALOG_TIMEOUT);
								
								if(connectingDialog != null && connectingDialog.isShowing()){
									// if we made it here without being interrupted, the connection was unsuccessful - dismiss the dialog and enter offline mode
									connectingDialog.dismiss();
									Toast.makeText(MainActivity.this, "Connection timed out", Toast.LENGTH_SHORT).show();
									setOfflineMode(true);
								}
							} catch (InterruptedException e) {
								// do nothing
							}
							Looper.loop();
						}
					}).start();
					
					// message the SDL service, telling it to attempt a connection with the input IP address
					Message msg = Message.obtain(null, SdlService.ServiceMessages.CONNECT);
                    msg.obj = resultData;
                	sendMessageToService(msg);
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
	
	/**
	 * Launches the appropriate dialog for whichever command item was clicked.
	 * 
	 * @param command The command that was clicked
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
	
	/**
	 * Creates an alert dialog, allowing the user to manually send an alert command.
	 */
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

	/**
	 * Creates a speak dialog, allowing the user to manually send a speak command.
	 */
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

	/**
	 * Creates a show dialog, allowing the user to manually send a show command.
	 */	
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

	/**
	 * Creates a button subscribe dialog, allowing the user to manually send a button subscribe command.
	 */	
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

	/**
	 * Creates an add command dialog, allowing the user to manually send an add command command.
	 */	
	private void createAddCommandDialog(List<MenuItem> allBanks){
		BaseAlertDialog addCommandDialog = new AddCommandDialog(this, allBanks);
		addCommandDialog.setListener(new BaseAlertDialog.Listener() {
			@Override
			public void onResult(Object resultData) {
				sendSdlMessageToService((RPCRequest) resultData);
			}
		});
		addCommandDialog.show();
	}

	/**
	 * Creates an add submenu dialog, allowing the user to manually send an add submenu command.
	 */	
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

	/**
	 * Creates a create interaction choice set dialog, allowing the user to manually send a create interaction choice set command.
	 */	
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

	/**
	 * Creates a change registration dialog, allowing the user to manually send a change registration command.
	 */	
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

	/**
	 * Creates a delete command dialog, allowing the user to manually send a delete command command.
	 */	
	private void createDeleteCommandDialog(List<MenuItem> commandList){
		BaseAlertDialog deleteCommandDialog = new DeleteCommandDialog(this, commandList);
		deleteCommandDialog.setListener(new BaseAlertDialog.Listener() {
			
			@Override
			public void onResult(Object resultData) {
				sendSdlMessageToService((RPCRequest) resultData);
			}
		});
		deleteCommandDialog.show();
	}

	/**
	 * Creates a delete submenu dialog, allowing the user to manually send a delete submenu command.
	 */	
	private void createDeleteSubmenuDialog(List<MenuItem> submenuList){
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
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		int menuItemId = item.getItemId();
		switch(menuItemId){
		case R.id.menu_connect:
			setOfflineMode(false);
			showSdlConnectionDialog();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}

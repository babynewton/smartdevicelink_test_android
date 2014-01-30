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
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.livio.sdl.IpAddress;
import com.livio.sdl.SdlFunctionBank;
import com.livio.sdl.SdlFunctionBankManager;
import com.livio.sdl.enums.EnumClickListener;
import com.livio.sdl.enums.SdlCommand;
import com.livio.sdl.services.SdlService;
import com.livio.sdltester.dialogs.AddCommandDialog;
import com.livio.sdltester.dialogs.AddSubMenuDialog;
import com.livio.sdltester.dialogs.BaseAlertDialog;
import com.livio.sdltester.dialogs.ButtonSubscriptionDialog;
import com.livio.sdltester.dialogs.ChangeRegistrationDialog;
import com.livio.sdltester.dialogs.ConnectingDialog;
import com.livio.sdltester.dialogs.CreateInteractionChoiceSetDialog;
import com.livio.sdltester.dialogs.SdlAlertDialog;
import com.livio.sdltester.dialogs.SdlConnectionDialog;
import com.livio.sdltester.dialogs.SendMessageDialog;
import com.livio.sdltester.dialogs.ShowDialog;
import com.livio.sdltester.dialogs.SpeakDialog;
import com.livio.sdltester.utils.UpCounter;
import com.smartdevicelink.proxy.RPCMessage;
import com.smartdevicelink.proxy.RPCRequest;
import com.smartdevicelink.proxy.RPCResponse;
import com.smartdevicelink.proxy.rpc.AddCommand;
import com.smartdevicelink.proxy.rpc.AddSubMenu;


public class MainActivity extends Activity{
	
	private ListView commandList;
	private UpCounter idGenerator = new UpCounter();
	
	/* Messenger for communicating with service. */
    Messenger serviceMsgr = null;
    
    /* Flag indicating whether we have called bind on the service. */
    boolean isBound = false, isConnected = false;

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
			case SdlService.ClientMessages.ON_FOREGROUND_STATE:
				onForegroundStateReceived( (Boolean) msg.obj);
				break;
			case SdlService.ClientMessages.ON_MESSAGE_RESULT:
				onMessageResponseReceived((RPCResponse) msg.obj);
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
    }
	
	private void sendMessageToService(Message msg){
		try {
			serviceMsgr.send(msg);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	private void sendSdlMessageToService(RPCRequest request){
		Message msg = Message.obtain(null, SdlService.ServiceMessages.SEND_MESSAGE);
		msg.obj = request;
		sendMessageToService(msg);
		logSdlMessageSent(request);
	}
	
	private void logSdlMessageSent(RPCRequest request){
		listViewAdapter.add(request);
		listViewAdapter.notifyDataSetChanged();
	}
	
	private void logSdlMessageReceived(RPCResponse response){
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
		if(!isConnected){
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
	
	private void onMessageResponseReceived(RPCResponse response){
		// TODO
		logSdlMessageReceived(response);
	}
	
	private void showSdlConnectionDialog(){
		//TODO - save & load IP addresses from memory
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
				if(resultData == null){
					connectionAttempts--;
					if(connectionAttempts != 0){
						Toast.makeText(MainActivity.this, "Invalid IP - try again", Toast.LENGTH_SHORT).show();
						showSdlConnectionDialog();
					}
					else{
						Toast.makeText(MainActivity.this, "Too many attempts - goodbye", Toast.LENGTH_SHORT).show();
						finish();
					}
				}
				else{
					IpAddress ipAddress = (IpAddress) resultData;
					MyApplicationPreferences.saveIpAddress(MainActivity.this, ipAddress.getIpAddress());
					MyApplicationPreferences.saveTcpPort(MainActivity.this, ipAddress.getTcpPort());
					
					connectingDialog = new ConnectingDialog(MainActivity.this);
					connectingDialog.show();
					
					Message msg = Message.obtain(null, SdlService.ServiceMessages.CONNECT);
                    msg.obj = resultData;
                	sendMessageToService(msg);
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
			createAddCommandDialog();
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
		case DELETE_SUB_MENU:
		case SET_GLOBAL_PROPERTIES:
		case RESET_GLOBAL_PROPERTIES:
		case SET_MEDIA_CLOCK_TIMER:
		case DELETE_INTERACTION_CHOICE_SET:
		case PERFORM_INTERACTION:
		case ENCODED_SYNC_PDATA:
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
		case SHOW_CONSTANT_TBT:
		case UPDATE_TURN_LIST:
		case ALERT_MANEUVER:
		case DIAL_NUMBER:
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
	
	private void createAddCommandDialog(){
		List<SdlFunctionBank> allBanks = SdlFunctionBankManager.getInstance().getAllBanks();
		Collections.sort(allBanks, new SdlFunctionBank.IdComparator());
		
		BaseAlertDialog addCommandDialog = new AddCommandDialog(this, allBanks);
		addCommandDialog.setListener(new BaseAlertDialog.Listener() {
			@Override
			public void onResult(Object resultData) {
				((AddCommand) resultData).setCmdID(idGenerator.next());
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
				((AddSubMenu) resultData).setMenuID(idGenerator.next());
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

}

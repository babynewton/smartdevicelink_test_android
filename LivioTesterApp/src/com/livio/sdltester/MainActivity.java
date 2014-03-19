package com.livio.sdltester;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.livio.sdl.IpAddress;
import com.livio.sdl.SdlImageItem;
import com.livio.sdl.SdlImageItem.SdlImageItemComparator;
import com.livio.sdl.SdlLogMessage;
import com.livio.sdl.SdlService;
import com.livio.sdl.adapters.SdlMessageAdapter;
import com.livio.sdl.dialogs.BaseAlertDialog;
import com.livio.sdl.dialogs.IndeterminateProgressDialog;
import com.livio.sdl.dialogs.JsonDialog;
import com.livio.sdl.dialogs.ListViewDialog;
import com.livio.sdl.enums.EnumComparator;
import com.livio.sdl.enums.SdlButton;
import com.livio.sdl.enums.SdlCommand;
import com.livio.sdl.menu.MenuItem;
import com.livio.sdl.utils.Timeout;
import com.livio.sdl.utils.WifiUtils;
import com.livio.sdltester.dialogs.AddCommandDialog;
import com.livio.sdltester.dialogs.AddSubMenuDialog;
import com.livio.sdltester.dialogs.ButtonSubscriptionDialog;
import com.livio.sdltester.dialogs.ButtonUnsubscriptionDialog;
import com.livio.sdltester.dialogs.ChangeRegistrationDialog;
import com.livio.sdltester.dialogs.CreateInteractionChoiceSetDialog;
import com.livio.sdltester.dialogs.DeleteCommandDialog;
import com.livio.sdltester.dialogs.DeleteFileDialog;
import com.livio.sdltester.dialogs.DeleteInteractionDialog;
import com.livio.sdltester.dialogs.DeleteSubmenuDialog;
import com.livio.sdltester.dialogs.GetDtcsDialog;
import com.livio.sdltester.dialogs.PerformInteractionDialog;
import com.livio.sdltester.dialogs.PutFileDialog;
import com.livio.sdltester.dialogs.ReadDidsDialog;
import com.livio.sdltester.dialogs.ScrollableMessageDialog;
import com.livio.sdltester.dialogs.SdlAlertDialog;
import com.livio.sdltester.dialogs.SdlConnectionDialog;
import com.livio.sdltester.dialogs.SetAppIconDialog;
import com.livio.sdltester.dialogs.SetMediaClockTimerDialog;
import com.livio.sdltester.dialogs.ShowDialog;
import com.livio.sdltester.dialogs.SliderDialog;
import com.livio.sdltester.dialogs.SpeakDialog;
import com.smartdevicelink.proxy.RPCMessage;
import com.smartdevicelink.proxy.RPCRequest;
import com.smartdevicelink.proxy.rpc.ListFiles;
import com.smartdevicelink.proxy.rpc.enums.FileType;


public class MainActivity extends Activity{
	/**
	 * Used when requesting information from the SDL service, these constants can be used
	 * to perform different tasks when the information is asynchronously returned by the service.
	 *
	 * @author Mike Burke
	 *
	 */
	private static final class ResultCodes{
		private static final class SubmenuResult{
			private static final int ADD_COMMAND_DIALOG = 0;
			private static final int DELETE_SUBMENU_DIALOG = 1;
		}
		private static final class CommandResult{
			private static final int DELETE_COMMAND_DIALOG = 0;
		}
		private static final class ButtonSubscriptionResult{
			private static final int BUTTON_SUBSCRIBE = 0;
			private static final int BUTTON_UNSUBSCRIBE = 1;
		}
		private static final class InteractionSetResult{
			private static final int PERFORM_INTERACTION = 0;
			private static final int DELETE_INTERACTION_SET = 1;
		}
		private static final class PutFileResult{
			private static final int PUT_FILE = 0;
			private static final int ADD_COMMAND = 1;
			private static final int CHOICE_INTERACTION_SET = 2;
			private static final int DELETE_FILE = 3;
			private static final int SET_APP_ICON = 4;
			private static final int SHOW = 5;
		}
	}
	
	private static enum ConnectionStatus{
		CONNECTED("Connected"),
		OFFLINE_MODE("Offline Mode"),
		;
		
		private final String friendlyName;
		private ConnectionStatus(String friendlyName){
			this.friendlyName = friendlyName;
		}
		
		@Override
		public String toString(){
			return friendlyName;
		}
	}
	
	private static final int CONNECTING_DIALOG_TIMEOUT = 10000; // duration to attempt a connection (10s)
	
	private String connectionStatusFormat;
	
	private ListView commandList;
	private TextView tv_connectionStatus;
	private SdlMessageAdapter listViewAdapter;
	
	/* Messenger for communicating with service. */
    private Messenger serviceMsgr = null;
    
    private boolean isBound = false;

    private BaseAlertDialog connectionDialog;
	private IndeterminateProgressDialog connectingDialog;
	private Timeout connectionTimeout;
	
	// cache for all images available to send to SDL service
	private HashMap<String, SdlImageItem> imageCache;
	private List<MenuItem> submenuCache = null;
	
    /*
     * Target we publish for clients to send messages to IncomingHandler.
     */
    final Messenger mMessenger = new Messenger(new IncomingHandler());
    
	@SuppressLint("HandlerLeak")
	private class IncomingHandler extends Handler{
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			// see SdlService.java in the com.livio.sdl package for details on these incoming messages
			
			switch(msg.what){
			case SdlService.ClientMessages.SDL_CONNECTED:
				updateConnectionStatus(ConnectionStatus.CONNECTED);
				
				// clear the command log since we're starting fresh from here
				clearSdlLog();
				
				// dismiss the connecting dialog if it's showing
				if(connectingDialog != null && connectingDialog.isShowing()){
					connectingDialog.dismiss();
				}
				if(connectionTimeout != null){
					connectionTimeout.cancel();
				}
				break;
			case SdlService.ClientMessages.SDL_DISCONNECTED:
				resetService();
				updateConnectionStatus(ConnectionStatus.OFFLINE_MODE);
				clearSdlLog();
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
			case SdlService.ClientMessages.BUTTON_SUBSCRIPTIONS_RECEIVED:
				onButtonSubscriptionsReceived((List<SdlButton>) msg.obj, msg.arg1);
				break;
			case SdlService.ClientMessages.INTERACTION_SETS_RECEIVED:
				onInteractionListReceived((List<MenuItem>) msg.obj, msg.arg1);
				break;
			case SdlService.ClientMessages.PUT_FILES_RECEIVED:
				onPutFileListReceived((List<String>) msg.obj, msg.arg1);
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
		try {
			serviceMsgr.send(msg);
		} catch (RemoteException e) {
			e.printStackTrace();
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
	 * Sends a request for the most up-to-date list of button subscriptions with a request code so this
	 * activity knows what to do when the response comes back.
	 * 
	 * @param reqCode The request code to associate with the request
	 */
	private void sendButtonSubscriptionRequest(int reqCode){
		Message msg = Message.obtain(null, SdlService.ServiceMessages.REQUEST_BUTTON_SUBSCRIPTIONS);
		msg.replyTo = mMessenger;
		msg.arg1 = reqCode;
		sendMessageToService(msg);
	}
	
	/**
	 * Sends a request for the most up-to-date list of button subscriptions with a request code so this
	 * activity knows what to do when the response comes back.
	 * 
	 * @param reqCode The request code to associate with the request
	 */
	private void sendInteractionSetRequest(int reqCode){
		Message msg = Message.obtain(null, SdlService.ServiceMessages.REQUEST_INTERACTION_SETS);
		msg.replyTo = mMessenger;
		msg.arg1 = reqCode;
		sendMessageToService(msg);
	}
	
	/**
	 * Sends a request for the most up-to-date list of images added so far with a request code so this
	 * activity knows what to do when the response comes back.
	 * 
	 * @param reqCode The request code to associate with the request
	 */
	private void sendPutFileRequest(int reqCode){
		Message msg = Message.obtain(null, SdlService.ServiceMessages.REQUEST_PUT_FILES);
		msg.replyTo = mMessenger;
		msg.arg1 = reqCode;
		sendMessageToService(msg);
	}
	
	/**
	 * Resets the SDL service.
	 */
	private void resetService(){
		Intent sdlService = new Intent(MainActivity.this, SdlService.class);
		stopService(sdlService);
		startService(sdlService);
	}
	
	/**
	 * Adds the input RPCMessage to the list view.
	 * 
	 * @param request The message to log
	 */
	private void logSdlMessage(RPCMessage request){
		listViewAdapter.add(new SdlLogMessage(request));
		listViewAdapter.notifyDataSetChanged();
		
		// after adding a new item, auto-scroll to the bottom of the list
		commandList.setSelection(listViewAdapter.getCount() - 1);
	}
	
	private void clearSdlLog(){
		listViewAdapter.clear();
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
            stopService(new Intent(MainActivity.this, SdlService.class));
            isBound = false;
        }
    }
	
    /* ********** Android Life-Cycle ********** */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		SdlService.setDebug(true);

		createImageCache();
		init();
		doBindService();
		showSdlConnectionDialog();
	}

	// create an image cache for the images that are available to send to the head-unit.  this allows easy image look-up
	// based on the filename of the image.  We'll do this on a thread since processing images can be CPU intensive.
	private void createImageCache(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				// grab information from the image resources enum
				SdlTesterImageResource[] values = SdlTesterImageResource.values();
				imageCache = new HashMap<String, SdlImageItem>(values.length);
				
				for(SdlTesterImageResource img : values){
					// create an SdlImageItem object for each image
					Bitmap bitmap = BitmapFactory.decodeResource(getResources(), img.getImageId());
					String imageName = img.toString();
					FileType imageType = img.getFileType();
					SdlImageItem item = new SdlImageItem(bitmap, imageName, imageType);
					
					// map the image name to its associated SdlImageItem object
					imageCache.put(imageName, item);
				}
			}
		}).start();
	}

	// initializes views in the main activity
	private void init(){
		// set up the "Send Message" button
		findViewById(R.id.btn_main_sendMessage).setOnClickListener(
			new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Context context = MainActivity.this;
					String dialogTitle = context.getResources().getString(R.string.sdl_command_dialog_title);
					
					// grab the command list and sort the commands by name
					// TODO make this lazily instantiated so we don't have to sort every time the button is clicked
					List<SdlCommand> commandList = Arrays.asList(SdlCommand.values());
					Collections.sort(commandList, new EnumComparator<SdlCommand>());
					
					// show the dialog with the commandList we created above
					BaseAlertDialog commandDialog = new ListViewDialog<SdlCommand>(context, dialogTitle, commandList);
					commandDialog.setListener(new BaseAlertDialog.Listener() {
						@Override
						public void onResult(Object resultData) {
							// when a list item is clicked, dispatch the click to the showCommandDialog method
							showCommandDialog((SdlCommand) resultData);
						}
					});
					commandDialog.show();
			}
		});
		
		// set up the command log
		commandList = (ListView) findViewById(R.id.list_main_commandList);
		listViewAdapter = new SdlMessageAdapter(this);
		commandList.setAdapter(listViewAdapter);
		commandList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// when an item is clicked, show it's associated JSON dialog
				SdlLogMessage logMessage = listViewAdapter.getItem(position);
				BaseAlertDialog jsonDialog = new JsonDialog(MainActivity.this, logMessage);
				jsonDialog.show();
			}
		});
		
		tv_connectionStatus = (TextView) findViewById(R.id.tv_connectionStatus);
	}
	
	// updates the connection status TextView
	private void updateConnectionStatus(ConnectionStatus status){
		if(connectionStatusFormat == null){
			connectionStatusFormat = getResources().getString(R.string.connection_status_format);
		}
		
		if(status == ConnectionStatus.OFFLINE_MODE){
			sendMessageToService(Message.obtain(null, SdlService.ServiceMessages.OFFLINE_MODE));
		}
		
		String text = new StringBuilder().append(connectionStatusFormat).append(" ").append(status.toString()).toString();
		tv_connectionStatus.setText(text);
	}

	@Override
	protected void onDestroy() {
		sendMessageToService(Message.obtain(null, SdlService.ServiceMessages.DISCONNECT));
		doUnbindService();
		super.onDestroy();
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
			submenuCache = submenuList;
			sendPutFileRequest(ResultCodes.PutFileResult.ADD_COMMAND);
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
	 * Called when the up-to-date list of button subscriptions is received.  The request code can be used
	 * to perform different operations based on the request code that is sent with the initial request.
	 * 
	 * @param buttonSubscriptionList The list of button subscriptions
	 * @param reqCode The request code that was sent with the request
	 */
	private void onButtonSubscriptionsReceived(List<SdlButton> buttonSubscriptionList, int reqCode){
		switch(reqCode){
		case ResultCodes.ButtonSubscriptionResult.BUTTON_SUBSCRIBE:
			if(buttonSubscriptionList.size() == SdlButton.values().length){
				Toast.makeText(MainActivity.this, getResources().getString(R.string.button_subscriptions_already_subscribed), Toast.LENGTH_LONG).show();
			}
			else{
				List<SdlButton> buttonsNotSubscribedTo = filterSubscribedButtons(buttonSubscriptionList);
				Collections.sort(buttonsNotSubscribedTo, new EnumComparator<SdlButton>());
				createButtonSubscribeDialog(buttonsNotSubscribedTo);
			}
			break;
		case ResultCodes.ButtonSubscriptionResult.BUTTON_UNSUBSCRIBE:
			if(buttonSubscriptionList.size() == 0){
				Toast.makeText(MainActivity.this, getResources().getString(R.string.button_subscriptions_none_subscribed), Toast.LENGTH_LONG).show();
			}
			else{
				Collections.sort(buttonSubscriptionList, new EnumComparator<SdlButton>());
				createButtonUnsubscribeDialog(buttonSubscriptionList);
			}
			break;
		default:
			break;
		}
	}
	
	/**
	 * Called when the up-to-date list of interaction sets is received.  The request code can be used
	 * to perform different operations based on the request code that is sent with the initial request.
	 * 
	 * @param interactionSetList The list of interaction sets
	 * @param reqCode The request code that was sent with the request
	 */
	private void onInteractionListReceived(List<MenuItem> interactionSetList, int reqCode){
		switch(reqCode){
		case ResultCodes.InteractionSetResult.PERFORM_INTERACTION:
			if(interactionSetList.size() == 0){
				Toast.makeText(MainActivity.this, getResources().getString(R.string.interaction_list_none_added), Toast.LENGTH_LONG).show();
			}
			else{
				Collections.sort(interactionSetList, new MenuItem.IdComparator());
				createPerformInteractionDialog(interactionSetList);
			}
			break;
		case ResultCodes.InteractionSetResult.DELETE_INTERACTION_SET:
			if(interactionSetList.size() == 0){
				Toast.makeText(MainActivity.this, getResources().getString(R.string.interaction_list_none_added), Toast.LENGTH_LONG).show();
			}
			else{
				Collections.sort(interactionSetList, new MenuItem.IdComparator());
				createDeleteInteractionDialog(interactionSetList);
			}
			break;
		default:
			break;
		}
	}
	
	/**
	 * Called when the up-to-date list of put file images have been received.  The request code can be used
	 * to perform different operations based on the request code that is sent with the initial request.
	 * 
	 * @param putFileList The list of put file image names
	 * @param reqCode The request code that was sent with the request
	 */
	private void onPutFileListReceived(List<String> putFileList, int reqCode){
		List<SdlImageItem> availableItems;
		
		switch(reqCode){
		case ResultCodes.PutFileResult.PUT_FILE:
			availableItems = filterAddedItems(putFileList);
			
			if(availableItems.size() > 0){
				createPutFileDialog(availableItems);
			}
			else{
				Toast.makeText(this, "All images have been added!", Toast.LENGTH_LONG).show();
			}
			break;
		case ResultCodes.PutFileResult.DELETE_FILE:
			availableItems = filterUnaddedItems(putFileList);
			
			if(availableItems.size() > 0){
				createDeleteFileDialog(availableItems);
			}
			else{
				Toast.makeText(this, "No images have been added!", Toast.LENGTH_LONG).show();
			}
			break;
		case ResultCodes.PutFileResult.SET_APP_ICON:
			availableItems = filterUnaddedItems(putFileList);
			
			if(availableItems.size() > 0){
				createSetAppIconDialog(availableItems);
			}
			else{
				Toast.makeText(this, "No images have been added!", Toast.LENGTH_LONG).show();
			}
			break;
		case ResultCodes.PutFileResult.ADD_COMMAND:
			availableItems = filterUnaddedItems(putFileList);
			createAddCommandDialog(submenuCache, availableItems);
			break;
		case ResultCodes.PutFileResult.SHOW:
			availableItems = filterUnaddedItems(putFileList);
			createShowDialog(availableItems);
			break;
		case ResultCodes.PutFileResult.CHOICE_INTERACTION_SET:
			availableItems = filterUnaddedItems(putFileList);
			createInteractionChoiceSetDialog(availableItems);
			break;
		default:
			break;
		}
	}
	
	/**
	 * Filters out any images that have already been added through the PutFile command.
	 * 
	 * @param putFileList The list of images that have been added through the PutFile command
	 * @return The list of images that have <b>not</b> been added through the PutFile command
	 */
	private List<SdlImageItem> filterAddedItems(List<String> putFileList){
		int itemsInFilteredList = imageCache.size() - putFileList.size();
		if(itemsInFilteredList == 0){
			return Collections.emptyList();
		}
		
		// first, we'll grab all image cache keys (aka image names) into a copy 
		Set<String> cacheKeys = new TreeSet<String>(imageCache.keySet());
		// then, we'll remove all images that have been added
		cacheKeys.removeAll(putFileList);
		
		
		List<SdlImageItem> result = new ArrayList<SdlImageItem>(itemsInFilteredList);
		
		// now, we'll loop through the remaining image names and create a list from them
		for(String name : cacheKeys){
			result.add(imageCache.get(name));
		}

		Collections.sort(result, new SdlImageItemComparator());
		
		return result;
	}
	
	/**
	 * Filters out any images that have <b>not</b> been added through the PutFile command.
	 * 
	 * @param putFileList The list of images that have been added through the PutFile command
	 * @return The list of images that have been added through the PutFile command
	 */
	private List<SdlImageItem> filterUnaddedItems(List<String> putFileList){
		List<SdlImageItem> result = new ArrayList<SdlImageItem>(putFileList.size());
		for(String name : putFileList){
			result.add(imageCache.get(name));
		}
		
		Collections.sort(result, new SdlImageItemComparator());
		return result;
	}
	
	/**
	 * Finds any buttons that are <b>not</b> in the input list of button subscriptions
	 * and adds them to the listview adapter.
	 * 
	 * @param buttonSubscriptions A list of buttons that have been subscribed to
	 */
	private List<SdlButton> filterSubscribedButtons(List<SdlButton> buttonSubscriptions){
		final SdlButton[] buttonValues = SdlButton.values();
		final int numItems = buttonValues.length - buttonSubscriptions.size();
		List<SdlButton> result = new ArrayList<SdlButton>(numItems);
		
		for(SdlButton button : buttonValues){
			if(!buttonSubscriptions.contains(button)){
				result.add(button);
			}
		}

		return result;
	}
	
	/**
	 * Shows the SDL connection dialog, which allows the user to enter the IP address for the core component they would like to connect to.
	 */
	private void showSdlConnectionDialog(){
		// restore any old IP address from preferences
		String savedIpAddress = LivioSdlTesterPreferences.restoreIpAddress(MainActivity.this);
		String savedTcpPort = LivioSdlTesterPreferences.restoreTcpPort(MainActivity.this);
		
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
					updateConnectionStatus(ConnectionStatus.OFFLINE_MODE);
					return;
				}
				
				String addressString = result.getIpAddress();
				String portString = result.getTcpPort();
				
				boolean ipAddressValid = WifiUtils.validateIpAddress(addressString);
				boolean ipPortValid = WifiUtils.validateTcpPort(portString);
				
				if(ipAddressValid && ipPortValid){
					// if the user entered valid IP settings, save them to preferences so they don't have to re-enter them next time
					LivioSdlTesterPreferences.saveIpAddress(MainActivity.this, addressString);
					LivioSdlTesterPreferences.saveTcpPort(MainActivity.this, portString);
					
					// show an indeterminate connecting dialog
					connectingDialog = new IndeterminateProgressDialog(MainActivity.this, "Connecting");
					connectingDialog.show();
					
					// and start a timeout thread in case the connection isn't successful
					if(connectionTimeout == null){
						connectionTimeout = new Timeout(CONNECTING_DIALOG_TIMEOUT, new Timeout.Listener() {
							@Override public void onTimeoutCancelled() {}
							
							@Override
							public void onTimeoutCompleted() {
								if(connectingDialog != null && connectingDialog.isShowing()){
									// if we made it here without being interrupted, the connection was unsuccessful - dismiss the dialog and enter offline mode
									connectingDialog.dismiss();
								}
								
								Toast.makeText(MainActivity.this, "Connection timed out", Toast.LENGTH_SHORT).show();
								sendMessageToService(Message.obtain(null, SdlService.ServiceMessages.OFFLINE_MODE));
								updateConnectionStatus(ConnectionStatus.OFFLINE_MODE);
							}
						});
					}
					connectionTimeout.start();
					
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
			// shouldn't happen, but if an invalid command gets here, let's throw an exception.
			throw new IllegalArgumentException(getResources().getString(R.string.not_an_sdl_command));
		}
		
		switch(command){
		case PUT_FILE:
			// the put file dialog needs a list of images that have been added so far, so let's request
			// that list here and we'll actually show the dialog when it gets returned by the service.  See onPutFileListReceived().
			sendPutFileRequest(ResultCodes.PutFileResult.PUT_FILE);
			break;
		case DELETE_FILE:
			// the delete file dialog needs a list of images that have been added so far, so let's request
			// that list here and we'll actually show the dialog when it gets returned by the service.  See onPutFileListReceived().
			sendPutFileRequest(ResultCodes.PutFileResult.DELETE_FILE);
			break;
		case ALERT:
			createAlertDialog();
			break;
		case SPEAK:
			createSpeakDialog();
			break;
		case SHOW:
			// the put file dialog needs a list of images that have been added so far, so let's request
			// that list here and we'll actually show the dialog when it gets returned by the service.  See onPutFileListReceived().
			sendPutFileRequest(ResultCodes.PutFileResult.SHOW);
			break;
		case SUBSCRIBE_BUTTON:
			// the subscribe button dialog needs a list of buttons that have been subscribed to so far, so let's request
			// that list here and we'll actually show the dialog when it gets returned by the service.  See onButtonSubscriptionsReceived().
			sendButtonSubscriptionRequest(ResultCodes.ButtonSubscriptionResult.BUTTON_SUBSCRIBE);
			break;
		case UNSUBSCRIBE_BUTTON:
			// the unsubscribe button dialog needs a list of buttons that have been subscribed to so far, so let's request
			// that list here and we'll actually show the dialog when it gets returned by the service.  See onButtonSubscriptionsReceived().
			sendButtonSubscriptionRequest(ResultCodes.ButtonSubscriptionResult.BUTTON_UNSUBSCRIBE);
			break;
		case ADD_COMMAND:
			// the add command dialog needs a list of submenus that the command could be added to, so let's request that list here and
			// we'll actually show the dialog when the list gets returned by the service.  See onSubmenuListReceived().
			sendSubmenuListRequest(ResultCodes.SubmenuResult.ADD_COMMAND_DIALOG);
			break;
		case DELETE_COMMAND:
			// the delete command dialog needs a list of commands that have been added so far so the user can select which command to delete,
			// so let's request the list here and we'll show the dialog when it's returned by the service.  See onCommandListReceived().
			sendCommandListRequest(ResultCodes.CommandResult.DELETE_COMMAND_DIALOG);
			break;
		case ADD_SUBMENU:
			createAddSubmenuDialog();
			break;
		case DELETE_SUB_MENU:
			// the delete submenu dialog needs a list of commands that have been added so far so the user can select which submenu to delete,
			// so let's request the list here and we'll show the dialog when it's returned by the service.  See onSubmenuListReceived().
			sendSubmenuListRequest(ResultCodes.SubmenuResult.DELETE_SUBMENU_DIALOG);
			break;
		case CREATE_INTERACTION_CHOICE_SET:
			// the CreateInteractionChoiceSet dialog needs a list of images that have been added so far, so let's request
			// that list here and we'll actually show the dialog when it gets returned by the service.  See onPutFileListReceived().
			sendPutFileRequest(ResultCodes.PutFileResult.CHOICE_INTERACTION_SET);
			break;
		case PERFORM_INTERACTION:
			// the perform interaction dialog needs a list of interaction sets that have been added so far, so let's request
			// that list here and we'll actually show the dialog when it gets returned by the service.  See onInteractionListReceived().
			sendInteractionSetRequest(ResultCodes.InteractionSetResult.PERFORM_INTERACTION);
			break;
		case DELETE_INTERACTION_CHOICE_SET:
			// the delete interaction dialog needs a list of interaction sets that have been added so far, so let's request
			// that list here and we'll actually show the dialog when it gets returned by the service.  See onInteractionListReceived().
			sendInteractionSetRequest(ResultCodes.InteractionSetResult.DELETE_INTERACTION_SET);
			break;
		case CHANGE_REGISTRATION:
			createChangeRegistrationDialog();
			break;
		case GET_DTCS:
			createGetDtcsDialog();
			break;
		case READ_DIDS:
			createReadDidsDialog();
			break;
		case SLIDER:
			createSliderDialog();
			break;
		case SCROLLABLE_MESSAGE:
			createScrollableMessageDialog();
			break;
		case SET_MEDIA_CLOCK_TIMER:
			createSetMediaClockTimerDialog();
			break;
		case LIST_FILES:
			// list files command doesn't accept any parameters, so we can send it directly.
			sendSdlMessageToService(new ListFiles());
			break;
		case SET_APP_ICON:
			// the set app icon dialog needs a list of images that have been added so far, so let's request
			// that list here and we'll actually show the dialog when it gets returned by the service.  See onPutFileListReceived().
			sendPutFileRequest(ResultCodes.PutFileResult.SET_APP_ICON);
			break;
			
			// TODO these should maybe be added at some point maybe
//		case SET_GLOBAL_PROPERTIES:
//		case RESET_GLOBAL_PROPERTIES:
//			Toast.makeText(this, getResources().getString(R.string.not_implemented), Toast.LENGTH_SHORT).show();
//			break;
		default:
			break;
		}
	}
	
	// listener to be used when receiving a single RPCRequest from a dialog.
	private final BaseAlertDialog.Listener singleMessageListener = new BaseAlertDialog.Listener() {
		@Override
		public void onResult(final Object resultData) {
			if(resultData != null){
				new Thread(new Runnable() {
					@Override
					public void run() {
						sendSdlMessageToService((RPCRequest) resultData);
					}
				}).start();
			}
		}
	};
	
	// listener to be used when receiving a list of RPCRequests from a dialog.
	private final BaseAlertDialog.Listener multipleMessageListener = new BaseAlertDialog.Listener() {
		@Override
		public void onResult(final Object resultData) {
			if(resultData != null){
				new Thread(new Runnable() {
					@Override
					public void run() {
						@SuppressWarnings("unchecked")
						List<RPCRequest> msgList = (List<RPCRequest>) resultData;
						for(RPCRequest request : msgList){
							sendSdlMessageToService(request);
						}
					}
				}).start();
			}
		}
	};
	
	/**
	 * Creates an alert dialog, allowing the user to manually send an alert command.
	 */
	private void createAlertDialog(){
		BaseAlertDialog alertDialog = new SdlAlertDialog(this);
		alertDialog.setListener(singleMessageListener);
		alertDialog.show();
	}

	/**
	 * Creates a speak dialog, allowing the user to manually send a speak command.
	 */
	private void createSpeakDialog(){
		BaseAlertDialog speakDialog = new SpeakDialog(this);
		speakDialog.setListener(singleMessageListener);
		speakDialog.show();
	}

	/**
	 * Creates a show dialog, allowing the user to manually send a show command.
	 */	
	private void createShowDialog(List<SdlImageItem> images){
		BaseAlertDialog showDialog = new ShowDialog(this, images);
		showDialog.setListener(singleMessageListener);
		showDialog.show();
	}

	/**
	 * Creates a button subscribe dialog, allowing the user to manually send a button subscribe command. 
	 * 
	 * @param buttonSubscriptions The list used to populate the dialog
	 */
	private void createButtonSubscribeDialog(List<SdlButton> buttonSubscriptions){
		BaseAlertDialog buttonSubscribeDialog = new ButtonSubscriptionDialog(this, buttonSubscriptions);
		buttonSubscribeDialog.setListener(multipleMessageListener);
		buttonSubscribeDialog.show();
	}
	
	/**
	 * Creates a button unsubscribe dialog, allowing the user to manually send a button unsubscribe command.
	 * 
	 * @param buttonSubscriptions The list used to populate the dialog
	 */
	private void createButtonUnsubscribeDialog(List<SdlButton> buttonSubscriptions){
		BaseAlertDialog buttonUnsubscribeDialog = new ButtonUnsubscriptionDialog(this, buttonSubscriptions);
		buttonUnsubscribeDialog.setListener(multipleMessageListener);
		buttonUnsubscribeDialog.show();
	}

	/**
	 * Creates an add command dialog, allowing the user to manually send an add command command.
	 * 
	 * @param allBanks The list used to populate the dialog
	 */
	private void createAddCommandDialog(List<MenuItem> allBanks, List<SdlImageItem> availableItems){
		BaseAlertDialog addCommandDialog = new AddCommandDialog(this, allBanks, availableItems);
		addCommandDialog.setListener(singleMessageListener);
		addCommandDialog.show();
	}

	/**
	 * Creates an add submenu dialog, allowing the user to manually send an add submenu command.
	 */	
	private void createAddSubmenuDialog(){
		BaseAlertDialog submenuDialog = new AddSubMenuDialog(this);
		submenuDialog.setListener(singleMessageListener);
		submenuDialog.show();
	}

	/**
	 * Creates a create interaction choice set dialog, allowing the user to manually send a create interaction choice set command.
	 */	
	private void createInteractionChoiceSetDialog(List<SdlImageItem> addedImages){
		BaseAlertDialog createInteractionChoiceSetDialog = new CreateInteractionChoiceSetDialog(this, addedImages);
		createInteractionChoiceSetDialog.setListener(singleMessageListener);
		createInteractionChoiceSetDialog.show();
	}

	/**
	 * Creates a change registration dialog, allowing the user to manually send a change registration command.
	 */	
	private void createChangeRegistrationDialog(){
		BaseAlertDialog changeRegistrationDialog = new ChangeRegistrationDialog(this);
		changeRegistrationDialog.setListener(singleMessageListener);
		changeRegistrationDialog.show();
	}

	/**
	 * Creates a delete command dialog, allowing the user to manually send a delete command command.
	 * 
	 * @param commandList The list used to populate the dialog
	 */
	private void createDeleteCommandDialog(List<MenuItem> commandList){
		BaseAlertDialog deleteCommandDialog = new DeleteCommandDialog(this, commandList);
		deleteCommandDialog.setListener(singleMessageListener);
		deleteCommandDialog.show();
	}

	/**
	 * Creates a delete submenu dialog, allowing the user to manually send a delete submenu command.
	 * 
	 * @param submenuList The list used to populate the dialog
	 */
	private void createDeleteSubmenuDialog(List<MenuItem> submenuList){
		BaseAlertDialog deleteCommandDialog = new DeleteSubmenuDialog(this, submenuList);
		deleteCommandDialog.setListener(singleMessageListener);
		deleteCommandDialog.show();
	}
	
	/**
	 * Creates a perform interaction dialog, allowing the user to manually send a PerformInteraction command.
	 * 
	 * @param interactionList The list used to populate the dialog
	 */
	private void createPerformInteractionDialog(List<MenuItem> interactionList){
		BaseAlertDialog performInteractionDialog = new PerformInteractionDialog(this, interactionList);
		performInteractionDialog.setListener(singleMessageListener);
		performInteractionDialog.show();
	}
	
	/**
	 * Creates a delete interaction dialog, allowing the user to manually send a DeleteInteractionChoiceSet command.
	 * 
	 * @param interactionList The list used to populate the dialog
	 */
	private void createDeleteInteractionDialog(List<MenuItem> interactionList){
		BaseAlertDialog deleteInteractionDialog = new DeleteInteractionDialog(this, interactionList);
		deleteInteractionDialog.setListener(singleMessageListener);
		deleteInteractionDialog.show();
	}
	
	/**
	 * Creates a get DTCs dialog, allowing the user to manually send a GetDTCs command.
	 */
	private void createGetDtcsDialog(){
		BaseAlertDialog getDtcsDialog = new GetDtcsDialog(this);
		getDtcsDialog.setListener(singleMessageListener);
		getDtcsDialog.show();
	}
	
	/**
	 * Creates a read DIDs dialog, allowing the user to manually send a ReadDID command.
	 */
	private void createReadDidsDialog(){
		BaseAlertDialog getDtcsDialog = new ReadDidsDialog(this);
		getDtcsDialog.setListener(singleMessageListener);
		getDtcsDialog.show();
	}
	
	/**
	 * Creates a slider dialog, allowing the user to manually send a Slider command.
	 */
	private void createSliderDialog(){
		BaseAlertDialog getDtcsDialog = new SliderDialog(this);
		getDtcsDialog.setListener(singleMessageListener);
		getDtcsDialog.show();
	}
	
	/**
	 * Creates a scrollable message dialog, allowing the user to manually send a ScrollableMessage command.
	 */
	private void createScrollableMessageDialog(){
		BaseAlertDialog scrollableMessageDialog = new ScrollableMessageDialog(this);
		scrollableMessageDialog.setListener(singleMessageListener);
		scrollableMessageDialog.show();
	}
	
	/**
	 * Creates a set media clock timer dialog, allowing the user to manually send a SetMediaClockTimer command.
	 */
	private void createSetMediaClockTimerDialog(){
		BaseAlertDialog setMediaClockTimerDialog = new SetMediaClockTimerDialog(this);
		setMediaClockTimerDialog.setListener(singleMessageListener);
		setMediaClockTimerDialog.show();
	}
	
	/**
	 * Creates a put file dialog, allowing the user to manually send images through the PutFile command.
	 * 
	 * @param imagesAddedSoFar Images that have <b>not</b> already been added
	 */
	private void createPutFileDialog(List<SdlImageItem> imagesAddedSoFar){
		BaseAlertDialog putFileDialog = new PutFileDialog(this, imagesAddedSoFar);
		putFileDialog.setListener(multipleMessageListener);
		putFileDialog.show();
	}
	
	/**
	 * Creates a delete file dialog, allowing the user to manually delete files that have been added through the PutFile command.
	 * 
	 * @param imagesAddedSoFar The list of images that have been added so far
	 */
	private void createDeleteFileDialog(List<SdlImageItem> imagesAddedSoFar){
		BaseAlertDialog deleteFileDialog = new DeleteFileDialog(this, imagesAddedSoFar);
		deleteFileDialog.setListener(multipleMessageListener);
		deleteFileDialog.show();
	}
	
	/**
	 * Creates a set app icon dialog, allowing the user to manually set their app icon based on images that have been added through the PutFile command.
	 * 
	 * @param imagesAddedSoFar The list of images that have been added so far
	 */
	private void createSetAppIconDialog(List<SdlImageItem> imagesAddedSoFar){
		BaseAlertDialog setAppIconDialog = new SetAppIconDialog(this, imagesAddedSoFar);
		setAppIconDialog.setListener(singleMessageListener);
		setAppIconDialog.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		android.view.MenuItem connectToSdl = (android.view.MenuItem) menu.findItem(R.id.menu_connect);
		android.view.MenuItem disconnectFromSdl = (android.view.MenuItem) menu.findItem(R.id.menu_disconnect);
		android.view.MenuItem resetSdl = (android.view.MenuItem) menu.findItem(R.id.menu_reset);
		android.view.MenuItem clearListView = (android.view.MenuItem) menu.findItem(R.id.menu_clear_list);
		
		// show/hide connect/disconnect menu items
		boolean connected = tv_connectionStatus.getText().toString().contains(ConnectionStatus.CONNECTED.friendlyName);
		connectToSdl.setVisible(!connected); // if we are not connected, show the connect item
		disconnectFromSdl.setVisible(connected); // if we are connected, show the disconnect item
		resetSdl.setVisible(connected); // if we are connected, show reset SDL item
		
		boolean listHasItems = (listViewAdapter != null && listViewAdapter.getCount() > 0);
		clearListView.setVisible(listHasItems); // if the list has items, show the clear list item
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		int menuItemId = item.getItemId();
		switch(menuItemId){
		case R.id.menu_connect:
			showSdlConnectionDialog();
			return true;
		case R.id.menu_disconnect:
			sendMessageToService(Message.obtain(null, SdlService.ServiceMessages.DISCONNECT));
			return true;
		case R.id.menu_reset:
			sendMessageToService(Message.obtain(null, SdlService.ServiceMessages.RESET));
			return true;
		case R.id.menu_clear_list:
			clearSdlLog();
			return true;
		case R.id.menu_help:
			Intent helpIntent = new Intent(MainActivity.this, HelpActivity.class);
			startActivity(helpIntent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}

package com.livio.sdl.services;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.livio.sdl.IpAddress;
import com.livio.sdl.SdlBaseButton;
import com.livio.sdl.SdlBaseButton.OnClickListener;
import com.livio.sdl.SdlFunctionBankManager;
import com.livio.sdl.SdlFunctionButton;
import com.livio.sdl.SdlMenuButton;
import com.livio.sdltester.R;
import com.livio.sdltester.utils.UpCounter;
import com.smartdevicelink.exception.SmartDeviceLinkException;
import com.smartdevicelink.proxy.RPCRequest;
import com.smartdevicelink.proxy.RPCResponse;
import com.smartdevicelink.proxy.SmartDeviceLinkProxyALM;
import com.smartdevicelink.proxy.constants.Names;
import com.smartdevicelink.proxy.interfaces.IProxyListenerALM;
import com.smartdevicelink.proxy.rpc.AddCommand;
import com.smartdevicelink.proxy.rpc.AddCommandResponse;
import com.smartdevicelink.proxy.rpc.AddSubMenu;
import com.smartdevicelink.proxy.rpc.AddSubMenuResponse;
import com.smartdevicelink.proxy.rpc.AlertManeuverResponse;
import com.smartdevicelink.proxy.rpc.AlertResponse;
import com.smartdevicelink.proxy.rpc.ChangeRegistrationResponse;
import com.smartdevicelink.proxy.rpc.CreateInteractionChoiceSetResponse;
import com.smartdevicelink.proxy.rpc.DeleteCommandResponse;
import com.smartdevicelink.proxy.rpc.DeleteFileResponse;
import com.smartdevicelink.proxy.rpc.DeleteInteractionChoiceSetResponse;
import com.smartdevicelink.proxy.rpc.DeleteSubMenuResponse;
import com.smartdevicelink.proxy.rpc.DialNumberResponse;
import com.smartdevicelink.proxy.rpc.EndAudioPassThruResponse;
import com.smartdevicelink.proxy.rpc.GenericResponse;
import com.smartdevicelink.proxy.rpc.GetDTCsResponse;
import com.smartdevicelink.proxy.rpc.GetVehicleDataResponse;
import com.smartdevicelink.proxy.rpc.ListFilesResponse;
import com.smartdevicelink.proxy.rpc.OnAudioPassThru;
import com.smartdevicelink.proxy.rpc.OnButtonEvent;
import com.smartdevicelink.proxy.rpc.OnButtonPress;
import com.smartdevicelink.proxy.rpc.OnCommand;
import com.smartdevicelink.proxy.rpc.OnDriverDistraction;
import com.smartdevicelink.proxy.rpc.OnHMIStatus;
import com.smartdevicelink.proxy.rpc.OnLanguageChange;
import com.smartdevicelink.proxy.rpc.OnPermissionsChange;
import com.smartdevicelink.proxy.rpc.OnTBTClientState;
import com.smartdevicelink.proxy.rpc.OnVehicleData;
import com.smartdevicelink.proxy.rpc.PerformAudioPassThruResponse;
import com.smartdevicelink.proxy.rpc.PerformInteractionResponse;
import com.smartdevicelink.proxy.rpc.PutFileResponse;
import com.smartdevicelink.proxy.rpc.ReadDIDResponse;
import com.smartdevicelink.proxy.rpc.ResetGlobalPropertiesResponse;
import com.smartdevicelink.proxy.rpc.ScrollableMessageResponse;
import com.smartdevicelink.proxy.rpc.SetAppIconResponse;
import com.smartdevicelink.proxy.rpc.SetDisplayLayoutResponse;
import com.smartdevicelink.proxy.rpc.SetGlobalPropertiesResponse;
import com.smartdevicelink.proxy.rpc.SetMediaClockTimerResponse;
import com.smartdevicelink.proxy.rpc.ShowConstantTBTResponse;
import com.smartdevicelink.proxy.rpc.ShowResponse;
import com.smartdevicelink.proxy.rpc.SliderResponse;
import com.smartdevicelink.proxy.rpc.SpeakResponse;
import com.smartdevicelink.proxy.rpc.SubscribeButtonResponse;
import com.smartdevicelink.proxy.rpc.SubscribeVehicleDataResponse;
import com.smartdevicelink.proxy.rpc.UnsubscribeButtonResponse;
import com.smartdevicelink.proxy.rpc.UnsubscribeVehicleDataResponse;
import com.smartdevicelink.proxy.rpc.UpdateTurnListResponse;
import com.smartdevicelink.proxy.rpc.enums.HMILevel;
import com.smartdevicelink.proxy.rpc.enums.Language;
import com.smartdevicelink.transport.TCPTransportConfig;

/**
 * Performs all interactions with Smart Device Link in a long-running service that
 * clients can bind to in order to send information to the vehicle.
 *
 * @author Mike Burke
 *
 */
public class SdlService extends Service implements IProxyListenerALM{
	/* ********** Nested Classes ********** */
	
	/**
	 * Messages that can be received by a bound client.
	 *
	 * @author Mike Burke
	 *
	 */
	public static class ClientMessages{
		/**
		 * Message.what integer called when SDL has successfully created a connection.
		 */
		public static final int SDL_CONNECTED = 0;
		/**
		 * Message.what integer called when SDL has disconnected.
		 */
		public static final int SDL_DISCONNECTED = 1;
		/**
		 * Message.what integer called when a ServiceMessages.REQUEST_FOREGROUND_STATE message has been received.
		 */
		public static final int FOREGROUND_STATE_RECEIVED = 2;
		/**
		 * Message.what integer called when a RPCResponse result has been received.
		 */
		public static final int ON_MESSAGE_RESULT = 3;
		/**
		 * Message.what integer called when a ServiceMessages.REQUEST_SUBMENU_LIST message has been received.
		 */
		public static final int SUBMENU_LIST_RECEIVED = 4;
	}
	
	/**
	 * Messages that can be sent to the service by a bound client.
	 *
	 * @author Mike Burke
	 *
	 */
	public static class ServiceMessages{
		/**
		 * Message.what integer used to register your activity as a client bound to this service.
		 */
		public static final int REGISTER_CLIENT = 1;
		/**
		 * Message.what integer used to unregister your activity as a client bound to this service.
		 */
		public static final int UNREGISTER_CLIENT = 2;
		/**
		 * Message.what integer commanding the service to attempt an SDL connection.
		 */
		public static final int CONNECT = 3;
		/**
		 * Message.what integer commanding the service to disconnect an existing SDL connection.
		 */
		public static final int DISCONNECT = 4;
		/**
		 * Message.what integer commanding the service to send an RPCRequest.
		 */
		public static final int SEND_MESSAGE = 5;
		/**
		 * Message.what integer commanding the service to respond with the current foreground state of the applicaiton.
		 */
		public static final int REQUEST_FOREGROUND_STATE = 6;
		/**
		 * Message.what integer commanding the service to respond with a list of existing submenus that have been added.
		 */
		public static final int REQUEST_SUBMENU_LIST = 7;
	}
	
	/**
	 * Messages that can be shown on the vehicle head-unit.  Any static
	 * text that your app would like to show on the head-unit can be defined
	 * in this class.
	 *
	 * @author Mike Burke
	 *
	 */
	protected static class MetadataMessages{
		public static final String BLANK = " ";
		public static final String APP_NAME = "Livio SDL Tester";
		public static final String APP_SLOGAN = "More Music, Less Work";
	}
	
	/* ********** Static variables ********** */
	private static final boolean IS_MEDIA_APP = false;					/*		All of these variables		*/
	private static final Language DEFAULT_LANGUAGE = Language.EN_US;	/*		are needed to start up		*/
	private static final String APP_ID = "appId";						/*		the SDL proxy object		*/
	private static final boolean WIFI_AUTO_RECONNECT = true;			/*									*/
	
	protected static boolean debug = false;
	
	/* ********** Instance variables ********** */
	protected List<Messenger> clients = null; // list of bound clients
	
	protected UpCounter correlationIdGenerator; // id generator for correlation ids
	protected UpCounter commandIdGenerator; // id generator for commands & submenus
	protected boolean appHasForeground = false; // tracks app's foreground state
	protected boolean appIsLoaded = false; // set to true once the app gets its first HMI update
	
	private SparseArray<SdlBaseButton> functionButtons = new SparseArray<SdlBaseButton>(); // maps a function button id to its object
	private SparseArray<SdlBaseButton> menuButtons = new SparseArray<SdlBaseButton>(); // maps a menu button id to its object
	
	protected SmartDeviceLinkProxyALM sdlProxy = null; // the proxy object which sends our requests and receives responses
	protected IpAddress currentIp; // keeps track of the current ip address in case we need to reset
	
	
	/* ********** Messenger methods to & from the client ********** */
	
	protected final Messenger messenger = new Messenger(new IncomingHandler());
	
	@SuppressLint("HandlerLeak")
	protected class IncomingHandler extends Handler{
		@Override
		public void handleMessage(Message msg){
			switch(msg.what){
				case ServiceMessages.REGISTER_CLIENT:
					if(clients == null){
						clients = new ArrayList<Messenger>();
					}
					
					clients.add(msg.replyTo);
					break;
				case ServiceMessages.UNREGISTER_CLIENT:
					if(clients != null && clients.size() > 0){
						clients.remove(msg.replyTo);
					}
					break;
				case ServiceMessages.CONNECT:
					IpAddress inputIp = (IpAddress) msg.obj;
					startSdlProxy(inputIp);
					break;
				case ServiceMessages.DISCONNECT:
					stopSdlProxy();
					break;
				case ServiceMessages.SEND_MESSAGE:
					sendSdlCommand((RPCRequest) msg.obj);
					break;
				case ServiceMessages.REQUEST_FOREGROUND_STATE:
					foregroundStateRequested(appHasForeground);
					break;
				case ServiceMessages.REQUEST_SUBMENU_LIST:
					submenuListRequested();
					break;
				default:
					break;
			}
		}
	}
	
	/**
	 * Sends a message to all registered clients.
	 * 
	 * @param msg The message to send
	 */
	protected void sendMessage(Message msg){
		if(clients != null){
			for(Messenger client : clients){
				try {
					client.send(msg);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Sends an RPCResponse message to all registered clients.
	 * 
	 * @param response The response to send
	 */
	protected void sendMessageResponse(RPCResponse response){
		Message msg = Message.obtain(null, ClientMessages.ON_MESSAGE_RESULT);
		msg.obj = response;
		sendMessage(msg);
	}
	
	/**
	 * Sends the current app foreground state to all registered clients. 
	 * 
	 * @param foregroundState The current foreground state
	 */
	protected void foregroundStateRequested(boolean foregroundState){
		Message msg = Message.obtain(null, ClientMessages.FOREGROUND_STATE_RECEIVED);
		msg.obj = foregroundState;
		sendMessage(msg);
	}
	
	protected void submenuListRequested(){
		Message msg = Message.obtain(null, ClientMessages.SUBMENU_LIST_RECEIVED);
		msg.obj = getSubmenuList();
		sendMessage(msg);
	}
	

	/* ********** Android service life cycle methods ********** */
	@Override
	public void onCreate() {
		log("onCreate called");
		correlationIdGenerator = new UpCounter(100);
		commandIdGenerator = new UpCounter(100);
		super.onCreate();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return messenger.getBinder();
	}
	
	
	/* ********** Proxy life cycle methods ********** */
	/**
	 * Starts up SDL if it isn't already started.  If SDL is already started, this method does nothing.
	 * To reset the proxy, use the resetProxy method.
	 * 
	 * @param inputIp The IP address to attempt a connection on
	 */
	protected void startSdlProxy(IpAddress inputIp){
		if(sdlProxy == null){
			sdlProxy = createSdlProxyObject(inputIp);
		}
	}
	
	/**
	 * Creates a SmartDeviceLinkProxyALM object and automatically attempts a connection
	 * to the input IP address.
	 * 
	 * @param inputIp The IP address to attempt a connection on
	 * @return The created SmartDeviceLinkProxyALM object
	 */
	protected SmartDeviceLinkProxyALM createSdlProxyObject(IpAddress inputIp){
		int tcpPort = Integer.parseInt(inputIp.getTcpPort());
		String ipAddress = inputIp.getIpAddress();
		String appName = getResources().getString(R.string.app_name);
		
		SmartDeviceLinkProxyALM result = null;
		try {
			result = new SmartDeviceLinkProxyALM((IProxyListenerALM)this, null, appName, null, null,
					null, IS_MEDIA_APP, null, DEFAULT_LANGUAGE, DEFAULT_LANGUAGE, APP_ID,
					null, false, false, new TCPTransportConfig(tcpPort, ipAddress, WIFI_AUTO_RECONNECT));
			currentIp = inputIp;
		} catch (SmartDeviceLinkException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * Disposes of any current proxy object if it exists and automatically creates a new
	 * proxy connection to the previously connected IP address.
	 */
	protected void resetProxy(){
		if(sdlProxy != null){
			try {
				sdlProxy.dispose();
			} catch (SmartDeviceLinkException e) {
				e.printStackTrace();
			}
			sdlProxy = null;
		}
		
		startSdlProxy(currentIp);
	}
	
	/**
	 * Disposes of any current proxy object and sets the object to null so it cannot be
	 * used again.
	 */
	protected void stopSdlProxy(){
		if(sdlProxy != null){
			try {
				sdlProxy.dispose();
			} catch (SmartDeviceLinkException e) {
				e.printStackTrace();
			}
		}
		
		sdlProxy = null;
	}
	
	/* ********** Proxy communication methods ********** */
	/**
	 * Sends an RPCRequest to the current SDL connection.
	 * 
	 * @param command The request to send
	 */
	protected void sendSdlCommand(RPCRequest command){
		if(command == null){
			throw new NullPointerException("Cannot send a null command.");
		}
		
		if(sdlProxy == null){
			throw new IllegalStateException("Proxy object is null, so no commands can be sent.");
		}
		
		// if this is an AddCommand or AddSubMenu request, we must give the object an id before sending it in order to receive callbacks
		setCommandId(command);
		
		// give the command a correlation id and send it off
		command.setCorrelationID(correlationIdGenerator.next());
		try {
			sdlProxy.sendRPCRequest(command);
		} catch (SmartDeviceLinkException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Gives an id to the input RPCRequest command if needed and adds the command to a sparse array for tracking.
	 * This method is only applicable to AddCommand & AddSubMenu requests as of SDL 2.0.
	 * 
	 * @param command The command that potentially needs a command id
	 */
	protected void setCommandId(RPCRequest command){
		String name = command.getFunctionName();
		
		if(name.equals(Names.AddCommand)){
			AddCommand addCommand = (AddCommand) command;
			addCommand.setCmdID(commandIdGenerator.next());
			SdlBaseButton addCommandButton = createBaseButton(addCommand);
			functionButtons.put(addCommandButton.getId(), addCommandButton);
		}
		else if(name.equals(Names.AddSubMenu)){
			AddSubMenu addSubMenu = (AddSubMenu) command;
			addSubMenu.setMenuID(commandIdGenerator.next());
			SdlBaseButton addSubMenuButton = createBaseButton(addSubMenu);
			menuButtons.put(addSubMenuButton.getId(), addSubMenuButton);
		}
	}
	
	/**
	 * Translates the AddCommand object into a SdlBaseButton object, complete with a click listener.
	 * 
	 * @param command The command to translate
	 * @return The translated SdlBaseButton object
	 */
	protected SdlBaseButton createBaseButton(AddCommand command){
		final String name = command.getMenuParams().getMenuName();
		final SdlBaseButton result = new SdlFunctionButton(name, command.getCmdID(), new OnClickListener(){
			@Override
			public void onClick(int parent, int id) {
				Toast.makeText(SdlService.this, name + " clicked!", Toast.LENGTH_SHORT).show();
			}
		});
		
		return result;
	}
	
	/**
	 * Translates the AddSubMenu object into a SdlBaseButton object.
	 * 
	 * @param command The command to translate
	 * @return The translated SdlBaseButton object
	 */
	protected SdlBaseButton createBaseButton(AddSubMenu command){
		final String name = command.getMenuName();
		final SdlBaseButton result = new SdlMenuButton(name, command.getMenuID());
		return result;
	}
	
	protected List<SdlBaseButton> getSubmenuList(){
		final int listSize = menuButtons.size();
		List<SdlBaseButton> result = new ArrayList<SdlBaseButton>(listSize);
		
		for(int i=0; i<listSize; i++){
			SdlBaseButton oldButton = menuButtons.valueAt(i);

			// make a clone of the button object so clients don't have edit access to our real object
			SdlBaseButton newButton = new SdlBaseButton(oldButton.getName(), oldButton.getId(), oldButton.isMenuButton());
			result.add(newButton);
		}
		
		return result;
	}
	
	
	
	
	
	
	
	/**
	 * Called when app is first loaded and receives full HMI control 
	 */
	private void onAppLoaded(){
		Message msg = Message.obtain(null, ClientMessages.SDL_CONNECTED);
		sendMessage(msg);
	}

	/* ********** IProxyListenerALM interface methods ********** */
	
	/* Most useful callbacks */
	@Override public void onOnHMIStatus(OnHMIStatus newStatus) {
		HMILevel hmiLevel = newStatus.getHmiLevel();
		if(hmiLevel == HMILevel.HMI_FULL){
			if(!appIsLoaded){
				onAppLoaded();
				appIsLoaded = true;
			}
			appHasForeground = true;
		}
		else{
			appHasForeground = false;
		}
	}
	@Override public void onOnCommand(OnCommand notification) {}
	@Override public void onOnButtonPress(OnButtonPress notification) {}
	
	/* Not very useful callbacks */
	@Override public void onOnPermissionsChange(OnPermissionsChange notification) {}
	@Override public void onOnVehicleData(OnVehicleData notification) {}
	@Override public void onOnAudioPassThru(OnAudioPassThru notification) {}
	@Override public void onOnLanguageChange(OnLanguageChange notification) {}
	@Override public void onOnDriverDistraction(OnDriverDistraction notification) {}
	@Override public void onOnTBTClientState(OnTBTClientState notification) {}
	@Override public void onError(String info, Exception e) {}
	@Override public void onOnButtonEvent(OnButtonEvent notification) {}
	@Override public void onProxyClosed(String info, Exception e) {}
	
	/* Message responses */
	@Override public void onGenericResponse(GenericResponse response) {sendMessageResponse(response);}
	@Override public void onAddCommandResponse(AddCommandResponse response) {sendMessageResponse(response);}
	@Override public void onAddSubMenuResponse(AddSubMenuResponse response) {sendMessageResponse(response);}
	@Override public void onCreateInteractionChoiceSetResponse(CreateInteractionChoiceSetResponse response) {sendMessageResponse(response);}
	@Override public void onAlertResponse(AlertResponse response) {sendMessageResponse(response);}
	@Override public void onDeleteCommandResponse(DeleteCommandResponse response) {sendMessageResponse(response);}
	@Override public void onDeleteInteractionChoiceSetResponse(DeleteInteractionChoiceSetResponse response) {sendMessageResponse(response);}
	@Override public void onDeleteSubMenuResponse(DeleteSubMenuResponse response) {sendMessageResponse(response);}
	@Override public void onPerformInteractionResponse(PerformInteractionResponse response) {sendMessageResponse(response);}
	@Override public void onResetGlobalPropertiesResponse(ResetGlobalPropertiesResponse response) {sendMessageResponse(response);}
	@Override public void onSetGlobalPropertiesResponse(SetGlobalPropertiesResponse response) {sendMessageResponse(response);}
	@Override public void onSetMediaClockTimerResponse(SetMediaClockTimerResponse response) {sendMessageResponse(response);}
	@Override public void onShowResponse(ShowResponse response) {sendMessageResponse(response);}
	@Override public void onSpeakResponse(SpeakResponse response) {sendMessageResponse(response);}
	@Override public void onSubscribeButtonResponse(SubscribeButtonResponse response) {sendMessageResponse(response);}
	@Override public void onUnsubscribeButtonResponse(UnsubscribeButtonResponse response) {sendMessageResponse(response);}
	@Override public void onSubscribeVehicleDataResponse(SubscribeVehicleDataResponse response) {sendMessageResponse(response);}
	@Override public void onUnsubscribeVehicleDataResponse(UnsubscribeVehicleDataResponse response) {sendMessageResponse(response);}
	@Override public void onGetVehicleDataResponse(GetVehicleDataResponse response) {sendMessageResponse(response);}
	@Override public void onReadDIDResponse(ReadDIDResponse response) {sendMessageResponse(response);}
	@Override public void onGetDTCsResponse(GetDTCsResponse response) {sendMessageResponse(response);}
	@Override public void onPerformAudioPassThruResponse(PerformAudioPassThruResponse response) {sendMessageResponse(response);}
	@Override public void onEndAudioPassThruResponse(EndAudioPassThruResponse response) {sendMessageResponse(response);}
	@Override public void onPutFileResponse(PutFileResponse response) {sendMessageResponse(response);}
	@Override public void onDeleteFileResponse(DeleteFileResponse response) {sendMessageResponse(response);}
	@Override public void onListFilesResponse(ListFilesResponse response) {sendMessageResponse(response);}
	@Override public void onSetAppIconResponse(SetAppIconResponse response) {sendMessageResponse(response);}
	@Override public void onScrollableMessageResponse(ScrollableMessageResponse response) {sendMessageResponse(response);}
	@Override public void onChangeRegistrationResponse(ChangeRegistrationResponse response) {sendMessageResponse(response);}
	@Override public void onSetDisplayLayoutResponse(SetDisplayLayoutResponse response) {sendMessageResponse(response);}
	@Override public void onSliderResponse(SliderResponse response) {sendMessageResponse(response);}
	@Override public void onAlertManeuverResponse(AlertManeuverResponse response) {sendMessageResponse(response);}
	@Override public void onShowConstantTBTResponse(ShowConstantTBTResponse response) {sendMessageResponse(response);}
	@Override public void onUpdateTurnListResponse(UpdateTurnListResponse response) {sendMessageResponse(response);}
	@Override public void onDialNumberResponse(DialNumberResponse response) {sendMessageResponse(response);}
	

	/* ********** Debug & log methods ********** */
	/**
	 * Enables debug mode for this class and any classes used in this class.
	 * 
	 * @param enable Enable flag for debug mode
	 */
	public static void setDebug(boolean enable){
		debug = enable;
		SdlFunctionBankManager.setDebug(enable);
	}
	
	private static void log(String msg){
		if(debug){
			Log.d("SdlService", msg);
		}
	}
}

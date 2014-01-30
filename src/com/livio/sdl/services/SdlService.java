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

import com.livio.sdl.IpAddress;
import com.livio.sdl.SdlFunctionBankManager;
import com.livio.sdltester.R;
import com.livio.sdltester.utils.UpCounter;
import com.smartdevicelink.exception.SmartDeviceLinkException;
import com.smartdevicelink.proxy.RPCRequest;
import com.smartdevicelink.proxy.RPCResponse;
import com.smartdevicelink.proxy.SmartDeviceLinkProxyALM;
import com.smartdevicelink.proxy.interfaces.IProxyListenerALM;
import com.smartdevicelink.proxy.rpc.AddCommandResponse;
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

public class SdlService extends Service implements IProxyListenerALM{
	/*********** Nested Classes ***********/
	
	public static class ClientMessages{
		public static final int SDL_CONNECTED = 0;
		public static final int SDL_DISCONNECTED = 1;
		public static final int ON_FOREGROUND_STATE = 2;
		public static final int ON_MESSAGE_RESULT = 3;
	}
	
	public static class ServiceMessages{
		public static final int REGISTER_CLIENT = 1;
		public static final int UNREGISTER_CLIENT = 2;
		public static final int CONNECT = 3;
		public static final int DISCONNECT = 4;
		public static final int SEND_MESSAGE = 5;
		public static final int REQUEST_FOREGROUND_STATE = 6;
	}
	
	protected static class MetadataMessages{
		public static final String BLANK = " ";
		public static final String APP_NAME = "Livio SDL Tester";
		public static final String APP_SLOGAN = "More Music, Less Work";
	}
	
	/*********** Static variables ***********/
	private static final boolean IS_MEDIA_APP = false;
	private static final Language DEFAULT_LANGUAGE = Language.EN_US;
	private static final String APP_ID = "appId";
	private static final boolean WIFI_AUTO_RECONNECT = true;
	
	protected static boolean debug = false;
	
	/*********** Instance variables ***********/
	protected List<Messenger> clients = null; // list of bound clients
	
	protected UpCounter correlationIdGenerator; // id generator for correlation ids
	protected boolean appHasForeground = false; // tracks app's foreground state
	protected boolean appIsLoaded = false; // set to true once the app gets its first HMI update
	
	protected SmartDeviceLinkProxyALM sdlProxy = null;
	protected IpAddress currentIp;
	
	
	/*********** Messenger methods to & from the client ***********/
	
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
					RPCRequest rpcObject = (RPCRequest) msg.obj;
					sendSdlCommand(rpcObject);
					break;
				case ServiceMessages.REQUEST_FOREGROUND_STATE:
					foregroundStateRequested();
					break;
				default:
					break;
			}
		}
	}
	
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
	
	protected void sendMessageResponse(RPCResponse response){
		Message msg = Message.obtain(null, ClientMessages.ON_MESSAGE_RESULT);
		msg.obj = response;
		sendMessage(msg);
	}
	

	/*********** Android service life cycle methods ***********/
	@Override
	public void onCreate() {
		log("onCreate called");
		correlationIdGenerator = new UpCounter(100);
		super.onCreate();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return messenger.getBinder();
	}
	
	
	/*********** Proxy life cycle methods ***********/
	protected void startSdlProxy(IpAddress inputIp){
		if(sdlProxy == null){
			sdlProxy = createSdlProxyObject(inputIp);
		}
	}
	
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
	
	public void resetProxy(){
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
	
	public void stopSdlProxy(){
		if(sdlProxy != null){
			try {
				sdlProxy.dispose();
			} catch (SmartDeviceLinkException e) {
				e.printStackTrace();
			}
		}
		
		sdlProxy = null;
	}
	
	/*********** Proxy communication methods ***********/
	protected void sendSdlCommand(RPCRequest command){
		if(command == null){
			throw new NullPointerException("Cannot send a null command.");
		}
		
		if(sdlProxy == null){
			throw new IllegalStateException("Proxy object is null, so no commands can be sent.");
		}
		
		command.setCorrelationID(correlationIdGenerator.next());
		try {
			sdlProxy.sendRPCRequest(command);
		} catch (SmartDeviceLinkException e) {
			e.printStackTrace();
		}
	}
	
	protected void foregroundStateRequested(){
		Message msg = Message.obtain(null, ClientMessages.ON_FOREGROUND_STATE);
		msg.obj = appHasForeground;
		sendMessage(msg);
	}
	
	
	
	
	
	
	
	
	
	// called when app is first loaded and receives full HMI control
	private void onAppLoaded(){
		Message msg = Message.obtain(null, ClientMessages.SDL_CONNECTED);
		sendMessage(msg);
	}

	/*********** IProxyListenerALM interface methods ***********/
	
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
	

	/*********** Debug & log methods ***********/
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

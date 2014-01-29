package com.livio.sdl.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.util.SparseArray;

import com.livio.sdl.IpAddress;
import com.livio.sdl.SdlBaseButton;
import com.livio.sdl.SdlFunctionBank;
import com.livio.sdl.SdlFunctionBankManager;
import com.livio.sdl.SdlHandler;
import com.livio.sdl.SdlShow;
import com.livio.sdl.managers.SdlManager;
import com.livio.sdltester.R;
import com.livio.sdltester.utils.UpCounter;
import com.smartdevicelink.proxy.rpc.OnHMIStatus;
import com.smartdevicelink.proxy.rpc.Show;
import com.smartdevicelink.proxy.rpc.enums.AudioStreamingState;
import com.smartdevicelink.proxy.rpc.enums.ButtonName;
import com.smartdevicelink.proxy.rpc.enums.HMILevel;

public class SdlService extends Service implements SdlHandler.Listener{
	private static final boolean IMAGES_FOR_BUTTONS_ENABLED = false;
	
	private static boolean debug = false;
	
	public static class ClientMessages{
		public static final int SDL_CONNECTED = 0;
		public static final int SDL_DISCONNECTED = 1;
	}
	
	public static class ServiceMessages{
		public static final int REGISTER_CLIENT = 1;
		public static final int UNREGISTER_CLIENT = 2;
		public static final int CONNECT = 3;
		public static final int DISCONNECT = 4;
		public static final int SHOW = 5;
	}
	
	
	private SparseArray<Bitmap> imageCache = null;
	private List<Messenger> clients = null;
	
	private boolean receiverRegistered = false;
	
	private Stack<SdlFunctionBank> menuStack = new Stack<SdlFunctionBank>();
	
	private SdlHandler sdlListener = new SdlHandler(this);
	
	private UpCounter commandIdGenerator;
	
	private boolean appIsVisible = false;
	private AudioStreamingState audioState = AudioStreamingState.NOT_AUDIBLE;
	
	private SdlShow homeMetadata;
	
	protected static class BankNames{
		public static final String HOME = "Home";
	}
	
	private enum TransportControl{
		PLAY,
		PAUSE,
		PLAY_PAUSE_TOGGLE,
		STOP,
		FAST_FORWARD,
		REWIND,
		;
	}
	
	// enum that maps function buttons to their text & image resources
	protected enum Buttons{
		BACK("Back", R.drawable.close_button),
		
		//future buttons go here
		;
		
		private final String NAME;
		private final int IMAGE_ID;
		
		private Buttons(String name, int imageId){
			this.NAME = name;
			this.IMAGE_ID = imageId;
		}
		
		public String getFriendlyName(){
			return this.NAME;
		}
		
		public int getImageId(){
			return this.IMAGE_ID;
		}
	}
	
	protected static class MetadataMessages{
		public static final String BLANK = " ";
		public static final String APP_NAME = "Livio SDL Tester";
		public static final String APP_SLOGAN = "More Music, Less Work";
	}
	
	private final Messenger messenger = new Messenger(new IncomingHandler());
	
	@SuppressLint("HandlerLeak")
	private class IncomingHandler extends Handler{
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
					SdlManager.getInstance().startSdlProxy(inputIp);
					break;
				case ServiceMessages.DISCONNECT:
					// TODO
					break;
				case ServiceMessages.SHOW:
					Show showObject = (Show) msg.obj;
					SdlManager.getInstance().show(showObject);
					break;
				default:
					break;
			}
		}
	}
	
	private void pushBankToMenuStack(){
		SdlFunctionBank bank = SdlFunctionBankManager.getInstance().getCurrentBank();
		menuStack.push(bank);
	}
	
	private void popBankFromMenuStack(){
		SdlFunctionBank bank = menuStack.pop();
		SdlFunctionBankManager.getInstance().setCurrentBank(bank.getName());
		SdlManager.getInstance().clearFunctionBank();
		SdlManager.getInstance().addFunctionBank(bank);
	}
	
	private void createHomeBank(){
		
	}
	
	private SdlBaseButton backButton;

	private void createBackButton(){
		backButton = new SdlBaseButton(Buttons.BACK.getFriendlyName(), commandIdGenerator.next(), false,
				new SdlBaseButton.OnClickListener(){
					@Override
					public void onClick(int parentId, int buttonId){
						popBankFromMenuStack();
					}
		});
	}
	
	private SdlShow currentShow;
	
	private void showBank(String bankName){
		log(new StringBuilder().append("showing bank: ").append(bankName).toString());
		
		SdlFunctionBank currentBank = SdlFunctionBankManager.getInstance().getCurrentBank();
		if(currentBank != null && currentBank.getName().equals(bankName)){
			return;
		}
		
		// TODO
		if(bankName.equals(BankNames.HOME)){
			currentBank = SdlFunctionBankManager.getInstance().setCurrentBank(BankNames.HOME);
			if(currentShow == null){
				currentShow = homeMetadata;
				SdlManager.getInstance().show(homeMetadata);
			}
			SdlManager.getInstance().clearFunctionBank();
			SdlManager.getInstance().addFunctionBank(currentBank);
		}
	
	}
	
	private void updateMetadata(String line1, String line2, Bitmap image){
		currentShow = new SdlShow(line1, line2, image);
		SdlManager.getInstance().show(currentShow);
	}
	
	@Override
	public void onSdlConnected(){
		log("onSdlConnected");
					
		// tell UI we're connected
		sendMessage(ClientMessages.SDL_CONNECTED);
	}
	
	@Override
	public void onSdlDisconnected(){
		sendMessage(ClientMessages.SDL_DISCONNECTED);
		stopSelf();
		SdlManager.getInstance().resetProxy(); // TODO - is this the right thing to do here?
	}
	
	
	private boolean firstHmiChange = true;
	
	@Override
	public void onHmiChange(OnHMIStatus newStatus) {
		if(firstHmiChange){

			// tell UI we're connected
			sendMessage(ClientMessages.SDL_CONNECTED);
			
			if(homeMetadata == null){
				homeMetadata = new SdlShow(MetadataMessages.APP_NAME, MetadataMessages.APP_SLOGAN);
			}
			firstHmiChange = false;
		}
		HMILevel newLevel = newStatus.getHmiLevel();
		if(newLevel == HMILevel.HMI_FULL){
			if(!appIsVisible){
				appBroughtToForeground();
				appIsVisible = true;
			}
		}
		else{
			appIsVisible = false;
		}
		
		if(audioState != newStatus.getAudioStreamingState()){
			// audio state changed - either pause or play
			audioState = newStatus.getAudioStreamingState();
			if(audioState == AudioStreamingState.AUDIBLE){
				
			}
			// audio state will be not-audible when user switches to CD for example.
			else if(audioState == AudioStreamingState.NOT_AUDIBLE){
				
			}
			else if(audioState == AudioStreamingState.ATTENUATED){
				
			}
		}
	}

	@Override
	public void onButtonClick(int buttonId) {
		SdlFunctionBank currBank = SdlFunctionBankManager.getInstance().getCurrentBank(); 
		if(currBank != null){
			int parentId = currBank.getId(); 
			SdlFunctionBankManager.getInstance().processClick(parentId, buttonId);
			log(new StringBuilder().append("Button with id ").append(buttonId).append(" and parent id ").append(parentId).append(" was clicked.").toString());
		}
	}

	@Override
	public void onMediaButtonClick(ButtonName button) {
		log("onMediaButtonClick");
		
		switch(button){
		case OK:
			onTransportControlReceived(TransportControl.PLAY_PAUSE_TOGGLE);
			break;
		case SEEKLEFT:
			onTransportControlReceived(TransportControl.REWIND);
			break;
		case SEEKRIGHT:
			onTransportControlReceived(TransportControl.FAST_FORWARD);
			break;
		default:
			break;
		}
	}

	@Override
	public void onPerformInteractionCanceled() {
//		showBank(BankNames.HOME);
	}
	
	private void sendMessage(int action){
		if(clients != null){
			for(Messenger client : clients){
				try {
					client.send(Message.obtain(null, action));
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void onTransportControlReceived(TransportControl input){
		log(new StringBuilder().append("onTransportControlReceived with input ").append(input).toString());
		
		switch(input){
		case PLAY:
			
			break;
		case PAUSE:
			
			break;
		case PLAY_PAUSE_TOGGLE:
			
			break;
		case STOP:
			
			break;
		case FAST_FORWARD:
			
			break;
		case REWIND:
			
			break;
		default:
			break;
		}
	}
	
	private void appBroughtToForeground(){
		log("appBroughtToForeground");
		Show show = new Show();
		show.setMainField1(MetadataMessages.APP_NAME);
		show.setMainField2(MetadataMessages.APP_SLOGAN);
		SdlManager.getInstance().show(show);
		
		// TODO - bring this shit back in?
//		createHomeBank();
//		
//		// start session with home bank
//		showBank(BankNames.HOME);
//		
//		// once the buttons are loaded, we have to subscribe to them.
//		SdlManager.getInstance().subscribeToMediaButtons();
	}
	
	@Override
	public void onCreate() {
		log("onCreate called");
		registerReceivers();
		commandIdGenerator = new UpCounter();
		
		SdlManager.getInstance().setContext(this);
		SdlManager.getInstance().addListener(sdlListener);
		super.onCreate();
	}
	
	private void registerReceivers(){
		IntentFilter filter = new IntentFilter();
//		filter.addAction(MusicService.BroadcastActions.PLAY_STATUS_UPDATE);
//		filter.addAction(MusicService.BroadcastActions.SONG_PLAYING);
//		filter.addAction(MusicService.BroadcastActions.SONG_COMPLETE);
//		registerReceiver(musicServiceReceiver, filter);
		receiverRegistered = true;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		log("onDestroy called");
		if(receiverRegistered){
//			unregisterReceiver(musicServiceReceiver);
			receiverRegistered = false;
		}
		shutdown();
		commandIdGenerator.reset();
		super.onDestroy();
	}
	
	@SuppressWarnings("unused")
	private void initialize(){
		if(IMAGES_FOR_BUTTONS_ENABLED && imageCache == null){
			createImageCache();
		}
	}
	
	@SuppressWarnings("unused")
	private void shutdown(){
		if(IMAGES_FOR_BUTTONS_ENABLED && imageCache != null){
			imageCache.clear();
			imageCache = null;
		}
	}
	
	private void createImageCache(){
		imageCache = new SparseArray<Bitmap>(Buttons.values().length);
		
		// add function button images to a cache
		for(Buttons button : Buttons.values()){
			final int IMAGE_ID = button.IMAGE_ID;
			imageCache.put(IMAGE_ID, BitmapFactory.decodeResource(getResources(), IMAGE_ID));
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return messenger.getBinder();
	}
	
	public static void setDebug(boolean enable){
		debug = enable;
		SdlManager.setDebug(enable);
		SdlFunctionBankManager.setDebug(enable);
	}
	
	private static void log(String msg){
		if(debug){
			Log.d("SdlService", msg);
		}
	}



}

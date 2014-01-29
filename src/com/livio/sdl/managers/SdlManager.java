package com.livio.sdl.managers;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.util.Log;
import android.util.SparseArray;

import com.livio.sdl.IpAddress;
import com.livio.sdl.SdlBaseButton;
import com.livio.sdl.SdlFunctionBank;
import com.livio.sdl.SdlHandler;
import com.livio.sdl.SdlShow;
import com.livio.sdl.enums.SdlTransportType;
import com.livio.sdltester.R;
import com.livio.sdltester.utils.UpCounter;
import com.smartdevicelink.exception.SmartDeviceLinkException;
import com.smartdevicelink.proxy.RPCRequest;
import com.smartdevicelink.proxy.SmartDeviceLinkProxyALM;
import com.smartdevicelink.proxy.TTSChunkFactory;
import com.smartdevicelink.proxy.interfaces.IProxyListenerALM;
import com.smartdevicelink.proxy.rpc.AddCommand;
import com.smartdevicelink.proxy.rpc.AddCommandResponse;
import com.smartdevicelink.proxy.rpc.AddSubMenu;
import com.smartdevicelink.proxy.rpc.AddSubMenuResponse;
import com.smartdevicelink.proxy.rpc.AlertManeuverResponse;
import com.smartdevicelink.proxy.rpc.AlertResponse;
import com.smartdevicelink.proxy.rpc.ChangeRegistrationResponse;
import com.smartdevicelink.proxy.rpc.Choice;
import com.smartdevicelink.proxy.rpc.CreateInteractionChoiceSet;
import com.smartdevicelink.proxy.rpc.CreateInteractionChoiceSetResponse;
import com.smartdevicelink.proxy.rpc.DeleteCommand;
import com.smartdevicelink.proxy.rpc.DeleteCommandResponse;
import com.smartdevicelink.proxy.rpc.DeleteFileResponse;
import com.smartdevicelink.proxy.rpc.DeleteInteractionChoiceSet;
import com.smartdevicelink.proxy.rpc.DeleteInteractionChoiceSetResponse;
import com.smartdevicelink.proxy.rpc.DeleteSubMenuResponse;
import com.smartdevicelink.proxy.rpc.DialNumberResponse;
import com.smartdevicelink.proxy.rpc.EndAudioPassThruResponse;
import com.smartdevicelink.proxy.rpc.GenericResponse;
import com.smartdevicelink.proxy.rpc.GetDTCsResponse;
import com.smartdevicelink.proxy.rpc.GetVehicleDataResponse;
import com.smartdevicelink.proxy.rpc.ListFilesResponse;
import com.smartdevicelink.proxy.rpc.MenuParams;
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
import com.smartdevicelink.proxy.rpc.PerformInteraction;
import com.smartdevicelink.proxy.rpc.PerformInteractionResponse;
import com.smartdevicelink.proxy.rpc.PutFile;
import com.smartdevicelink.proxy.rpc.PutFileResponse;
import com.smartdevicelink.proxy.rpc.ReadDIDResponse;
import com.smartdevicelink.proxy.rpc.ResetGlobalPropertiesResponse;
import com.smartdevicelink.proxy.rpc.ScrollableMessageResponse;
import com.smartdevicelink.proxy.rpc.SetAppIconResponse;
import com.smartdevicelink.proxy.rpc.SetDisplayLayoutResponse;
import com.smartdevicelink.proxy.rpc.SetGlobalPropertiesResponse;
import com.smartdevicelink.proxy.rpc.SetMediaClockTimerResponse;
import com.smartdevicelink.proxy.rpc.Show;
import com.smartdevicelink.proxy.rpc.ShowConstantTBTResponse;
import com.smartdevicelink.proxy.rpc.ShowResponse;
import com.smartdevicelink.proxy.rpc.SliderResponse;
import com.smartdevicelink.proxy.rpc.SpeakResponse;
import com.smartdevicelink.proxy.rpc.SubscribeButtonResponse;
import com.smartdevicelink.proxy.rpc.SubscribeVehicleDataResponse;
import com.smartdevicelink.proxy.rpc.TTSChunk;
import com.smartdevicelink.proxy.rpc.UnsubscribeButtonResponse;
import com.smartdevicelink.proxy.rpc.UnsubscribeVehicleDataResponse;
import com.smartdevicelink.proxy.rpc.UpdateTurnListResponse;
import com.smartdevicelink.proxy.rpc.enums.ButtonName;
import com.smartdevicelink.proxy.rpc.enums.FileType;
import com.smartdevicelink.proxy.rpc.enums.HMILevel;
import com.smartdevicelink.proxy.rpc.enums.ImageType;
import com.smartdevicelink.proxy.rpc.enums.InteractionMode;
import com.smartdevicelink.proxy.rpc.enums.Language;
import com.smartdevicelink.proxy.rpc.enums.Result;
import com.smartdevicelink.proxy.rpc.enums.SpeechCapabilities;
import com.smartdevicelink.proxy.rpc.enums.TextAlignment;
import com.smartdevicelink.transport.TCPTransportConfig;

public class SdlManager implements IProxyListenerALM{
	
	private static boolean debug = false;
	
	private static SdlManager instance = null;
	private List<SdlHandler> listeners = null;
	private Context context;
	private boolean sdlConnected = false;
	private IpAddress currentIpAddress;
	
	private SdlManager(){}
	
	public static SdlManager getInstance(){
		if(instance == null){
			instance = new SdlManager();
		}
		
		return instance;
	}
	
	public void addListener(SdlHandler listener){
		if(listeners == null){
			listeners = new ArrayList<SdlHandler>();
		}
		
		listeners.add(listener);
	}
	
	public void setContext(Context context){
		this.context = context;
	}
	

	
	public void startSdlProxy(IpAddress inputIp){
		// if proxy hasn't been created yet or if the ip address changed, create a new proxy object
		if(syncProxy == null || !inputIp.equals(currentIpAddress)){
			currentIpAddress = inputIp;
			syncProxy = createSyncProxyObject(inputIp);
		}
	}
	
	public void resetProxy(){
		if(syncProxy != null){
			try {
				syncProxy.dispose();
			} catch (SmartDeviceLinkException e) {
				e.printStackTrace();
			}
			syncProxy = null;
		}
		
		startSdlProxy(currentIpAddress);
	}
	
	public void stopSdlProxy(){
		if(syncProxy != null){
			try {
				syncProxy.dispose();
			} catch (SmartDeviceLinkException e) {
				e.printStackTrace();
			}
			
			syncProxy = null;
		}
		initialize();
	}
	
	private void initialize(){
		sdlConnected = false;
		syncProxy = null;
	}
	
	/*********************************************************************************************************************/
	/*********************************************************************************************************************/
	/*********************************************************************************************************************/
	/*********************************************************************************************************************/
	/*********************************************************************************************************************/
	/*     EVERYTHING FROM HERE ON DOWN IS SDL...........................................................................*/
	/*********************************************************************************************************************/
	/*********************************************************************************************************************/
	/*********************************************************************************************************************/
	/*********************************************************************************************************************/
	/*********************************************************************************************************************/

	private static final String SHARED_PREFS_NAME = "music_player_sdl_prefs"; // TODO - make sure other activities use this same name
	
	// not sure what these are supposed to actually do...
	private static final String APP_SYNONYM1 = "Smart Device Link";
	
	private static final int INTERACTION_TIMEOUT = 20000; // 20s
	
	private static final SpeechCapabilities APP_TTS_TEXT_TYPE = SpeechCapabilities.TEXT;
	private static final String APP_TTS_TEXT = "text";
	
	private static final String APP_ID = "appId";
	private static final Language DEFAULT_LANG = Language.EN_US;
	private static final Language DEFAULT_HMI_LANG = Language.EN_US;
	
	private static final boolean AUTO_RECONNECT = true;
	
	private HMILevel hmiLevel = HMILevel.HMI_NONE;
	
	private UpCounter correlationIdGenerator = new UpCounter();
	
	private SdlTransportType transportType = SdlTransportType.WIFI;
	private SmartDeviceLinkProxyALM syncProxy;
	
	private SmartDeviceLinkProxyALM createSyncProxyObject(IpAddress ipAddress){
		SmartDeviceLinkProxyALM result = null;
		
		try {
			// TODO - save these to settings if necessary
//			SharedPreferences settings = getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
			boolean isMediaApp = true;
			String appName = context.getResources().getString(R.string.app_name);
			

			Vector<String> vrSynonyms = new Vector<String>();
			vrSynonyms.add(APP_SYNONYM1);
			
			Vector<TTSChunk> chunks = new Vector<TTSChunk>();
			chunks.add(TTSChunkFactory.createChunk(APP_TTS_TEXT_TYPE, APP_TTS_TEXT));
			
			int tcpPort = Integer.parseInt(ipAddress.getTcpPort());
			String ipAddressStr = ipAddress.getIpAddress();
			
			// TODO - move Bluetooth stuff to a differnt method
//			if (transportType == SdlTransportType.BLUETOOTH) {
//				log("connecting to SYNC over Bluetooth");
//				
//				result = new SmartDeviceLinkProxyALM((IProxyListenerALM)this, null, appName, chunks, null,
//						vrSynonyms, isMediaApp, null, DEFAULT_LANG, DEFAULT_HMI_LANG, APP_ID,
//						null, false, false);
//			}
//			else {
			log("connecting to SYNC over WiFi");
			
			result = new SmartDeviceLinkProxyALM((IProxyListenerALM)this, null, appName, chunks, null,
					null, isMediaApp, null, DEFAULT_LANG, DEFAULT_HMI_LANG, APP_ID,
					null, false, false, new TCPTransportConfig(tcpPort, ipAddressStr, AUTO_RECONNECT));
//			}
		} catch (SmartDeviceLinkException e) {
			e.printStackTrace();
			return null;
		}
		
		
		return result;
	}
	
	private void updateHmiLevel(HMILevel newLevel){
		hmiLevel = newLevel;
	}
	
	public void show(SdlShow item){
		if(hmiLevel != HMILevel.HMI_FULL){
			log(new StringBuilder().append("tried to do a show, but HMI level is only ").append(hmiLevel).toString());
			return;
		}
		log(new StringBuilder().append("sending a show command: ").append(item).toString());
		
		try {
			if(item.hasImage()){
				// TODO - the current SDL image object is dumb, so it isn't supported in the SdlShow object.  Use a bitmap instead.
//				syncProxy.show(item.getLine1(), item.getLine2(), null, null, item.getImage(), null, null, null, correlationIdGenerator.next());
			}
			else{
				syncProxy.show(item.getLine1(), item.getLine2(), TextAlignment.LEFT_ALIGNED, correlationIdGenerator.next());
			}
			
		} catch (SmartDeviceLinkException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void show(Show item){
		if(hmiLevel != HMILevel.HMI_FULL){
			log(new StringBuilder().append("tried to do a show, but HMI level is only ").append(hmiLevel).toString());
			return;
		}
		log(new StringBuilder().append("sending a show command: ").append(item).toString());
		
		sendSyncCommand(item);
	}
	
	// maps correlation id to associated choice set
	SparseArray<SdlFunctionBank> choiceSets = new SparseArray<SdlFunctionBank>();
	
	public void createChoiceSet(SdlFunctionBank bank){
		// TODO - bring this code back in
//		if(bank == null || bank.size() < 0){
//			return;
//		}
//		
//		final int SIZE = bank.size();
//		log(new StringBuilder().append("creating choice set with ").append(SIZE).append( "items.").toString());
//		
//		Vector<Choice> choiceItems = new Vector<Choice>(SIZE);
//		
//		Iterator<SdlFunctionButton> iterator = bank.iterator();
//		while(iterator.hasNext()){
//			choiceItems.add(createChoiceMenuItem(iterator.next()));
//		}
//		
//		CreateInteractionChoiceSet choiceSet = new CreateInteractionChoiceSet();
//		choiceSet.setChoiceSet(choiceItems);
//		choiceSet.setInteractionChoiceSetID(bank.getId());
//		
//		int correlationId = correlationIdGenerator.nextId();
//		choiceSet.setCorrelationID(correlationId);
//		choiceSets.put(correlationId, bank);
//		try {
//			syncProxy.sendRPCRequest(choiceSet);
//		} catch (SyncException e) {
//			e.printStackTrace();
//		}
		
		// TODO - remove this code, debugging only
		Vector<Choice> choiceItems = new Vector<Choice>(3);
		Choice choice = new Choice();
		choice.setChoiceID(0);
		choice.setMenuName("Artists");
		Vector<String> vrCommands = new Vector<String>();
		vrCommands.add("Artists");
		vrCommands.add("Choice 1");
		choice.setVrCommands(vrCommands);
		choiceItems.add(choice);

		choice = new Choice();
		choice.setChoiceID(1);
		choice.setMenuName("Albums");
		vrCommands = new Vector<String>();
		vrCommands.add("Albums");
		vrCommands.add("Choice 2");
		choice.setVrCommands(vrCommands);
		choiceItems.add(choice);

		choice = new Choice();
		choice.setChoiceID(2);
		choice.setMenuName("Songs");
		vrCommands = new Vector<String>();
		vrCommands.add("Songs");
		vrCommands.add("Choice 3");
		choice.setVrCommands(vrCommands);
		choiceItems.add(choice);
		
		CreateInteractionChoiceSet msg = new CreateInteractionChoiceSet();
		msg.setChoiceSet(choiceItems);
		msg.setCorrelationID(correlationIdGenerator.next());
		msg.setInteractionChoiceSetID(0);
		try {
			syncProxy.sendRPCRequest(msg);
		} catch (SmartDeviceLinkException e) {
			e.printStackTrace();
		}
	}
	
	public void deleteChoiceSet(int id){
		DeleteInteractionChoiceSet deleteInteractionChoiceSet = new DeleteInteractionChoiceSet();
		deleteInteractionChoiceSet.setInteractionChoiceSetID(id);
		sendSyncCommand(deleteInteractionChoiceSet);
	}
	
	public void performInteraction(SdlFunctionBank bank, int id){
		log(new StringBuilder().append("performing interaction from ").append(bank.getName()).append(" function bank.").toString());
		
		PerformInteraction msg = new PerformInteraction();
		msg.setInitialText(bank.getName());
		msg.setTimeout(INTERACTION_TIMEOUT);
		Vector<TTSChunk> voicePrompt = new Vector<TTSChunk>(1);
		voicePrompt.add(TTSChunkFactory.createChunk(SpeechCapabilities.TEXT, bank.getName()));
		msg.setInitialPrompt(voicePrompt);
		msg.setInteractionMode(InteractionMode.BOTH);
		Vector<Integer> idList = new Vector<Integer>();
		idList.add(bank.getId());
		msg.setInteractionChoiceSetIDList(idList);
		sendSyncCommand(msg);
		
	}
	
	private Choice createChoiceMenuItem(SdlBaseButton button){
		Choice choice = new Choice();
		choice.setChoiceID(button.getId());
		if(button.getImage() != null){
//			choice.setImage(button.getImage()); TODO
		}
		choice.setMenuName(button.getName());
		return choice;
	}
	
	private synchronized void sendSyncCommand(RPCRequest request){
		try{
			request.setCorrelationID(correlationIdGenerator.next());
			syncProxy.sendRPCRequest(request);
		}catch(SmartDeviceLinkException e){
			e.printStackTrace();
		}
	}
	
	public void addFunctionBank(SdlFunctionBank bank){
		currentBank = bank;
//		final int parentId = bank.getId();
		
		log("adding function bank");
		if(bank != null){
			int i = 0;
			Iterator<SdlBaseButton> iterator = bank.iterator();
			while(iterator.hasNext()){
				SdlBaseButton button = iterator.next();
				if(button.isMenuButton()){
					addSubMenu(button, i++);
				}
				else{
					addCommand(button, i++, 0);
				}
			}
		}
	}
	
	private SdlFunctionBank currentBank;
	public void clearFunctionBank(){
		if(currentBank != null){
			for(SdlBaseButton button : currentBank){
				final int ID = button.getId();
				deleteCommand(ID);
			}
		}
	}
	
	public void deleteCommand(int id){
		DeleteCommand msg = new DeleteCommand();
		msg.setCorrelationID(correlationIdGenerator.next());
		msg.setCmdID(id);
		try {
			syncProxy.sendRPCRequest(msg);
		} catch (SmartDeviceLinkException e) {
			e.printStackTrace();
		}
	}
	
	public void addSubMenu(SdlBaseButton button, int position){
		log(new StringBuilder().append("adding new subMenu ").toString());
		
		AddSubMenu msg = new AddSubMenu();
		msg.setMenuID(button.getId());
		msg.setMenuName(button.getName());
		msg.setPosition(position);
		sendSyncCommand(msg);
	}
	
	public void putFile(String imageName, Bitmap bitmap, ImageType imageType){
		log("sending put file");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.JPEG, 100, baos);
		byte[] bitmapAsBytes = baos.toByteArray();
		
		// TODO
		PutFile msg = new PutFile();
		msg.setBulkData(bitmapAsBytes);
		msg.setSmartDeviceLinkFileName(imageName);
		msg.setFileType(FileType.GRAPHIC_JPEG);
		msg.setPersistentFile(false);
		sendSyncCommand(msg);
	}
	
	public void addCommand(SdlBaseButton button, int position, int parentId){
		log(new StringBuilder().append("adding new command ").append(button.getName()).toString());
		
		AddCommand msg = new AddCommand();
		msg.setCmdID(button.getId());
		
		MenuParams menuParams = new MenuParams();
		menuParams.setMenuName(button.getName());
		menuParams.setPosition(position);
		menuParams.setParentID(parentId);
		msg.setMenuParams(menuParams);
		
		if(button.getImage() != null){
//			msg.setCmdIcon(button.getImage()); TODO
		}
		
		sendSyncCommand(msg);
	}
	
	@Override
	public void onOnHMIStatus(OnHMIStatus notification) {
		log(new StringBuilder().append("onOnHMIStatus: ").append(notification.getHmiLevel()).toString());
		
		if( !sdlConnected ){
			log("SDL is connected");
			notifyListenersOnConnected();
			sdlConnected = true;
		}
		
		updateHmiLevel(notification.getHmiLevel());
		notifyListenersHmiChange(notification);
	}
	
	public void subscribeToMediaButtons(){
		try {
			syncProxy.subscribeButton(ButtonName.OK, correlationIdGenerator.next());
			syncProxy.subscribeButton(ButtonName.SEEKLEFT, correlationIdGenerator.next());
			syncProxy.subscribeButton(ButtonName.SEEKRIGHT, correlationIdGenerator.next());
		} catch (SmartDeviceLinkException e) {
			e.printStackTrace();
		}
	}
	
	private void notifyListenersHmiChange(OnHMIStatus hmiLevel){
		if(listeners != null){
			for(SdlHandler listener : listeners){
				listener.createOnHmiChangeMessage(hmiLevel).sendToTarget();
			}
		}
	}

	@Override
	public void onProxyClosed(String info, Exception e) {
		log("onProxyClosed");
		notifyListenersOnDisconnected();
		sdlConnected = false;
	}
	
	private void notifyListenersOnConnected(){
		if(listeners != null){
			for(SdlHandler listener : listeners){
				listener.createOnConnectMessage().sendToTarget();
			}
		}
	}
	
	private void notifyListenersOnDisconnected(){
		if(listeners != null){
			for(SdlHandler listener : listeners){
				listener.createOnDisconnectMessage().sendToTarget();
			}
		}
	}

	@Override
	public void onError(String info, Exception e) {
		log("onError");
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGenericResponse(GenericResponse response) {
		log("onGenericResponse");
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onOnCommand(OnCommand notification) {
		log("onOnCommand");
		// TODO Auto-generated method stub
		int commandId = notification.getCmdID();
		notifyListenersButtonClick(commandId);
		
	}
	
	private void notifyListenersButtonClick(int commandId){
		if(listeners != null){
			for(SdlHandler listener : listeners){
				listener.createOnButtonClickMessage(commandId).sendToTarget();
			}
		}
	}

	@Override
	public void onAddCommandResponse(AddCommandResponse response) {
		boolean success = response.getSuccess();
		Result rc = response.getResultCode();
		log(new StringBuilder().append("onAddCommandResponse: ").append("success: ").append(success).append(", req code: ").append(rc).toString());
		
	}

	@Override
	public void onAddSubMenuResponse(AddSubMenuResponse response) {
		log("onAddSubMenuResponse");
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCreateInteractionChoiceSetResponse(CreateInteractionChoiceSetResponse response) {
		log("onCreateInteractionChoiceSetResponse");
		
		if(response.getResultCode() == Result.SUCCESS){
			// choice set was successfully added
			performChoiceSetInteraction(response);
		}
		if(response.getResultCode() == Result.DUPLICATE_NAME){
			// TODO - remove old one and use new one
		}
	}
	
	private void performChoiceSetInteraction(CreateInteractionChoiceSetResponse positiveResponse){
		int corrId = positiveResponse.getCorrelationID();
		
		PerformInteraction interaction = new PerformInteraction();
		interaction.setCorrelationID(correlationIdGenerator.next());
		
		SdlFunctionBank bankToShow = choiceSets.get(corrId);
		if(bankToShow != null){
			Vector<Integer> choiceSetIds = new Vector<Integer>(1);
			choiceSetIds.add(bankToShow.getId());
			interaction.setInteractionChoiceSetIDList(choiceSetIds);
			interaction.setInitialText(bankToShow.getName());
			
			Vector<TTSChunk> ttsChunks = new Vector<TTSChunk>();
			ttsChunks.add(TTSChunkFactory.createChunk(SpeechCapabilities.TEXT, bankToShow.getName()));
			interaction.setInitialPrompt(ttsChunks);
			
			interaction.setInteractionMode(InteractionMode.MANUAL_ONLY);
			
			Vector<Integer> choiceIds = new Vector<Integer>(1);
			choiceIds.add(bankToShow.getId());
			interaction.setInteractionChoiceSetIDList(choiceIds);
			interaction.setTimeout(INTERACTION_TIMEOUT);
		}
		
//		Vector<TTSChunk> ttsChunks = new Vector<TTSChunk>();
//		ttsChunks.add(TTSChunkFactory.createChunk(SpeechCapabilities.TEXT, "Please select an artist"));
//		interaction.setInitialPrompt(ttsChunks);
//		
//		interaction.setInteractionMode(InteractionMode.MANUAL_ONLY);
//		Vector<Integer> choiceIds = new Vector<Integer>(1);
//		choiceIds.add(0);
//		interaction.setInteractionChoiceSetIDList(choiceIds);
//		interaction.setTimeout(10000);
		
		
		try {
			syncProxy.sendRPCRequest(interaction);
		} catch (SmartDeviceLinkException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onAlertResponse(AlertResponse response) {
		log("onAlertResponse");
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDeleteCommandResponse(DeleteCommandResponse response) {
		boolean success = response.getSuccess();
		Result rc = response.getResultCode();
		log(new StringBuilder().append("onDeleteCommandResponse: ").append("success: ").append(success).append(", req code: ").append(rc).toString());
		
	}

	@Override
	public void onDeleteInteractionChoiceSetResponse(DeleteInteractionChoiceSetResponse response) {
		log("onDeleteInteractionChoiceSetResponse");
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDeleteSubMenuResponse(DeleteSubMenuResponse response) {
		log("onDeleteSubMenuResponse");
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPerformInteractionResponse(PerformInteractionResponse response) {
		log("onPerformInteractionResponse");
		// TODO Auto-generated method stub
		if(response.getSuccess()){
			log("perform interaction was successful");
		}
		else{
			log("unable to perform interaction");
			log(new StringBuilder()
					.append("Perform Interaction Response:").append("\n")
					.append("Function name: ").append(response.getFunctionName()).append("\n")
					.append("Info: ").append(response.getInfo()).append("\n")
					.append("Message type: ").append(response.getMessageType()).append("\n")
					.append("Choice id: ").append(response.getChoiceID()).append("\n")
					.append("Correlation id: ").append(response.getCorrelationID()).append("\n")
					.append("Result code: ").append(response.getResultCode()).append("\n")
					.toString());
		}
	}

	@Override
	public void onOnButtonPress(OnButtonPress notification) {
		log("onOnButtonPress");
		ButtonName button = notification.getButtonName();
		notifyListenersMediaButtonPressed(button);
	}
	
	private void notifyListenersMediaButtonPressed(ButtonName button){
		if(listeners != null){
			for(SdlHandler listener : listeners){
				listener.createOnMediaButtonClickMessage(button).sendToTarget();
			}
		}
	}


	public static void setDebug(boolean enable){
		debug = enable;
	}
	
	private static void log(String msg){
		if(debug){
			Log.d("SdlManager", msg);
		}
	}

	/*********************************************************************************************************************/
	/*********************************************************************************************************************/
	/*********************************************************************************************************************/
	/*********************************************************************************************************************/
	/*********************************************************************************************************************/
	/*********************************************************************************************************************/
	/*********************************************************************************************************************/
	/* You can stop reading here.  Everything below this point are worthless methods that don't matter, but are          */
	/* required for the "IProxyListenerALM" interface.  Method names are logged out when the methods are called, but     */
	/* that's it.                                                                                                        */
	/*********************************************************************************************************************/
	/*********************************************************************************************************************/
	/*********************************************************************************************************************/
	/*********************************************************************************************************************/
	/*********************************************************************************************************************/
	/*********************************************************************************************************************/
	/*********************************************************************************************************************/
	@Override
	public void onResetGlobalPropertiesResponse(ResetGlobalPropertiesResponse response) {
		log("onResetGlobalPropertiesResponse");
		
	}

	@Override
	public void onSetGlobalPropertiesResponse(SetGlobalPropertiesResponse response) {
		log("onSetGlobalPropertiesResponse");
		
	}

	@Override
	public void onSetMediaClockTimerResponse(SetMediaClockTimerResponse response) {
		log("onSetMediaClockTimerResponse");
		
	}

	@Override
	public void onShowResponse(ShowResponse response) {
		log("onShowResponse");
		
	}

	@Override
	public void onSpeakResponse(SpeakResponse response) {
		log("onSpeakResponse");
		
	}

	@Override
	public void onOnButtonEvent(OnButtonEvent notification) {
		log("onOnButtonEvent");
	}
	
	@Override
	public void onSubscribeButtonResponse(SubscribeButtonResponse response) {
		log(new StringBuilder().append("onSubscribeButtonResponse: ").append(response.getSuccess()).toString());
	}

	@Override
	public void onUnsubscribeButtonResponse(UnsubscribeButtonResponse response) {
		log("onUnsubscribeButtonResponse");
	}

	@Override
	public void onOnPermissionsChange(OnPermissionsChange notification) {
		log("onOnPermissionsChange");
	}

	@Override
	public void onSubscribeVehicleDataResponse(SubscribeVehicleDataResponse response) {
		log("onSubscribeVehicleDataResponse");
	}

	@Override
	public void onUnsubscribeVehicleDataResponse(UnsubscribeVehicleDataResponse response) {
		log("onUnsubscribeVehicleDataResponse");
	}

	@Override
	public void onGetVehicleDataResponse(GetVehicleDataResponse response) {
		log("onGetVehicleDataResponse");
	}

	@Override
	public void onReadDIDResponse(ReadDIDResponse response) {
		log("onReadDIDResponse");
	}

	@Override
	public void onGetDTCsResponse(GetDTCsResponse response) {
		log("onGetDTCsResponse");
	}

	@Override
	public void onOnVehicleData(OnVehicleData notification) {
		log("onOnVehicleData");
	}

	@Override
	public void onPerformAudioPassThruResponse(PerformAudioPassThruResponse response) {
		log("onPerformAudioPassThruResponse");
	}

	@Override
	public void onEndAudioPassThruResponse(EndAudioPassThruResponse response) {
		log("onEndAudioPassThruResponse");
	}

	@Override
	public void onOnAudioPassThru(OnAudioPassThru notification) {
		log("onOnAudioPassThru");
	}

	@Override
	public void onPutFileResponse(PutFileResponse response) {
		log("onPutFileResponse");
		if(response.getSuccess()){
			log("successfully added file to head-unit");
		}
	}

	@Override
	public void onDeleteFileResponse(DeleteFileResponse response) {
		log("onDeleteFileResponse");
	}

	@Override
	public void onListFilesResponse(ListFilesResponse response) {
		log("onListFilesResponse");
	}

	@Override
	public void onSetAppIconResponse(SetAppIconResponse response) {
		log("onSetAppIconResponse");
	}

	@Override
	public void onScrollableMessageResponse(ScrollableMessageResponse response) {
		log("onScrollableMessageResponse");
	}

	@Override
	public void onChangeRegistrationResponse(ChangeRegistrationResponse response) {
		log("onChangeRegistrationResponse");
	}

	@Override
	public void onSetDisplayLayoutResponse(SetDisplayLayoutResponse response) {
		log("onSetDisplayLayoutResponse");
	}

	@Override
	public void onOnLanguageChange(OnLanguageChange notification) {
		log("onOnLanguageChange");
	}

	@Override
	public void onSliderResponse(SliderResponse response) {
		log("onSliderResponse");
	}

	@Override
	public void onAlertManeuverResponse(AlertManeuverResponse response) {
		log("onAlertManeuverResponse");
	}

	@Override
	public void onShowConstantTBTResponse(ShowConstantTBTResponse response) {
		log("onShowConstantTBTResponse");
	}

	@Override
	public void onUpdateTurnListResponse(UpdateTurnListResponse response) {
		log("onUpdateTurnListResponse");
	}

	@Override
	public void onDialNumberResponse(DialNumberResponse response) {
		log("onDialNumberResponse");
	}

	@Override
	public void onOnDriverDistraction(OnDriverDistraction notification) {
		log("onOnDriverDistraction");
	}

	@Override
	public void onOnTBTClientState(OnTBTClientState notification) {
		log("onOnTBTClientState");
	}
}

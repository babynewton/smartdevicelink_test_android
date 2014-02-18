package com.livio.sdl;

import com.smartdevicelink.proxy.RPCRequest;
import com.smartdevicelink.proxy.constants.Names;
import com.smartdevicelink.proxy.interfaces.IProxyListenerALM;
import com.smartdevicelink.proxy.rpc.AddCommandResponse;
import com.smartdevicelink.proxy.rpc.AddSubMenuResponse;
import com.smartdevicelink.proxy.rpc.AlertResponse;
import com.smartdevicelink.proxy.rpc.ChangeRegistrationResponse;
import com.smartdevicelink.proxy.rpc.CreateInteractionChoiceSetResponse;
import com.smartdevicelink.proxy.rpc.DeleteCommandResponse;
import com.smartdevicelink.proxy.rpc.DeleteFileResponse;
import com.smartdevicelink.proxy.rpc.DeleteInteractionChoiceSetResponse;
import com.smartdevicelink.proxy.rpc.DeleteSubMenuResponse;
import com.smartdevicelink.proxy.rpc.GetDTCsResponse;
import com.smartdevicelink.proxy.rpc.GetVehicleDataResponse;
import com.smartdevicelink.proxy.rpc.ListFilesResponse;
import com.smartdevicelink.proxy.rpc.PerformInteractionResponse;
import com.smartdevicelink.proxy.rpc.PutFileResponse;
import com.smartdevicelink.proxy.rpc.ReadDIDResponse;
import com.smartdevicelink.proxy.rpc.ResetGlobalPropertiesResponse;
import com.smartdevicelink.proxy.rpc.ScrollableMessageResponse;
import com.smartdevicelink.proxy.rpc.SetAppIconResponse;
import com.smartdevicelink.proxy.rpc.SetGlobalPropertiesResponse;
import com.smartdevicelink.proxy.rpc.SetMediaClockTimerResponse;
import com.smartdevicelink.proxy.rpc.ShowResponse;
import com.smartdevicelink.proxy.rpc.SliderResponse;
import com.smartdevicelink.proxy.rpc.SpeakResponse;
import com.smartdevicelink.proxy.rpc.SubscribeButtonResponse;
import com.smartdevicelink.proxy.rpc.SubscribeVehicleDataResponse;
import com.smartdevicelink.proxy.rpc.UnsubscribeButtonResponse;
import com.smartdevicelink.proxy.rpc.UnsubscribeVehicleDataResponse;
import com.smartdevicelink.proxy.rpc.enums.Result;

public final class SdlResponseFactory {

	private SdlResponseFactory() {}
	
	public static void sendGenericResponseForRequest(RPCRequest request, IProxyListenerALM listener){
		if(listener == null){
			throw new NullPointerException();
		}
		
		final boolean success = true;
		final int correlationID = request.getCorrelationID();
		final String reqName = request.getFunctionName();
		final Result resCode = Result.SUCCESS;
		
		if(reqName == Names.Alert){
			AlertResponse result = new AlertResponse();
			result.setSuccess(success);
			result.setCorrelationID(correlationID);
			result.setResultCode(resCode);
			listener.onAlertResponse(result);
		}
		else if(reqName == Names.Speak){
			SpeakResponse result = new SpeakResponse();
			result.setSuccess(true);
			result.setCorrelationID(correlationID);
			result.setResultCode(resCode);
			listener.onSpeakResponse(result);
		}
		else if(reqName == Names.Show){
			ShowResponse result = new ShowResponse();
			result.setSuccess(true);
			result.setCorrelationID(correlationID);
			result.setResultCode(resCode);
			listener.onShowResponse(result);
		}
		else if(reqName == Names.SubscribeButton){
			SubscribeButtonResponse result = new SubscribeButtonResponse();
			result.setSuccess(true);
			result.setCorrelationID(correlationID);
			listener.onSubscribeButtonResponse(result);
			result.setResultCode(resCode);
			
		}
		else if(reqName == Names.UnsubscribeButton){
			UnsubscribeButtonResponse result = new UnsubscribeButtonResponse();
			result.setSuccess(true);
			result.setCorrelationID(correlationID);
			result.setResultCode(resCode);
			listener.onUnsubscribeButtonResponse(result);
		}
		else if(reqName == Names.AddCommand){
			AddCommandResponse result = new AddCommandResponse();
			result.setSuccess(true);
			result.setCorrelationID(correlationID);
			result.setResultCode(resCode);
			listener.onAddCommandResponse(result);
		}
		else if(reqName == Names.DeleteCommand){
			DeleteCommandResponse result = new DeleteCommandResponse();
			result.setSuccess(true);
			result.setCorrelationID(correlationID);
			result.setResultCode(resCode);
			listener.onDeleteCommandResponse(result);
		}
		else if(reqName == Names.AddSubMenu){
			AddSubMenuResponse result = new AddSubMenuResponse();
			result.setSuccess(true);
			result.setCorrelationID(correlationID);
			result.setResultCode(resCode);
			listener.onAddSubMenuResponse(result);
		}
		else if(reqName == Names.DeleteSubMenu){
			DeleteSubMenuResponse result = new DeleteSubMenuResponse();
			result.setSuccess(true);
			result.setCorrelationID(correlationID);
			result.setResultCode(resCode);
			listener.onDeleteSubMenuResponse(result);
		}
		else if(reqName == Names.SetGlobalProperties){
			SetGlobalPropertiesResponse result = new SetGlobalPropertiesResponse();
			result.setSuccess(true);
			result.setCorrelationID(correlationID);
			result.setResultCode(resCode);
			listener.onSetGlobalPropertiesResponse(result);
		}
		else if(reqName == Names.ResetGlobalProperties){
			ResetGlobalPropertiesResponse result = new ResetGlobalPropertiesResponse();
			result.setSuccess(true);
			result.setCorrelationID(correlationID);
			result.setResultCode(resCode);
			listener.onResetGlobalPropertiesResponse(result);
		}
		else if(reqName == Names.SetMediaClockTimer){
			SetMediaClockTimerResponse result = new SetMediaClockTimerResponse();
			result.setSuccess(true);
			result.setCorrelationID(correlationID);
			result.setResultCode(resCode);
			listener.onSetMediaClockTimerResponse(result);
		}
		else if(reqName == Names.CreateInteractionChoiceSet){
			CreateInteractionChoiceSetResponse result = new CreateInteractionChoiceSetResponse();
			result.setSuccess(true);
			result.setCorrelationID(correlationID);
			result.setResultCode(resCode);
			listener.onCreateInteractionChoiceSetResponse(result);
		}
		else if(reqName == Names.DeleteInteractionChoiceSet){
			DeleteInteractionChoiceSetResponse result = new DeleteInteractionChoiceSetResponse();
			result.setSuccess(true);
			result.setCorrelationID(correlationID);
			result.setResultCode(resCode);
			listener.onDeleteInteractionChoiceSetResponse(result);
		}
		else if(reqName == Names.PerformInteraction){
			PerformInteractionResponse result = new PerformInteractionResponse();
			result.setSuccess(true);
			result.setCorrelationID(correlationID);
			result.setResultCode(resCode);
			listener.onPerformInteractionResponse(result);
		}
		else if(reqName == Names.Slider){
			SliderResponse result = new SliderResponse();
			result.setSuccess(true);
			result.setCorrelationID(correlationID);
			result.setResultCode(resCode);
			listener.onSliderResponse(result);
		}
		else if(reqName == Names.ScrollableMessage){
			ScrollableMessageResponse result = new ScrollableMessageResponse();
			result.setSuccess(true);
			result.setCorrelationID(correlationID);
			listener.onScrollableMessageResponse(result);
		}
		else if(reqName == Names.ChangeRegistration){
			ChangeRegistrationResponse result = new ChangeRegistrationResponse();
			result.setSuccess(true);
			result.setCorrelationID(correlationID);
			result.setResultCode(resCode);
			listener.onChangeRegistrationResponse(result);
		}
		else if(reqName == Names.PutFile){
			PutFileResponse result = new PutFileResponse();
			result.setSuccess(true);
			result.setCorrelationID(correlationID);
			result.setResultCode(resCode);
			listener.onPutFileResponse(result);
		}
		else if(reqName == Names.DeleteFile){
			DeleteFileResponse result = new DeleteFileResponse();
			result.setSuccess(true);
			result.setCorrelationID(correlationID);
			result.setResultCode(resCode);
			listener.onDeleteFileResponse(result);
		}
		else if(reqName == Names.ListFiles){
			ListFilesResponse result = new ListFilesResponse();
			result.setSuccess(true);
			result.setCorrelationID(correlationID);
			result.setResultCode(resCode);
			listener.onListFilesResponse(result);
		}
		else if(reqName == Names.SetAppIcon){
			SetAppIconResponse result = new SetAppIconResponse();
			result.setSuccess(true);
			result.setCorrelationID(correlationID);
			result.setResultCode(resCode);
			listener.onSetAppIconResponse(result);
		}
		else if(reqName == Names.SubscribeVehicleData){
			SubscribeVehicleDataResponse result = new SubscribeVehicleDataResponse();
			result.setSuccess(true);
			result.setCorrelationID(correlationID);
			result.setResultCode(resCode);
			listener.onSubscribeVehicleDataResponse(result);
		}
		else if(reqName == Names.UnsubscribeVehicleData){
			UnsubscribeVehicleDataResponse result = new UnsubscribeVehicleDataResponse();
			result.setSuccess(true);
			result.setCorrelationID(correlationID);
			result.setResultCode(resCode);
			listener.onUnsubscribeVehicleDataResponse(result);
		}
		else if(reqName == Names.GetVehicleData){
			GetVehicleDataResponse result = new GetVehicleDataResponse();
			result.setSuccess(true);
			result.setCorrelationID(correlationID);
			result.setResultCode(resCode);
			listener.onGetVehicleDataResponse(result);
		}
		else if(reqName == Names.ReadDID){
			ReadDIDResponse result = new ReadDIDResponse();
			result.setSuccess(true);
			result.setCorrelationID(correlationID);
			result.setResultCode(resCode);
			listener.onReadDIDResponse(result);
		}
		else if(reqName == Names.GetDTCs){
			GetDTCsResponse result = new GetDTCsResponse();
			result.setSuccess(true);
			result.setCorrelationID(correlationID);
			result.setResultCode(resCode);
			listener.onGetDTCsResponse(result);
		}
	}

}

package com.livio.sdl;

import com.smartdevicelink.proxy.RPCRequest;
import com.smartdevicelink.proxy.RPCResponse;
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

/**
 * A factory class to generate responses based on the input requests.
 *
 * @author Mike Burke
 *
 */
public final class SdlResponseFactory {

	private SdlResponseFactory() {}
	
	/**
	 * Creates a generic "Success" response for the input request with no input checking.  The created
	 * response is sent back to the appropriate method of the input listener.
	 * 
	 * @param request The request to respond to
	 * @param listener The listener to inform when the response is complete
	 */
	public static void sendGenericResponseForRequest(RPCRequest request, IProxyListenerALM listener){
		if(listener == null){
			throw new NullPointerException();
		}

		final String reqName = request.getFunctionName();
		final int correlationId = request.getCorrelationID();
		
		// this is really bad
		if(reqName == Names.Alert){
			AlertResponse result = new AlertResponse();
			setSuccessParams(result, correlationId);
			listener.onAlertResponse(result);
		}
		else if(reqName == Names.Speak){
			SpeakResponse result = new SpeakResponse();
			setSuccessParams(result, correlationId);
			listener.onSpeakResponse(result);
		}
		else if(reqName == Names.Show){
			ShowResponse result = new ShowResponse();
			setSuccessParams(result, correlationId);
			listener.onShowResponse(result);
		}
		else if(reqName == Names.SubscribeButton){
			SubscribeButtonResponse result = new SubscribeButtonResponse();
			setSuccessParams(result, correlationId);
			listener.onSubscribeButtonResponse(result);
			
		}
		else if(reqName == Names.UnsubscribeButton){
			UnsubscribeButtonResponse result = new UnsubscribeButtonResponse();
			setSuccessParams(result, correlationId);
			listener.onUnsubscribeButtonResponse(result);
		}
		else if(reqName == Names.AddCommand){
			AddCommandResponse result = new AddCommandResponse();
			setSuccessParams(result, correlationId);
			listener.onAddCommandResponse(result);
		}
		else if(reqName == Names.DeleteCommand){
			DeleteCommandResponse result = new DeleteCommandResponse();
			setSuccessParams(result, correlationId);
			listener.onDeleteCommandResponse(result);
		}
		else if(reqName == Names.AddSubMenu){
			AddSubMenuResponse result = new AddSubMenuResponse();
			setSuccessParams(result, correlationId);
			listener.onAddSubMenuResponse(result);
		}
		else if(reqName == Names.DeleteSubMenu){
			DeleteSubMenuResponse result = new DeleteSubMenuResponse();
			setSuccessParams(result, correlationId);
			listener.onDeleteSubMenuResponse(result);
		}
		else if(reqName == Names.SetGlobalProperties){
			SetGlobalPropertiesResponse result = new SetGlobalPropertiesResponse();
			setSuccessParams(result, correlationId);
			listener.onSetGlobalPropertiesResponse(result);
		}
		else if(reqName == Names.ResetGlobalProperties){
			ResetGlobalPropertiesResponse result = new ResetGlobalPropertiesResponse();
			setSuccessParams(result, correlationId);
			listener.onResetGlobalPropertiesResponse(result);
		}
		else if(reqName == Names.SetMediaClockTimer){
			SetMediaClockTimerResponse result = new SetMediaClockTimerResponse();
			setSuccessParams(result, correlationId);
			listener.onSetMediaClockTimerResponse(result);
		}
		else if(reqName == Names.CreateInteractionChoiceSet){
			CreateInteractionChoiceSetResponse result = new CreateInteractionChoiceSetResponse();
			setSuccessParams(result, correlationId);
			listener.onCreateInteractionChoiceSetResponse(result);
		}
		else if(reqName == Names.DeleteInteractionChoiceSet){
			DeleteInteractionChoiceSetResponse result = new DeleteInteractionChoiceSetResponse();
			setSuccessParams(result, correlationId);
			listener.onDeleteInteractionChoiceSetResponse(result);
		}
		else if(reqName == Names.PerformInteraction){
			PerformInteractionResponse result = new PerformInteractionResponse();
			setSuccessParams(result, correlationId);
			listener.onPerformInteractionResponse(result);
		}
		else if(reqName == Names.Slider){
			SliderResponse result = new SliderResponse();
			setSuccessParams(result, correlationId);
			listener.onSliderResponse(result);
		}
		else if(reqName == Names.ScrollableMessage){
			ScrollableMessageResponse result = new ScrollableMessageResponse();
			setSuccessParams(result, correlationId);
			listener.onScrollableMessageResponse(result);
		}
		else if(reqName == Names.ChangeRegistration){
			ChangeRegistrationResponse result = new ChangeRegistrationResponse();
			setSuccessParams(result, correlationId);
			listener.onChangeRegistrationResponse(result);
		}
		else if(reqName == Names.PutFile){
			PutFileResponse result = new PutFileResponse();
			setSuccessParams(result, correlationId);
			listener.onPutFileResponse(result);
		}
		else if(reqName == Names.DeleteFile){
			DeleteFileResponse result = new DeleteFileResponse();
			setSuccessParams(result, correlationId);
			listener.onDeleteFileResponse(result);
		}
		else if(reqName == Names.ListFiles){
			ListFilesResponse result = new ListFilesResponse();
			setSuccessParams(result, correlationId);
			listener.onListFilesResponse(result);
		}
		else if(reqName == Names.SetAppIcon){
			SetAppIconResponse result = new SetAppIconResponse();
			setSuccessParams(result, correlationId);
			listener.onSetAppIconResponse(result);
		}
		else if(reqName == Names.SubscribeVehicleData){
			SubscribeVehicleDataResponse result = new SubscribeVehicleDataResponse();
			setSuccessParams(result, correlationId);
			listener.onSubscribeVehicleDataResponse(result);
		}
		else if(reqName == Names.UnsubscribeVehicleData){
			UnsubscribeVehicleDataResponse result = new UnsubscribeVehicleDataResponse();
			setSuccessParams(result, correlationId);
			listener.onUnsubscribeVehicleDataResponse(result);
		}
		else if(reqName == Names.GetVehicleData){
			GetVehicleDataResponse result = new GetVehicleDataResponse();
			setSuccessParams(result, correlationId);
			listener.onGetVehicleDataResponse(result);
		}
		else if(reqName == Names.ReadDID){
			ReadDIDResponse result = new ReadDIDResponse();
			setSuccessParams(result, correlationId);
			listener.onReadDIDResponse(result);
		}
		else if(reqName == Names.GetDTCs){
			GetDTCsResponse result = new GetDTCsResponse();
			setSuccessParams(result, correlationId);
			listener.onGetDTCsResponse(result);
		}
	}
	
	private static void setSuccessParams(RPCResponse response, int correlationId){
		response.setSuccess(true);
		response.setCorrelationID(correlationId);
		response.setResultCode(Result.SUCCESS);
	}

}

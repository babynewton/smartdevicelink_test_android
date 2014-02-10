package com.livio.sdltester.dialogs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.content.DialogInterface;

import com.livio.sdl.dialogs.BaseMultipleListViewDialog;
import com.livio.sdl.enums.SdlButton;
import com.livio.sdl.enums.SdlCommand;
import com.smartdevicelink.proxy.RPCRequest;
import com.smartdevicelink.proxy.rpc.UnsubscribeButton;

/**
 * Shows a dialog allowing the user to unsubscribe from buttons that have been subscribed to.  This class
 * requires an input list of SDL buttons that have been subscribed to so far.  Button subscriptions should
 * be queried from the SDL service prior to showing this dialog to be sure information is up to date.
 * 
 * The result of this dialog is a list of RPC requests, one request per selected item since SDL can't do
 * these as a batch as of version 2.0.
 *
 * @author Mike Burke
 *
 */
public class ButtonUnsubscriptionDialog extends BaseMultipleListViewDialog<SdlButton> {

	private static final SdlCommand SYNC_COMMAND = SdlCommand.UNSUBSCRIBE_BUTTON;
	private static final String DIALOG_TITLE = SYNC_COMMAND.toString();
	
	public ButtonUnsubscriptionDialog(Context context, List<SdlButton> buttonSubscriptions) {
		super(context, DIALOG_TITLE, buttonSubscriptions);
		setPositiveButton(positiveButton);
		createDialog();
	}
	
	//dialog button click listeners
	private final DialogInterface.OnClickListener positiveButton = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			// called when the OK button is clicked.
			
			if(selectedItems == null || selectedItems.size() == 0){
				// if no items were selected, send an empty list as the result
				notifyListener(Collections.emptyList());
			}
			else{
				List<RPCRequest> buttonUnsubscribeMessages = new ArrayList<RPCRequest>(selectedItems.size());
				
				// loop through the selected items and create RPC requests for each one since they can't be done in a batch.
				for(SdlButton button : selectedItems){
					UnsubscribeButton unsubscribeButton = new UnsubscribeButton();
					unsubscribeButton.setButtonName(SdlButton.translateToLegacy(button));
					buttonUnsubscribeMessages.add(unsubscribeButton);
				}
				notifyListener(buttonUnsubscribeMessages);
			}
		}
	};
}

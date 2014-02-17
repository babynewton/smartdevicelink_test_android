package com.livio.sdl.adapters;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.livio.sdl.R;
import com.livio.sdl.SdlLogMessage;

/**
 * A custom adapter class for showing SDL log messages in a listview.  The adapter shows the message
 * name, details and a timestamp of when the message was first logged.
 *
 * @author Mike Burke
 *
 */
public class SdlMessageAdapter extends ArrayAdapter<SdlLogMessage> {
	
	private static final int REQUEST_COLOR = Color.BLUE;
	private static final int RESPONSE_SUCCESS_COLOR = 0xFF2D9C08; // dark green
	private static final int RESPONSE_FAILURE_COLOR = Color.RED;
	private static final int NOTIFICATION_COLOR = Color.BLACK;

	public SdlMessageAdapter(Context context, List<SdlLogMessage> objects) {
		super(context, R.layout.sdl_message_listview_row, objects);
	}

	public SdlMessageAdapter(Context context) {
		super(context, R.layout.sdl_message_listview_row);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		
		if(convertView == null){
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.sdl_message_listview_row, null);
		}
		
		SdlLogMessage item = getItem(position);
		
		if(item != null){
			populateView(view, item);
		}
		
		
		return view;
	}
	
	/**
	 * Populate the input parent view with information from the SDL log message.
	 * 
	 * @param view The view to populate
	 * @param item The data with which to populate the view
	 */
	private void populateView(View view, SdlLogMessage item){
		TextView tv_messageName = (TextView) view.findViewById(R.id.tv_messageName);
		TextView tv_messageDetails = (TextView) view.findViewById(R.id.tv_messageDetail);
		TextView tv_timestamp = (TextView) view.findViewById(R.id.tv_timestamp);
		
		// set text values based on input message
		tv_messageName.setText(item.getFunctionName());
		tv_messageDetails.setText(item.getDetails());
		tv_timestamp.setText(item.getTimeStamp());
		
		String messageType = item.getMessageType();
		
		// set the text colors based on the inputs
		if(messageType.equals(SdlLogMessage.REQUEST)){
			tv_messageName.setTextColor(REQUEST_COLOR);
		}
		else if(messageType.equals(SdlLogMessage.RESPONSE)){
			if(item.getSuccess()){
				tv_messageName.setTextColor(RESPONSE_SUCCESS_COLOR);
				tv_messageDetails.setTextColor(RESPONSE_SUCCESS_COLOR);
			}
			else{
				tv_messageName.setTextColor(RESPONSE_FAILURE_COLOR);
				tv_messageDetails.setTextColor(RESPONSE_FAILURE_COLOR);
			}
		}
		else if(messageType.equals(SdlLogMessage.NOTIFICATION)){
			tv_messageName.setTextColor(NOTIFICATION_COLOR);
		}
	}
	
}

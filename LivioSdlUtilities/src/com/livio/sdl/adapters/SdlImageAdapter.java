package com.livio.sdl.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.livio.sdl.R;
import com.livio.sdl.SdlImageItem;

public class SdlImageAdapter extends ArrayAdapter<SdlImageItem> {


	public SdlImageAdapter(Context context, List<SdlImageItem> objects) {
		super(context, R.layout.simple_listview_with_image, objects);
	}

	public SdlImageAdapter(Context context) {
		super(context, R.layout.simple_listview_with_image);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		
		if(convertView == null){
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.simple_listview_with_image, null);
		}
		
		SdlImageItem item = getItem(position);
		
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
	private void populateView(View view, SdlImageItem item){
		ImageView iv_image = (ImageView) view.findViewById(R.id.iv_rowImage);
		TextView tv_text = (TextView) view.findViewById(R.id.tv_rowText);
		
		iv_image.setImageBitmap(item.getBitmap());
		tv_text.setText(item.getImageName());
	}
}

package com.livio.sdltester.dialogs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.livio.sdl.SdlImageItem;
import com.livio.sdl.SdlImageItem.SdlImageItemComparator;
import com.livio.sdl.SdlRequestFactory;
import com.livio.sdl.dialogs.BaseAlertDialog;
import com.livio.sdl.dialogs.BaseOkCancelDialog;
import com.livio.sdl.dialogs.ImageListDialog;
import com.livio.sdl.enums.SdlCommand;
import com.livio.sdl.utils.SdlUtils;
import com.livio.sdltester.R;
import com.smartdevicelink.proxy.RPCMessage;
import com.smartdevicelink.proxy.RPCRequest;

public class PutFileDialog extends BaseOkCancelDialog {

	private static final SdlCommand SYNC_COMMAND = SdlCommand.PUT_FILE;
	private static final String DIALOG_TITLE = SYNC_COMMAND.toString();
	
	private ImageButton ib_putFile_selectAnImage;
	private EditText et_putFile_imageName;
	private CheckBox cb_putFile_isPersistent;
	private CheckBox cb_putFile_addAll;
	
	private SdlImageItem selectedImage = null;
	private List<SdlImageItem> availableImages;
	
	private HashMap<SdlImageItem, byte[]> bitmapRawByteMap;
	
	public PutFileDialog(Context context, List<SdlImageItem> availableImages) {
		super(context, DIALOG_TITLE, R.layout.put_file);
		this.availableImages = availableImages;
		Collections.sort(this.availableImages, new SdlImageItemComparator());
		startImageProcessing();
		setupViews();
		setPositiveButton(okButtonListener);
		createDialog();
	}
	
	/**
	 * Preemptively processes all available images in the background so the calculations
	 * don't have to be done when the user makes a selection.  By the time the user
	 * clicks on something, the calculations should be complete.
	 */
	private void startImageProcessing(){
		final int size = availableImages.size();
		bitmapRawByteMap = new HashMap<SdlImageItem, byte[]>(size);
		
		// loop through all images and start a thread to process them
		for(final SdlImageItem item : availableImages){
			new Thread(new Runnable() {
				@Override
				public void run() {
					byte[] bitmapData = SdlUtils.bitmapToByteArray(item.getBitmap(), item.getImageType());
					addDataToMap(item, bitmapData);
				}
			}).start();
		}
	}
	
	/**
	 * Allows synchronized editting of the raw byte hash map.  HashMaps are not thread safe,
	 * so we must protect the data structure through a synchronized method.
	 *
	 * @param key The key to add
	 * @param value The value to add
	 */
	private synchronized void addDataToMap(SdlImageItem key, byte[] value){
		bitmapRawByteMap.put(key, value);
	}
	
	private void setupViews(){
		ib_putFile_selectAnImage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				BaseAlertDialog selectImageDialog = new ImageListDialog(context, availableImages);
				selectImageDialog.setListener(new BaseAlertDialog.Listener() {
					@Override
					public void onResult(Object resultData) {
						selectedImage = (SdlImageItem) resultData;
						onItemSelected(selectedImage);
					}
				});
				selectImageDialog.show();
			}
		});
	}

	private void onItemSelected(SdlImageItem item){
		ib_putFile_selectAnImage.setImageBitmap(item.getBitmap());
		et_putFile_imageName.setText(item.getImageName());
	}
	
	@Override
	protected void findViews(View parent) {
		et_putFile_imageName = (EditText) parent.findViewById(R.id.et_putFile_imageName);
		cb_putFile_isPersistent = (CheckBox) parent.findViewById(R.id.cb_putFile_isPersistent);
		ib_putFile_selectAnImage = (ImageButton) parent.findViewById(R.id.ib_putFile_selectAnImage);
		
		cb_putFile_addAll = (CheckBox) parent.findViewById(R.id.cb_putFile_addAll);
		cb_putFile_addAll.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				enableViews(!isChecked);
			}
		});
	}
	
	private void enableViews(boolean enable){
		int visibility = (enable) ? View.VISIBLE : View.GONE;
		et_putFile_imageName.setVisibility(visibility);
		cb_putFile_isPersistent.setVisibility(visibility);
		ib_putFile_selectAnImage.setVisibility(visibility);
	}
	
	//dialog button click listeners
	private final DialogInterface.OnClickListener okButtonListener = new DialogInterface.OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			List<RPCMessage> messages = new ArrayList<RPCMessage>(availableImages.size());
			boolean persistentFile = cb_putFile_isPersistent.isChecked();
			
			if(cb_putFile_addAll.isChecked()){
				Set<SdlImageItem> keySet = bitmapRawByteMap.keySet();
				Iterator<SdlImageItem> iterator = keySet.iterator();
				
				while(iterator.hasNext()){
					SdlImageItem item = iterator.next();
					RPCRequest result = SdlRequestFactory.putFile(item.getImageName(), item.getImageType(), persistentFile, bitmapRawByteMap.get(item));
					messages.add(result);
				}
				
				notifyListener(messages);
			}
			else if(selectedImage != null){
				String name = et_putFile_imageName.getText().toString();
				if(name.length() > 0){
					RPCRequest result = SdlRequestFactory.putFile(name, selectedImage.getImageType(), persistentFile, bitmapRawByteMap.get(selectedImage));
					messages.add(result);

					notifyListener(messages);
				}
				else{
					Toast.makeText(context, "Must enter a name for the image.", Toast.LENGTH_LONG).show();
				}
			}
			else{
				Toast.makeText(context, "Must select an image to add.", Toast.LENGTH_LONG).show();
			}
		}
	};

}

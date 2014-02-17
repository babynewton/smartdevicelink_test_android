package com.livio.sdltester.dialogs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap.CompressFormat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.livio.sdl.SdlImageItem;
import com.livio.sdl.SdlImageItem.SdlImageItemComparator;
import com.livio.sdl.dialogs.BaseAlertDialog;
import com.livio.sdl.dialogs.BaseOkCancelDialog;
import com.livio.sdl.enums.SdlCommand;
import com.livio.sdl.utils.AndroidUtils;
import com.livio.sdl.utils.SdlUtils;
import com.livio.sdltester.R;
import com.smartdevicelink.proxy.RPCMessage;
import com.smartdevicelink.proxy.rpc.PutFile;
import com.smartdevicelink.proxy.rpc.enums.FileType;

public class PutFileDialog extends BaseOkCancelDialog {

	private static final SdlCommand SYNC_COMMAND = SdlCommand.PUT_FILE;
	private static final String DIALOG_TITLE = SYNC_COMMAND.toString();
	
	private ImageButton ib_putFile_selectAnImage;
	private EditText et_putFile_imageName;
	private CheckBox cb_putFile_isPersistent;
	private CheckBox cb_putFile_addAll;
	
	private SdlImageItem selectedImage = null;
	private List<SdlImageItem> availableImages;
	
	public PutFileDialog(Context context, List<SdlImageItem> availableImages) {
		super(context, DIALOG_TITLE, R.layout.put_file);
		this.availableImages = availableImages;
		Collections.sort(this.availableImages, new SdlImageItemComparator());
		setupViews();
		setPositiveButton(okButtonListener);
		createDialog();
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
		cb_putFile_addAll = (CheckBox) parent.findViewById(R.id.cb_putFile_addAll);
		ib_putFile_selectAnImage = (ImageButton) parent.findViewById(R.id.ib_putFile_selectAnImage);
	}
	
	//dialog button click listeners
	private final DialogInterface.OnClickListener okButtonListener = new DialogInterface.OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			List<RPCMessage> messages = new ArrayList<RPCMessage>(availableImages.size());
			boolean persistentFile = cb_putFile_isPersistent.isChecked();
			
			if(cb_putFile_addAll.isChecked()){
				for(SdlImageItem item : availableImages){
					FileType type = item.getImageType();
					CompressFormat format = SdlUtils.convertImageTypeToCompressFormat(type);
					byte[] bitmapData = AndroidUtils.bitmapToRawBytes(item.getBitmap(), format); // TODO - encoding all these images is taking a long time. - add a loading() dialog.
					
					PutFile putFile = new PutFile();
					putFile.setSmartDeviceLinkFileName(item.getImageName());
					putFile.setFileType(item.getImageType());
					putFile.setPersistentFile(persistentFile);
					putFile.setBulkData(bitmapData);
					messages.add(putFile);
				}
				
				notifyListener(messages);
			}
			else if(selectedImage != null){
				String name = et_putFile_imageName.getText().toString();
				if(name.length() > 0){
					FileType type = selectedImage.getImageType();
					CompressFormat format = SdlUtils.convertImageTypeToCompressFormat(type);
					byte[] bitmapData = AndroidUtils.bitmapToRawBytes(selectedImage.getBitmap(), format);
					
					PutFile putFile = new PutFile();
					putFile.setSmartDeviceLinkFileName(name);
					putFile.setFileType(type);
					putFile.setPersistentFile(persistentFile);
					putFile.setBulkData(bitmapData);
					messages.add(putFile);

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

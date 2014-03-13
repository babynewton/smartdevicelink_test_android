package com.livio.sdltester.dialogs;

import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

import com.livio.sdl.SdlImageItem;
import com.livio.sdl.dialogs.BaseAlertDialog;
import com.livio.sdl.dialogs.BaseOkCancelDialog;
import com.livio.sdl.dialogs.ImageListDialog;
import com.livio.sdl.utils.SdlUtils;
import com.livio.sdltester.R;
import com.smartdevicelink.proxy.rpc.Choice;

public class ChoiceItemDialog extends BaseOkCancelDialog {

	private static final String DIALOG_TITLE = "Create a Choice";
	private List<SdlImageItem> availableImages;
	
	private EditText et_choiceName, et_choiceVr, et_imageName;
	private CheckBox cb_hasImage;
	private BaseAlertDialog imagesDialog;
	
	public ChoiceItemDialog(Context context, List<SdlImageItem> availableImages) {
		super(context, DIALOG_TITLE, R.layout.choice_set_item);
		setPositiveButton(okButton);
		this.availableImages = availableImages;
		if(availableImages == null || availableImages.size() <= 0){
			cb_hasImage.setVisibility(View.GONE);
			et_imageName.setVisibility(View.GONE);
		}
		createDialog();
	}

	@Override
	protected void findViews(View parent) {
		et_choiceName = (EditText) parent.findViewById(R.id.et_choice_name);
		et_choiceVr = (EditText) parent.findViewById(R.id.et_choice_vr_text);
		et_imageName = (EditText) parent.findViewById(R.id.et_choice_imageName);
		cb_hasImage = (CheckBox) parent.findViewById(R.id.check_enable_image);
		cb_hasImage.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					showImagesDialog();
				}
				else{
					et_imageName.setText("");
				}
			}
		});
	}
	
	private void showImagesDialog(){
		if(imagesDialog == null){
			imagesDialog = new ImageListDialog(context, availableImages);
			imagesDialog.setListener(new BaseAlertDialog.Listener() {
				@Override
				public void onResult(Object resultData) {
					SdlImageItem selectedItem = (SdlImageItem) resultData;
					if(selectedItem != null){
						et_imageName.setText(selectedItem.getImageName());
					}
				}
			});
		}
		
		imagesDialog.show();
	}
	
	private DialogInterface.OnClickListener okButton = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			String choiceName = et_choiceName.getText().toString();
			String choiceVr = et_choiceVr.getText().toString();
			String imageName = et_imageName.getText().toString();
			
			if(choiceName.length() > 0){
				if(imageName.length() <= 0){
					imageName = null;
				}
				
				Choice choice = SdlUtils.createChoice(choiceName, choiceVr, imageName);
				notifyListener(choice);
			}
			else{
				Toast.makeText(context, "Choice must have a name.", Toast.LENGTH_LONG).show();
			}
		}
	};

}

package com.livio.sdltester.dialogs;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import android.content.Context;
import android.content.DialogInterface;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.livio.sdl.SdlImageItem;
import com.livio.sdl.SdlRequestFactory;
import com.livio.sdl.dialogs.BaseAlertDialog;
import com.livio.sdl.dialogs.BaseOkCancelDialog;
import com.livio.sdl.dialogs.ImageListDialog;
import com.livio.sdl.enums.SdlCommand;
import com.livio.sdl.utils.SdlUtils;
import com.livio.sdltester.R;
import com.smartdevicelink.proxy.RPCRequest;
import com.smartdevicelink.proxy.rpc.Choice;

public class CreateInteractionChoiceSetDialog extends BaseOkCancelDialog{
	
	// TODO list
	// 1. Create an easy way for a user to select an image to send when the include image checkmark is checked.
	// 2. Send the list of choice items to the SDL proxy service through an intent or some other means
	//
	//

	private static final SdlCommand SYNC_COMMAND = SdlCommand.CREATE_INTERACTION_CHOICE_SET;
	private static final String DIALOG_TITLE = SYNC_COMMAND.toString();

	private static final int MAX_CHOICES = 10;
	
	private LinearLayout ll_itemList;
	private Button but_addItem;
	
	private BaseAlertDialog imageDialog = null;
	private List<SdlImageItem> allImages;
	private SparseArray<SdlImageItem> selectedImages;
	
	public CreateInteractionChoiceSetDialog(Context context, List<SdlImageItem> images){
		super(context, DIALOG_TITLE, R.layout.create_choice_interaction_set);
		setPositiveButton(okButtonListener);
		this.allImages = images;
		inflateChoiceView();
		createDialog();
	}

	@Override
	protected void findViews(View parent) {
		ll_itemList = (LinearLayout) view.findViewById(R.id.ll_choiceItems);
		but_addItem = (Button) view.findViewById(R.id.but_addItem);
		but_addItem.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				inflateChoiceView();
			}
		});
	}
	
	private void inflateChoiceView(){
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		final int itemNumber = getChoiceCount()+1;
		if(itemNumber > MAX_CHOICES){
			Toast.makeText(context, context.getResources().getString(R.string.max_choices), Toast.LENGTH_LONG).show();
			return;
		}
		
		final View viewToAdd = inflater.inflate(R.layout.choice_set_item, null);
		TextView tv_itemNumber = (TextView) viewToAdd.findViewById(R.id.tv_choiceItemNumber);
		tv_itemNumber.setText(createItemNumberText(itemNumber));
		
		ImageButton ib_close = (ImageButton) viewToAdd.findViewById(R.id.ib_close);
		int visibility = (itemNumber == 1) ? View.GONE : View.VISIBLE;
		ib_close.setVisibility(visibility);
		ib_close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ll_itemList.removeView(viewToAdd);
				adjustItemNumbers();
			}
		});
		
		final EditText et_imageName = (EditText) viewToAdd.findViewById(R.id.et_choice_imageName);
		
		CheckBox cb_hasImage = (CheckBox) viewToAdd.findViewById(R.id.check_enable_image);
		cb_hasImage.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				et_imageName.setEnabled(isChecked);
				
				if(isChecked){
					addImageToItem(itemNumber, et_imageName);
				}
				else{
					selectedImages.remove(itemNumber);
				}
			}
		});
		et_imageName.setEnabled(cb_hasImage.isChecked());
		
		int index = getChoiceCount();
		ll_itemList.addView(viewToAdd, index);
	}
	
	private void addImageToItem(final int itemNumber, final EditText et_imageName){
		if(selectedImages == null){
			selectedImages = new SparseArray<SdlImageItem>(MAX_CHOICES);
		}
		
		if(imageDialog == null){
			imageDialog = new ImageListDialog(context, allImages);
			imageDialog.setListener(new BaseAlertDialog.Listener() {
				@Override
				public void onResult(Object resultData) {
					SdlImageItem item = (SdlImageItem) resultData;
					selectedImages.put(itemNumber, item);
					et_imageName.setText(item.getImageName());
				}
			});
		}
		
		imageDialog.show();
	}
	
	private void adjustItemNumbers(){
		int numberOfItems = getChoiceCount();
		for(int i=0; i < numberOfItems; i++){
			View view = ll_itemList.getChildAt(i);
			TextView tv_itemNumber = (TextView) view.findViewById(R.id.tv_choiceItemNumber);
			
			final int itemNumber = i+1;
			tv_itemNumber.setText(createItemNumberText(itemNumber)); 
		}
	}
	
	private int getChoiceCount(){
		return ll_itemList.getChildCount()-1; // don't include the "Add" button
	}
	
	private String createItemNumberText(int itemNumber){
		String itemNumberStr = context.getResources().getString(R.string.item_number);
		return new StringBuilder().append(itemNumberStr).append(itemNumber).toString();
	}
	
	//dialog button listeners
	private final DialogInterface.OnClickListener okButtonListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			List<DataHolder> itemList = createChoiceInteractionSetList();
			Vector<Choice> choiceItems = new Vector<Choice>(itemList.size());
			
			for(int i=0; i < itemList.size(); i++){
				final DataHolder item = itemList.get(i);
				final String choiceName = item.choiceName;
				final String voiceRecKeyword = item.voiceRecKeyword;
				final String imageName = item.imageName;
				
				// make sure the choice at least has a name to display
				if(choiceName.length() > 0){
					Choice choice = SdlUtils.createChoice(choiceName, voiceRecKeyword, imageName);
					choiceItems.add(choice);
				}
			}
			
			if(choiceItems.size() > 0){
				RPCRequest result = SdlRequestFactory.createInteractionChoiceSet(choiceItems);
				notifyListener(result);
			}
			else{
				Toast.makeText(context, "Must enter at least 1 choice name.", Toast.LENGTH_LONG).show();
			}
		}
	};
	
	private List<DataHolder> createChoiceInteractionSetList(){
		int numberOfChoices = getChoiceCount();
		List<DataHolder> result = new ArrayList<DataHolder>(numberOfChoices);
		
		for(int i=0; i < numberOfChoices; i++){
			View currView = ll_itemList.getChildAt(i);
			DataHolder currItem = createChoiceSetItem(currView);
			result.add(currItem);
		}
		
		return result;
	}
	
	private DataHolder createChoiceSetItem(View view){
		String choiceName, voiceKeyword, imageName;
		
		choiceName = ((EditText) view.findViewById(R.id.et_choice_name)).getText().toString();
		voiceKeyword = ((EditText) view.findViewById(R.id.et_choice_vr_text)).getText().toString();
		imageName = ((EditText) view.findViewById(R.id.et_choice_imageName)).getText().toString();
		if(imageName == null || imageName.length() <= 0){
			imageName = null;
		}
		
		return new DataHolder(choiceName, voiceKeyword, imageName);
	}
	
	private static final class DataHolder{
		private String choiceName;
		private String voiceRecKeyword;
		private String imageName;
		
		protected DataHolder(String choiceName, String voiceRecKeyword, String imageName){
			this.choiceName = choiceName;
			this.voiceRecKeyword = voiceRecKeyword;
			this.imageName = imageName;
		}
	}

}

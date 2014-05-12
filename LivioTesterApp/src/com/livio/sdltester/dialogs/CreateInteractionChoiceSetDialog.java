package com.livio.sdltester.dialogs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.livio.sdl.SdlImageItem;
import com.livio.sdl.SdlImageItem.SdlImageItemComparator;
import com.livio.sdl.SdlRequestFactory;
import com.livio.sdl.adapters.SdlImageAdapter;
import com.livio.sdl.dialogs.BaseAlertDialog;
import com.livio.sdl.dialogs.BaseOkCancelDialog;
import com.livio.sdl.dialogs.TextViewOkCancelDialog;
import com.livio.sdl.enums.SdlCommand;
import com.livio.sdltester.R;
import com.smartdevicelink.proxy.RPCRequest;
import com.smartdevicelink.proxy.rpc.Choice;
import com.smartdevicelink.proxy.rpc.enums.FileType;

public class CreateInteractionChoiceSetDialog extends BaseOkCancelDialog{

	private static final SdlCommand SYNC_COMMAND = SdlCommand.CREATE_INTERACTION_CHOICE_SET;
	private static final String DIALOG_TITLE = SYNC_COMMAND.toString();

	private static final int MAX_CHOICES = 10;
	
	private List<Choice> choiceItemList = new ArrayList<Choice>(MAX_CHOICES);
	
	private Button but_addItem;
	private ListView lv_choiceItems;
	private ArrayAdapter<SdlImageItem> adapter;
	private List<SdlImageItem> adapterList = new ArrayList<SdlImageItem>(MAX_CHOICES);
	
	private BaseAlertDialog choiceDialog = null;
	private List<SdlImageItem> allImages;
	
	public CreateInteractionChoiceSetDialog(Context context, List<SdlImageItem> images){
		super(context, DIALOG_TITLE, R.layout.create_choice_interaction_set);
		setPositiveButton(okButtonListener);
		adapter = new SdlImageAdapter(context, adapterList);
		lv_choiceItems.setAdapter(adapter);
		this.allImages = images;
		createDialog();
	}

	@Override
	protected void findViews(View parent) {
		lv_choiceItems = (ListView) view.findViewById(R.id.lv_choices);
		lv_choiceItems.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
				BaseAlertDialog deleteDialog = new TextViewOkCancelDialog(context, "Delete", "Do you want to delete this item?");
				deleteDialog.setListener(new BaseAlertDialog.Listener() {
					@Override
					public void onResult(Object resultData) {
						boolean delete = (Boolean) resultData;
						if(delete){
							choiceItemList.remove(position);
							adapterList.remove(position);
							adapter.notifyDataSetChanged();
						}
					}
				});
				deleteDialog.show();
			}
		});
		but_addItem = (Button) view.findViewById(R.id.but_addItem);
		but_addItem.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showChoiceDialog();
			}
		});
	}
	
	private void showChoiceDialog(){
		choiceDialog = new ChoiceItemDialog(context, allImages);
		choiceDialog.setListener(new BaseAlertDialog.Listener() {
			@Override
			public void onResult(Object resultData) {
				Choice choice = (Choice) resultData;
				addChoiceToList(choice);
			}
		});
		choiceDialog.show();
	}
	
	private void addChoiceToList(Choice choice){
		choiceItemList.add(choice);
		
		if(choice.getImage() != null){
			// if user selected an image, figure out which one they selected
			int imageIndex = Collections.binarySearch(allImages, new SdlImageItem(null, choice.getImage().getValue(), null), new SdlImageItemComparator());
			if(imageIndex >= 0 && imageIndex < allImages.size()){
				SdlImageItem item = allImages.get(imageIndex);
				adapter.add(new SdlImageItem(item.getBitmap(), choice.getMenuName(), FileType.GRAPHIC_JPEG));
				adapter.notifyDataSetChanged();
			}
		}
		else{
			// if the user didn't select an image, we'll create an empty one to display in the adapter
			Bitmap image = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
			adapter.add(new SdlImageItem(image, choice.getMenuName(), FileType.GRAPHIC_JPEG));
			adapter.notifyDataSetChanged();
		}
	}
	
	//dialog button listeners
	private final DialogInterface.OnClickListener okButtonListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			Vector<Choice> choiceItems = new Vector<Choice>(choiceItemList);
			
			if(choiceItems.size() > 0){
				RPCRequest result = SdlRequestFactory.createInteractionChoiceSet(choiceItems);
				notifyListener(result);
			}
			else{
				Toast.makeText(context, "Must enter at least 1 choice name.", Toast.LENGTH_LONG).show();
			}
		}
	};

}

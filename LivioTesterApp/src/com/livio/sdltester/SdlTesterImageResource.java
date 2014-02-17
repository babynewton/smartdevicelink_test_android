package com.livio.sdltester;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.smartdevicelink.proxy.rpc.enums.FileType;

public enum SdlTesterImageResource {
	IC_APP_ICON("App Icon", FileType.GRAPHIC_PNG, R.drawable.ic_launcher),
	IC_ADD("Add Item", FileType.GRAPHIC_PNG, R.drawable.add),
	IC_ADD_TO_FAVORITES("Add to Favorites", FileType.GRAPHIC_PNG, R.drawable.add_to_favorites),
	IC_ANCHOR("Anchor", FileType.GRAPHIC_PNG, R.drawable.anchor),
	IC_GAME_PAD("Game Pad", FileType.GRAPHIC_PNG, R.drawable.game_pad),
	IC_REMOVE_FROM_FAVORITES("Remove from Favorites", FileType.GRAPHIC_PNG, R.drawable.remove_from_favorites),
	IC_REMOVE("Remove Item", FileType.GRAPHIC_PNG, R.drawable.remove),
	IC_ROCKET("Rocket", FileType.GRAPHIC_PNG, R.drawable.rocket),
	IC_UNDO("Undo", FileType.GRAPHIC_PNG, R.drawable.undo),
	IC_ZOOM_IN("Zoom In", FileType.GRAPHIC_PNG, R.drawable.zoom_in),
	IC_ZOOM_OUT("Zoom Out", FileType.GRAPHIC_PNG, R.drawable.zoom_out),
	
	;
	
	private String friendlyName;
	private int imageId;
	private FileType fileType;
	private SdlTesterImageResource(String friendlyName, FileType type, int imageId){
		this.friendlyName = friendlyName;
		this.imageId = imageId;
		this.fileType = type;
	}
	
	@Override
	public String toString(){
		return friendlyName;
	}
	
	public int getImageId(){
		return imageId;
	}
	public FileType getFileType() {
		return fileType;
	}

	public Bitmap getBitmap(Resources res){
		Bitmap result = BitmapFactory.decodeResource(res, imageId);
		return result;
	}
	
	public static Bitmap getBitmap(Resources res, SdlTesterImageResource image){
		int resId = image.getImageId();
		Bitmap result = BitmapFactory.decodeResource(res, resId);
		return result;
	}

}

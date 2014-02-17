package com.livio.sdl;

import java.util.Comparator;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;

import com.livio.sdl.utils.AndroidUtils;
import com.livio.sdl.utils.SdlUtils;
import com.smartdevicelink.proxy.rpc.Image;
import com.smartdevicelink.proxy.rpc.enums.FileType;
import com.smartdevicelink.proxy.rpc.enums.ImageType;

/**
 * Represents an SDL image object.  This includes the bitmap object itself, 
 * a filename to be used on SDL and an ImageType representing the type of image
 * we're looking at.
 *
 * @author Mike Burke
 *
 */
public class SdlImageItem {
	
	public static class SdlImageItemComparator implements Comparator<SdlImageItem>{
		@Override
		public int compare(SdlImageItem lhs, SdlImageItem rhs) {
			return lhs.getImageName().compareTo(rhs.getImageName());
		}
	}

	private Bitmap bitmap;
	private String imageName;
	private FileType imageType;
	
	public SdlImageItem(Bitmap bitmap, String imageName, FileType imageType) {
		this.bitmap = bitmap;
		this.imageName = imageName;
		this.imageType = imageType;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public String getImageName() {
		return imageName;
	}
	
	public FileType getImageType(){
		return imageType;
	}
	
	public Image toImage(){
		CompressFormat format = SdlUtils.convertImageTypeToCompressFormat(imageType);
		
		Image image = new Image();
		image.setImageType(ImageType.DYNAMIC);
		image.setValue(imageName);
		image.setBulkData(AndroidUtils.bitmapToRawBytes(bitmap, format));
		
		return image;
	}
	
	public static Image toImage(SdlImageItem item){
		return item.toImage();
	}

}

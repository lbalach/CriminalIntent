package com.lacreatelit.android.criminalintent;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.Display;
import android.widget.ImageView;

/*
 * Get a BitmapDrawable from a local file that is scaled down to fit the current
 * Window size
 */

public class PictureUtils {

	@SuppressWarnings("deprecation")
	public static BitmapDrawable getScaledDrawable(Activity activity, 
			String path) {
		
		// Get the dimensions to which the image needs to get scaled to
		Display display = activity.getWindowManager().getDefaultDisplay();
		float displayWidth = display.getWidth();
		float displayHeight = display.getHeight();
		
		// Read in the dimensions of the image on the disk
		BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
		
		// Used to figure out the dimensions of the bitmap without loading the 
		// bitmap into the memory
		bitmapOptions.inJustDecodeBounds = true;
		
		BitmapFactory.decodeFile(path, bitmapOptions);
		
		float imageWidth = bitmapOptions.outWidth;
		float imageHeight = bitmapOptions.outHeight;
		
		int inSampleSize = 1;
		if(imageHeight > displayHeight || imageWidth > displayWidth) {
			if(imageWidth > imageHeight) {
				inSampleSize = Math.round(imageHeight / displayHeight);
			} else {
				inSampleSize = Math.round(imageWidth / displayWidth);
			}
		}
		
		bitmapOptions = new BitmapFactory.Options();
		bitmapOptions.inSampleSize = inSampleSize;
		
		Bitmap bitmap = BitmapFactory.decodeFile(path, bitmapOptions);
		return new BitmapDrawable(activity.getResources(), bitmap);
		
	}
	
	public static void cleanImageView(ImageView imageView) {
		
		if(!(imageView.getDrawable() instanceof BitmapDrawable))
			return;
		
		// Clean up the view's image for the sake of memory
		BitmapDrawable bitmapDrawable = (BitmapDrawable)imageView.getDrawable();
		bitmapDrawable.getBitmap().recycle();
		imageView.setImageDrawable(null);
	}

}

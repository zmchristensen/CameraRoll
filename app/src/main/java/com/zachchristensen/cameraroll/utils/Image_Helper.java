package com.zachchristensen.cameraroll.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

public class Image_Helper {

	public static Bitmap loadFullImage( Context context, Uri photoUri  ) {
	    Cursor photoCursor = null;

	    try {
	        // Attempt to fetch asset filename for image
	        String[] projection = { MediaStore.Images.Media.DATA };
	        photoCursor = context.getContentResolver().query( photoUri, 
	                                                    projection, null, null, null );

	        if ( photoCursor != null && photoCursor.getCount() == 1 ) {
	            photoCursor.moveToFirst();
	            String photoFilePath = photoCursor.getString(
	                photoCursor.getColumnIndex(MediaStore.Images.Media.DATA) );

	            // Load image from path
	            return BitmapFactory.decodeFile( photoFilePath, null );
	        }
	    } finally {
	        if ( photoCursor != null ) {
	            photoCursor.close();
	        }
	    }

	    return null;
	}
	
	public static Bitmap loadThumbnailImage(Context context, String url ) {
	    // Get original image ID
	    int originalImageId = Integer.parseInt(url.substring(url.lastIndexOf("/") + 1, url.length()));

	    // Get (or create upon demand) the micro thumbnail for the original image.
	    return MediaStore.Images.Thumbnails.getThumbnail(context.getContentResolver(),
	                        originalImageId, MediaStore.Images.Thumbnails.MICRO_KIND, null);
	}
}

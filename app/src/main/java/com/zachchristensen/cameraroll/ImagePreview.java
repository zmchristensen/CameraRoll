package com.zachchristensen.cameraroll;

import com.zachchristensen.cameraroll.utils.Image_Helper;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

public class ImagePreview extends Activity {

	public static final String IMAGE_URI = "image_uri";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_imagepreview);
	
		String uri = getIntent().getStringExtra(IMAGE_URI);
		Log.i("image_uri", uri);
		
		ImageView preview = (ImageView) findViewById(R.id.preview);
		preview.setImageBitmap(Image_Helper.loadFullImage(this, Uri.parse(uri)));
		
	}
}

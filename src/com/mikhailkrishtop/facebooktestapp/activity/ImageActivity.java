package com.mikhailkrishtop.facebooktestapp.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

import com.mikhailkrishtop.facebooktestapp.R;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ImageActivity extends Activity {

	public static final String IMAGE = "image-parcelable";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image);

		String imageUri = (String) getIntent().getExtras().getString(IMAGE);

		if (imageUri == null) {
			finish();
		}

		ImageLoader.getInstance().displayImage(imageUri,
				(ImageView) findViewById(R.id.imageholder));
	}

}

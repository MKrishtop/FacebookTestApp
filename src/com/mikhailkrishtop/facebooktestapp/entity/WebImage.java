package com.mikhailkrishtop.facebooktestapp.entity;

import android.graphics.Bitmap;

public class WebImage {
	
	public Bitmap thumbnail;
	public String source;
	
	public WebImage(Bitmap thumbnail, String source) {
		this.thumbnail = thumbnail;
		this.source = source;
	}

}

package com.mikhailkrishtop.facebooktestapp.adapter;

import java.util.ArrayList;
import java.util.List;

import com.mikhailkrishtop.facebooktestapp.entity.WebImage;


import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter{
	private Context context;
	 
    List<WebImage> images;
 
    public ImageAdapter(Context context){
        this.context = context;
        images = new ArrayList<WebImage>();
    }
 
    @Override
    public int getCount() {
        return images.size();
    }
 
    @Override
    public Object getItem(int position) {
        return images.get(position);
    }
 
    @Override
    public long getItemId(int position) {
        return 0;
    }
 
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = new ImageView(context);
        imageView.setImageBitmap(images.get(position).thumbnail);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return imageView;
    }
    
    public void addImage(String source, Bitmap b) {
    	images.add(new WebImage(b, source));
    }
    
    public String getImageSource(int position) {
    	return images.get(position).source;
    }
}

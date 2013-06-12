package com.mikhailkrishtop.facebooktestapp.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.model.GraphUser;
import com.mikhailkrishtop.facebooktestapp.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

public class FriendsListAdapter extends ArrayAdapter<GraphUser>{
	
	private static final String PICTURE_TEMPLATE_URL = "http://graph.facebook.com/%s/picture?width=100&height=100";
	
	private final Context context;
	private List<GraphUser> users;

    public FriendsListAdapter(Context context, List<GraphUser> users) {
        super(context, R.layout.friend_list_item);
        this.context = context;
        this.users = users;
    }
    
    @Override
	public int getCount() {
		return users.size();
	}
    
	@Override
	public GraphUser getItem(int position) {
		return users.get(position);
	}

	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.friend_list_item, null);
        
        TextView friendName = (TextView) rowView.findViewById(R.id.friend_name);
        final ImageView friendPhoto = (ImageView) rowView.findViewById(R.id.friend_photo);
        friendName.setText(users.get(position).getName());
        
        ImageLoader.getInstance().loadImage(
        		String.format(PICTURE_TEMPLATE_URL, users.get(position).getId()), 
        		new SimpleImageLoadingListener() {
		            @Override
		            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
		            	friendPhoto.setImageBitmap(loadedImage);	
		            	friendPhoto.setVisibility(View.VISIBLE);
		            }
        		});
        
        return rowView;
    }
}

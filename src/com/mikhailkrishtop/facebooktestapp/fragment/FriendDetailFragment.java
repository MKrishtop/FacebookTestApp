package com.mikhailkrishtop.facebooktestapp.fragment;



import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

import com.facebook.model.GraphUser;
import com.mikhailkrishtop.facebooktestapp.R;
import com.mikhailkrishtop.facebooktestapp.activity.FriendDetailActivity;
import com.mikhailkrishtop.facebooktestapp.activity.ImageActivity;
import com.mikhailkrishtop.facebooktestapp.adapter.ImageAdapter;
import com.mikhailkrishtop.facebooktestapp.other.FriendPhotoDownloaderTask;
import com.mikhailkrishtop.facebooktestapp.other.FriendsGraphRepository;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

public class FriendDetailFragment extends Fragment {

	@SuppressWarnings("unused")
	private static final String DEBUG_TAG = FriendDetailActivity.class
			.getSimpleName() + ".debug";

	public static final String ARG_ITEM_ID = "item";

	private GraphUser currentUser;
	private GridView grid;
	private ImageAdapter adapter;

	public FriendDetailFragment() { }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments().containsKey(ARG_ITEM_ID)) {

			currentUser = FriendsGraphRepository.getInstance().getFriendById(
					getArguments().getString(ARG_ITEM_ID));
			Log.i("INFO", currentUser.toString());
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_friend_detail,
				container, false);

		if (currentUser != null) {
			((TextView) rootView.findViewById(R.id.friend_detail))
					.setText(createUserInfo(currentUser));
			
			grid = (GridView)rootView.findViewById(R.id.friend_album);
			adapter = new ImageAdapter(getActivity().getApplicationContext());
			grid.setAdapter(adapter);
			grid.setOnItemClickListener(new OnThumbnailClickListener());
			
			new FriendPhotoDownloaderTask(
					currentUser.getId(),
					grid, adapter,
					getActivity())
			.execute();
		}
		
		return rootView;
	}

	private CharSequence createUserInfo(GraphUser user) {
		StringBuilder userInfo = new StringBuilder("");

		userInfo.append(String.format("First name: %s\n\n", user.getFirstName()));
		userInfo.append(String.format("Last name: %s\n\n", user.getLastName()));
		userInfo.append(String.format("Gender: %s\n\n", user.getProperty("gender")));
		userInfo.append(String.format("Locale: %s\n\n", user.getProperty("locale")));

		return userInfo.toString();
	}
	
	private class OnThumbnailClickListener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position,
				long id) {
			String source = adapter.getImageSource(position);
			ImageLoader.getInstance().loadImage(source,
					new SimpleImageLoadingListener() {
						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
							Intent intent = new Intent(
									FriendDetailFragment.this.getActivity(),
									ImageActivity.class);
							intent.putExtra(ImageActivity.IMAGE, imageUri);
							startActivity(intent);
						}
					});
		}
	}
	
	
}

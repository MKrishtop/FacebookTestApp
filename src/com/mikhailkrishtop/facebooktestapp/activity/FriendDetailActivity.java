package com.mikhailkrishtop.facebooktestapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import com.mikhailkrishtop.facebooktestapp.R;
import com.mikhailkrishtop.facebooktestapp.fragment.FriendDetailFragment;

public class FriendDetailActivity extends FragmentActivity {
	
	@SuppressWarnings("unused")
	private static final String DEBUG_TAG = FriendDetailActivity.class.getSimpleName() + ".debug";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friend_detail);

		if (savedInstanceState == null) {
			Bundle arguments = new Bundle();
			arguments.putString(FriendDetailFragment.ARG_ITEM_ID, getIntent()
					.getStringExtra(FriendDetailFragment.ARG_ITEM_ID));
			FriendDetailFragment fragment = new FriendDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.add(R.id.friend_detail_container, fragment).commit();
		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpTo(this, new Intent(this,
					FriendListActivity.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}

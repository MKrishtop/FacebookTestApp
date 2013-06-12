package com.mikhailkrishtop.facebooktestapp.activity;

import java.util.Arrays;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.facebook.Request;
import com.facebook.Request.GraphUserListCallback;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.Session.StatusCallback;
import com.facebook.SessionDefaultAudience;
import com.facebook.SessionLoginBehavior;
import com.facebook.SessionState;
import com.facebook.internal.SessionTracker;
import com.facebook.internal.Utility;
import com.facebook.model.GraphUser;
import com.mikhailkrishtop.facebooktestapp.R;
import com.mikhailkrishtop.facebooktestapp.fragment.FriendDetailFragment;
import com.mikhailkrishtop.facebooktestapp.fragment.FriendListFragment;
import com.mikhailkrishtop.facebooktestapp.other.FriendsGraphRepository;

public class FriendListActivity extends FragmentActivity implements
		FriendListFragment.Callbacks {

	@SuppressWarnings("unused")
	private static final String DEBUG_TAG = FriendDetailActivity.class
			.getSimpleName() + ".debug";

	private static final String USER_FIELDS = "id, name, birthday, locale, first_name, middle_name, last_name, gender, languages, link, age_range, timezone, email, location, picture";
	private static final List<String> FRIENDS_PERMISSIONS = Arrays.asList("user_birthday", "email", "user_location", "friends_about_me", "friends_photo_video_tags", "friends_photos", "user_photos");
	
	private boolean mTwoPane;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friend_list);

		signInWithFacebook();

		if (findViewById(R.id.friend_detail_container) != null) {
			mTwoPane = true;

			((FriendListFragment) getSupportFragmentManager().findFragmentById(
					R.id.friend_list_fragment)).setActivateOnItemClick(true);
		}

	}

	private void getFriends() {
		Session activeSession = Session.getActiveSession();
		if (activeSession.getState().isOpened()) {
			Request friendRequest = Request.newMyFriendsRequest(activeSession,
					new FriendsListCallback());
			Bundle params = new Bundle();
			params.putString("fields", USER_FIELDS);
			friendRequest.setParameters(params);
			friendRequest.executeAsync();
		}
	}

	private void signInWithFacebook() {

		SessionTracker sessionTracker = new SessionTracker(getBaseContext(),
				new StatusCallback() {
					@Override
					public void call(Session session, SessionState state,
							Exception exception) {
					}
				}, null, false);

		String applicationId = Utility
				.getMetadataApplicationId(getBaseContext());
		Session currentSession;
		if (Session.getActiveSession() == null)
			currentSession = sessionTracker.getSession();
		else
			currentSession = Session.getActiveSession();

		if (currentSession == null || currentSession.getState().isClosed()) {
			sessionTracker.setSession(null);
			Session session = new Session.Builder(getBaseContext())
					.setApplicationId(applicationId).build();
			Session.setActiveSession(session);
			currentSession = session;
		}

		if (!currentSession.isOpened()) {
			Session.OpenRequest openRequest = null;
			openRequest = new Session.OpenRequest(FriendListActivity.this);

			if (openRequest != null) {
				openRequest.setDefaultAudience(SessionDefaultAudience.FRIENDS);
				openRequest.setPermissions(FRIENDS_PERMISSIONS);
				openRequest.setLoginBehavior(SessionLoginBehavior.SSO_WITH_FALLBACK);

				currentSession.openForRead(openRequest);
				Session.setActiveSession(currentSession);
				getFriends();
			}
		} else {
			Request.executeMeRequestAsync(currentSession,
				new Request.GraphUserCallback() {
					@Override
					public void onCompleted(GraphUser user, Response response) {
						getFriends();
					}
			});
		}

	}

	@Override
	public void onItemSelected(GraphUser user) {
		if (mTwoPane) {
			Bundle arguments = new Bundle();
			arguments.putString(FriendDetailFragment.ARG_ITEM_ID, user.getId());
			FriendDetailFragment fragment = new FriendDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.friend_detail_container, fragment).commit();

		} else {
			Intent detailIntent = new Intent(this, FriendDetailActivity.class);
			detailIntent.putExtra(FriendDetailFragment.ARG_ITEM_ID,
					user.getId());
			startActivity(detailIntent);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		Session.getActiveSession().onActivityResult(this, requestCode,
				resultCode, data);
		getFriends();
	}

	private class FriendsListCallback implements GraphUserListCallback {
		@Override
		public void onCompleted(List<GraphUser> users, Response response) {
			if (users != null) {
				for (GraphUser g : users) {
					Log.i("INFO", g.getName());
				}
				Fragment f = getSupportFragmentManager().findFragmentById(
						R.id.friend_list_fragment);
				if (!(f instanceof FriendListFragment)) {
					throw new IllegalStateException("Wrong fragment retrieved.");
				}
				FriendsGraphRepository.getInstance().setFriends(users);
				((FriendListFragment) f).updateData();
			}
		}
	}
}

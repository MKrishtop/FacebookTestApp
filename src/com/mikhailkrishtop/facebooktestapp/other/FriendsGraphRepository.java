package com.mikhailkrishtop.facebooktestapp.other;

import java.util.List;

import com.facebook.model.GraphUser;

public class FriendsGraphRepository {
	
	private List<GraphUser> users;
	
	private static volatile FriendsGraphRepository inst;
	
	private FriendsGraphRepository(){}
	
	public static synchronized FriendsGraphRepository getInstance() {
		if (inst == null) {
			inst = new FriendsGraphRepository();
		}
		return inst;
	}
	
	public synchronized void setFriends(List<GraphUser> users) {
		this.users = users;
	}
	
	public synchronized List<GraphUser> getFriends() {
		return users;
	}
	
	public synchronized GraphUser getFriendById(String id) {
		for (GraphUser u : users) {
			if (u.getId().equals(id)) return u;
		}
		return null;
	}

}

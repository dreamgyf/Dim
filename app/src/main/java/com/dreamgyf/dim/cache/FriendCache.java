package com.dreamgyf.dim.cache;

import com.dreamgyf.dim.entity.Friend;

import java.util.ArrayList;
import java.util.List;

public class FriendCache {

	private static List<Friend> friendList = new ArrayList<>();

	public static Friend findFriendById(int friendId) {
		synchronized (FriendCache.class) {
			for(int i = friendList.size() - 1; i >= 0; i--) {
				if(friendList.get(i).getId() == friendId) {
					return friendList.get(i);
				}
			}
			return null;
		}
	}

	public static void saveFriend(Friend friend) {
		synchronized (FriendCache.class) {
			for(int i = 0; i < friendList.size(); i++) {
				if(friendList.get(i).getId() == friend.getId()) {
					friendList.remove(i);
					break;
				}
			}
			friendList.add(friend);
		}
	}

	public static void saveFriend(List<Friend> friends) {
		for(Friend friend : friends) {
			saveFriend(friend);
		}
	}

}

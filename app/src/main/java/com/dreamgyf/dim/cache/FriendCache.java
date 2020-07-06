package com.dreamgyf.dim.cache;

import com.dreamgyf.dim.entity.Friend;

import java.util.ArrayList;
import java.util.List;

public class FriendCache {

	private static List<Friend> FriendList = new ArrayList<>();

	public static Friend findFriendById(int friendId) {
		synchronized (FriendCache.class) {
			for(int i = FriendList.size() - 1; i >= 0; i--) {
				if(FriendList.get(i).getId() == friendId) {
					return FriendList.get(i);
				}
			}
			return null;
		}
	}

	public static void saveFriend(Friend friend) {
		synchronized (FriendCache.class) {
			for(int i = 0; i < FriendList.size(); i++) {
				if(FriendList.get(i).getId() == friend.getId()) {
					FriendList.remove(i);
					break;
				}
			}
			FriendList.add(friend);
		}
	}

}

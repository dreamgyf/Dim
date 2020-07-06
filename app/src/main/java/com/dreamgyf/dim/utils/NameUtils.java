package com.dreamgyf.dim.utils;

import com.dreamgyf.dim.entity.Friend;

public class NameUtils {

	public static String getUsername(Friend friend) {
		if(friend.getRemarkName() != null) {
			return friend.getRemarkName();
		}
		else if(friend.getNickname() != null) {
			return friend.getNickname();
		}
		else {
			return friend.getUsername();
		}
	}
}

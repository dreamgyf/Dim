package com.dreamgyf.dim.utils;

import com.dreamgyf.dim.entity.Friend;
import com.dreamgyf.dim.entity.Group;
import com.dreamgyf.dim.entity.User;

public class NameUtils {

	public static String getUsername(User user) {
		if(user instanceof Friend && ((Friend) user).getRemarkName() != null) {
			return ((Friend) user).getRemarkName();
		}
		else if(user.getNickname() != null) {
			return user.getNickname();
		}
		else {
			return user.getUsername();
		}
	}

	public static String getGroupName(Group group) {
		return group.getName();
	}
}

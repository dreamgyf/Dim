package com.dreamgyf.dim.utils;

import com.dreamgyf.dim.entity.httpresp.User;

public class NameUtils {

	public static String getUsername(User user) {
		if(user.getRemarkName() != null) {
			return user.getRemarkName();
		}
		else if(user.getNickname() != null) {
			return user.getNickname();
		}
		else {
			return user.getUsername();
		}
	}
}

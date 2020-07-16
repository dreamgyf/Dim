package com.dreamgyf.dim.cache;

import com.dreamgyf.dim.entity.User;

import java.util.ArrayList;
import java.util.List;

public class UserCache {

	private static List<User> userList = new ArrayList<>();

	public static User findUser(int userId) {
		synchronized (UserCache.class) {
			for(int i = userList.size() - 1;i >= 0;i--) {
				if(userList.get(i).getId() == userId) {
					return userList.get(i);
				}
			}
			return null;
		}
	}

	public static void saveUser(User user) {
		synchronized (UserCache.class) {
			for(int i = 0;i < userList.size();i++) {
				if(userList.get(i).getId() == user.getId()) {
					userList.remove(i);
					break;
				}
			}
			userList.add(user);
		}
	}

	public static void saveUser(List<User> users) {
		for(User user : users) {
			saveUser(user);
		}
	}
}

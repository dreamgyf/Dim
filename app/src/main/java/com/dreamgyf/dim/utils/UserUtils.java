package com.dreamgyf.dim.utils;

import com.dreamgyf.dim.api.service.UserApiService;
import com.dreamgyf.dim.cache.UserCache;
import com.dreamgyf.dim.entity.Friend;
import com.dreamgyf.dim.entity.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.rxjava3.core.Observable;

public class UserUtils {

	/**
	 * 优先级 Friend本地缓存 -> User本地缓存 -> User网络数据
	 */
	public static Observable<User> getUser(int userId) {
		return Observable.<User>create(emitter -> {
			Friend friend = findFriend(userId);
			if(friend != null) {
				emitter.onNext(friend);
			} else {
				User user = UserCache.findUser(userId);
				if(user != null) {
					emitter.onNext(user);
				} else {
					emitter.onError(new Throwable("user not found in cache"));
				}
			}
		})
				.onErrorResumeNext(t -> UserApiService.getInstance().fetchUserInfo(userId)
						.flatMap(user -> {
							UserCache.saveUser(user);
							return Observable.just(user);
						}));
	}

	private static User my;

	public static User my() {
		return my;
	}

	public static void updateMy(User my) {
		UserUtils.my = my;
	}

	private final static List<Friend> friendList = Collections.synchronizedList(new ArrayList<>());

	public static Friend getFriend(int position) {
		return friendList.get(position);
	}

	public static int friendCount() {
		return friendList.size();
	}

	public static Friend findFriend(int id) {
		for(int i = friendList.size() - 1;i >= 0;i--) {
			if(friendList.get(i).getId() == id) {
				return friendList.get(i);
			}
		}
		return null;
	}

	public static void addFriend(Friend friend) {
		if(friendList.isEmpty()) {
			friendList.add(friend);
			return;
		}
		for(int i = 0;i < friendList.size();i++) {
			if(friend.compareTo(friendList.get(i)) < 0) {
				friendList.add(i, friend);
				return;
			}
			if(i == friendList.size() - 1) {
				friendList.add(friend);
				return;
			}
		}
	}

	public static void addFriend(List<Friend> friends) {
		for(Friend friend : friends) {
			addFriend(friend);
		}
	}

	public static void removeFriend(int id) {
		for(int i = friendList.size() - 1;i >= 0;i--) {
			if(friendList.get(i).getId() == id) {
				friendList.remove(i);
				return;
			}
		}
	}

}

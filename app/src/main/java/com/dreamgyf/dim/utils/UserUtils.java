package com.dreamgyf.dim.utils;

import com.dreamgyf.dim.api.service.UserApiService;
import com.dreamgyf.dim.cache.FriendCache;
import com.dreamgyf.dim.cache.UserCache;
import com.dreamgyf.dim.entity.User;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.functions.Function;

public class UserUtils {

	public static Observable<User> fetchUser(int userId) {
		return Observable
				.just(FriendCache.findFriendById(userId))
				.flatMap(new Function<User, ObservableSource<User>>() {
					@Override
					public ObservableSource<User> apply(User user) throws Throwable {
						if(user != null) {
							return Observable.just(user);
						} else {
							return Observable.just(UserCache.findUserById(userId));
						}
					}
				})
				.flatMap(new Function<User, ObservableSource<User>>() {
					@Override
					public ObservableSource<User> apply(User user) throws Throwable {
						if(user != null) {
							return Observable.just(user);
						} else {
							return UserApiService.getInstance().fetchUserInfo(userId);
						}
					}
				});
	}

}

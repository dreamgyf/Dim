package com.dreamgyf.dim.api.service;

import com.dreamgyf.dim.api.UserApi;
import com.dreamgyf.dim.data.StaticData;
import com.dreamgyf.dim.entity.User;
import com.dreamgyf.dim.utils.RetrofitUtils;

import io.reactivex.rxjava3.core.Observable;

public class UserApiService {

	private volatile static UserApiService INSTANCE;

	private UserApi mUserApi;

	private UserApiService(String baseUrl) {
		mUserApi = RetrofitUtils.createApi(UserApi.class, baseUrl, "");
	}

	public static UserApiService getInstance() {
		if (INSTANCE == null) {
			synchronized (UserApiService.class) {
				if (INSTANCE == null) {
					INSTANCE = new UserApiService(StaticData.DOMAIN);
				}
			}
		}
		return INSTANCE;
	}

	public Observable<User> fetchUserInfo(int userId) {
		return mUserApi.fetchUserInfo(userId);
	}

}

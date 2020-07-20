package com.dreamgyf.dim.api.service;

import com.dreamgyf.dim.api.FriendApi;
import com.dreamgyf.dim.data.StaticData;
import com.dreamgyf.dim.entity.Friend;
import com.dreamgyf.dim.utils.RetrofitUtils;
import com.dreamgyf.dim.utils.UserUtils;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class FriendApiService {

	private volatile static FriendApiService INSTANCE;

	private FriendApi mApi;

	private FriendApiService(String baseUrl) {
		mApi = RetrofitUtils.createApi(FriendApi.class, baseUrl, "");
	}

	public static FriendApiService getInstance() {
		if (INSTANCE == null) {
			synchronized (FriendApiService.class) {
				if (INSTANCE == null) {
					INSTANCE = new FriendApiService(StaticData.DOMAIN);
				}
			}
		}
		return INSTANCE;
	}

	public Observable<Boolean> checkIsFriend(int userId) {
		return mApi.checkIsFriend(UserUtils.my().getId(), userId);
	}

	public Observable<Friend> fetchFriendInfo(int friendId) {
		return mApi.fetchFriendInfo(UserUtils.my().getId(), friendId);
	}

	public Observable<Boolean> addFriend(int myId, int userId, String remarkOfMy, String remarkOfUser) {
		Map<String, Object> params = new HashMap<>();
		params.put("myId", myId);
		params.put("userId", userId);
		params.put("remarkOfMy", remarkOfMy);
		params.put("remarkOfUser", remarkOfUser);
		String requestJson = new Gson().toJson(params);
		RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=UTF-8"), requestJson);
		return mApi.addFriend(requestBody);
	}
}

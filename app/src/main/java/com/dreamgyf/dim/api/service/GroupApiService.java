package com.dreamgyf.dim.api.service;

import com.dreamgyf.dim.api.GroupApi;
import com.dreamgyf.dim.data.StaticData;
import com.dreamgyf.dim.utils.RetrofitUtils;
import com.dreamgyf.dim.utils.UserUtils;

import io.reactivex.rxjava3.core.Observable;

public class GroupApiService {

	private volatile static GroupApiService INSTANCE;

	private GroupApi mApi;

	private GroupApiService(String baseUrl) {
		mApi = RetrofitUtils.createApi(GroupApi.class, baseUrl, "");
	}

	public static GroupApiService getInstance() {
		if (INSTANCE == null) {
			synchronized (GroupApiService.class) {
				if (INSTANCE == null) {
					INSTANCE = new GroupApiService(StaticData.DOMAIN);
				}
			}
		}
		return INSTANCE;
	}

	public Observable<Boolean> checkInGroup(int groupId) {
		return mApi.checkInGroup(UserUtils.my().getId(), groupId);
	}
}

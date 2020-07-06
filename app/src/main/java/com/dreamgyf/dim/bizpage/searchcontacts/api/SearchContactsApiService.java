package com.dreamgyf.dim.bizpage.searchcontacts.api;

import com.dreamgyf.dim.bizpage.login.api.LoginApiService;
import com.dreamgyf.dim.data.StaticData;
import com.dreamgyf.dim.entity.User;
import com.dreamgyf.dim.utils.RetrofitUtils;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;

public class SearchContactsApiService {

	private volatile static SearchContactsApiService INSTANCE;

	private SearchContactsApi mSearchContactsApi;

	private SearchContactsApiService(String baseUrl) {
		mSearchContactsApi = RetrofitUtils.createApi(SearchContactsApi.class, baseUrl, "");
	}

	public static SearchContactsApiService getInstance() {
		if (INSTANCE == null) {
			synchronized (LoginApiService.class) {
				if (INSTANCE == null) {
					INSTANCE = new SearchContactsApiService(StaticData.DOMAIN);
				}
			}
		}
		return INSTANCE;
	}

	public Observable<List<User>> searchUser(int myId, String keyword) {
		return mSearchContactsApi.searchUser(myId, keyword);
	}

}

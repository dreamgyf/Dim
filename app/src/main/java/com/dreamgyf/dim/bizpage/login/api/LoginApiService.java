package com.dreamgyf.dim.bizpage.login.api;

import com.dreamgyf.dim.data.StaticData;
import com.dreamgyf.dim.entity.httpresp.LoginResp;
import com.dreamgyf.dim.utils.RetrofitUtils;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class LoginApiService {

	private volatile static LoginApiService INSTANCE;

	private LoginApi mLoginApi;

	private LoginApiService(String baseUrl) {
		mLoginApi = RetrofitUtils.createApi(LoginApi.class, baseUrl, "");
	}

	public static LoginApiService getInstance() {
		if (INSTANCE == null) {
			synchronized (LoginApiService.class) {
				if (INSTANCE == null) {
					INSTANCE = new LoginApiService(StaticData.DOMAIN);
				}
			}
		}
		return INSTANCE;
	}

	public Observable<LoginResp> login(String username, String passwordSha256) {
		Map<String, String> params = new HashMap<>();
		params.put("username", username);
		params.put("password", passwordSha256);
		String requestJson = new Gson().toJson(params);
		RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=UTF-8"), requestJson);
		return mLoginApi.login(requestBody);
	}
}

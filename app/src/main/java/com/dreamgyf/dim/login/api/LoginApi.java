package com.dreamgyf.dim.login.api;

import com.dreamgyf.dim.entity.httpresp.LoginResp;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LoginApi {

	@POST("/signin")
	Observable<LoginResp> login(@Body RequestBody requestBody);
}

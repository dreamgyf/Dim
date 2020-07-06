package com.dreamgyf.dim.bizpage.login.api;

import com.dreamgyf.dim.entity.httpresp.LoginResp;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LoginApi {

	@POST("/user/signin")
	Observable<LoginResp> login(@Body RequestBody requestBody);
}

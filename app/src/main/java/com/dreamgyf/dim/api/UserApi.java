package com.dreamgyf.dim.api;

import com.dreamgyf.dim.entity.User;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface UserApi {

	@GET("/user/userinfo")
	Observable<User> fetchUserInfo(@Query("userId") int userId);
}

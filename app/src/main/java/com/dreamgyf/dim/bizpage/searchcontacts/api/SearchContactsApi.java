package com.dreamgyf.dim.bizpage.searchcontacts.api;

import com.dreamgyf.dim.entity.User;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SearchContactsApi {

	@GET("/user/search")
	Observable<List<User>> searchUser(@Query("myId") int myId, @Query("keyword") String keyword);

}

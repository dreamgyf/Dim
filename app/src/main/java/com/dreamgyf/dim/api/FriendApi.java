package com.dreamgyf.dim.api;

import com.dreamgyf.dim.entity.Friend;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface FriendApi {
	@GET("/friend/check")
	Observable<Boolean> checkIsFriend(@Query("myId") int myId, @Query("userId") int userId);

	@GET("/friend/friendinfo")
	Observable<Friend> fetchFriendInfo(@Query("myId") int myId, @Query("friendId") int friendId);
}

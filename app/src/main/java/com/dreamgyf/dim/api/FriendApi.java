package com.dreamgyf.dim.api;

import com.dreamgyf.dim.entity.Friend;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface FriendApi {
	@GET("/friend/check")
	Observable<Boolean> checkIsFriend(@Query("myId") int myId, @Query("userId") int userId);

	@GET("/friend/friendinfo")
	Observable<Friend> fetchFriendInfo(@Query("myId") int myId, @Query("friendId") int friendId);

	@PUT("/friend/addFriend")
	Observable<Boolean> addFriend(@Body RequestBody requestBody);
}

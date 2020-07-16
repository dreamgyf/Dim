package com.dreamgyf.dim.api;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GroupApi {

	@GET("/group/check")
	Observable<Boolean> checkInGroup(@Query("myId") int myId, @Query("groupId") int groupId);

}

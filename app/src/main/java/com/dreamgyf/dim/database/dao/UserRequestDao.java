package com.dreamgyf.dim.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.dreamgyf.dim.database.entity.UserRequest;

import java.util.List;

@Dao
public interface UserRequestDao {

	@Query("select * from user_request where receiver_id = (:myId) or sender_id = (:myId) order by receive_time asc")
	List<UserRequest> getUserRequestList(int myId);

	@Query("select * from (" +
			"select * from user_request where receiver_id = (:myId) or sender_id = (:myId) order by receive_time desc limit (:limit) offset ((:page - 1) * :limit)" +
			") order by receive_time asc")
	List<UserRequest> getUserRequestListByPage(int myId, int page, int limit);

	@Query("select * from (" +
			"select * from user_request where receiver_id = (:myId) or sender_id = (:myId) order by receive_time desc limit (:limit) offset (:offset)" +
			") order by receive_time asc")
	List<UserRequest> getUserRequestListByOffset(int myId, int offset, int limit);

	@Insert
	void insertUserRequest(UserRequest userRequest);

	@Update
	void updateUserRequest(UserRequest userRequest);

	@Query("select * from user_request where receiver_id = (:userId) or sender_id = (:myId) order by receive_time desc")
	UserRequest findUserRequestByReceiverId(int myId, int userId);
}

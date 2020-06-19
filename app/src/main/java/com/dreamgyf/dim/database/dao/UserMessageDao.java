package com.dreamgyf.dim.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.dreamgyf.dim.database.entity.UserMessage;

import java.util.List;

@Dao
public interface UserMessageDao {

	@Query("select * from user_message where my_id = (:myId) and user_id = (:userId) order by receive_time asc")
	List<UserMessage> getUserMessageList(int myId, int userId);

	@Query("select * from (" +
			"select * from user_message where my_id = (:myId) and user_id = (:userId) order by receive_time desc limit (:limit) offset ((:page - 1) * :limit)" +
			") order by receive_time asc")
	List<UserMessage> getUserMessageListByPage(int myId, int userId,int page,int limit);

	@Query("select * from (" +
			"select * from user_message where my_id = (:myId) and user_id = (:userId) order by receive_time desc limit (:limit) offset (:offset)" +
			") order by receive_time asc")
	List<UserMessage> getUserMessageListByOffset(int myId, int userId,int offset,int limit);

	@Insert
	void insertUserMessage(UserMessage userMessage);
}

package com.dreamgyf.dim.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.dreamgyf.dim.database.entity.GroupMessage;

import java.util.List;

@Dao
public interface GroupMessageDao {

	@Query("select * from group_message where my_id = (:myId) and group_id = (:groupId) order by receive_time asc")
	List<GroupMessage> getGroupMessageList(int myId, int groupId);

	@Query("select * from (" +
			"select * from group_message where my_id = (:myId) and group_id = (:groupId) order by receive_time desc limit (:limit) offset ((:page - 1) * :limit)" +
			") order by receive_time asc")
	List<GroupMessage> getGroupMessageListByPage(int myId, int groupId,int page,int limit);

	@Query("select * from (" +
			"select * from group_message where my_id = (:myId) and group_id = (:groupId) order by receive_time desc limit (:limit) offset (:offset)" +
			") order by receive_time asc")
	List<GroupMessage> getGroupMessageListByOffset(int myId, int groupId,int offset,int limit);

	@Insert
	void insertGroupMessage(GroupMessage groupMessage);
}

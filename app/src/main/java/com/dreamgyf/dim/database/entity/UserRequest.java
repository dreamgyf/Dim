package com.dreamgyf.dim.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.sql.Timestamp;

@Entity(tableName = "user_request")
public class UserRequest {

	@PrimaryKey(autoGenerate = true)
	public int id;

	@ColumnInfo(name = "receiver_id", index = true)
	public int receiverId;

	@ColumnInfo(name = "sender_id", index = true)
	public int senderId;

	@ColumnInfo(name = "message_type")
	public String verify;

	@ColumnInfo(name = "content")
	public String remark;

	/**
	 * 0 - 待处理
	 * 1 - 已接受
	 */
	@ColumnInfo(name = "status")
	public int status;

	@ColumnInfo(name = "receive_time")
	public Timestamp receiveTime;

}

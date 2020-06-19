package com.dreamgyf.dim.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.sql.Timestamp;

@Entity(tableName = "user_message")
public class UserMessage extends Message{

	@PrimaryKey(autoGenerate = true)
	public int id;

	@ColumnInfo(name = "my_id", index = true)
	public int myId;

	@ColumnInfo(name = "user_id", index = true)
	public int userId;

	@ColumnInfo(name = "message_type")
	public int messageType;

	@ColumnInfo(name = "content")
	public String content;

	@ColumnInfo(name = "receive_time")
	public Timestamp receiveTime;

}

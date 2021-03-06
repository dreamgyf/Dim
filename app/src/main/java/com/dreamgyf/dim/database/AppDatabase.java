package com.dreamgyf.dim.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.dreamgyf.dim.database.converters.Converters;
import com.dreamgyf.dim.database.dao.GroupMessageDao;
import com.dreamgyf.dim.database.dao.UserMessageDao;
import com.dreamgyf.dim.database.dao.UserRequestDao;
import com.dreamgyf.dim.database.entity.GroupMessage;
import com.dreamgyf.dim.database.entity.UserMessage;
import com.dreamgyf.dim.database.entity.UserRequest;

@Database(entities = {UserMessage.class, GroupMessage.class, UserRequest.class},
		version = 1,exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
	public abstract UserMessageDao userMessageDao();
	public abstract GroupMessageDao groupMessageDao();
	public abstract UserRequestDao userRequestDao();
}

package com.dreamgyf.dim.database.converters;

import androidx.room.TypeConverter;

import java.sql.Timestamp;

public class Converters {

	@TypeConverter
	public static Timestamp toTimestamp(long value) {
		return new Timestamp(value);
	}

	@TypeConverter
	public static long fromTimestamp(Timestamp timestamp) {
		return timestamp.getTime();
	}

}

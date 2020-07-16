package com.dreamgyf.dim.utils;

import android.app.Notification;
import android.content.Context;
import android.graphics.BitmapFactory;

import androidx.core.app.NotificationCompat;

import com.dreamgyf.dim.R;

public class NotificationUtils {

	public static Notification build(Context context, String title, String content) {
		NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"Message");
		Notification notification = builder.setContentTitle(title)
				.setContentText(content)
				.setSmallIcon(R.drawable.small_logo)
				.setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.drawable.small_logo))
				.setWhen(System.currentTimeMillis())
				.build();
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		return notification;
	}
}

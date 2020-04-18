package com.dreamgyf.dim;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MainApplication extends Application {

    private int activityCount = 0;

    private NotificationManager notificationManager;

    @Override
    public void onCreate() {
        super.onCreate();

        String test = "@@verify@@@@remark@@";
        int verifyPos = test.indexOf("@@verify@@");
        int remarkPos = test.indexOf("@@remark@@");
        String verify = test.substring(verifyPos + 10,remarkPos);
        String remark = test.substring(remarkPos + 10);
        String[] testArray = test.split("@@DIM@@");

        initNotification();

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {
                Log.d("Activity","one activity started");
                activityCount++;
                notificationManager.cancelAll();
            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {

            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {

            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {
                Log.d("Activity","one activity stopped");
                activityCount--;
            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {

            }
        });
    }

    private void initNotification() {
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "Message";
            String channelName = "聊天消息";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public boolean isAppBackground() {
        return activityCount == 0;
    }

    public NotificationManager getNotificationManager() {
        return notificationManager;
    }
}

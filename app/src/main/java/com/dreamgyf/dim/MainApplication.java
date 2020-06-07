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

import com.dreamgyf.dim.base.mqtt.MessageReceiveHandler;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class MainApplication extends Application {

    private static MainApplication INSTANCE;

    public static MainApplication getInstance() {
        return INSTANCE;
    }

    private int activityCount = 0;

    private NotificationManager notificationManager;

    private List<OnActivityPauseListener> mPauseListenerList = new ArrayList<>();

    public interface OnActivityPauseListener {
        void onPause();
    }

    public void addOnActivityPauseListener(OnActivityPauseListener listener) {
        mPauseListenerList.add(listener);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        INSTANCE = this;

        initNotification();
        createMessageReceiveHandler();

        String test = "@@verify@@@@remark@@";
        int verifyPos = test.indexOf("@@verify@@");
        int remarkPos = test.indexOf("@@remark@@");
        String verify = test.substring(verifyPos + 10,remarkPos);
        String remark = test.substring(remarkPos + 10);
        String[] testArray = test.split("@@DIM@@");


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
                for(OnActivityPauseListener listener : mPauseListenerList) {
                    if(listener != null) {
                        listener.onPause();
                    }
                }
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

    private void createMessageReceiveHandler() {
        try {
            Constructor<MessageReceiveHandler> constructor =  MessageReceiveHandler.class.getDeclaredConstructor(MainApplication.class);
            constructor.setAccessible(true);
            MessageReceiveHandler object = constructor.newInstance(this);
            Field instance = MessageReceiveHandler.class.getDeclaredField("INSTANCE");
            instance.setAccessible(true);
            instance.set(object,object);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

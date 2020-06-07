package com.dreamgyf.dim.entity;

import android.content.Intent;

import com.dreamgyf.dim.base.broadcast.BroadcastActions;
import com.dreamgyf.dim.entity.httpresp.User;
import com.google.gson.Gson;

import java.io.Serializable;

public class Conversation implements Serializable {

    public static class Type {
        public final static int USER = 0;
        public final static int GROUP = 1;
    }

    public static Intent createBroadcast(Conversation conversation) {
        Intent intent = new Intent(BroadcastActions.UPDATE_CONVERSATION);
        Gson gson = new Gson();
        String json = gson.toJson(conversation);
        intent.putExtra("conversation", json);
        return intent;
    }

    public static Conversation parseIntent(Intent intent) {
        String json = intent.getStringExtra("conversation");
        Gson gson = new Gson();
        return gson.fromJson(json, Conversation.class);
    }

    private int type;

    private User user;

    private Group group;

    private String currentMessage;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public String getCurrentMessage() {
        return currentMessage;
    }

    public void setCurrentMessage(String currentMessage) {
        this.currentMessage = currentMessage;
    }
}

package com.dreamgyf.dim.entity;

import com.dreamgyf.dim.entity.httpresp.User;

import java.io.Serializable;

public class Conversation implements Serializable {

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

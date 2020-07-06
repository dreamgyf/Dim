package com.dreamgyf.dim.entity;

import java.io.Serializable;

public class Conversation implements Serializable {

    private int type;

    private Friend mFriend;

    private Group group;

    private String currentMessage;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Friend getFriend() {
        return mFriend;
    }

    public void setFriend(Friend friend) {
        this.mFriend = friend;
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

package com.dreamgyf.dim.entity.httpresp;

import com.dreamgyf.dim.entity.Friend;
import com.dreamgyf.dim.entity.Group;

import java.io.Serializable;
import java.util.List;

public class LoginResp extends Response implements Serializable {

    private Friend my;

    private List<Friend> friendList;

    private List<Group> groupList;


    public Friend getMy() {
        return this.my;
    }

    public void setMy(Friend my) {
        this.my = my;
    }

    public List<Friend> getFriendList() {
        return this.friendList;
    }

    public void setFriendList(List<Friend> friendList) {
        this.friendList = friendList;
    }

    public List<Group> getGroupList() {
        return this.groupList;
    }

    public void setGroupList(List<Group> groupList) {
        this.groupList = groupList;
    }

}

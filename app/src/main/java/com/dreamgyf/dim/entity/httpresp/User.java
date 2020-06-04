package com.dreamgyf.dim.entity.httpresp;

import java.io.Serializable;

public class User implements Serializable,Comparable<User> {

    private Integer id;

    private String username;

    private String nickname;

    private String remarkName;

    private Integer avatarId;

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return this.nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getRemarkName() {
        return this.remarkName;
    }

    public void setRemarkName(String remarkName) {
        this.remarkName = remarkName;
    }

    public Integer getAvatarId() {
        return avatarId;
    }

    public void setAvatarId(Integer avatarId) {
        this.avatarId = avatarId;
    }

    @Override
    public int compareTo(User user) {
        String thisUsername;
        if(this.remarkName != null) {
            thisUsername = this.remarkName;
        }
        else if(this.nickname != null) {
            thisUsername = this.nickname;
        }
        else {
            thisUsername = this.username;
        }
        String thatUsername;
        if(user.remarkName != null) {
            thatUsername = user.remarkName;
        }
        else if(user.nickname != null) {
            thatUsername = user.nickname;
        }
        else {
            thatUsername = user.username;
        }
        return thisUsername.compareTo(thatUsername);
    }
}

package com.dreamgyf.dim.entity.httpresp;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class User implements Serializable, Parcelable,Comparable<User> {

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

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }
        dest.writeString(username);
        dest.writeString(nickname);
        dest.writeString(remarkName);
        if (avatarId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(avatarId);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            User user = new User();
            if (in.readByte() == 0) {
                user.setId(null);
            } else {
                user.setId(in.readInt());
            }
            user.setUsername(in.readString());
            user.setNickname(in.readString());
            user.setRemarkName(in.readString());
            if (in.readByte() == 0) {
                user.setAvatarId(null);
            } else {
                user.setAvatarId(in.readInt());
            }
            return user;
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}

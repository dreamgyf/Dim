package com.dreamgyf.dim.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class User implements Serializable, Parcelable, Comparable<User> {

	private int id;

	private String username;

	private String nickname;

	private int avatarId;

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
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

	public int getAvatarId() {
		return this.avatarId;
	}

	public void setAvatarId(int avatarId) {
		this.avatarId = avatarId;
	}

	@Override
	public int compareTo(User user) {
		String thisUsername;
		if (this.nickname != null) {
			thisUsername = this.nickname;
		} else {
			thisUsername = this.username;
		}
		String thatUsername;
		if (user.nickname != null) {
			thatUsername = user.nickname;
		} else {
			thatUsername = user.username;
		}
		return thisUsername.compareTo(thatUsername);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeString(username);
		dest.writeString(nickname);
		dest.writeInt(avatarId);
	}

	protected User(Parcel in) {
		id = in.readInt();
		username = in.readString();
		nickname = in.readString();
		avatarId = in.readInt();
	}

	public static final Creator<User> CREATOR = new Creator<User>() {
		@Override
		public User createFromParcel(Parcel in) {
			return new User(in);
		}

		@Override
		public User[] newArray(int size) {
			return new User[size];
		}
	};
}

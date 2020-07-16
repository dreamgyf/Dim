package com.dreamgyf.dim.entity;

import android.os.Parcel;

public class Friend extends User {

	private String remarkName;

	public String getRemarkName() {
		return this.remarkName;
	}

	public void setRemarkName(String remarkName) {
		this.remarkName = remarkName;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeString(remarkName);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	protected Friend(Parcel in) {
		super(in);
		this.remarkName = in.readString();
	}

	public static final Creator<Friend> CREATOR = new Creator<Friend>() {
		@Override
		public Friend createFromParcel(Parcel in) {
			return new Friend(in);
		}

		@Override
		public Friend[] newArray(int size) {
			return new Friend[size];
		}
	};

}

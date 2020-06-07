package com.dreamgyf.dim.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Group implements Serializable, Parcelable {

    private int id;

    private String name;


    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Group> CREATOR = new Creator<Group>() {
        @Override
        public Group createFromParcel(Parcel in) {
            Group group = new Group();
            group.setId(in.readInt());
            group.setName(in.readString());
            return group;
        }

        @Override
        public Group[] newArray(int size) {
            return new Group[size];
        }
    };
}

package com.dreamgyf.dim.entity;

import java.io.Serializable;

public class Message implements Serializable {

    private int type;

    private String content;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

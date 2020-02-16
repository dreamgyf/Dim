package com.dreamgyf.dim.entity;

import com.dreamgyf.dim.R;

import java.io.Serializable;

public class Message implements Serializable {

    public static class Type {
        public static final int SEND_TEXT = R.layout.recycler_view_message_send_text_item;
        public static final int RECEIVE_TEXT = R.layout.recycler_view_message_receive_text_item;
//        public static final int SEND_IMAGE = R.layout.recycler_view_message_send_image_item;
//        public static final int RECEIVE_IMAGE = R.layout.recycler_view_message_receive_image_item;
//        public static final int SEND_VIDEO = R.layout.recycler_view_message_send_video_item;
//        public static final int RECEIVE_VIDEO = R.layout.recycler_view_message_receive_video_item;
//        public static final int SEND_FILE = R.layout.recycler_view_message_send_file_item;
//        public static final int RECEIVE_FILE = R.layout.recycler_view_message_receive_file_item;
//        public static final int RECEIVE_AUDIO = R.layout.recycler_view_message_receive_audio_item;
//        public static final int SEND_AUDIO = R.layout.recycler_view_message_send_audio_item;
    }

    private int type;

    private User user;

    private String content;

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

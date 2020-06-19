package com.dreamgyf.dim.base.enums;

public class MessageType {

	public static final int SEND_TEXT = 0;
	public static final int RECEIVE_TEXT = 1;
//	public static final int SEND_IMAGE = R.layout.recycler_view_message_send_image_item;
//	public static final int RECEIVE_IMAGE = R.layout.recycler_view_message_receive_image_item;
//	public static final int SEND_VIDEO = R.layout.recycler_view_message_send_video_item;
//	public static final int RECEIVE_VIDEO = R.layout.recycler_view_message_receive_video_item;
//	public static final int SEND_FILE = R.layout.recycler_view_message_send_file_item;
//	public static final int RECEIVE_FILE = R.layout.recycler_view_message_receive_file_item;
//	public static final int RECEIVE_AUDIO = R.layout.recycler_view_message_receive_audio_item;
//	public static final int SEND_AUDIO = R.layout.recycler_view_message_send_audio_item;

	public static class Analyzer {

		public static int analyze(boolean isReceive, String content) {
			if (isReceive) {
				return RECEIVE_TEXT;
			} else {
				return SEND_TEXT;
			}
		}

	}

}

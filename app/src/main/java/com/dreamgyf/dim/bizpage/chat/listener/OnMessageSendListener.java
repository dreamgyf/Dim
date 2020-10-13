package com.dreamgyf.dim.bizpage.chat.listener;

import com.dreamgyf.dim.database.entity.Message;

public interface OnMessageSendListener {
	void onSendSuccess(int chatType, Message message);
}

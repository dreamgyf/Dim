package com.dreamgyf.dim.chat.listener;

import com.dreamgyf.dim.database.entity.Message;

public interface OnMessageSendListener {

	void onSendSuccess(int chatType, Message message);

	void onSendError(Throwable t);

}

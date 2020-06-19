package com.dreamgyf.dim.eventbus.event;

import com.dreamgyf.dim.database.entity.UserMessage;

public class UserMessageEvent {

	private UserMessage userMessage;

	public UserMessageEvent(UserMessage userMessage) {
		this.userMessage = userMessage;
	}

	public UserMessage getUserMessage() {
		return userMessage;
	}

	public void setUserMessage(UserMessage userMessage) {
		this.userMessage = userMessage;
	}
}

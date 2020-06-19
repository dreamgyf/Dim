package com.dreamgyf.dim.eventbus.event;

import com.dreamgyf.dim.entity.Conversation;

public class ConversationEvent {

	private Conversation conversation;

	public ConversationEvent(Conversation conversation) {
		this.conversation = conversation;
	}

	public Conversation getConversation() {
		return conversation;
	}

	public void setConversation(Conversation conversation) {
		this.conversation = conversation;
	}
}

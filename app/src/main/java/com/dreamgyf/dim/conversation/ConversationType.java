package com.dreamgyf.dim.conversation;

import com.dreamgyf.dim.entity.Conversation;

public class ConversationType {

	public final static int FRIEND = 0;

	public final static int GROUP = 1;

	public static int analyzeType(Conversation conversation) {
		if(conversation.getGroup() != null) {
			return GROUP;
		} else {
			return FRIEND;
		}
	}
}

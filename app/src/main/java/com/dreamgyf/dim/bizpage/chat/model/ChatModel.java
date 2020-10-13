package com.dreamgyf.dim.bizpage.chat.model;

import com.dreamgyf.dim.base.mqtt.MqttTopicHandler;
import com.dreamgyf.dim.bizpage.chat.listener.OnMessageSendListener;
import com.dreamgyf.dim.data.StaticData;
import com.dreamgyf.dim.database.entity.UserMessage;
import com.dreamgyf.dim.enums.ChatType;
import com.dreamgyf.dim.enums.MessageType;
import com.dreamgyf.dim.utils.UserUtils;
import com.dreamgyf.gmqyttf.client.options.MqttPublishOption;

import java.sql.Timestamp;

public class ChatModel implements IChatModel {

	private OnMessageSendListener mOnMessageSendListener;

	public void setOnMessageSendListener(OnMessageSendListener onMessageSendListener) {
		mOnMessageSendListener = onMessageSendListener;
	}

	@Override
	public void sendMessage(int chatType, int toId, String text) {
		if (chatType == ChatType.USER) {
			String topic = MqttTopicHandler.build(MqttTopicHandler.SEND_FRIEND_MESSAGE, UserUtils.my().getId(), toId);
			StaticData.mqttClient.publish(topic, text, new MqttPublishOption().QoS(2));
			if (mOnMessageSendListener != null) {
				UserMessage userMessage = new UserMessage();
				userMessage.myId = UserUtils.my().getId();
				userMessage.userId = toId;
				userMessage.messageType = MessageType.SEND_TEXT;
				userMessage.content = text;
				userMessage.receiveTime = new Timestamp(System.currentTimeMillis());
				mOnMessageSendListener.onSendSuccess(chatType,userMessage);
			}
		}
	}
}

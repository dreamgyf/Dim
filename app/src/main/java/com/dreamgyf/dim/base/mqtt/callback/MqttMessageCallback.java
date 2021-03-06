package com.dreamgyf.dim.base.mqtt.callback;

import com.dreamgyf.dim.base.mqtt.MqttTopicHandler;
import com.dreamgyf.dim.base.mqtt.entity.MqttReceiveMessageEntity;
import com.dreamgyf.dim.data.StaticData;

public class MqttMessageCallback {

	public static void onMessageArrived(String topic, String message) {
		MqttTopicHandler.Result topicRes = MqttTopicHandler.analyze(topic);
		MqttReceiveMessageEntity entity = new MqttReceiveMessageEntity();
		entity.setTopicRes(topicRes);
		entity.setMessage(message);
		switch (topicRes.getType()) {
			case MqttTopicHandler.RECEIVE_FRIEND_MESSAGE: {
				try {
					StaticData.receiveFriendMessageQueue.put(entity);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			break;
			case MqttTopicHandler.RECEIVE_GROUP_MESSAGE: {
				try {
					StaticData.receiveGroupMessageQueue.put(entity);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			break;
			case MqttTopicHandler.RECEIVE_FRIEND_ADD: {
				try {
					StaticData.receiveAddFriendRequestQueue.put(entity);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			break;
			case MqttTopicHandler.RECEIVE_GROUP_ADD: {
				try {
					StaticData.receiveAddGroupRequestQueue.put(entity);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			break;
			case MqttTopicHandler.RECEIVE_FRIEND_ACCEPT: {
				try {
					StaticData.receiveAcceptFriendRequestQueue.put(entity);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			break;
			case MqttTopicHandler.RECEIVE_FRIEND_REFUSE: {
				try {
					StaticData.receiveRefuseFriendRequestQueue.put(entity);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			break;
		}
	}
}

package com.dreamgyf.dim.base.mqtt.entity;

import com.dreamgyf.dim.base.mqtt.MqttTopicHandler;

public class MqttReceiveMessageEntity {

	private MqttTopicHandler.Result topicRes;

	private String message;

	public MqttTopicHandler.Result getTopicRes() {
		return topicRes;
	}

	public void setTopicRes(MqttTopicHandler.Result topicRes) {
		this.topicRes = topicRes;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}

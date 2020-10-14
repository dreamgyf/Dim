package com.dreamgyf.dim.base.mqtt;

import com.dreamgyf.dim.MainApplication;
import com.dreamgyf.dim.base.mqtt.callback.MqttConnectCallback;
import com.dreamgyf.dim.base.mqtt.callback.MqttMessageCallback;
import com.dreamgyf.dim.utils.ToastUtils;
import com.dreamgyf.gmqyttf.client.MqttClient;
import com.dreamgyf.gmqyttf.client.callback.MqttClientCallback;
import com.dreamgyf.gmqyttf.client.options.MqttPublishOption;
import com.dreamgyf.gmqyttf.common.enums.MqttVersion;
import com.dreamgyf.gmqyttf.common.params.MqttTopic;
import com.dreamgyf.gmqyttf.common.throwable.exception.MqttException;

public class MqttClientService {

	private final static String SERVER = "mq.tongxinmao.com";
	private final static int PORT = 18831;
	private final static int SUBSCRIBE_QOS = 2;
	private final static int PUBLISH_QOS = 2;

	private static MqttClient mqttClient;

	private static boolean isConnected;

	public static void connect(String clientId, MqttConnectCallback connectCallback) {
		mqttClient = new MqttClient.Builder()
				.clientId(clientId)
				.cleanSession(false)
				.build(MqttVersion.V3_1_1);
		mqttClient.setCallback(new MqttClientCallback() {
			@Override
			public void onConnectSuccess() {
				if (connectCallback != null) {
					connectCallback.onConnectSuccess();
				}
			}

			@Override
			public void onConnectionException(MqttException e) {
				mqttClient = null;
				ToastUtils.sendToast(MainApplication.getInstance().getApplicationContext(), "连接聊天服务器失败");
				//TODO 尝试重连
				isConnected = false;
			}

			@Override
			public void onSubscribeFailure(MqttTopic mqttTopic) {
				//TODO 重试
			}

			@Override
			public void onMessageReceived(String topic, String message) {
				MqttMessageCallback.onMessageArrived(topic, message);
			}
		});
		mqttClient.connect(SERVER, PORT);
		isConnected = true;
	}

	public static void disConnect() {
		if (isConnected) {
			mqttClient.disconnect();
			isConnected = false;
			mqttClient = null;
		}
	}

	public static void subscribe(String... topics) {
		MqttTopic[] mqttTopics = new MqttTopic[topics.length];
		for (int i = 0; i < topics.length; i++) {
			mqttTopics[i] = new MqttTopic(topics[i], SUBSCRIBE_QOS);
		}
		mqttClient.subscribe(mqttTopics);
	}

	public static void publish(String topic, String message) {
		mqttClient.publish(topic, message, new MqttPublishOption().QoS(PUBLISH_QOS));
	}
}

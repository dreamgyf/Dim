package com.dreamgyf.dim.bizpage.addcontacts.model;

import android.os.Handler;
import android.os.Looper;

import com.dreamgyf.dim.base.mqtt.MqttMessageHandler;
import com.dreamgyf.dim.base.mqtt.MqttTopicHandler;
import com.dreamgyf.dim.bizpage.addcontacts.listener.OnSendRequestListener;
import com.dreamgyf.dim.data.StaticData;
import com.dreamgyf.dim.utils.UserUtils;
import com.dreamgyf.exception.MqttException;
import com.dreamgyf.mqtt.client.MqttPublishOptions;

import java.io.IOException;

public class AddContactsModel implements IAddContactsModel {

	private Handler mHandler = new Handler(Looper.getMainLooper());

	private OnSendRequestListener mOnSendRequestListener;

	@Override
	public void setOnSendRequestListener(OnSendRequestListener listener) {
		mOnSendRequestListener = listener;
	}

	@Override
	public void addFriend(int id, String verifyText, String remarkText) {
		String topic = MqttTopicHandler.build(MqttTopicHandler.SEND_FRIEND_ADD, UserUtils.my().getId(), id);
		String message = new MqttMessageHandler.Builder()
				.setType(MqttMessageHandler.Type.REQUEST)
				.setVerifyText(verifyText)
				.setRemarkText(remarkText)
				.build();

		try {
			StaticData.mqttClient.publish(topic, message
					, new MqttPublishOptions().setQoS(2), (t, m) ->
							mHandler.post(() -> {
								if (mOnSendRequestListener != null) {
									mOnSendRequestListener.onSuccess();
								}
							}));
		} catch (IOException | MqttException e) {
			e.printStackTrace();
			mHandler.post(() -> {
				if (mOnSendRequestListener != null) {
					mOnSendRequestListener.onFailure(e);
				}
			});
		}
	}

	@Override
	public void addGroup(int id, String verifyText, String remarkText) {
		String topic = MqttTopicHandler.build(MqttTopicHandler.SEND_GROUP_ADD, UserUtils.my().getId(), id);
		String message = new MqttMessageHandler.Builder()
				.setType(MqttMessageHandler.Type.REQUEST)
				.setVerifyText(verifyText)
				.setRemarkText(remarkText)
				.build();
		try {
			StaticData.mqttClient.publish(topic, message
					, new MqttPublishOptions().setQoS(2), (t, m) ->
							mHandler.post(() -> {
								if (mOnSendRequestListener != null) {
									mOnSendRequestListener.onSuccess();
								}
							}));
		} catch (IOException | MqttException e) {
			e.printStackTrace();
			mHandler.post(() -> {
				if (mOnSendRequestListener != null) {
					mOnSendRequestListener.onFailure(e);
				}
			});
		}
	}
}

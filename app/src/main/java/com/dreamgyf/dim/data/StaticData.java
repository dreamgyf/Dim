package com.dreamgyf.dim.data;

import com.dreamgyf.dim.base.mqtt.entity.MqttReceiveMessageEntity;
import com.dreamgyf.mqtt.client.MqttClient;

import java.util.concurrent.LinkedBlockingQueue;

public class StaticData {

    public static MqttClient mqttClient;

    public static final LinkedBlockingQueue<MqttReceiveMessageEntity> receiveFriendMessageQueue = new LinkedBlockingQueue<>();

    public static final LinkedBlockingQueue<MqttReceiveMessageEntity> receiveGroupMessageQueue = new LinkedBlockingQueue<>();

    public static final LinkedBlockingQueue<MqttReceiveMessageEntity> receiveAddFriendRequestQueue = new LinkedBlockingQueue<>();

    public static final LinkedBlockingQueue<MqttReceiveMessageEntity> receiveAddGroupRequestQueue = new LinkedBlockingQueue<>();

    public final static String DOMAIN = "http://47.100.255.133:8088";

}

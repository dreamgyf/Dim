package com.dreamgyf.dim.data;

import com.dreamgyf.dim.base.mqtt.entity.MqttReceiveMessageEntity;

import java.util.concurrent.LinkedBlockingQueue;

public class StaticData {

    public static final LinkedBlockingQueue<MqttReceiveMessageEntity> receiveFriendMessageQueue = new LinkedBlockingQueue<>();

    public static final LinkedBlockingQueue<MqttReceiveMessageEntity> receiveGroupMessageQueue = new LinkedBlockingQueue<>();

    public static final LinkedBlockingQueue<MqttReceiveMessageEntity> receiveAddFriendRequestQueue = new LinkedBlockingQueue<>();

    public static final LinkedBlockingQueue<MqttReceiveMessageEntity> receiveAddGroupRequestQueue = new LinkedBlockingQueue<>();

    public static final LinkedBlockingQueue<MqttReceiveMessageEntity> receiveAcceptFriendRequestQueue = new LinkedBlockingQueue<>();

    public static final LinkedBlockingQueue<MqttReceiveMessageEntity> receiveRefuseFriendRequestQueue = new LinkedBlockingQueue<>();

    public final static String DOMAIN = "http://47.100.255.133:8088";

}

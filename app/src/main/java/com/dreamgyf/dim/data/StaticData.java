package com.dreamgyf.dim.data;

import com.dreamgyf.dim.base.mqtt.entity.MqttReceiveMessageEntity;
import com.dreamgyf.dim.entity.Group;
import com.dreamgyf.dim.entity.Friend;
import com.dreamgyf.mqtt.client.MqttClient;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class StaticData {

    public static MqttClient mqttClient;

    public static Friend my;

    public static List<Friend> friendList;

    public static List<Group> groupList;

    public static final LinkedBlockingQueue<MqttReceiveMessageEntity> receiveFriendMessageQueue = new LinkedBlockingQueue<>();

    public static final LinkedBlockingQueue<MqttReceiveMessageEntity> receiveGroupMessageQueue = new LinkedBlockingQueue<>();

    public final static String DOMAIN = "http://47.100.255.133:8088";

}

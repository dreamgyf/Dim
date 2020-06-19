package com.dreamgyf.dim.data;

import com.dreamgyf.dim.base.mqtt.entity.MqttReceiveMessageEntity;
import com.dreamgyf.dim.entity.Group;
import com.dreamgyf.dim.entity.Message;
import com.dreamgyf.dim.entity.httpresp.User;
import com.dreamgyf.mqtt.client.MqttClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

public class StaticData {

    public static MqttClient mqttClient;

    public static User my;

    public static List<User> friendList;

    public static List<Group> groupList;

    public static final LinkedBlockingQueue<MqttReceiveMessageEntity> receiveFriendMessageQueue = new LinkedBlockingQueue<>();

    public static final LinkedBlockingQueue<MqttReceiveMessageEntity> receiveGroupMessageQueue = new LinkedBlockingQueue<>();

    public static final Map<Integer,List<Message>> friendMessageMap = new HashMap<>();

    public static final Map<Integer,List<Message>> groupMessageMap = new HashMap<>();

    public final static String DOMAIN = "http://dreamgyf.me:8088";

}

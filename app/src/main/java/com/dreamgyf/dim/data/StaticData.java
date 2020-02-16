package com.dreamgyf.dim.data;

import com.dreamgyf.dim.entity.Conversation;
import com.dreamgyf.dim.entity.Group;
import com.dreamgyf.dim.entity.Message;
import com.dreamgyf.dim.entity.User;
import com.dreamgyf.mqtt.client.MqttClient;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class StaticData {

    public static MqttClient mqttClient;

    public static User my;

    public static List<User> friendList;

    public static List<Group> groupList;

    public static final LinkedList<Conversation> conversationList = new LinkedList<>();

    public static final Map<Integer,List<Message>> friendMessageMap = new HashMap<>();

    public static final Map<Integer,List<Message>> groupMessageMap = new HashMap<>();

    public static final Object conversationListLock = new Object();

    public static final Object friendMessageMapLock = new Object();

    public static final Object groupMessageMapLock = new Object();

    public final static String DOMAIN = "http://www.newbee.cf:8088";
}

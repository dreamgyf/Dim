package com.dreamgyf.dim.data;

import com.dreamgyf.dim.entity.Conversation;
import com.dreamgyf.dim.entity.Data;
import com.dreamgyf.mqtt.client.MqttClient;

import java.util.LinkedList;

public class StaticData {

    public static MqttClient mqttClient;

    public static Integer userId;

    public static Data data;

    public static LinkedList<Conversation> conversationList;

    public static final Object conversationListLock = new Object();

    public final static String DOMAIN = "http://www.newbee.cf:8088";
}

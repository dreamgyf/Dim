package com.dreamgyf.dim.base.mqtt;

/**
 * /Dim/自己的id/message/friend/别人的id      接收到别人的消息
 * /Dim/群组的id/message/group/别人的id      接收到群组里别人发的消息
 * /Dim/自己的id/add/friend/别人的id      接收到别人的好友请求
 * /Dim/群组的id/add/group/别人的id      接收到别人的加入群组请求（自己是群管理的情况下）
 * /Dim/自己的id/accept/friend/别人的id      自己的好友请求被同意
 * /Dim/自己的id/refuse/friend/别人的id      自己的好友请求被拒绝
 */
public class MqttTopicHandler {

    public static final int RECEIVE_FRIEND_MESSAGE = 0;

    public static final int SEND_FRIEND_MESSAGE = 1;

    public static final int RECEIVE_GROUP_MESSAGE = 2;

    public static final int SEND_GROUP_MESSAGE = 3;

    public static final int RECEIVE_FRIEND_ADD = 4;

    public static final int SEND_FRIEND_ADD = 5;

    public static final int RECEIVE_GROUP_ADD = 6;

    public static final int SEND_GROUP_ADD = 7;

    public static final int RECEIVE_FRIEND_ACCEPT = 8;

    public static final int SEND_FRIEND_ACCEPT = 9;

    public static final int RECEIVE_GROUP_ACCEPT = 10;

    public static final int SEND_GROUP_ACCEPT = 11;

    public static final int RECEIVE_FRIEND_REFUSE = 12;

    public static final int SEND_FRIEND_REFUSE = 13;

    public static final int RECEIVE_GROUP_REFUSE = 14;

    public static final int SEND_GROUP_REFUSE = 15;

    public static class Result {

        private int type;

        private int fromId;

        private int toId;

        private Result(int type, int fromId, int toId) {
            this.type = type;
            this.fromId = fromId;
            this.toId = toId;
        }

        public int getType() {
            return type;
        }

        public int getFromId() {
            return fromId;
        }

        public int getToId() {
            return toId;
        }
    }

    public static String build(int type, Integer myId, Integer otherId) {
        switch (type) {
            case SEND_FRIEND_MESSAGE: return "/Dim/" + otherId + "/message/friend/" + myId;
            case SEND_GROUP_MESSAGE: return "/Dim/" + otherId + "/message/group/" + myId;
            case SEND_FRIEND_ADD: return "/Dim/" + otherId + "/add/friend/" + myId;
            case SEND_GROUP_ADD: return "/Dim/" + otherId + "/add/group/" + myId;
            case SEND_FRIEND_ACCEPT: return "/Dim/" + otherId + "/accept/friend/" + myId;
            case SEND_GROUP_ACCEPT: return "/Dim/" + otherId + "/accept/group/" + myId;
            case SEND_FRIEND_REFUSE: return "/Dim/" + otherId + "/refuse/friend/" + myId;
            case SEND_GROUP_REFUSE: return "/Dim/" + otherId + "/refuse/group/" + myId;
            default: throw new IllegalArgumentException("不能构建此类Topic");
        }
    }

    public static Result analyze(String topic) {
        String[] topicParam = topic.split("/");
        int toId = Integer.parseInt(topicParam[2]);
        String messageType = topicParam[3];
        String fromType = topicParam[4];
        int fromId = Integer.parseInt(topicParam[5]);
        switch (messageType) {
            case "message": {
                switch (fromType) {
                    case "friend": return new Result(RECEIVE_FRIEND_MESSAGE,fromId,toId);
                    case "group": return new Result(RECEIVE_GROUP_MESSAGE,fromId,toId);
                }
            }
            case "add": {
                switch (fromType) {
                    case "friend": return new Result(RECEIVE_FRIEND_ADD,fromId,toId);
                    case "group": return new Result(RECEIVE_GROUP_ADD,fromId,toId);
                }
            }
            case "refuse": {
                switch (fromType) {
                    case "friend": return new Result(RECEIVE_FRIEND_ACCEPT,fromId,toId);
                    case "group": return new Result(RECEIVE_GROUP_ACCEPT,fromId,toId);
                }
            }
            case "accept": {
                switch (fromType) {
                    case "friend": return new Result(RECEIVE_FRIEND_REFUSE,fromId,toId);
                    case "group": return new Result(RECEIVE_GROUP_REFUSE,fromId,toId);
                }
            }
            default: return new Result(-1,-1,-1);
        }
    }
}

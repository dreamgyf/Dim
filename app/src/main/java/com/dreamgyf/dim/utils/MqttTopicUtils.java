package com.dreamgyf.dim.utils;

/**
 * /Dim/自己的id/message/friend/别人的id      接收到别人的消息
 * /Dim/群组的id/message/group/别人的id      接收到群组里别人发的消息
 * /Dim/自己的id/add/friend/别人的id      接收到别人的好友请求
 * /Dim/群组的id/add/group/别人的id      接收到别人的加入群组请求（自己是群管理的情况下）
 */
public class MqttTopicUtils {

    public static final int RECEIVE_FRIEND_MESSAGE = 0;

    public static final int SEND_FRIEND_MESSAGE = 1;

    public static final int RECEIVE_GROUP_MESSAGE = 2;

    public static final int SEND_GROUP_MESSAGE = 3;

    public static final int RECEIVE_FRIEND_ADD = 4;

    public static final int SEND_FRIEND_ADD = 5;

    public static final int RECEIVE_GROUP_ADD = 6;

    public static final int SEND_GROUP_ADD = 7;

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

    public static String build(int type, Integer myId, Integer otherId) throws Exception {
        switch (type) {
            case SEND_FRIEND_MESSAGE: return "/Dim/" + otherId + "/message/friend/" + myId;
            case SEND_GROUP_MESSAGE: return "/Dim/" + otherId + "/message/group/" + myId;
            case SEND_FRIEND_ADD: return "/Dim/" + otherId + "/add/friend/" + myId;
            case SEND_GROUP_ADD: return "/Dim/" + otherId + "/add/group/" + myId;
            default: throw new Exception("不能构建此类Topic");
        }
    }

    public static Result analyze(String topic) {
        String[] topicParam = topic.split("/");
        Integer toId = Integer.valueOf(topicParam[2]);
        String messageType = topicParam[3];
        String fromType = topicParam[4];
        Integer fromId = Integer.valueOf(topicParam[5]);
        switch (messageType) {
            case "message":
                switch (fromType) {
                    case "friend": return new Result(RECEIVE_FRIEND_MESSAGE,fromId,toId);
                    case "group": return new Result(RECEIVE_GROUP_MESSAGE,fromId,toId);
                }
            default: return new Result(-1,-1,-1);
        }
    }
}

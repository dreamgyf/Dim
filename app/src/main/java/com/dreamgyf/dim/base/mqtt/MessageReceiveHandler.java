package com.dreamgyf.dim.base.mqtt;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.util.Log;

import com.dreamgyf.dim.MainApplication;
import com.dreamgyf.dim.base.broadcast.BroadcastActions;
import com.dreamgyf.dim.base.mqtt.entity.MqttReceiveMessageEntity;
import com.dreamgyf.dim.data.StaticData;
import com.dreamgyf.dim.entity.Conversation;
import com.dreamgyf.dim.entity.Group;
import com.dreamgyf.dim.entity.Message;
import com.dreamgyf.dim.entity.httpresp.User;
import com.dreamgyf.dim.utils.NameUtils;
import com.dreamgyf.dim.utils.NotificationUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MessageReceiveHandler {

	private volatile static MessageReceiveHandler INSTANCE = null;

	private MainApplication mApplication;

	private NotificationManager mNotificationManager;

	private MessageReceiveHandler(MainApplication application) {
		this.mApplication = application;
		this.mNotificationManager = application.getNotificationManager();
	}

	public static MessageReceiveHandler getInstance() {
		return INSTANCE;
	}

	private ExecutorService mExecutors = Executors.newFixedThreadPool(5);

	private Boolean isRunning = false;

	private Runnable mReceiveFriendMessage = () -> {
		Thread.currentThread().setName("Thread-ReceiveFriendMessage");
		while (isRunning) {
			try {
				MqttReceiveMessageEntity messageEntity = StaticData.receiveFriendMessageQueue.take();
				Log.e("asd123","asd123");
				MqttTopicHandler.Result topicRes = messageEntity.getTopicRes();
				String message = messageEntity.getMessage();
				int friendId = topicRes.getFromId();
				for (User friend : StaticData.friendList) {
					if (friend.getId().equals(friendId)) {
						if (mApplication.isAppBackground()) {
							//通知
							Notification notification = NotificationUtils.build(mApplication, NameUtils.getUsername(friend), message);
							mNotificationManager.notify(friend.getId(), notification);
						}
						//更新会话数据
						Conversation conversation = new Conversation();
						conversation.setUser(friend);
						conversation.setCurrentMessage(message);
						StaticData.addConversation(conversation);
						Intent updateConversation = new Intent();
						updateConversation.setAction(BroadcastActions.UPDATE_CONVERSATION);
						mApplication.sendBroadcast(updateConversation);
						//更新聊天数据
						List<Message> messageList = StaticData.friendMessageMap.get(friendId);
						if (messageList == null)
							messageList = new ArrayList<>();
						Message m = new Message();
						m.setUser(friend);
						m.setType(Message.Type.RECEIVE_TEXT);
						m.setContent(message);
						messageList.add(m);
						StaticData.friendMessageMap.put(friendId, messageList);
						//广播通知更新
						Intent updateMessage = new Intent();
						updateMessage.setAction(BroadcastActions.UPDATE_MESSAGE);
						updateMessage.putExtra("user", friend);
						mApplication.sendBroadcast(updateMessage);
						break;
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	};

	private Runnable mReceiveGroupMessage = () -> {
		Thread.currentThread().setName("Thread-ReceiveGroupMessage");
		while (isRunning) {
			MqttReceiveMessageEntity messageEntity = null;
			try {
				messageEntity = StaticData.receiveGroupMessageQueue.take();
				MqttTopicHandler.Result topicRes = messageEntity.getTopicRes();
				String message = messageEntity.getMessage();
				int userId = topicRes.getFromId();
				int groupId = topicRes.getToId();
				for (Group group : StaticData.groupList) {
					if (group.getId().equals(groupId)) {
						Conversation conversation = new Conversation();
						conversation.setGroup(group);
						try {
							URL url = new URL(StaticData.DOMAIN + "/userinfo");
							HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
							httpURLConnection.setRequestMethod("POST");
							httpURLConnection.setRequestProperty("Content-Result", "application/json; charset=UTF-8");
							Map<String, Object> params = new HashMap<>();
							params.put("myId", StaticData.my.getId());
							params.put("friendId", userId);
							String post = new Gson().toJson(params);
							httpURLConnection.setDoOutput(true);
							httpURLConnection.setDoInput(true);
							OutputStream os = httpURLConnection.getOutputStream();
							os.write(post.getBytes("UTF-8"));
							os.flush();
							os.close();
							if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
								BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
								String resp = "";
								String line;
								while ((line = in.readLine()) != null)
									resp += line;
								in.close();
								JsonObject jsonObject = new JsonParser().parse(resp).getAsJsonObject();
								User user = new Gson().fromJson(jsonObject, User.class);
								conversation.setUser(user);
								conversation.setCurrentMessage(message);
								StaticData.addConversation(conversation);
								Intent updateConversation = new Intent();
								updateConversation.setAction(BroadcastActions.UPDATE_CONVERSATION);
								mApplication.sendBroadcast(updateConversation);
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
						break;
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	};

	public void start() {
		if (!isRunning) {
			isRunning = true;
			mExecutors.execute(mReceiveFriendMessage);
//			mExecutors.execute(mReceiveGroupMessage);
			Log.i("MessageReceiveHandler", "消息接收处理器已启动");
		}
	}

	public void stop() {
		if (isRunning) {
			isRunning = false;
			Log.i("MessageReceiveHandler", "消息接收处理器已停止");
		}
	}
}

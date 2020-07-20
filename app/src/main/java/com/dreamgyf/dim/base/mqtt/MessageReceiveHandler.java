package com.dreamgyf.dim.base.mqtt;

import android.app.Notification;
import android.app.NotificationManager;
import android.util.Log;

import com.dreamgyf.dim.MainApplication;
import com.dreamgyf.dim.api.service.FriendApiService;
import com.dreamgyf.dim.base.http.HttpObserver;
import com.dreamgyf.dim.base.mqtt.entity.MqttReceiveMessageEntity;
import com.dreamgyf.dim.data.StaticData;
import com.dreamgyf.dim.database.entity.GroupMessage;
import com.dreamgyf.dim.database.entity.UserMessage;
import com.dreamgyf.dim.database.entity.UserRequest;
import com.dreamgyf.dim.entity.Conversation;
import com.dreamgyf.dim.entity.Friend;
import com.dreamgyf.dim.entity.Group;
import com.dreamgyf.dim.entity.User;
import com.dreamgyf.dim.enums.ConversationType;
import com.dreamgyf.dim.enums.MessageType;
import com.dreamgyf.dim.enums.UserRequestStatus;
import com.dreamgyf.dim.eventbus.FriendUpdateEvent;
import com.dreamgyf.dim.utils.GroupUtils;
import com.dreamgyf.dim.utils.NameUtils;
import com.dreamgyf.dim.utils.NotificationUtils;
import com.dreamgyf.dim.utils.UserUtils;

import org.greenrobot.eventbus.EventBus;

import java.sql.Timestamp;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MessageReceiveHandler {

	static class NotifyTag {
		final static String FRIEND_CHAT = "friend_chat";
		final static String GROUP_CHAT = "group_chat";
		final static String FRIEND_REQUEST = "friend_request";
		final static String GROUP_REQUEST = "group_request";
	}

	private volatile static MessageReceiveHandler INSTANCE = null;

	private MainApplication mApplication;

	private NotificationManager mNotificationManager;

	private EventBus mEventBus = EventBus.getDefault();

	private MessageReceiveHandler(MainApplication application) {
		this.mApplication = application;
		this.mNotificationManager = application.getNotificationManager();
	}

	public static MessageReceiveHandler getInstance() {
		return INSTANCE;
	}

	private ExecutorService mExecutors = Executors.newFixedThreadPool(10);

	private Boolean isRunning = false;

	private Runnable mReceiveFriendMessage = () -> {
		Thread.currentThread().setName("Thread-ReceiveFriendMessage");
		while (isRunning) {
			try {
				MqttReceiveMessageEntity messageEntity = StaticData.receiveFriendMessageQueue.take();
				MqttTopicHandler.Result topicRes = messageEntity.getTopicRes();
				String message = messageEntity.getMessage();
				int friendId = topicRes.getFromId();
				Friend friend = UserUtils.findFriend(friendId);
				if(friend == null) {
					continue;
				}
				String title = NameUtils.getUsername(friend);
				//后续要对message做处理
				String subtitle = message;
				String content = message;
				if (mApplication.isAppBackground()) {
					Notification notification = NotificationUtils.build(mApplication, title, subtitle);
					mNotificationManager.notify(NotifyTag.FRIEND_CHAT, friendId, notification);
				}
				//更新会话数据
				Conversation conversation = new Conversation();
				conversation.setType(ConversationType.FRIEND_CHAT);
				conversation.setId(friendId);
				conversation.setAvatarId(friend.getAvatarId());
				conversation.setTitle(title);
				conversation.setSubtitle(subtitle);
				mEventBus.post(conversation);
				//更新聊天数据
				UserMessage userMessage = new UserMessage();
				userMessage.myId = UserUtils.my().getId();
				userMessage.userId = friendId;
				userMessage.messageType = MessageType.Analyzer.analyze(true, message);
				userMessage.content = content;
				userMessage.receiveTime = new Timestamp(System.currentTimeMillis());
				mApplication.getDatabase().userMessageDao().insertUserMessage(userMessage);
				mEventBus.post(userMessage);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	};

	private Runnable mReceiveGroupMessage = () -> {
		Thread.currentThread().setName("Thread-ReceiveGroupMessage");
		while (isRunning) {
			try {
				MqttReceiveMessageEntity messageEntity = StaticData.receiveGroupMessageQueue.take();
				MqttTopicHandler.Result topicRes = messageEntity.getTopicRes();
				String message = messageEntity.getMessage();
				int userId = topicRes.getFromId();
				int groupId = topicRes.getToId();
				Group group = GroupUtils.findGroup(groupId);
				if(group == null) {
					continue;
				}
				//后续要对message做处理
				String subtitle = message;
				String content = message;
				if (mApplication.isAppBackground()) {
					Notification notification = NotificationUtils.build(mApplication, group.getName(), subtitle);
					mNotificationManager.notify(NotifyTag.GROUP_CHAT, groupId, notification);
				}
				//更新会话数据
				Conversation conversation = new Conversation();
				conversation.setType(ConversationType.FRIEND_CHAT);
				conversation.setId(groupId);
				conversation.setAvatarId(0);
				conversation.setTitle(group.getName());
				conversation.setSubtitle(subtitle);
				mEventBus.post(conversation);

				GroupMessage groupMessage = new GroupMessage();
				groupMessage.myId = UserUtils.my().getId();
				groupMessage.userId = userId;
				groupMessage.groupId = groupId;
				groupMessage.messageType = MessageType.Analyzer.analyze(true, message);
				groupMessage.content = content;
				groupMessage.receiveTime = new Timestamp(System.currentTimeMillis());
				mApplication.getDatabase().groupMessageDao().insertGroupMessage(groupMessage);
				mEventBus.post(groupMessage);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	};

	private Runnable mReceiveAddFriendRequest = () -> {
		Thread.currentThread().setName("Thread-ReceiveAddFriendRequest");
		while (isRunning) {
			try {
				MqttReceiveMessageEntity messageEntity = StaticData.receiveAddFriendRequestQueue.take();
				MqttTopicHandler.Result topicRes = messageEntity.getTopicRes();
				String message = messageEntity.getMessage();
				int userId = topicRes.getFromId();
				UserUtils.getUser(userId).subscribe(new HttpObserver<User>() {
					@Override
					public void onSuccess(User user) throws Throwable {
						String title = "好友请求";
						String subtitle = NameUtils.getUsername(user) + "想添加你为好友";
						if (mApplication.isAppBackground()) {
							Notification notification = NotificationUtils.build(mApplication, title, subtitle);
							mNotificationManager.notify(NotifyTag.FRIEND_REQUEST, userId, notification);
						}
						//更新会话数据
						Conversation conversation = new Conversation();
						conversation.setType(ConversationType.FRIEND_REQUEST);
						conversation.setId(userId);
						//TODO 设置好友请求头像为默认
						conversation.setAvatarId(user.getAvatarId());
						conversation.setTitle(title);
						conversation.setSubtitle(subtitle);
						mEventBus.post(conversation);

						MqttMessageHandler.Parser parser = MqttMessageHandler.Parser.parse(message);
						UserRequest userRequest = new UserRequest();
						userRequest.receiverId = UserUtils.my().getId();
						userRequest.senderId = userId;
						userRequest.verify = parser.getVerifyText();
						userRequest.remark = parser.getRemarkText();
						userRequest.status = UserRequestStatus.WAIT;
						userRequest.receiveTime = new Timestamp(System.currentTimeMillis());
						mApplication.getDatabase().userRequestDao().insertUserRequest(userRequest);
						mEventBus.post(userRequest);
					}

					@Override
					public void onFailed(Throwable t) throws Throwable {

					}

					@Override
					public void onCaughtThrowable(Throwable t) {

					}
				});
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	};

	private Runnable mReceiveAcceptFriendRequest = () -> {
		Thread.currentThread().setName("Thread-ReceiveAcceptFriendRequest");
		while (isRunning) {
			try {
				MqttReceiveMessageEntity messageEntity = StaticData.receiveAcceptFriendRequestQueue.take();
				MqttTopicHandler.Result topicRes = messageEntity.getTopicRes();
				int userId = topicRes.getFromId();
				UserRequest userRequest = MainApplication.getInstance().getDatabase().userRequestDao().findUserRequestByReceiverId(UserUtils.my().getId(), userId);
				if(userRequest != null && userRequest.status == UserRequestStatus.WAIT) {
					FriendApiService.getInstance().fetchFriendInfo(userId)
							.subscribe(new HttpObserver<Friend>() {
								@Override
								public void onSuccess(Friend friend) throws Throwable {
									userRequest.status = UserRequestStatus.ACCEPT;
									MainApplication.getInstance().getDatabase().userRequestDao().updateUserRequest(userRequest);
									UserUtils.addFriend(friend);
									EventBus.getDefault().post(new FriendUpdateEvent());
								}

								@Override
								public void onFailed(Throwable t) throws Throwable {

								}

								@Override
								public void onCaughtThrowable(Throwable t) {

								}
							});
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
			mExecutors.execute(mReceiveGroupMessage);
			mExecutors.execute(mReceiveAddFriendRequest);
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

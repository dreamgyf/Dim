package com.dreamgyf.dim.bizpage.userrequest.model;

import android.os.Handler;
import android.os.Looper;

import com.dreamgyf.dim.MainApplication;
import com.dreamgyf.dim.api.service.FriendApiService;
import com.dreamgyf.dim.base.http.HttpObserver;
import com.dreamgyf.dim.base.mqtt.MqttTopicHandler;
import com.dreamgyf.dim.data.StaticData;
import com.dreamgyf.dim.database.entity.UserRequest;
import com.dreamgyf.dim.entity.Friend;
import com.dreamgyf.dim.enums.UserRequestStatus;
import com.dreamgyf.dim.eventbus.FriendUpdateEvent;
import com.dreamgyf.dim.utils.UserUtils;
import com.dreamgyf.gmqyttf.client.options.MqttPublishOption;

import org.greenrobot.eventbus.EventBus;

import io.reactivex.rxjava3.schedulers.Schedulers;

public class UserRequestModel implements IUserRequestModel {

	private Handler mMainHandler = new Handler(Looper.getMainLooper());

	private OnUserRequestHandleListener mOnUserRequestHandleListener;

	@Override
	public void setOnUserRequestHandleListener(OnUserRequestHandleListener listener) {
		mOnUserRequestHandleListener = listener;
	}

	@Override
	public void acceptRequest(UserRequest userRequest, String remarkOfUser) {
		FriendApiService.getInstance().addFriend(userRequest.receiverId, userRequest.senderId, userRequest.remark, remarkOfUser)
				.subscribeOn(Schedulers.io())
				.observeOn(Schedulers.io())
				.subscribe(new HttpObserver<Boolean>() {
					@Override
					public void onSuccess(Boolean aBoolean) throws Throwable {
						if(aBoolean) {
							String topic = MqttTopicHandler.build(MqttTopicHandler.SEND_FRIEND_ACCEPT, UserUtils.my().getId(), userRequest.senderId);
							StaticData.mqttClient.publish(topic, "", new MqttPublishOption().QoS(2));
							userRequest.status = UserRequestStatus.ACCEPT;
							MainApplication.getInstance().getDatabase().userRequestDao().updateUserRequest(userRequest);
							FriendApiService.getInstance().fetchFriendInfo(userRequest.senderId)
									.subscribe(new HttpObserver<Friend>() {
										@Override
										public void onSuccess(Friend friend) throws Throwable {
											UserUtils.addFriend(friend);
											EventBus.getDefault().post(new FriendUpdateEvent());
											mMainHandler.post(() -> {
												if(mOnUserRequestHandleListener != null) {
													mOnUserRequestHandleListener.onAcceptSuccess(userRequest.senderId);
												}
											});
										}

										@Override
										public void onFailed(Throwable t) throws Throwable {
											mMainHandler.post(() -> {
												if(mOnUserRequestHandleListener != null) {
													mOnUserRequestHandleListener.onAcceptSuccess(userRequest.senderId);
												}
											});
										}

										@Override
										public void onCaughtThrowable(Throwable t) {
											mMainHandler.post(() -> {
												if(mOnUserRequestHandleListener != null) {
													mOnUserRequestHandleListener.onAcceptSuccess(userRequest.senderId);
												}
											});
										}
									});
						} else {
							onFailed(new Throwable());
						}
					}

					@Override
					public void onFailed(Throwable t) throws Throwable {
						onCaughtThrowable(t);
					}

					@Override
					public void onCaughtThrowable(Throwable t) {
						if(mOnUserRequestHandleListener != null) {
							mOnUserRequestHandleListener.onAcceptFailure(userRequest.senderId);
						}
					}
				});
	}

	@Override
	public void refuseRequest(UserRequest userRequest) {
		String topic = MqttTopicHandler.build(MqttTopicHandler.SEND_FRIEND_REFUSE, UserUtils.my().getId(), userRequest.senderId);
		StaticData.mqttClient.publish(topic, "", new MqttPublishOption().QoS(2));
		userRequest.status = UserRequestStatus.REFUSE;
		MainApplication.getInstance().getDatabase().userRequestDao().updateUserRequest(userRequest);
		mMainHandler.post(() -> {
			if(mOnUserRequestHandleListener != null) {
				mOnUserRequestHandleListener.onRefuseSuccess(userRequest.senderId);
			}
		});
	}
}

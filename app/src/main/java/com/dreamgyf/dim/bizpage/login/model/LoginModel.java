package com.dreamgyf.dim.bizpage.login.model;

import android.os.Handler;
import android.os.Looper;

import com.dreamgyf.dim.base.http.HttpObserver;
import com.dreamgyf.dim.base.mqtt.callback.MqttMessageCallback;
import com.dreamgyf.dim.base.mqtt.exception.MqttConnectException;
import com.dreamgyf.dim.bizpage.login.api.LoginApiService;
import com.dreamgyf.dim.bizpage.login.listener.OnLoginListener;
import com.dreamgyf.dim.data.StaticData;
import com.dreamgyf.dim.entity.httpresp.LoginResp;
import com.dreamgyf.dim.utils.GroupUtils;
import com.dreamgyf.dim.utils.UserUtils;
import com.dreamgyf.gmqyttf.client.MqttClient;
import com.dreamgyf.gmqyttf.client.callback.MqttClientCallback;
import com.dreamgyf.gmqyttf.common.enums.MqttVersion;
import com.dreamgyf.gmqyttf.common.params.MqttTopic;
import com.dreamgyf.gmqyttf.common.throwable.exception.MqttException;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class LoginModel implements ILoginModel {

	private Handler mHandler = new Handler(Looper.getMainLooper());

	private OnLoginListener mOnLoginListener;

	private String mUsername;

	private String mPasswordSha256;

	@Override
	public void login(String username, String passwordSha256) {
		LoginApiService.getInstance().login(username, passwordSha256)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new HttpObserver<LoginResp>() {
					@Override
					public void onSuccess(LoginResp loginResp) throws Throwable {
						mUsername = username;
						mPasswordSha256 = passwordSha256;
						connectMqttBroker(Observable.just(loginResp));
					}

					@Override
					public void onFailed(Throwable t) throws Throwable {
						if (mOnLoginListener != null) {
							mOnLoginListener.onLoginFailed(t);
						}
					}

					@Override
					public void onCaughtThrowable(Throwable t) {
						if (mOnLoginListener != null) {
							mOnLoginListener.onLoginFailed(t);
						}
					}
				});
	}

	private void connectMqttBroker(Observable<LoginResp> observable) {
		observable.subscribeOn(Schedulers.io())
				.observeOn(Schedulers.io())
				.subscribe(new HttpObserver<LoginResp>() {
					@Override
					public void onSuccess(LoginResp loginResp) throws Throwable {
						StaticData.mqttClient = new MqttClient.Builder()
								.clientId("Dim" + loginResp.getMy().getUsername())
								.cleanSession(false)
								.build(MqttVersion.V3_1_1);
						StaticData.mqttClient.setCallback(new MqttClientCallback() {
							@Override
							public void onConnectSuccess() {
								subscribeMqttTopic(Observable.just(loginResp));
							}

							@Override
							public void onConnectionException(MqttException e) {
								StaticData.mqttClient = null;
								if (mOnLoginListener != null) {
									mHandler.post(() -> mOnLoginListener.onLoginFailed(new MqttConnectException("连接聊天服务器失败")));
								}
							}

							@Override
							public void onSubscribeFailure(MqttTopic mqttTopic) {
								StaticData.mqttClient = null;
								if (mOnLoginListener != null) {
									mHandler.post(() -> mOnLoginListener.onLoginFailed(new MqttConnectException("连接聊天服务器失败")));
								}
							}

							@Override
							public void onMessageReceived(String topic, String message) {
								MqttMessageCallback.onMessageArrived(topic, message);
							}
						});
						StaticData.mqttClient.connect("mq.tongxinmao.com", 18831);
					}

					@Override
					public void onFailed(Throwable t) throws Throwable {
						StaticData.mqttClient = null;
						if (mOnLoginListener != null) {
							mHandler.post(() -> mOnLoginListener.onLoginFailed(t));
						}
					}

					@Override
					public void onCaughtThrowable(Throwable t) {
						StaticData.mqttClient = null;
						if (mOnLoginListener != null) {
							mHandler.post(() -> mOnLoginListener.onLoginFailed(t));
						}
					}
				});
	}

	private void subscribeMqttTopic(Observable<LoginResp> observable) {
		observable.subscribeOn(Schedulers.io())
				.observeOn(Schedulers.io())
				.subscribe(new HttpObserver<LoginResp>() {
					@Override
					public void onSuccess(LoginResp loginResp) throws Throwable {
						StaticData.mqttClient.subscribe(new MqttTopic("/Dim/" + loginResp.getMy().getId() + "/#", 2));
						UserUtils.updateMy(loginResp.getMy());
						UserUtils.addFriend(loginResp.getFriendList());
						GroupUtils.addGroup(loginResp.getGroupList());
						if (mOnLoginListener != null) {
							mHandler.post(() -> mOnLoginListener.onLoginSuccess(mUsername,mPasswordSha256));
						}
					}

					@Override
					public void onFailed(Throwable t) throws Throwable {
						StaticData.mqttClient = null;
						if (mOnLoginListener != null) {
							mHandler.post(() -> mOnLoginListener.onLoginFailed(t));
						}
					}

					@Override
					public void onCaughtThrowable(Throwable t) {
						StaticData.mqttClient = null;
						if (mOnLoginListener != null) {
							mHandler.post(() -> mOnLoginListener.onLoginFailed(t));
						}
					}
				});
	}

	public void setOnLoginListener(OnLoginListener onLoginListener) {
		mOnLoginListener = onLoginListener;
	}
}

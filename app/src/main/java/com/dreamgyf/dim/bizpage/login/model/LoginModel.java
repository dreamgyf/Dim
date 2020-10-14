package com.dreamgyf.dim.bizpage.login.model;

import android.os.Handler;
import android.os.Looper;

import com.dreamgyf.dim.base.http.HttpObserver;
import com.dreamgyf.dim.base.mqtt.MqttClientService;
import com.dreamgyf.dim.bizpage.login.api.LoginApiService;
import com.dreamgyf.dim.bizpage.login.listener.OnLoginListener;
import com.dreamgyf.dim.entity.httpresp.LoginResp;
import com.dreamgyf.dim.utils.GroupUtils;
import com.dreamgyf.dim.utils.UserUtils;

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
						MqttClientService.connect("Dim" + loginResp.getMy().getUsername(),
								() -> subscribeMqttTopic(Observable.just(loginResp)));
					}

					@Override
					public void onFailed(Throwable t) throws Throwable {
						MqttClientService.disConnect();
						if (mOnLoginListener != null) {
							mHandler.post(() -> mOnLoginListener.onLoginFailed(t));
						}
					}

					@Override
					public void onCaughtThrowable(Throwable t) {
						MqttClientService.disConnect();
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
						MqttClientService.subscribe("/Dim/" + loginResp.getMy().getId() + "/#");
						UserUtils.updateMy(loginResp.getMy());
						UserUtils.addFriend(loginResp.getFriendList());
						GroupUtils.addGroup(loginResp.getGroupList());
						if (mOnLoginListener != null) {
							mHandler.post(() -> mOnLoginListener.onLoginSuccess(mUsername, mPasswordSha256));
						}
					}

					@Override
					public void onFailed(Throwable t) throws Throwable {
						MqttClientService.disConnect();
						if (mOnLoginListener != null) {
							mHandler.post(() -> mOnLoginListener.onLoginFailed(t));
						}
					}

					@Override
					public void onCaughtThrowable(Throwable t) {
						MqttClientService.disConnect();
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

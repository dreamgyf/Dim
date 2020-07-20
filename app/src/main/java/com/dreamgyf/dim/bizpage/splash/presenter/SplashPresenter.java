package com.dreamgyf.dim.bizpage.splash.presenter;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.dreamgyf.dim.bizpage.main.view.MainActivity;
import com.dreamgyf.dim.base.http.exception.HttpRespException;
import com.dreamgyf.dim.base.http.exception.NetworkConnectException;
import com.dreamgyf.dim.base.mqtt.exception.MqttConnectException;
import com.dreamgyf.dim.base.mvp.presenter.BasePresenter;
import com.dreamgyf.dim.bizpage.login.listener.OnLoginListener;
import com.dreamgyf.dim.bizpage.login.model.LoginModel;
import com.dreamgyf.dim.bizpage.login.view.LoginActivity;
import com.dreamgyf.dim.sharedpreferences.DataAccessUtils;
import com.dreamgyf.dim.bizpage.splash.model.SplashModel;
import com.dreamgyf.dim.bizpage.splash.view.SplashActivity;
import com.dreamgyf.dim.utils.PermissionsUtils;

import java.util.Map;

import retrofit2.HttpException;

public class SplashPresenter extends BasePresenter<SplashModel, SplashActivity> implements ISplashPresenter {

	private Activity mActivity;

	public SplashPresenter(SplashActivity view) {
		super(view);
	}

	@Override
	protected void onAttach() {
		mActivity = getView();
		verifyPermissions();
	}

	@Override
	protected void onDetach() {

	}

	@Override
	protected SplashModel bindModel() {
		return new SplashModel();
	}

	@Override
	public void verifyPermissions() {
		if(!PermissionsUtils.verifyStoragePermissions(mActivity)) {
			tryLogin();
		}
	}

	@Override
	public void tryLogin() {
		Map<String, String> userInfo = DataAccessUtils.getUserAccount(mActivity);
		if (userInfo.get("username") != null) {
			LoginModel loginModel = new LoginModel();
			loginModel.setOnLoginListener(new OnLoginListener() {
				@Override
				public void onLoginSuccess(String username, String passwordSha256) {
					Intent intent = new Intent(mActivity, MainActivity.class);
					mActivity.startActivity(intent);
					mActivity.finish();
				}

				@Override
				public void onLoginFailed(Throwable t) {
					Toast toast = Toast.makeText(mActivity, null, Toast.LENGTH_SHORT);
					if (t instanceof NetworkConnectException || t instanceof HttpRespException || t instanceof HttpException || t instanceof MqttConnectException) {
						toast.setText(t.getMessage());
					} else {
						toast.setText("未知错误");
					}
					toast.show();
					Intent intent = new Intent(mActivity, LoginActivity.class);
					mActivity.startActivity(intent);
					mActivity.finish();
				}
			});
			loginModel.login(userInfo.get("username"), userInfo.get("password"));
		} else {
			Intent intent = new Intent(mActivity, LoginActivity.class);
			mActivity.startActivity(intent);
			mActivity.finish();
		}
	}
}

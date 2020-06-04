package com.dreamgyf.dim.login.presenter;

import android.os.Handler;
import android.os.Looper;

import com.dreamgyf.dim.base.mvp.presenter.BasePresenter;
import com.dreamgyf.dim.login.view.LoginActivity;
import com.dreamgyf.dim.login.listener.OnLoginListener;
import com.dreamgyf.dim.login.model.LoginModel;
import com.dreamgyf.dim.sharedpreferences.DataAccessUtils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginPresenter extends BasePresenter<LoginModel, LoginActivity> implements ILoginPresenter {

	Handler mHandler = new Handler(Looper.getMainLooper());

	private LoginModel mLoginModel;

	private LoginActivity mLoginActivity;

	private OnLoginListener mOnLoginListener;

	public LoginPresenter(LoginActivity loginActivity) {
		super(loginActivity);
	}

	@Override
	protected void onAttach() {
		mLoginModel = getModel();
		mLoginActivity = getView();
	}

	@Override
	protected void onDetach() {

	}

	@Override
	protected LoginModel bindModel() {
		return new LoginModel();
	}

	@Override
	public void login(String username, String password) {
		try {
			MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
			sha256.update(password.getBytes());
			String passwordSha256 = new BigInteger(1, sha256.digest()).toString(16);
			mLoginModel.login(username, passwordSha256);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			if(mOnLoginListener != null) {
				mOnLoginListener.onLoginFailed(e);
			}
		}
	}

	public void setOnLoginListener(OnLoginListener onLoginListener) {
		mOnLoginListener = new OnLoginListener() {
			@Override
			public void onLoginSuccess(String username, String passwordSha256) {
				DataAccessUtils.setUserAccount(mLoginActivity,username,passwordSha256);
				onLoginListener.onLoginSuccess(username,passwordSha256);
			}

			@Override
			public void onLoginFailed(Throwable t) {
				onLoginListener.onLoginFailed(t);
			}
		};
		mLoginModel.setOnLoginListener(mOnLoginListener);
	}
}

package com.dreamgyf.dim.bizpage.splash.view;

import android.os.Bundle;

import androidx.annotation.NonNull;

import com.dreamgyf.dim.R;
import com.dreamgyf.dim.base.mvp.activity.BaseActivity;
import com.dreamgyf.dim.bizpage.splash.model.SplashModel;
import com.dreamgyf.dim.bizpage.splash.presenter.SplashPresenter;

public class SplashActivity extends BaseActivity<SplashModel,SplashActivity, SplashPresenter> implements ISplashView {

	private SplashPresenter mPresenter;

	@NonNull
	@Override
	public SplashPresenter bindPresenter() {
		return new SplashPresenter(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		mPresenter = getPresenter();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		mPresenter.tryLogin();
	}
}

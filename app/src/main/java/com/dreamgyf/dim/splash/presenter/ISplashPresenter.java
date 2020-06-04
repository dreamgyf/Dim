package com.dreamgyf.dim.splash.presenter;

import com.dreamgyf.dim.base.mvp.presenter.IBasePresenter;
import com.dreamgyf.dim.splash.view.SplashActivity;
import com.dreamgyf.dim.splash.model.SplashModel;

public interface ISplashPresenter extends IBasePresenter<SplashModel, SplashActivity> {

	void verifyPermissions();

	void tryLogin();
}

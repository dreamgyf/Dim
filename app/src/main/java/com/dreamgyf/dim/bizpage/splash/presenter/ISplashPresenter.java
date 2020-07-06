package com.dreamgyf.dim.bizpage.splash.presenter;

import com.dreamgyf.dim.base.mvp.presenter.IBasePresenter;
import com.dreamgyf.dim.bizpage.splash.view.SplashActivity;
import com.dreamgyf.dim.bizpage.splash.model.SplashModel;

public interface ISplashPresenter extends IBasePresenter<SplashModel, SplashActivity> {

	void verifyPermissions();

	void tryLogin();
}

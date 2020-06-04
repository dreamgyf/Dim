package com.dreamgyf.dim.login.presenter;

import com.dreamgyf.dim.base.mvp.presenter.IBasePresenter;
import com.dreamgyf.dim.login.view.LoginActivity;
import com.dreamgyf.dim.login.model.LoginModel;

public interface ILoginPresenter extends IBasePresenter<LoginModel, LoginActivity> {
	void login(String username, String password);
}

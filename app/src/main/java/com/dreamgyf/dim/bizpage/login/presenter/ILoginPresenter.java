package com.dreamgyf.dim.bizpage.login.presenter;

import com.dreamgyf.dim.base.mvp.presenter.IBasePresenter;
import com.dreamgyf.dim.bizpage.login.view.LoginActivity;
import com.dreamgyf.dim.bizpage.login.model.LoginModel;

public interface ILoginPresenter extends IBasePresenter<LoginModel, LoginActivity> {
	void login(String username, String password);
}

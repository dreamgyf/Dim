package com.dreamgyf.dim.bizpage.login.model;

import com.dreamgyf.dim.base.mvp.model.IBaseModel;

public interface ILoginModel extends IBaseModel {
	void login(String username, String passwordSha256);
}

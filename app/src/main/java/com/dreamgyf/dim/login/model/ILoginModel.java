package com.dreamgyf.dim.login.model;

import com.dreamgyf.dim.base.mvp.model.IBaseModel;

public interface ILoginModel extends IBaseModel {
	void login(String username, String passwordSha256);
}

package com.dreamgyf.dim.bizpage.login.listener;

public interface OnLoginListener {
	void onLoginSuccess(String username, String passwordSha256);

	void onLoginFailed(Throwable t);
}

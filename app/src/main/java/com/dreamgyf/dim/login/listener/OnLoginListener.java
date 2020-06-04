package com.dreamgyf.dim.login.listener;

public interface OnLoginListener {
	void onLoginSuccess(String username, String passwordSha256);

	void onLoginFailed(Throwable t);
}

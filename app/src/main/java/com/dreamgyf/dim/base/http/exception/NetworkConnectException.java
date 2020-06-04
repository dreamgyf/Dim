package com.dreamgyf.dim.base.http.exception;

import androidx.annotation.Nullable;

public class NetworkConnectException extends Throwable {
	public NetworkConnectException() {
		super();
	}

	public NetworkConnectException(@Nullable String message) {
		super(message);
	}

	public NetworkConnectException(@Nullable String message, @Nullable Throwable cause) {
		super(message, cause);
	}

	public NetworkConnectException(@Nullable Throwable cause) {
		super(cause);
	}

	protected NetworkConnectException(@Nullable String message, @Nullable Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}

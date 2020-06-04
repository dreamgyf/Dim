package com.dreamgyf.dim.base.mqtt.exception;

import androidx.annotation.Nullable;

public class MqttConnectException extends Throwable {
	public MqttConnectException() {
		super();
	}

	public MqttConnectException(@Nullable String message) {
		super(message);
	}

	public MqttConnectException(@Nullable String message, @Nullable Throwable cause) {
		super(message, cause);
	}

	public MqttConnectException(@Nullable Throwable cause) {
		super(cause);
	}

	protected MqttConnectException(@Nullable String message, @Nullable Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}

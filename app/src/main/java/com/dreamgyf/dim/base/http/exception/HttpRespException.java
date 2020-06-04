package com.dreamgyf.dim.base.http.exception;

import java.io.IOException;

public class HttpRespException extends IOException {
	public HttpRespException() {
		super();
	}

	public HttpRespException(String message) {
		super(message);
	}

	public HttpRespException(String message, Throwable cause) {
		super(message, cause);
	}

	public HttpRespException(Throwable cause) {
		super(cause);
	}
}

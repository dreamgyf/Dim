package com.dreamgyf.dim.base.http;

import com.dreamgyf.dim.base.http.exception.HttpRespException;
import com.dreamgyf.dim.base.http.exception.NetworkConnectException;

import java.net.ConnectException;
import java.net.UnknownHostException;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import retrofit2.HttpException;

public abstract class HttpObserver<T> implements Observer<T> {
	@Override
	public void onSubscribe(@NonNull Disposable d) {

	}

	@Override
	public void onNext(@NonNull T t) {
		try {
			onSuccess(t);
		} catch (Throwable throwable) {
			onCaughtThrowable(throwable);
		}
	}

	@Override
	public void onError(@NonNull Throwable e) {
		try {
			//网络连接错误
			if (e instanceof ConnectException || e instanceof UnknownHostException) {
				onFailed(new NetworkConnectException("网络连接错误"));
			} else if (e instanceof HttpRespException) {
				onFailed(e);
			} else if (e instanceof HttpException) {
				int code = ((HttpException) e).code();
				String message = null;
				switch (code) {
					case 400:
						message = "错误请求";
						break;
					case 401:
						message = "未授权";
						break;
					case 403:
						message = "禁止访问";
						break;
					case 404:
						message = "未找到";
						break;
					case 405:
						message = "方法未允许";
						break;
					case 406:
						message = "无法访问";
						break;
					case 407:
						message = "代理服务器认证要求";
						break;
					case 408:
						message = "请求超时";
						break;
					case 409:
						message = "冲突";
						break;
					case 410:
						message = "已经不存在";
						break;
					case 411:
						message = "需要数据长度";
						break;
					case 412:
						message = "先决条件错误";
						break;
					case 413:
						message = "请求实体过大";
						break;
					case 414:
						message = "请求URI过长";
						break;
					case 415:
						message = "不支持的媒体格式";
						break;
					case 416:
						message = "请求范围无法满足";
						break;
					case 417:
						message = "期望失败";
						break;
					case 418:
						message = "服务器拒绝服务";
						break;
					case 421:
						message = "连接过多";
						break;
					case 422:
						message = "语义错误，无法响应";
						break;
					case 423:
						message = "当前资源被锁定";
						break;
					case 424:
						message = "当前请求失败";
						break;
					case 425:
						message = "无需集合";
						break;
					case 426:
						message = "客户端应当切换到TLS/1.0";
						break;
					case 428:
						message = "发送条件请求";
						break;
					case 429:
						message = "请求过多";
						break;
					case 431:
						message = "请求头太大";
						break;
					case 449:
						message = "需要重试";
						break;
					case 451:
						message = "因法律原因不可用";
						break;
				}
				if (message == null && code >= 500 && code < 600) {
					message = "服务器错误";
				}
				if (message == null) {
					message = "未知错误";
				}
				onFailed(new Throwable(message));
			} else {
				onFailed(new Throwable("未知错误"));
			}
		} catch (Throwable throwable) {
			onCaughtThrowable(throwable);
		}
	}

	@Override
	public void onComplete() {

	}

	public abstract void onSuccess(T t) throws Throwable;

	public abstract void onFailed(Throwable t) throws Throwable;

	public abstract void onCaughtThrowable(Throwable t);
}

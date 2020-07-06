package com.dreamgyf.dim.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {

	/**
	 * 部分小米手机需要现创建Toast再设置文字，否则通知会连包名一起显示
	 */
	public static void sendToast(Context context, String message) {
		Toast toast = Toast.makeText(context,"",Toast.LENGTH_SHORT);
		toast.setText(message);
		toast.show();
	}
}

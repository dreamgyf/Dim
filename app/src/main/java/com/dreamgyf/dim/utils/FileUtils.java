package com.dreamgyf.dim.utils;

import android.content.Context;
import android.os.Environment;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {

	public static String getPath(Context context) {
		String path = "";
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			//判断sd卡是否存在，不存在，存储在应用内部
			path = Environment.getExternalStorageDirectory() + "/Android/data/com.dreamgyf.dim";
		} else {
			path = context.getFilesDir() + "";
		}
		return path;
	}

	public static void write(InputStream is, OutputStream os) throws IOException {
		byte[] buffer = new byte[1024];
		int len;
		while((len = is.read(buffer)) != -1) {
			os.write(buffer,0,len);
		}
		is.close();
		os.close();
	}

}

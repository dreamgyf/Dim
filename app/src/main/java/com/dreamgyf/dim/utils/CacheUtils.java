package com.dreamgyf.dim.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CacheUtils {

    public static File cacheAvatar(Context context,Integer id,InputStream in) throws IOException {
        String path = "";
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            //判断sd卡是否存在，不存在，存储在应用内部
            path = Environment.getExternalStorageDirectory() + "/Android/data/com.dreamgyf.dim/";
        } else {
            path = context.getFilesDir() + "/";
        }
        File avatarDir = new File(path + "cache/avatar");
        if (!avatarDir.exists()) {
            avatarDir.mkdirs();
        }
        File avatar = new File(avatarDir.getAbsolutePath() + "/" + id + ".jpg");
        OutputStream out = new FileOutputStream(avatar);
        byte[] buffer = new byte[1024];
        int len;
        while((len = in.read(buffer)) != -1) {
            out.write(buffer,0,len);
        }
        in.close();
        out.close();
        return avatar;
    }

    public static File loadAvatar(Context context,Integer id) {
        String path = "";
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            //判断sd卡是否存在，不存在，存储在应用内部
            path = Environment.getExternalStorageDirectory() + "/Android/data/com.dreamgyf.dim/";
        } else {
            path = context.getFilesDir() + "/";
        }
        File avatarDir = new File(path + "cache/avatar");
        if (!avatarDir.exists()) {
            avatarDir.mkdirs();
        }
        File avatar = new File(avatarDir.getAbsolutePath() + "/" + id + ".jpg");
        return avatar.exists() ? avatar : null;
    }
}

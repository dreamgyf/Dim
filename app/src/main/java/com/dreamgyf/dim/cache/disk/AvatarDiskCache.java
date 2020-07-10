package com.dreamgyf.dim.cache.disk;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.dreamgyf.dim.utils.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class AvatarDiskCache {

	public static Bitmap getAvatar(Context context, int id) throws FileNotFoundException {
		File avatarDir = new File(FileUtils.getPath(context) + "/cache/avatar");
		if (!avatarDir.exists()) {
			avatarDir.mkdirs();
		}
		File avatar = new File(avatarDir.getAbsolutePath() + "/" + id + ".jpg");
		if(!avatar.exists()) {
			throw new FileNotFoundException("not found id:" + id + " avatar in disk");
		}
		return BitmapFactory.decodeFile(avatar.getPath());
	}

	public static Bitmap saveAvatar(Context context, int id, InputStream is) throws IOException {
		File avatarDir = new File(FileUtils.getPath(context) + "/cache/avatar");
		if (!avatarDir.exists()) {
			avatarDir.mkdirs();
		}
		File avatarFile = new File(avatarDir.getAbsolutePath() + "/" + id + ".jpg");
		FileUtils.write(is, new FileOutputStream(avatarFile));
		return BitmapFactory.decodeFile(avatarFile.getPath());
	}
}

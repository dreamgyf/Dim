package com.dreamgyf.dim.cache.memory;

import android.graphics.Bitmap;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

public class AvatarMemoryCache {

	private final static Map<Integer, Bitmap> bitmapCache = new HashMap<>();

	public static Bitmap getAvatar(int id) throws FileNotFoundException {
		synchronized (bitmapCache) {
			Bitmap bitmap = bitmapCache.get(id);
			if (bitmap == null) {
				throw new FileNotFoundException("not found id:" + id + " avatar in memory");
			}
			return bitmap;
		}
	}

	public static void saveAvatar(int id, Bitmap bitmap) {
		synchronized (bitmapCache) {
			bitmapCache.put(id, bitmap);
		}
	}
}

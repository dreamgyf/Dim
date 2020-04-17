package com.dreamgyf.dim.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class BitmapUtils {

    public static Bitmap file2Bitmap(File file) throws FileNotFoundException {
        FileInputStream fis = new FileInputStream(file);
        return BitmapFactory.decodeStream(fis);
    }
}

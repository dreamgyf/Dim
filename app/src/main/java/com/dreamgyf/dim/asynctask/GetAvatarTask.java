package com.dreamgyf.dim.asynctask;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.dreamgyf.dim.data.StaticData;
import com.dreamgyf.dim.utils.CacheUtils;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetAvatarTask extends AsyncTask<Integer, Void, Bitmap> {

    private Context context;

    private ImageView avatar;

    public GetAvatarTask(Context context, ImageView avatar) {
        super();
        this.context = context;
        this.avatar = avatar;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if(bitmap != null)
            avatar.setImageBitmap(bitmap);
    }

    @Override
    protected Bitmap doInBackground(Integer... integers) {
        try {
            File avatarFile = null;
            avatarFile = CacheUtils.loadAvatar(context,integers[0]);
            if(avatarFile != null) {
                return BitmapFactory.decodeFile(avatarFile.getPath());
            }
            else {
                URL url = new URL(StaticData.DOMAIN + "/image/avatar/get/" + integers[0]);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                if(httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStream in = httpURLConnection.getInputStream();
                    avatarFile = CacheUtils.cacheAvatar(context,integers[0],in);
                    return BitmapFactory.decodeFile(avatarFile.getPath());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }
}

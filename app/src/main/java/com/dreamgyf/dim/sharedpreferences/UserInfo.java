package com.dreamgyf.dim.sharedpreferences;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class UserInfo {

    public static void setUserInfo(Context context,String username, String password) {
        SharedPreferences userInfo = context.getSharedPreferences("user",MODE_PRIVATE);
        SharedPreferences.Editor editor = userInfo.edit();
        editor.putString("username",username);
        editor.putString("password",password);
        editor.apply();
    }

    public static Map<String,String> getUserInfo(Context context) {
        SharedPreferences userInfo = context.getSharedPreferences("user",MODE_PRIVATE);
        Map<String,String> res = new HashMap<>();
        res.put("username",userInfo.getString("username",null));
        res.put("password",userInfo.getString("password",null));
        return res;
    }
}

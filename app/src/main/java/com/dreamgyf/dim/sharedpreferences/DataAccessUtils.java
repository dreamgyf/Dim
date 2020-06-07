package com.dreamgyf.dim.sharedpreferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.dreamgyf.dim.entity.Conversation;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class DataAccessUtils {

    public static void setUserAccount(Context context, String username, String password) {
        SharedPreferences userInfo = context.getSharedPreferences("user_account",MODE_PRIVATE);
        SharedPreferences.Editor editor = userInfo.edit();
        editor.putString("username",username);
        editor.putString("password",password);
        editor.apply();
    }

    public static Map<String,String> getUserAccount(Context context) {
        SharedPreferences userInfo = context.getSharedPreferences("user_account",MODE_PRIVATE);
        Map<String,String> res = new HashMap<>();
        res.put("username",userInfo.getString("username",null));
        res.put("password",userInfo.getString("password",null));
        return res;
    }

    public static LinkedList<Conversation> getConversationList(Context context) {
        SharedPreferences data = context.getSharedPreferences("data",MODE_PRIVATE);
        String json = data.getString("conversation",null);
        if(json == null) {
            return new LinkedList<>();
        }
        Gson gson = new Gson();
        return gson.fromJson(json,new TypeToken<LinkedList<Conversation>>(){}.getType());
    }

    public static void saveConversationList(Context context,LinkedList<Conversation> conversationList) {
        SharedPreferences data = context.getSharedPreferences("data",MODE_PRIVATE);
        SharedPreferences.Editor editor = data.edit();
        Gson gson = new Gson();
        editor.putString("conversation",gson.toJson(conversationList));
        editor.apply();
    }
}

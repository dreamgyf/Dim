package com.dreamgyf.dim;

import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dreamgyf.dim.broadcast.BroadcastActions;
import com.dreamgyf.dim.data.StaticData;
import com.dreamgyf.dim.entity.Conversation;
import com.dreamgyf.dim.entity.Group;
import com.dreamgyf.dim.entity.Message;
import com.dreamgyf.dim.entity.User;
import com.dreamgyf.dim.sharedpreferences.UserInfo;
import com.dreamgyf.dim.utils.MqttTopicAnalyzer;
import com.dreamgyf.dim.utils.PermissionsUtils;
import com.dreamgyf.exception.MqttException;
import com.dreamgyf.mqtt.MqttVersion;
import com.dreamgyf.mqtt.client.MqttClient;
import com.dreamgyf.mqtt.client.MqttTopic;
import com.dreamgyf.mqtt.client.callback.MqttConnectCallback;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private Handler handler = new Handler();

    private RelativeLayout loginButton;

    private EditText usernameText;

    private EditText passwordText;

    private ImageView staticLogin;

    private ImageView animLogin;

    private Animatable loadingAnim;

    private boolean isLogining = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initEditText();
        initLoginButton();

        initData();

        PermissionsUtils.verifyStoragePermissions(this);
    }

    private void initData() {
        Map<String,String> userInfo = UserInfo.getUserInfo(this);
        if(userInfo.get("username") != null) {
            login(userInfo.get("username"),userInfo.get("password"));
        }
    }

    private void initEditText() {
        usernameText = findViewById(R.id.username);
        passwordText = findViewById(R.id.password);
        passwordText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_GO) {
                    try {
                        String username = usernameText.getText().toString();
                        String password = passwordText.getText().toString();
                        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
                        sha256.update(password.getBytes());
                        String passwordSha256 = new BigInteger(1, sha256.digest()).toString(16);
                        login(username,passwordSha256);
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }
        });
    }

    private void initLoginButton() {
        loginButton = findViewById(R.id.login);
        staticLogin = findViewById(R.id.staticLogin);
        animLogin = findViewById(R.id.animLogin);
        loadingAnim = (Animatable) animLogin.getDrawable();
        //点击登录
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String username = usernameText.getText().toString();
                    String password = passwordText.getText().toString();
                    MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
                    sha256.update(password.getBytes());
                    String passwordSha256 = new BigInteger(1, sha256.digest()).toString(16);
                    login(username,passwordSha256);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void login(final String username, final String passwordSha256) {
        staticLogin.setVisibility(View.INVISIBLE);
        animLogin.setVisibility(View.VISIBLE);
        loadingAnim.start();
        if(!isLogining) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    isLogining = true;
                    //查询数据库,获取用户信息
                    try {
                        URL url = new URL(StaticData.DOMAIN + "/signin");
                        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                        httpURLConnection.setRequestMethod("POST");
                        httpURLConnection.setRequestProperty("Content-Type","application/json; charset=UTF-8");
                        Map<String,String> params = new HashMap<>();
                        params.put("username",username);
                        params.put("password", passwordSha256);
                        String post = new Gson().toJson(params);
                        httpURLConnection.setDoOutput(true);
                        httpURLConnection.setDoInput(true);
                        OutputStream os = httpURLConnection.getOutputStream();
                        os.write(post.getBytes("UTF-8"));
                        os.flush();
                        os.close();
                        if(httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                            BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                            String resp = "";
                            String line;
                            while((line = in.readLine()) != null)
                                resp += line;
                            in.close();
                            final JsonObject jsonObject = new JsonParser().parse(resp).getAsJsonObject();
                            if("0".equals(jsonObject.get("code").getAsString())) {
                                Gson gson = new Gson();
                                //保存登录信息
                                UserInfo.setUserInfo(LoginActivity.this,username,passwordSha256);
                                //保存信息
                                JsonObject data = jsonObject.get("data").getAsJsonObject();
                                StaticData.my = gson.fromJson(data.get("my"), User.class);
                                StaticData.friendList = gson.fromJson(data.get("friendList"), new TypeToken<List<User>>(){}.getType());
                                Collections.sort(StaticData.friendList);
                                StaticData.groupList = gson.fromJson(data.get("groupList"), new TypeToken<List<Group>>(){}.getType());
                                try {
                                    StaticData.mqttClient = new MqttClient.Builder().setVersion(MqttVersion.V_3_1_1).setClientId("Dim" + StaticData.my.getUsername()).setCleanSession(false).setBroker("mq.tongxinmao.com").setPort(18831).build();
                                    StaticData.mqttClient.setCallback((topic, message) -> {
                                        Log.e("Login Message",message);
                                        MqttTopicAnalyzer.Result topicRes = MqttTopicAnalyzer.analyze(topic);
                                        switch (topicRes.getType()) {
                                            case MqttTopicAnalyzer.RECEIVE_FRIEND_MESSAGE: {
                                                for(User friend : StaticData.friendList) {
                                                    if(friend.getId().equals(topicRes.getFromId())) {
                                                        Conversation conversation = new Conversation();
                                                        //更新会话数据
                                                        conversation.setUser(friend);
                                                        conversation.setCurrentMessage(message);
                                                        StaticData.addConversation(conversation);
                                                        Intent updateConversation = new Intent();
                                                        updateConversation.setAction(BroadcastActions.UPDATE_CONVERSATION);
                                                        sendBroadcast(updateConversation);
                                                        //更新聊天数据
                                                        List<Message> messageList = StaticData.friendMessageMap.get(topicRes.getFromId());
                                                        if(messageList == null)
                                                            messageList = new ArrayList<>();
                                                        Message m = new Message();
                                                        m.setUser(friend);
                                                        m.setType(Message.Type.RECEIVE_TEXT);
                                                        m.setContent(message);
                                                        messageList.add(m);
                                                        StaticData.friendMessageMap.put(topicRes.getFromId(),messageList);
                                                        break;
                                                    }
                                                }
                                                break;
                                            }
                                        }
                                    });
                                    StaticData.mqttClient.connect(new MqttConnectCallback() {
                                        @Override
                                        public void onSuccess() {
                                            try {
                                                StaticData.mqttClient.subscribe(new MqttTopic("/Dim/" + StaticData.my.getId() + "/#").setQoS(2));
                                            } catch (MqttException e) {
                                                e.printStackTrace();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                            handler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    staticLogin.setVisibility(View.VISIBLE);
                                                    animLogin.setVisibility(View.INVISIBLE);
                                                    loadingAnim.stop();
                                                }
                                            });
                                            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }

                                        @Override
                                        public void onFailure() {
                                            handler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    staticLogin.setVisibility(View.VISIBLE);
                                                    animLogin.setVisibility(View.INVISIBLE);
                                                    loadingAnim.stop();
                                                }
                                            });
                                            Toast errorInfo = Toast.makeText(LoginActivity.this,null,Toast.LENGTH_SHORT);
                                            errorInfo.setText("连接聊天服务器失败");
                                            errorInfo.show();
                                        }
                                    });
                                } catch (IOException | MqttException e) {
                                    e.printStackTrace();
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            staticLogin.setVisibility(View.VISIBLE);
                                            animLogin.setVisibility(View.INVISIBLE);
                                            loadingAnim.stop();
                                            Toast errorInfo = Toast.makeText(LoginActivity.this,null,Toast.LENGTH_SHORT);
                                            errorInfo.setText("连接聊天服务器失败");
                                            errorInfo.show();
                                        }
                                    });
                                }
                            }
                            else {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        staticLogin.setVisibility(View.VISIBLE);
                                        animLogin.setVisibility(View.INVISIBLE);
                                        loadingAnim.stop();
                                        Toast errorInfo = Toast.makeText(LoginActivity.this,null,Toast.LENGTH_SHORT);
                                        errorInfo.setText(jsonObject.get("msg").getAsString());
                                        errorInfo.show();
                                    }
                                });
                            }
                        }
                        else {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    staticLogin.setVisibility(View.VISIBLE);
                                    animLogin.setVisibility(View.INVISIBLE);
                                    loadingAnim.stop();
                                    Toast errorInfo = Toast.makeText(LoginActivity.this,null,Toast.LENGTH_SHORT);
                                    errorInfo.setText("网络连接错误");
                                    errorInfo.show();
                                }
                            });
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast errorInfo = Toast.makeText(LoginActivity.this,null,Toast.LENGTH_SHORT);
                                errorInfo.setText("网络连接错误");
                                errorInfo.show();
                            }
                        });
                    } finally {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                staticLogin.setVisibility(View.VISIBLE);
                                animLogin.setVisibility(View.INVISIBLE);
                                loadingAnim.stop();
                            }
                        });
                        isLogining = false;
                    }

                }
            }).start();
        }
    }
}

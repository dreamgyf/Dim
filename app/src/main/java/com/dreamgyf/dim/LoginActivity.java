package com.dreamgyf.dim;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.dreamgyf.dim.data.StaticData;
import com.dreamgyf.exception.MqttException;
import com.dreamgyf.mqtt.MqttVersion;
import com.dreamgyf.mqtt.client.MqttClient;
import com.dreamgyf.mqtt.client.callback.MqttConnectCallback;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private Handler handler = new Handler();

    private LinearLayout loginButton;

    private EditText usernameText;

    private EditText passwordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initEditText();
        initLoginButton();

//        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//        startActivity(intent);
//        finish();
    }

    private void initEditText() {
        usernameText = findViewById(R.id.username);
        passwordText = findViewById(R.id.password);
    }

    private void initLoginButton() {
        loginButton = findViewById(R.id.login);
        //点击登录
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取输入用户名密码
                final String username = usernameText.getText().toString();
                final String password = passwordText.getText().toString();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //查询数据库,获取用户信息
                        try {
                            URL url = new URL(StaticData.DOMAIN + "/signIn");
                            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                            httpURLConnection.setRequestMethod("POST");
                            Map<String,String> params = new HashMap<>();
                            params.put("username",username);
                            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
                            sha256.update(password.getBytes());
                            params.put("password", new BigInteger(1, sha256.digest()).toString(16));
                            String post = new Gson().toJson(params);
                            httpURLConnection.setDoOutput(true);
                            DataOutputStream dos = new DataOutputStream(httpURLConnection.getOutputStream());
                            dos.writeBytes(post);
                            dos.flush();
                            dos.close();
                            BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                            String resp = "";
                            String line;
                            while((line = in.readLine()) != null)
                                resp += line;
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        }
                        try {
                            StaticData.mqttClient = new MqttClient.Builder().setVersion(MqttVersion.V_3_1_1).setClientId("Dim497163175").setBroker("mq.tongxinmao.com").setPort(18831).build();
                            StaticData.mqttClient.connect(new MqttConnectCallback() {
                                @Override
                                public void onSuccess() {
                                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }

                                @Override
                                public void onFailure() {

                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (MqttException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

            }
        });
    }
}

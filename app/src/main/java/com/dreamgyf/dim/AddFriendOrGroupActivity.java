package com.dreamgyf.dim;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.dreamgyf.dim.data.StaticData;
import com.dreamgyf.dim.entity.Group;
import com.dreamgyf.dim.entity.httpresp.User;
import com.dreamgyf.dim.base.mqtt.MqttTopicHandler;
import com.dreamgyf.mqtt.client.MqttPublishOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AddFriendOrGroupActivity extends AppCompatActivity {

    private Handler handler = new Handler();

    private Intent intent;

    private User user;

    private Group group;

    private EditText verifyText;

    private EditText remarkText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend_or_group);

        intent = getIntent();
        user = (User) intent.getSerializableExtra("user");
        group = (Group) intent.getSerializableExtra("group");

        initToolbar();

        verifyText = findViewById(R.id.verify_text);
        remarkText = findViewById(R.id.remark_text);

        if(user != null) {
            findViewById(R.id.remark_view).setVisibility(View.VISIBLE);
        }
        else {
            findViewById(R.id.remark_view).setVisibility(View.INVISIBLE);
        }
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_friend_or_group,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: finish(); break;
            case R.id.send: {
                AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
                    @Override
                    protected void onPostExecute(Boolean isFriend) {
                        if(!isFriend) {
                            String topic;
                            String message;
                            try {
                                if(user != null) {
                                    topic = MqttTopicHandler.build(MqttTopicHandler.SEND_FRIEND_ADD,StaticData.my.getId(),user.getId());
                                    message = "@@verify@@" + verifyText.getText().toString() + "@@remark@@" + remarkText.getText().toString();
                                }
                                else {
                                    topic = MqttTopicHandler.build(MqttTopicHandler.SEND_FRIEND_ADD,StaticData.my.getId(),user.getId());
                                    message = "@@verify@@" + verifyText.getText().toString();
                                }
                                StaticData.mqttClient.publish(topic,message,new MqttPublishOptions().setQoS(2),(t, m) -> {
                                    handler.post(() -> {
                                        Toast toast = Toast.makeText(AddFriendOrGroupActivity.this,null,Toast.LENGTH_SHORT);
                                        toast.setText("请求已发送");
                                        toast.show();
                                        AddFriendOrGroupActivity.this.setResult(0);
                                        AddFriendOrGroupActivity.this.finish();
                                    });
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast toast = Toast.makeText(AddFriendOrGroupActivity.this,null,Toast.LENGTH_SHORT);
                                toast.setText("请求发送失败");
                                toast.show();
                            }
                        }
                        else {
                            Toast toast = Toast.makeText(AddFriendOrGroupActivity.this,null,Toast.LENGTH_SHORT);
                            toast.setText("请求发送失败");
                            toast.show();
                        }
                    }

                    @Override
                    protected Boolean doInBackground(Void... voids) {
                        try {
                            URL url;
                            if(user != null)
                                url = new URL(StaticData.DOMAIN + "/friend/check?myId=" + StaticData.my.getId() + "&userId=" + user.getId());
                            else
                                url = new URL(StaticData.DOMAIN + "/group/checkmyId=" + StaticData.my.getId() + "&groupId=" + group.getId());
                            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                            if(httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                                BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                                String resp = "";
                                String line;
                                while((line = in.readLine()) != null)
                                    resp += line;
                                in.close();
                                JsonObject jsonObject = new JsonParser().parse(resp).getAsJsonObject();
                                if("0".equals(jsonObject.get("code").getAsString())) {
                                    Gson gson = new Gson();
                                    JsonObject data = jsonObject.get("data").getAsJsonObject();
                                    return gson.fromJson(data.get("isFriend"), Boolean.class);
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return false;
                    }
                };
                task.execute();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}

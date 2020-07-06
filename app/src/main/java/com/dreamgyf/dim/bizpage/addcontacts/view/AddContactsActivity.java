package com.dreamgyf.dim.bizpage.addcontacts.view;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.dreamgyf.dim.R;
import com.dreamgyf.dim.base.mqtt.MqttTopicHandler;
import com.dreamgyf.dim.base.mvp.activity.BaseActivity;
import com.dreamgyf.dim.bizpage.addcontacts.model.AddContactsModel;
import com.dreamgyf.dim.bizpage.addcontacts.presenter.AddContactsPresenter;
import com.dreamgyf.dim.data.StaticData;
import com.dreamgyf.dim.entity.Group;
import com.dreamgyf.dim.entity.User;
import com.dreamgyf.dim.utils.ToastUtils;
import com.dreamgyf.mqtt.client.MqttPublishOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AddContactsActivity extends BaseActivity<AddContactsModel,AddContactsActivity, AddContactsPresenter> implements IAddContactsView {

    public static class Type {
        public final static int USER = 0;
        public final static int GROUP = 1;
    }

    private Handler handler = new Handler();

    private Intent mIntent;

    private int mType;

    private User mUser;

    private Group mGroup;

    private EditText verifyText;

    private EditText remarkText;

    @NonNull
    @Override
    public AddContactsPresenter bindPresenter() {
        return new AddContactsPresenter(this);
    }

    public static Intent createIntent(Context context, User user) {
        Intent intent = new Intent(context, AddContactsActivity.class);
        intent.putExtra("type",Type.USER);
        intent.putExtra("user", (Parcelable) user);
        return intent;
    }

    public static Intent createIntent(Context context, Group group) {
        Intent intent = new Intent(context, AddContactsActivity.class);
        intent.putExtra("type",Type.GROUP);
        intent.putExtra("group", (Parcelable) group);
        return intent;
    }

    private void readIntent() {
        mIntent = getIntent();
        mType = mIntent.getIntExtra("type",-1);
        if(mType == Type.USER) {
            mUser = mIntent.getParcelableExtra("user");
        } else if(mType == Type.GROUP) {
            mGroup = mIntent.getParcelableExtra("group");
        } else {
            ToastUtils.sendToast(this, "参数异常");
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend_or_group);

        readIntent();

        initView();

        renderView();
    }

    private void initView() {
        initToolbar();
        verifyText = findViewById(R.id.verify_text);
        remarkText = findViewById(R.id.remark_text);
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void renderView() {

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
                                if(mUser != null) {
                                    topic = MqttTopicHandler.build(MqttTopicHandler.SEND_FRIEND_ADD,StaticData.my.getId(), mUser.getId());
                                    message = "@@verify@@" + verifyText.getText().toString() + "@@remark@@" + remarkText.getText().toString();
                                }
                                else {
                                    topic = MqttTopicHandler.build(MqttTopicHandler.SEND_FRIEND_ADD,StaticData.my.getId(), mUser.getId());
                                    message = "@@verify@@" + verifyText.getText().toString();
                                }
                                StaticData.mqttClient.publish(topic,message,new MqttPublishOptions().setQoS(2),(t, m) -> {
                                    handler.post(() -> {
                                        Toast toast = Toast.makeText(AddContactsActivity.this,null,Toast.LENGTH_SHORT);
                                        toast.setText("请求已发送");
                                        toast.show();
                                        AddContactsActivity.this.setResult(0);
                                        AddContactsActivity.this.finish();
                                    });
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast toast = Toast.makeText(AddContactsActivity.this,null,Toast.LENGTH_SHORT);
                                toast.setText("请求发送失败");
                                toast.show();
                            }
                        }
                        else {
                            Toast toast = Toast.makeText(AddContactsActivity.this,null,Toast.LENGTH_SHORT);
                            toast.setText("请求发送失败");
                            toast.show();
                        }
                    }

                    @Override
                    protected Boolean doInBackground(Void... voids) {
                        try {
                            URL url;
                            if(mUser != null)
                                url = new URL(StaticData.DOMAIN + "/friend/check?myId=" + StaticData.my.getId() + "&userId=" + mUser.getId());
                            else
                                url = new URL(StaticData.DOMAIN + "/group/check?myId=" + StaticData.my.getId() + "&groupId=" + mGroup.getId());
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

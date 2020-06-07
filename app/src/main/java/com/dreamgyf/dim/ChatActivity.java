package com.dreamgyf.dim;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dreamgyf.dim.adapter.ChatRecyclerViewAdapter;
import com.dreamgyf.dim.base.broadcast.BroadcastActions;
import com.dreamgyf.dim.data.StaticData;
import com.dreamgyf.dim.entity.Conversation;
import com.dreamgyf.dim.entity.Group;
import com.dreamgyf.dim.entity.Message;
import com.dreamgyf.dim.entity.httpresp.User;
import com.dreamgyf.exception.MqttException;
import com.dreamgyf.mqtt.client.MqttPublishOptions;
import com.dreamgyf.mqtt.client.callback.MqttPublishCallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatActivity extends AppCompatActivity {

    public static class Type {
        public final static int USER = 0;
        public final static int GROUP = 1;
    }

    private Handler handler = new Handler();

    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    private BroadcastReceiver receiver;

    private int mType;

    private User mUser;

    private Group mGroup;

    private RecyclerView recyclerView;

    private boolean isRecyclerViewScrolling = false;

    private EditText textInput;

    private ImageView sendButton;

    private ChatRecyclerViewAdapter chatRecyclerViewAdapter;

    public static Intent createIntent(Context context, User user) {
        Intent intent = new Intent(context,ChatActivity.class);
        intent.putExtra("type",Type.USER);
        intent.putExtra("user", (Parcelable) user);
        return intent;
    }

    public static Intent createIntent(Context context, Group group) {
        Intent intent = new Intent(context,ChatActivity.class);
        intent.putExtra("type",Type.GROUP);
        intent.putExtra("group", (Parcelable) group);
        return intent;
    }

    private void readIntent() {
        Intent intent = getIntent();
        mType = intent.getIntExtra("type",-1);
        switch (mType) {
            case Type.USER: {
                mUser = intent.getParcelableExtra("user");
                break;
            }
            case Type.GROUP: {
                mUser = intent.getParcelableExtra("group");
                break;
            }
            default: {
                Toast.makeText(this,"获取数据失败",Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        readIntent();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                isRecyclerViewScrolling = !(newState == RecyclerView.SCROLL_STATE_IDLE);
            }
        });
        if(mUser != null) {
            String username;
            if(mUser.getRemarkName() != null) {
                username = mUser.getRemarkName();
            }
            else if(mUser.getNickname() != null) {
                username = mUser.getNickname();
            }
            else {
                username = mUser.getUsername();
            }
            getSupportActionBar().setTitle(username);
            recyclerView.setAdapter(chatRecyclerViewAdapter = new ChatRecyclerViewAdapter(this, mUser));
            if(StaticData.friendMessageMap.get(mUser.getId()) == null) {
                StaticData.friendMessageMap.put(mUser.getId(),new ArrayList<>());
            }
            if(!StaticData.friendMessageMap.get(mUser.getId()).isEmpty())
                recyclerView.scrollToPosition(StaticData.friendMessageMap.get(mUser.getId()).size() - 1);
        }
        initRibbon();
        initBroadcast();
        scrollToBottom();
    }

    private void initRibbon() {
        textInput = findViewById(R.id.text_input);
        sendButton = findViewById(R.id.send_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = textInput.getText().toString();
                if("".equals(text))
                    return;
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                textInput.setText("");
                            }
                        });
                        String topic = "/Dim/";
                        if(mUser != null) {
                            topic += mUser.getId() + "/message/friend/";
                        }
                        else if(mGroup != null) {
                            topic += mGroup.getId() + "/message/group/";
                        }
                        topic += StaticData.my.getId();
                        try {
                            StaticData.mqttClient.publish(topic, text, new MqttPublishOptions().setQoS(2), new MqttPublishCallback() {
                                @Override
                                public void messageArrived(String topic, String message) {
                                    //更新聊天数据
                                    if(mUser != null) {
                                        List<Message> messageList = StaticData.friendMessageMap.get(mUser.getId());
                                        if(messageList == null)
                                            messageList = new ArrayList<>();
                                        Message m = new Message();
                                        m.setUser(StaticData.my);
                                        m.setType(Message.Type.SEND_TEXT);
                                        m.setContent(message);
                                        messageList.add(m);
                                        StaticData.friendMessageMap.put(mUser.getId(),messageList);
                                        //广播通知更新
                                        switch (mType) {
                                            case Type.USER: {
                                                //更新会话
                                                Conversation conversation = new Conversation();
                                                conversation.setType(Conversation.Type.USER);
                                                conversation.setUser(mUser);
                                                conversation.setCurrentMessage(message);
                                                sendBroadcast(Conversation.createBroadcast(conversation));
                                                //更新聊天页面
                                                Intent updateMessage = new Intent();
                                                updateMessage.setAction(BroadcastActions.UPDATE_MESSAGE);
                                                updateMessage.putExtra("user", (Parcelable) mUser);
                                                sendBroadcast(updateMessage);
                                                break;
                                            }
                                        }
                                    }
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (MqttException e) {
                            e.printStackTrace();
                        }
                    }
                };
                executorService.execute(runnable);
            }
        });
    }

    private void scrollToBottom() {
        if(!isRecyclerViewScrolling && chatRecyclerViewAdapter.getItemCount() != 0)
            recyclerView.smoothScrollToPosition(chatRecyclerViewAdapter.getItemCount() - 1);
    }

    private void initBroadcast() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(BroadcastActions.UPDATE_MESSAGE.equals(intent.getAction())) {
                    User u = (User) intent.getParcelableExtra("user");
                    Group g = (Group) intent.getParcelableExtra("group");
                    if(mUser != null && u != null && mUser.getId().equals(u.getId()))
                        chatRecyclerViewAdapter.notifyDataSetChanged();
                    else if(mGroup != null && g != null && mGroup.getId() == g.getId())
                        chatRecyclerViewAdapter.notifyDataSetChanged();
                    scrollToBottom();
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BroadcastActions.UPDATE_MESSAGE);
        registerReceiver(receiver,intentFilter);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:   //返回键的id
                this.finish();
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }
}

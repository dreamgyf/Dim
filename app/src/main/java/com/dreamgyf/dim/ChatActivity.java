package com.dreamgyf.dim;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dreamgyf.dim.adapter.ChatRecyclerViewAdapter;
import com.dreamgyf.dim.broadcast.BroadcastActions;
import com.dreamgyf.dim.data.StaticData;
import com.dreamgyf.dim.entity.Conversation;
import com.dreamgyf.dim.entity.Group;
import com.dreamgyf.dim.entity.Message;
import com.dreamgyf.dim.entity.User;
import com.dreamgyf.exception.MqttException;
import com.dreamgyf.mqtt.client.MqttPublishOptions;
import com.dreamgyf.mqtt.client.callback.MqttPublishCallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatActivity extends AppCompatActivity {

    private Handler handler = new Handler();

    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    private BroadcastReceiver receiver;

    private User user;

    private Group group;

    private RecyclerView recyclerView;

    private boolean isRecyclerViewScrolling = false;

    private EditText textInput;

    private ImageView sendButton;

    private ChatRecyclerViewAdapter chatRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");
        group = (Group) intent.getSerializableExtra("group");


        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                isRecyclerViewScrolling = !(newState == RecyclerView.SCROLL_STATE_IDLE);
            }
        });
        if(user != null) {
            String username;
            if(user.getRemarkName() != null) {
                username = user.getRemarkName();
            }
            else if(user.getNickname() != null) {
                username = user.getNickname();
            }
            else {
                username = user.getUsername();
            }
            getSupportActionBar().setTitle(username);
            recyclerView.setAdapter(chatRecyclerViewAdapter = new ChatRecyclerViewAdapter(this,user));
            if(StaticData.friendMessageMap.get(user.getId()) == null) {
                StaticData.friendMessageMap.put(user.getId(),new ArrayList<>());
            }
            if(!StaticData.friendMessageMap.get(user.getId()).isEmpty())
                recyclerView.scrollToPosition(StaticData.friendMessageMap.get(user.getId()).size() - 1);
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
                        if(user != null) {
                            topic += user.getId() + "/message/friend/";
                        }
                        else if(group != null) {
                            topic += group.getId() + "/message/group/";
                        }
                        topic += StaticData.my.getId();
                        try {
                            StaticData.mqttClient.publish(topic, text, new MqttPublishOptions().setQoS(2), new MqttPublishCallback() {
                                @Override
                                public void messageArrived(String topic, String message) {
                                    //更新聊天数据
                                    if(user != null) {
                                        List<Message> messageList = StaticData.friendMessageMap.get(user.getId());
                                        if(messageList == null)
                                            messageList = new ArrayList<>();
                                        Message m = new Message();
                                        m.setUser(StaticData.my);
                                        m.setType(Message.Type.SEND_TEXT);
                                        m.setContent(message);
                                        messageList.add(m);
                                        StaticData.friendMessageMap.put(user.getId(),messageList);
                                        //广播通知更新
                                        Intent updateMessage = new Intent();
                                        updateMessage.setAction(BroadcastActions.UPDATE_MESSAGE);
                                        updateMessage.putExtra("user",user);
                                        sendBroadcast(updateMessage);

                                        Conversation conversation = new Conversation();
                                        conversation.setUser(user);
                                        conversation.setGroup(group);
                                        conversation.setCurrentMessage(message);
                                        StaticData.addConversation(conversation);
                                        Intent updateConversation = new Intent();
                                        updateConversation.setAction(BroadcastActions.UPDATE_CONVERSATION);
                                        sendBroadcast(updateConversation);
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
                    User u = (User) intent.getSerializableExtra("user");
                    Group g = (Group) intent.getSerializableExtra("group");
                    if(user != null && u != null && user.getId().equals(u.getId()))
                        chatRecyclerViewAdapter.notifyDataSetChanged();
                    else if(group != null && g != null && group.getId().equals(g.getId()))
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

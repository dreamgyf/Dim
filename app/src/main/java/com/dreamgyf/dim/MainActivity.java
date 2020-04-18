package com.dreamgyf.dim;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.dreamgyf.dim.adapter.FriendRecyclerViewAdapter;
import com.dreamgyf.dim.adapter.MainViewPagerAdapter;
import com.dreamgyf.dim.adapter.MessagePageListViewAdapter;
import com.dreamgyf.dim.broadcast.BroadcastActions;
import com.dreamgyf.dim.data.StaticData;
import com.dreamgyf.dim.entity.Conversation;
import com.dreamgyf.dim.entity.Group;
import com.dreamgyf.dim.entity.Message;
import com.dreamgyf.dim.entity.User;
import com.dreamgyf.dim.utils.MqttTopicUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private MainApplication application;

    private NotificationManager notificationManager;

    private Handler handler = new Handler();

    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    private ViewPager viewPager;

    private List<View> viewList = new ArrayList<>();

    private BottomNavigationView bottomNavigationView;

    private MessagePageListViewAdapter messagePageListViewAdapter;

    private BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        application = (MainApplication) getApplication();
        notificationManager = application.getNotificationManager();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initViewPager();
        initBottomNavigation();

        StaticData.mqttClient.setCallback((topic, message) -> {
            Log.e("Main Message",message);
            MqttTopicUtils.Result resultRes = MqttTopicUtils.analyze(topic);
            switch (resultRes.getType()) {
                case MqttTopicUtils.RECEIVE_FRIEND_MESSAGE: {
                    for(User friend : StaticData.friendList) {
                        if(friend.getId().equals(resultRes.getFromId())) {
                            if(application.isAppBackground()) {
                                String username;
                                if(friend.getRemarkName() != null) {
                                    username = friend.getRemarkName();
                                }
                                else if(friend.getNickname() != null) {
                                    username = friend.getNickname();
                                }
                                else {
                                    username = friend.getUsername();
                                }
                                //通知
                                NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"Message");
                                Notification notification = builder.setContentTitle(username)
                                        .setContentText(message)
                                        .setSmallIcon(R.drawable.small_logo)
                                        .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.small_logo)).build();
                                notification.flags = Notification.FLAG_AUTO_CANCEL;
                                notificationManager.notify(friend.getId(),notification);
                            }
                            //更新会话数据
                            Conversation conversation = new Conversation();
                            conversation.setUser(friend);
                            conversation.setCurrentMessage(message);
                            StaticData.addConversation(conversation);
                            Intent updateConversation = new Intent();
                            updateConversation.setAction(BroadcastActions.UPDATE_CONVERSATION);
                            sendBroadcast(updateConversation);
                            //更新聊天数据
                            List<Message> messageList = StaticData.friendMessageMap.get(resultRes.getFromId());
                            if(messageList == null)
                                messageList = new ArrayList<>();
                            Message m = new Message();
                            m.setUser(friend);
                            m.setType(Message.Type.RECEIVE_TEXT);
                            m.setContent(message);
                            messageList.add(m);
                            StaticData.friendMessageMap.put(resultRes.getFromId(),messageList);
                            //广播通知更新
                            Intent updateMessage = new Intent();
                            updateMessage.setAction(BroadcastActions.UPDATE_MESSAGE);
                            updateMessage.putExtra("user",friend);
                            sendBroadcast(updateMessage);
                            break;
                        }
                    }
                    break;
                }
                case MqttTopicUtils.RECEIVE_GROUP_MESSAGE: {
                    for(Group group : StaticData.groupList) {
                        if(group.getId().equals(resultRes.getToId())) {
                            Conversation conversation = new Conversation();
                            conversation.setGroup(group);
                            try {
                                URL url = new URL(StaticData.DOMAIN + "/userinfo");
                                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                                httpURLConnection.setRequestMethod("POST");
                                httpURLConnection.setRequestProperty("Content-Result","application/json; charset=UTF-8");
                                Map<String,Object> params = new HashMap<>();
                                params.put("myId",StaticData.my.getId());
                                params.put("friendId", resultRes.getFromId());
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
                                    JsonObject jsonObject = new JsonParser().parse(resp).getAsJsonObject();
                                    User user = new Gson().fromJson(jsonObject,User.class);
                                    conversation.setUser(user);
                                    conversation.setCurrentMessage(message);
                                    StaticData.addConversation(conversation);
                                    Intent updateConversation = new Intent();
                                    updateConversation.setAction(BroadcastActions.UPDATE_CONVERSATION);
                                    sendBroadcast(updateConversation);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            break;
                        }
                    }
                    break;
                }
            }
        });

        initBroadcast();
    }

    private void initViewPager() {
        viewList.add(LayoutInflater.from(this).inflate(R.layout.main_viewpager_message,null));
        initMessagePage();
        viewList.add(LayoutInflater.from(this).inflate(R.layout.main_viewpager_friend,null));
        initFriendPage();
        viewList.add(LayoutInflater.from(this).inflate(R.layout.main_viewpager_my,null));
        initMyPage();
        viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(new MainViewPagerAdapter(viewList));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(bottomNavigationView != null)
                    bottomNavigationView.getMenu().getItem(position).setChecked(true);
                switch (position) {
                    case 0:
                        getSupportActionBar().setTitle("消息");
                        break;
                    case 1:
                        getSupportActionBar().setTitle("好友");
                        break;
                    case 2:
                        getSupportActionBar().setTitle("我");
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initMessagePage() {
        ListView listView = viewList.get(0).findViewById(R.id.listview);
        listView.setAdapter(messagePageListViewAdapter = new MessagePageListViewAdapter(this));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Conversation conversation = StaticData.conversationList.get(position);
                Intent intent = new Intent(MainActivity.this,ChatActivity.class);
                if(conversation.getGroup() != null)
                    intent.putExtra("group",conversation.getGroup());
                else
                    intent.putExtra("user",conversation.getUser());
                startActivity(intent);
            }
        });
    }

    private void initFriendPage() {
        RecyclerView recyclerView = viewList.get(1).findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new FriendRecyclerViewAdapter(this));
    }

    private void initMyPage() {

    }

    private void initBottomNavigation() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.message:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.friend:
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.my:
                        viewPager.setCurrentItem(2);
                        break;
                }
                return true;
            }
        });
    }

    private void initBroadcast() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(BroadcastActions.UPDATE_CONVERSATION.equals(intent.getAction())) {
                    messagePageListViewAdapter.notifyDataSetChanged();
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BroadcastActions.UPDATE_CONVERSATION);
        registerReceiver(receiver,intentFilter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu,menu);
        if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
            try {
                Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                m.setAccessible(true);
                m.invoke(menu, true);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addFriendButton:
                Intent intent = new Intent(this, SearchFriendOrGroupActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }
}

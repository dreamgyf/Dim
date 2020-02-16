package com.dreamgyf.dim;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.dreamgyf.dim.adapter.MainViewPagerAdapter;
import com.dreamgyf.dim.adapter.MessagePageListViewAdapter;
import com.dreamgyf.dim.broadcast.BroadcastActions;
import com.dreamgyf.dim.data.StaticData;
import com.dreamgyf.dim.entity.Conversation;
import com.dreamgyf.dim.entity.Group;
import com.dreamgyf.dim.entity.Message;
import com.dreamgyf.dim.entity.User;
import com.dreamgyf.mqtt.client.callback.MqttMessageCallback;
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

    private Handler handler = new Handler();

    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    private ViewPager viewPager;

    private List<View> viewList = new ArrayList<>();

    private BottomNavigationView bottomNavigationView;

    private MessagePageListViewAdapter messagePageListViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initViewPager();
        initBottomNavigation();

        StaticData.mqttClient.setCallback(new MqttMessageCallback() {
            /**
             * /Dim/自己的id/message/friend/别人的id      接收到别人的消息
             * /Dim/群组的id/message/group/别人的id      接收到群组里别人发的消息
             * /Dim/自己的id/add/friend/别人的id      接收到别人的好友请求
             * /Dim/群组的id/add/friend/别人的id      接收到别人的加入群组请求（自己是群管理的情况下）
             */
            @Override
            public void messageArrived(String topic, final String message) {
                final Conversation conversation = new Conversation();
                String[] topicParam = topic.split("/");
                Integer toId = Integer.valueOf(topicParam[2]);
                String type = topicParam[3];
                String from = topicParam[4];
                Integer fromId = Integer.valueOf(topicParam[5]);
                if("message".equals(type)) {
                    if("friend".equals(from)) {
                        for(User friend : StaticData.friendList) {
                            if(friend.getId().equals(fromId)) {
                                //更新会话数据
                                conversation.setUser(friend);
                                conversation.setCurrentMessage(message);
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        messagePageListViewAdapter.addConversation(conversation);
                                    }
                                });
                                //更新聊天数据
                                List<Message> messageList = StaticData.friendMessageMap.get(fromId);
                                if(messageList == null)
                                    messageList = new ArrayList<>();
                                Message m = new Message();
                                m.setUser(friend);
                                m.setType(Message.Type.RECEIVE_TEXT);
                                m.setContent(message);
                                messageList.add(m);
                                StaticData.friendMessageMap.put(fromId,messageList);
                                //广播通知更新
                                Intent updateMessage = new Intent();
                                updateMessage.setAction(BroadcastActions.UPDATE_MESSAGE);
                                updateMessage.putExtra("user",friend);
                                sendBroadcast(updateMessage);
                                break;
                            }
                        }
                    }
                    else if("group".equals(from)) {
                        for(Group group : StaticData.groupList) {
                            if(group.getId().equals(toId)) {
                                conversation.setGroup(group);
                                try {
                                    URL url = new URL(StaticData.DOMAIN + "/userinfo");
                                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                                    httpURLConnection.setRequestMethod("POST");
                                    httpURLConnection.setRequestProperty("Content-Type","application/json; charset=UTF-8");
                                    Map<String,Object> params = new HashMap<>();
                                    params.put("myId",StaticData.my.getId());
                                    params.put("friendId", fromId);
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
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                messagePageListViewAdapter.addConversation(conversation);
                                            }
                                        });
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                break;
                            }
                        }
                    }
                }
            }
        });
    }

    private void initViewPager() {
        viewList.add(LayoutInflater.from(this).inflate(R.layout.main_viewpager_message,null));
        initMessagePage();
        viewList.add(LayoutInflater.from(this).inflate(R.layout.main_viewpager_friend,null));
        viewList.add(LayoutInflater.from(this).inflate(R.layout.main_viewpager_my,null));
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
}

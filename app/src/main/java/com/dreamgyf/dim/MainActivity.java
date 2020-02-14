package com.dreamgyf.dim;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.dreamgyf.dim.adapter.MainViewPagerAdapter;
import com.dreamgyf.dim.adapter.MessagePageListViewAdapter;
import com.dreamgyf.dim.data.StaticData;
import com.dreamgyf.dim.entity.Conversation;
import com.dreamgyf.dim.entity.User;
import com.dreamgyf.mqtt.client.callback.MqttMessageCallback;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
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
            @Override
            public void messageArrived(String topic, final String message) {
                final Conversation conversation = new Conversation();
                String[] topicParam = topic.split("/");
                if("message".equals(topicParam[3])) {
                    if("friend".equals(topicParam[4])) {
                        User user = new User();
                        user.setId(Integer.parseInt(topicParam[5]));
                        user.setUsername("test1");
                        user.setNickname("test1");
                        conversation.setUser(user);
                        conversation.setCurrentMessage(message);
                    }
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        messagePageListViewAdapter.addConversation(conversation);
                    }
                });
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

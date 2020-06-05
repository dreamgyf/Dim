package com.dreamgyf.dim.main.view;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.dreamgyf.dim.ChatActivity;
import com.dreamgyf.dim.MainApplication;
import com.dreamgyf.dim.R;
import com.dreamgyf.dim.SearchFriendOrGroupActivity;
import com.dreamgyf.dim.adapter.MessagePageListViewAdapter;
import com.dreamgyf.dim.base.broadcast.BroadcastActions;
import com.dreamgyf.dim.base.mvp.activity.BaseActivity;
import com.dreamgyf.dim.contacts.adapter.FriendRecyclerViewAdapter;
import com.dreamgyf.dim.data.StaticData;
import com.dreamgyf.dim.entity.Conversation;
import com.dreamgyf.dim.main.adapter.MainViewPagerAdapter;
import com.dreamgyf.dim.main.model.MainModel;
import com.dreamgyf.dim.main.presenter.IMainPresenter;
import com.dreamgyf.dim.main.presenter.MainPresenter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends BaseActivity<MainModel, MainActivity, MainPresenter> implements IMainView {

    private IMainPresenter mPresenter;

    private MainApplication application;

    private NotificationManager notificationManager;

    private Handler handler = new Handler();

    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    private ViewPager viewPager;

    private List<View> viewList = new ArrayList<>();

    private BottomNavigationView bottomNavigationView;

    private MessagePageListViewAdapter messagePageListViewAdapter;

    private BroadcastReceiver receiver;

    @NonNull
    @Override
    public MainPresenter bindPresenter() {
        return new MainPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPresenter = getPresenter();
        application = (MainApplication) getApplication();
        notificationManager = application.getNotificationManager();


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initViewPager();
        initBottomNavigation();

        initBroadcast();
    }

    private void initViewPager() {
        List<View> viewList = new ArrayList<>();
        viewList.add(mPresenter.getViewPagerView(0));
        viewList.add(mPresenter.getViewPagerView(1));
        viewList.add(mPresenter.getViewPagerView(2));
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
                getSupportActionBar().setTitle(mPresenter.getViewPagerTitle(position));
                switch (position) {
                    case 0:
                        break;
                    case 1:
                        break;
                    case 2:
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
                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
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
        bottomNavigationView.setOnNavigationItemSelectedListener((item) -> {
            switch (item.getItemId()) {
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

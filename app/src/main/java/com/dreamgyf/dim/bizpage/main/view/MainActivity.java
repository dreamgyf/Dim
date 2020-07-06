package com.dreamgyf.dim.bizpage.main.view;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.dreamgyf.dim.R;
import com.dreamgyf.dim.bizpage.searchcontacts.view.SearchContactsActivity;
import com.dreamgyf.dim.base.mvp.activity.BaseActivity;
import com.dreamgyf.dim.bizpage.main.adapter.MainViewPagerAdapter;
import com.dreamgyf.dim.bizpage.main.model.MainModel;
import com.dreamgyf.dim.bizpage.main.presenter.IMainPresenter;
import com.dreamgyf.dim.bizpage.main.presenter.MainPresenter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity<MainModel, MainActivity, MainPresenter> implements IMainView {

    private IMainPresenter mPresenter;

    private ViewPager viewPager;

    private BottomNavigationView bottomNavigationView;

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

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initViewPager();
        initBottomNavigation();
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
                if (bottomNavigationView != null)
                    bottomNavigationView.getMenu().getItem(position).setChecked(true);
                getSupportActionBar().setTitle(mPresenter.getViewPagerTitle(position));
                mPresenter.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
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
                startActivity(SearchContactsActivity.createIntent(this));
                break;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

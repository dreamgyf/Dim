package com.dreamgyf.dim.bizpage.searchcontacts.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.dreamgyf.dim.R;
import com.dreamgyf.dim.bizpage.userinfo.UserInfoActivity;
import com.dreamgyf.dim.base.mvp.activity.BaseActivity;
import com.dreamgyf.dim.bizpage.searchcontacts.listener.OnSearchListener;
import com.dreamgyf.dim.bizpage.searchcontacts.model.ISearchContactsModel;
import com.dreamgyf.dim.bizpage.searchcontacts.presenter.ISearchContactsPresenter;
import com.dreamgyf.dim.bizpage.searchcontacts.presenter.SearchContactsPresenter;
import com.dreamgyf.dim.entity.User;

public class SearchContactsActivity extends BaseActivity<ISearchContactsModel, ISearchContactsView, SearchContactsPresenter> implements ISearchContactsView {

    private ISearchContactsPresenter mPresenter;

    private TextView addFriendButton;

    private TextView addGroupButton;

    private View addFriendView;

    private View addGroupView;

    private SearchView searchFriend;

    private SearchView searchGroup;

    private ListView mUserListView;

    private ListView mGroupListView;

    public static Intent createIntent(Context context) {
        return new Intent(context, SearchContactsActivity.class);
    }

    @NonNull
    @Override
    public SearchContactsPresenter bindPresenter() {
        return (SearchContactsPresenter) (mPresenter = new SearchContactsPresenter(this));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friend_or_group);

        initUserView();
        initGroupView();
        initTop();
    }

    private void initUserView() {
        addFriendView = findViewById(R.id.addFriendView);
        searchFriend = findViewById(R.id.searchFriend);
        searchFriend.setIconifiedByDefault(false);
        mUserListView = findViewById(R.id.friendListView);
        mPresenter.initUserListView(mUserListView);
        mPresenter.setOnSearchFriendListener(new OnSearchListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure() {
                Toast toast = Toast.makeText(SearchContactsActivity.this,"",Toast.LENGTH_SHORT);
                toast.setText("获取数据失败");
                toast.show();
            }
        });
        searchFriend.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchFriend.clearFocus();
                if(!TextUtils.isEmpty(query)) {
                    mPresenter.searchFriend(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        mUserListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User user = mPresenter.getUserItem(position);
                startActivity(UserInfoActivity.createIntent(SearchContactsActivity.this,user));
            }
        });
    }

    private void initGroupView() {
        addGroupView = findViewById(R.id.addGroupView);
        searchGroup = findViewById(R.id.searchGroup);
        searchGroup.setIconifiedByDefault(false);
        mGroupListView = findViewById(R.id.groupListView);
    }

    private void initTop() {
        addFriendButton = findViewById(R.id.addFriendButton);
        addGroupButton = findViewById(R.id.addGroupButton);
        selectAddFriend();
        addFriendButton.setOnClickListener((view)-> selectAddFriend());
        addGroupButton.setOnClickListener((view)-> selectAddGroup());
    }

    private void selectAddFriend() {
        addFriendButton.setTextColor(getResources().getColor(R.color.colorPrimary));
        addFriendButton.setBackgroundResource(R.drawable.shape_add_friend_button);
        addGroupButton.setTextColor(Color.WHITE);
        addGroupButton.setBackgroundColor(Color.TRANSPARENT);
        addFriendView.setVisibility(View.VISIBLE);
        addGroupView.setVisibility(View.GONE);
    }

    private void selectAddGroup() {
        addGroupButton.setTextColor(getResources().getColor(R.color.colorPrimary));
        addGroupButton.setBackgroundResource(R.drawable.shape_add_group_button);
        addFriendButton.setTextColor(Color.WHITE);
        addFriendButton.setBackgroundColor(Color.TRANSPARENT);
        addGroupView.setVisibility(View.VISIBLE);
        addFriendView.setVisibility(View.GONE);
    }
}

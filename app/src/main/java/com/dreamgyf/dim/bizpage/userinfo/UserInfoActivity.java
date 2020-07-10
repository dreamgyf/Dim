package com.dreamgyf.dim.bizpage.userinfo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.dreamgyf.dim.R;
import com.dreamgyf.dim.bizpage.addcontacts.view.AddContactsActivity;
import com.dreamgyf.dim.bizpage.chat.view.ChatActivity;
import com.dreamgyf.dim.entity.Friend;
import com.dreamgyf.dim.entity.User;
import com.dreamgyf.dim.utils.imageloader.ImageLoader;

public class UserInfoActivity extends AppCompatActivity {

    private User mUser;

    private boolean isFriend;

    private ImageView mIvAvatar;

    private TextView mTvNickname;

    private TextView mTvUsername;

    private LinearLayout bottomButton;

    private TextView mTvBottomButton;

    public static Intent createIntent(Context context, User user) {
        Intent intent = new Intent(context,UserInfoActivity.class);
        intent.putExtra("user", (Parcelable) user);
        return intent;
    }

    private void readIntent() {
        Intent intent = getIntent();
        mUser = intent.getParcelableExtra("user");
        if(mUser == null) {
            finish();
        }
        isFriend = mUser instanceof Friend;
        renderView();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initView();

        readIntent();
    }

    private void initView() {
        bottomButton = findViewById(R.id.bottom_button);
        mIvAvatar = findViewById(R.id.avatar);
        mTvNickname = findViewById(R.id.nickname);
        mTvUsername = findViewById(R.id.username);
        mTvBottomButton = findViewById(R.id.bottom_button_text);
    }

    private void renderView() {
        renderUserInfo();
        if(isFriend) {
            findViewById(R.id.only_friend).setVisibility(View.VISIBLE);
            mTvBottomButton.setText("发送信息");
            initSendMessageButton();
        }
        else {
            findViewById(R.id.only_friend).setVisibility(View.INVISIBLE);
            mTvBottomButton.setText("添加好友");
            initAddFriendButton();
        }
    }

    private void renderUserInfo() {
        ImageLoader.with(this).loadAvatar(mUser.getAvatarId()).into(mIvAvatar);
        mTvNickname.setText(mUser.getNickname() != null ? mUser.getNickname() : mUser.getUsername());
        mTvUsername.setText("用户名 : " + mUser.getUsername());
    }

    private void initSendMessageButton() {
        bottomButton.setOnClickListener((view) -> {
            startActivity(ChatActivity.createIntent(UserInfoActivity.this, (Friend) mUser));
            finish();
        });
    }

    private void initAddFriendButton() {
        bottomButton.setOnClickListener((view) -> {
            startActivityForResult(AddContactsActivity.createIntent(this, mUser),0);
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == 0)
            finish();
    }
}

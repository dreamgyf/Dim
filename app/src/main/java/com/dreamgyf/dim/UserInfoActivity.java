package com.dreamgyf.dim;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.dreamgyf.dim.asynctask.GetAvatarTask;
import com.dreamgyf.dim.data.StaticData;
import com.dreamgyf.dim.entity.User;

public class UserInfoActivity extends AppCompatActivity {

    private User user;

    private LinearLayout bottomButton;

    private Toolbar toolbar;

    private boolean isFriend = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        user = (User) getIntent().getSerializableExtra("user");
        for(User u : StaticData.friendList) {
            if(u.getId().equals(user.getId())){
                isFriend = true;
                break;
            }
        }

        initView();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initInfo();
        if(isFriend)
            initSendMessageButton();
        else
            initAddFriendButton();
    }

    private void initView() {
        bottomButton = findViewById(R.id.bottom_button);
        TextView bottomButtonText = findViewById(R.id.bottom_button_text);
        if(isFriend) {
            findViewById(R.id.only_friend).setVisibility(View.VISIBLE);
            bottomButtonText.setText("发送信息");
        }
        else {
            findViewById(R.id.only_friend).setVisibility(View.INVISIBLE);
            bottomButtonText.setText("添加好友");
        }
    }

    private void initInfo() {
        ImageView avatar = findViewById(R.id.avatar);
        TextView nickname = findViewById(R.id.nickname);
        TextView username = findViewById(R.id.username);
        GetAvatarTask task = new GetAvatarTask(this,avatar);
        task.execute(user.getAvatarId());
        nickname.setText(user.getNickname() != null ? user.getNickname() : user.getUsername());
        username.setText("用户名 : " + user.getUsername());
    }

    private void initSendMessageButton() {
        bottomButton.setOnClickListener((view) -> {
            Intent intent = new Intent(this,ChatActivity.class);
            intent.putExtra("user",user);
            startActivity(intent);
            finish();
        });
    }

    private void initAddFriendButton() {
        bottomButton.setOnClickListener((view) -> {
            Intent intent = new Intent(this,AddFriendOrGroupActivity.class);
            intent.putExtra("user",user);
            startActivityForResult(intent,0);
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
        if(requestCode == 0 && resultCode == 0)
            finish();
    }
}

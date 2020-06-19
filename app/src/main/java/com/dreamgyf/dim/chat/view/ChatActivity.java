package com.dreamgyf.dim.chat.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dreamgyf.dim.R;
import com.dreamgyf.dim.base.enums.ChatType;
import com.dreamgyf.dim.base.mvp.activity.BaseActivity;
import com.dreamgyf.dim.chat.adapter.GroupMessageRecyclerViewAdapter;
import com.dreamgyf.dim.chat.listener.OnMessageReceivedListener;
import com.dreamgyf.dim.chat.model.ChatModel;
import com.dreamgyf.dim.chat.presenter.ChatPresenter;
import com.dreamgyf.dim.entity.Group;
import com.dreamgyf.dim.entity.httpresp.User;
import com.dreamgyf.dim.framework.loadingrecyclerview.LoadingRecyclerView;
import com.dreamgyf.dim.utils.NameUtils;

public class ChatActivity extends BaseActivity<ChatModel,ChatActivity, ChatPresenter> implements IChatView {

    private ChatPresenter mPresenter;

    private Handler handler = new Handler();

    private int mType;

    private User mUser;

    private Group mGroup;

    private LoadingRecyclerView mRecyclerView;

    private boolean isRecyclerViewScrolling = false;

    private EditText textInput;

    private ImageView sendButton;

    private GroupMessageRecyclerViewAdapter mUserMessageRecyclerViewAdapter;

    public static Intent createIntent(Context context, User user) {
        Intent intent = new Intent(context,ChatActivity.class);
        intent.putExtra("type", ChatType.USER);
        intent.putExtra("user", (Parcelable) user);
        return intent;
    }

    public static Intent createIntent(Context context, Group group) {
        Intent intent = new Intent(context,ChatActivity.class);
        intent.putExtra("type", ChatType.GROUP);
        intent.putExtra("group", (Parcelable) group);
        return intent;
    }

    private void readIntent() {
        Intent intent = getIntent();
        mType = intent.getIntExtra("type",-1);
        switch (mType) {
            case ChatType.USER: {
                mUser = intent.getParcelableExtra("user");
                break;
            }
            case ChatType.GROUP: {
                mGroup = intent.getParcelableExtra("group");
                break;
            }
            default: {
                Toast.makeText(this,"获取数据失败",Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    public int getType() {
        return mType;
    }

    public User getUser() {
        return mUser;
    }

    public Group getGroup() {
        return mGroup;
    }

    @NonNull
    @Override
    public ChatPresenter bindPresenter() {
        return mPresenter = new ChatPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        readIntent();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if(mType == ChatType.USER) {
            getSupportActionBar().setTitle(NameUtils.getUsername(mUser));
        } else if(mType == ChatType.GROUP) {
            getSupportActionBar().setTitle(mGroup.getName());
        }

        mRecyclerView = findViewById(R.id.recycler_view);
        mPresenter.initRecyclerView(mRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                isRecyclerViewScrolling = !(newState == RecyclerView.SCROLL_STATE_IDLE);
            }
        });
        initRibbon();
        scrollToBottom(false);

        mPresenter.setOnMessageReceivedListener(new OnMessageReceivedListener() {
            @Override
            public void onReceived(String message) {
                scrollToBottom(true);
            }
        });
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
                textInput.setText("");
                mPresenter.sendMessage(text);
            }
        });
    }

    public void scrollToBottom(boolean smooth) {
        if(smooth) {
            if(!isRecyclerViewScrolling && mPresenter.getMessageItemCount() != 0) {
                mRecyclerView.smoothScrollToPosition(mPresenter.getMessageItemCount() - 1);
            }
        } else {
            if(!isRecyclerViewScrolling && mPresenter.getMessageItemCount() != 0) {
                mRecyclerView.scrollToPosition(mPresenter.getMessageItemCount() - 1);
            }
        }
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
}

package com.dreamgyf.dim.bizpage.addcontacts.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.dreamgyf.dim.R;
import com.dreamgyf.dim.base.mvp.activity.BaseActivity;
import com.dreamgyf.dim.bizpage.addcontacts.listener.OnSendRequestListener;
import com.dreamgyf.dim.bizpage.addcontacts.model.AddContactsModel;
import com.dreamgyf.dim.bizpage.addcontacts.presenter.AddContactsPresenter;
import com.dreamgyf.dim.bizpage.addcontacts.presenter.IAddContactsPresenter;
import com.dreamgyf.dim.entity.Group;
import com.dreamgyf.dim.entity.User;
import com.dreamgyf.dim.enums.IntentCode;
import com.dreamgyf.dim.utils.ToastUtils;

public class AddContactsActivity extends BaseActivity<AddContactsModel,AddContactsActivity, AddContactsPresenter> implements IAddContactsView {

    public static class Type {
        public final static int USER = 0;
        public final static int GROUP = 1;
    }

    private IAddContactsPresenter mPresenter;

    private Handler handler = new Handler();

    private Intent mIntent;

    private int mType;

    private User mUser;

    private Group mGroup;

    private EditText verifyText;

    private EditText remarkText;

    @NonNull
    @Override
    public AddContactsPresenter bindPresenter() {
        return (AddContactsPresenter) (mPresenter = new AddContactsPresenter(this));
    }

    public static Intent createIntent(Context context, User user) {
        Intent intent = new Intent(context, AddContactsActivity.class);
        intent.putExtra("type",Type.USER);
        intent.putExtra("user", (Parcelable) user);
        return intent;
    }

    public static Intent createIntent(Context context, Group group) {
        Intent intent = new Intent(context, AddContactsActivity.class);
        intent.putExtra("type",Type.GROUP);
        intent.putExtra("group", (Parcelable) group);
        return intent;
    }

    private void readIntent() {
        mIntent = getIntent();
        mType = mIntent.getIntExtra("type",-1);
        if(mType == Type.USER) {
            mUser = mIntent.getParcelableExtra("user");
        } else if(mType == Type.GROUP) {
            mGroup = mIntent.getParcelableExtra("group");
        } else {
            ToastUtils.sendToast(this, "参数异常");
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend_or_group);

        readIntent();

        initView();

        renderView();

        initSendEvent();
    }

    private void initView() {
        initToolbar();
        verifyText = findViewById(R.id.verify_text);
        remarkText = findViewById(R.id.remark_text);
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void renderView() {

    }

    private void initSendEvent() {
        mPresenter.setOnSendRequestListener(new OnSendRequestListener() {
            @Override
            public void onSuccess() {
                ToastUtils.sendToast(AddContactsActivity.this,"请求已发送");
                AddContactsActivity.this.setResult(IntentCode.FINISH);
                AddContactsActivity.this.finish();
            }

            @Override
            public void onFailure(Throwable t) {
                ToastUtils.sendToast(AddContactsActivity.this,t.getMessage());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_friend_or_group,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: finish(); break;
            case R.id.send: {
                if(mType == Type.USER) {
                    mPresenter.sendFriendRequest(mUser.getId(), verifyText.getText().toString(), remarkText.getText().toString());
                } else if(mType == Type.GROUP) {
                    mPresenter.sendGroupRequest(mGroup.getId(), verifyText.getText().toString(), remarkText.getText().toString());
                }
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}

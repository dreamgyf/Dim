package com.dreamgyf.dim.bizpage.userrequest.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dreamgyf.dim.R;
import com.dreamgyf.dim.base.mvp.activity.BaseActivity;
import com.dreamgyf.dim.bizpage.userrequest.model.IUserRequestModel;
import com.dreamgyf.dim.bizpage.userrequest.presenter.IUserRequestPresenter;
import com.dreamgyf.dim.bizpage.userrequest.presenter.UserRequestPresenter;
import com.dreamgyf.dim.utils.ToastUtils;
import com.dreamgyf.loadingrecyclerview.LoadingRecyclerView;

public class UserRequestActivity extends BaseActivity<IUserRequestModel, IUserRequestView, UserRequestPresenter> implements IUserRequestView {

	private IUserRequestPresenter mPresenter;

	private LoadingRecyclerView mRvUserRequest;

	private boolean isRecyclerViewScrolling = false;

	public static Intent createIntent(Context context) {
		return new Intent(context, UserRequestActivity.class);
	}

	@NonNull
	@Override
	public UserRequestPresenter bindPresenter() {
		return new UserRequestPresenter(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_request);
		mPresenter = getPresenter();

		initView();
	}

	private void initView() {
		mRvUserRequest = findViewById(R.id.rv_user_request);
		mPresenter.initRecyclerView(mRvUserRequest);
		mRvUserRequest.setLayoutManager(new LinearLayoutManager(this));
		mRvUserRequest.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
				isRecyclerViewScrolling = !(newState == RecyclerView.SCROLL_STATE_IDLE);
			}
		});
		mPresenter.setOnUserRequestHandleListener(new IUserRequestPresenter.OnUserRequestHandleListener() {
			@Override
			public void onAcceptSuccess(View itemView, int userId) {
				itemView.findViewById(R.id.container_operable).setVisibility(View.GONE);
				itemView.findViewById(R.id.container_inoperable).setVisibility(View.VISIBLE);
				((TextView) itemView.findViewById(R.id.tv_status)).setText("已同意");
			}

			@Override
			public void onRefuseSuccess(View itemView, int userId) {
				itemView.findViewById(R.id.container_operable).setVisibility(View.GONE);
				itemView.findViewById(R.id.container_inoperable).setVisibility(View.VISIBLE);
				((TextView) itemView.findViewById(R.id.tv_status)).setText("已拒绝");
			}

			@Override
			public void onAcceptFailure(View itemView, int userId) {
				ToastUtils.sendToast(UserRequestActivity.this, "接受请求失败");
			}

			@Override
			public void onRefuseFailure(View itemView, int userId) {
				ToastUtils.sendToast(UserRequestActivity.this, "拒绝请求失败");
			}
		});
	}

	@Override
	public void scrollToBottom(boolean smooth) {
		if(smooth) {
			if(!isRecyclerViewScrolling && mPresenter.getRequestItemCount() != 0) {
				mRvUserRequest.smoothScrollToPosition(mPresenter.getRequestItemCount() - 1);
			}
		} else {
			if(!isRecyclerViewScrolling && mPresenter.getRequestItemCount() != 0) {
				mRvUserRequest.scrollToPosition(mPresenter.getRequestItemCount() - 1);
			}
		}
	}
}

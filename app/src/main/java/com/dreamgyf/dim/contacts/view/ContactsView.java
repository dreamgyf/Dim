package com.dreamgyf.dim.contacts.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dreamgyf.dim.R;
import com.dreamgyf.dim.contacts.adapter.FriendRecyclerViewAdapter;
import com.dreamgyf.dim.contacts.presenter.ContactsPresenter;
import com.dreamgyf.dim.conversation.presenter.ConversationPresenter;

public class ContactsView implements IContactsView {

	private Context mContext;

	private ContactsPresenter mPresenter;

	private View mView;

	private RecyclerView mRecyclerView;

	public void bindPresenter(ContactsPresenter presenter) {
		this.mPresenter = presenter;
	}

	public void init() {
		mContext = mPresenter.getContext();
		mView = LayoutInflater.from(mContext).inflate(R.layout.main_viewpager_friend,null,false);
		mRecyclerView = mView.findViewById(R.id.recycler_view);
		mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
		mPresenter.initRecyclerView(mRecyclerView);
	}

	@Override
	public View getView() {
		return mView;
	}

	@Override
	public String getTitle() {
		return "通讯录";
	}

	@Override
	public void onSelected() {

	}
}

package com.dreamgyf.dim.conversation.view;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.dreamgyf.dim.ChatActivity;
import com.dreamgyf.dim.R;
import com.dreamgyf.dim.conversation.adapter.ConversationListViewAdapter;
import com.dreamgyf.dim.conversation.presenter.ConversationPresenter;
import com.dreamgyf.dim.data.StaticData;
import com.dreamgyf.dim.entity.Conversation;

public class ConversationView implements IConversationView {

	private Context mContext;

	private ConversationPresenter mPresenter;

	private View mView;

	private ListView mListView;

	public void bindPresenter(ConversationPresenter presenter) {
		this.mPresenter = presenter;
	}

	public void init() {
		mContext = mPresenter.getContext();
		mView = LayoutInflater.from(mContext).inflate(R.layout.main_viewpager_message,null,false);
		mListView = mView.findViewById(R.id.listview);
		mPresenter.initListView(mListView);
	}

	@Override
	public void onSelected() {

	}

	@Override
	public View getView() {
		return mView;
	}

	@Override
	public String getTitle() {
		return "消息";
	}
}

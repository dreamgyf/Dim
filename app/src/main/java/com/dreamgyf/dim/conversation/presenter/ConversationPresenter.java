package com.dreamgyf.dim.conversation.presenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.dreamgyf.dim.ChatActivity;
import com.dreamgyf.dim.MainApplication;
import com.dreamgyf.dim.base.broadcast.BroadcastActions;
import com.dreamgyf.dim.conversation.adapter.ConversationListViewAdapter;
import com.dreamgyf.dim.base.mvp.presenter.BasePresenter;
import com.dreamgyf.dim.conversation.model.ConversationModel;
import com.dreamgyf.dim.conversation.view.ConversationView;
import com.dreamgyf.dim.data.StaticData;
import com.dreamgyf.dim.entity.Conversation;
import com.dreamgyf.dim.sharedpreferences.DataAccessUtils;

import java.util.LinkedList;

public class ConversationPresenter extends BasePresenter<ConversationModel,ConversationView> implements IConversationPresenter {

	private Context mContext;

	private ConversationView mView;

	private ConversationModel mModel;

	private ListView mListView;

	private ConversationListViewAdapter mAdapter;

	private AdapterView.OnItemClickListener mOnItemClickListener;

	private BroadcastReceiver mReceiver;

	public ConversationPresenter(Context context) {
		super(new ConversationView());
		this.mContext = context;
	}

	@Override
	protected void onAttach() {
		mView = getView();
		mView.bindPresenter(this);
		mView.init();
		initBroadcast();
	}

	@Override
	protected void onDetach() {
		mContext.unregisterReceiver(mReceiver);
	}

	@Override
	protected ConversationModel bindModel() {
		return mModel = new ConversationModel();
	}

	public Context getContext() {
		return mContext;
	}

	@Override
	public void onSelected() {
		mView.onSelected();
	}

	public void initListView(ListView listView) {
		this.mListView = listView;
		mAdapter = new ConversationListViewAdapter(mContext, DataAccessUtils.getConversationList(mContext));
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Conversation conversation = (Conversation) mAdapter.getItem(position);
				switch (conversation.getType()) {
					case Conversation.Type.USER: {
						mContext.startActivity(ChatActivity.createIntent(mContext,conversation.getUser()));
						break;
					}
					case Conversation.Type.GROUP: {
						mContext.startActivity(ChatActivity.createIntent(mContext,conversation.getGroup()));
						break;
					}
				}
				if(mOnItemClickListener != null) {
					mOnItemClickListener.onItemClick(parent,view,position,id);
				}
			}
		});
		MainApplication.getInstance().addOnActivityPauseListener(this::syncConversation);
	}

	@Override
	public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
		this.mOnItemClickListener = listener;
	}

	@Override
	public void updateList(Conversation conversation) {
		mAdapter.update(conversation);
	}

	@Override
	public void syncConversation() {
		mAdapter.sync2Local();
	}

	private void initBroadcast() {
		mReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if (BroadcastActions.UPDATE_CONVERSATION.equals(intent.getAction())) {
					updateList(Conversation.parseIntent(intent));
				}
			}
		};
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BroadcastActions.UPDATE_CONVERSATION);
		mContext.registerReceiver(mReceiver, intentFilter);
	}
}

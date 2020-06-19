package com.dreamgyf.dim.conversation.presenter;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.dreamgyf.dim.MainApplication;
import com.dreamgyf.dim.base.enums.ChatType;
import com.dreamgyf.dim.base.mvp.presenter.BasePresenter;
import com.dreamgyf.dim.chat.view.ChatActivity;
import com.dreamgyf.dim.conversation.adapter.ConversationListViewAdapter;
import com.dreamgyf.dim.conversation.model.ConversationModel;
import com.dreamgyf.dim.conversation.view.ConversationView;
import com.dreamgyf.dim.entity.Conversation;
import com.dreamgyf.dim.eventbus.event.ConversationEvent;
import com.dreamgyf.dim.sharedpreferences.DataAccessUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class ConversationPresenter extends BasePresenter<ConversationModel,ConversationView> implements IConversationPresenter {

	private Context mContext;

	private ConversationView mView;

	private ConversationModel mModel;

	private ListView mListView;

	private ConversationListViewAdapter mAdapter;

	private AdapterView.OnItemClickListener mOnItemClickListener;

	public ConversationPresenter(Context context) {
		super(new ConversationView());
		this.mContext = context;
	}

	@Override
	protected void onAttach() {
		mView = getView();
		mView.bindPresenter(this);
		mView.init();
		EventBus.getDefault().register(this);
	}

	@Override
	protected void onDetach() {
		EventBus.getDefault().unregister(this);
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
					case ChatType.USER: {
						mContext.startActivity(ChatActivity.createIntent(mContext,conversation.getUser()));
						break;
					}
					case ChatType.GROUP: {
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

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onConversationEvent(ConversationEvent event) {
		Conversation conversation = event.getConversation();
		updateList(conversation);
	}

}

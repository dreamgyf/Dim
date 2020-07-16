package com.dreamgyf.dim.bizpage.conversation.presenter;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.dreamgyf.dim.MainApplication;
import com.dreamgyf.dim.base.mvp.presenter.BasePresenter;
import com.dreamgyf.dim.bizpage.chat.view.ChatActivity;
import com.dreamgyf.dim.bizpage.conversation.adapter.ConversationListViewAdapter;
import com.dreamgyf.dim.bizpage.conversation.model.ConversationModel;
import com.dreamgyf.dim.bizpage.conversation.view.ConversationView;
import com.dreamgyf.dim.entity.Conversation;
import com.dreamgyf.dim.enums.ConversationType;
import com.dreamgyf.dim.sharedpreferences.DataAccessUtils;
import com.dreamgyf.dim.utils.GroupUtils;
import com.dreamgyf.dim.utils.UserUtils;

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
		mAdapter = new ConversationListViewAdapter(mContext, DataAccessUtils.getConversationList(mContext, UserUtils.my().getId()));
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Conversation conversation = (Conversation) mAdapter.getItem(position);
				switch (conversation.getType()) {
					case ConversationType.FRIEND_CHAT: {
						mContext.startActivity(ChatActivity.createIntent(mContext, UserUtils.findFriend(conversation.getId())));
						break;
					}
					case ConversationType.GROUP_CHAT: {
						mContext.startActivity(ChatActivity.createIntent(mContext, GroupUtils.findGroup(conversation.getId())));
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
	public void onConversationEvent(Conversation conversation) {
		updateList(conversation);
	}

}

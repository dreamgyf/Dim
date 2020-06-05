package com.dreamgyf.dim.conversation.view;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.dreamgyf.dim.ChatActivity;
import com.dreamgyf.dim.R;
import com.dreamgyf.dim.adapter.MessagePageListViewAdapter;
import com.dreamgyf.dim.data.StaticData;
import com.dreamgyf.dim.entity.Conversation;

public class ConversationView implements IConversationView {

	private Context mContext;

	private View mView;

	private ListView mListView;

	public ConversationView(Context context) {
		this.mContext = context;
		init();
	}

	private void init() {
		mView = LayoutInflater.from(mContext).inflate(R.layout.main_viewpager_message,null,false);
		mListView = mView.findViewById(R.id.listview);
		mListView.setAdapter(new MessagePageListViewAdapter(mContext));
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Conversation conversation = StaticData.conversationList.get(position);
				Intent intent = new Intent(mContext, ChatActivity.class);
				if(conversation.getGroup() != null)
					intent.putExtra("group",conversation.getGroup());
				else
					intent.putExtra("user",conversation.getUser());
				mContext.startActivity(intent);
			}
		});
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

package com.dreamgyf.dim.bizpage.conversation.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dreamgyf.dim.R;
import com.dreamgyf.dim.entity.Conversation;
import com.dreamgyf.dim.sharedpreferences.DataAccessUtils;
import com.dreamgyf.dim.utils.UserUtils;
import com.dreamgyf.dim.utils.imageloader.ImageLoader;

import java.util.LinkedList;

public class ConversationListViewAdapter extends BaseAdapter {

	static class ViewHolder {
		ImageView avatar;
		TextView title;
		TextView subtitle;
	}

	private Context context;

	private final LinkedList<Conversation> list;

	public ConversationListViewAdapter(Context context, LinkedList<Conversation> list) {
		super();
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.listview_conversation_item, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.avatar = convertView.findViewById(R.id.avatar);
			viewHolder.title = convertView.findViewById(R.id.name);
			viewHolder.subtitle = convertView.findViewById(R.id.message);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		Conversation conversation = list.get(position);
		ImageLoader.with((Activity) context).loadAvatar(conversation.getAvatarId()).into(viewHolder.avatar);
		viewHolder.title.setText(conversation.getTitle());
		viewHolder.subtitle.setText(conversation.getSubtitle());
		return convertView;
	}

	public void update(Conversation conversation) {
		synchronized (list) {
			for (int i = 0; i < list.size(); i++) {
				if (conversation.getType() == list.get(i).getType() && conversation.getId() == list.get(i).getId()) {
					list.remove(i);
					break;
				}
			}
			list.add(0, conversation);
			notifyDataSetChanged();
		}
	}

	public void sync2Local() {
		DataAccessUtils.saveConversationList(context, UserUtils.my().getId(), list);
	}
}

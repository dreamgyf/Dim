package com.dreamgyf.dim.chat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dreamgyf.dim.R;
import com.dreamgyf.dim.asynctask.GetAvatarTask;
import com.dreamgyf.dim.base.enums.MessageType;
import com.dreamgyf.dim.database.entity.UserMessage;
import com.dreamgyf.dim.entity.httpresp.User;

import java.util.ArrayList;
import java.util.List;

public class UserMessageRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private Context context;

	private User user;

	private final List<UserMessage> mMessageList = new ArrayList<>();

	public UserMessageRecyclerViewAdapter(Context context, User user) {
		super();
		this.context = context;
		this.user = user;
	}

	@Override
	public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
		switch (getItemViewType(position)) {
			case R.layout.recycler_view_message_send_text_item:
			case R.layout.recycler_view_message_receive_text_item:
				((TextViewHolder) holder).message.setText(mMessageList.get(position).content);
				//设置头像
				GetAvatarTask getAvatarTask = new GetAvatarTask(context, ((TextViewHolder) holder).avatar);
				getAvatarTask.execute(user.getAvatarId());
				break;
		}
	}

	@NonNull
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(context).inflate(viewType, parent, false);
		switch (viewType) {
			case R.layout.recycler_view_message_send_text_item:
			case R.layout.recycler_view_message_receive_text_item:
				return new TextViewHolder(view);
			default:
				return null;
		}
	}

	@Override
	public int getItemViewType(int position) {
		int type = -1;
		switch (mMessageList.get(position).messageType) {
			case MessageType.RECEIVE_TEXT:
				type = R.layout.recycler_view_message_receive_text_item;
				break;
			case MessageType.SEND_TEXT:
				type = R.layout.recycler_view_message_send_text_item;
				break;
		}
		return type;
	}

	@Override
	public int getItemCount() {
		return mMessageList.size();
	}

	public void addUserMessage(UserMessage userMessage) {
		synchronized (mMessageList) {
			mMessageList.add(userMessage);
			notifyDataSetChanged();
		}
	}

	public void addUserMessage(List<UserMessage> userMessage) {
		synchronized (mMessageList) {
			mMessageList.addAll(userMessage);
			notifyDataSetChanged();
		}
	}

	public void loadUserMessageRecord(List<UserMessage> userMessage) {
		synchronized (mMessageList) {
			mMessageList.addAll(0, userMessage);
			notifyItemRangeInserted(0,userMessage.size());
		}
	}

	private class TextViewHolder extends RecyclerView.ViewHolder {
		private ImageView avatar;
		private TextView message;

		private TextViewHolder(@NonNull View itemView) {
			super(itemView);
			avatar = itemView.findViewById(R.id.avatar);
			message = itemView.findViewById(R.id.message);
		}
	}

}

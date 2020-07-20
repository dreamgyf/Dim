package com.dreamgyf.dim.bizpage.chat.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dreamgyf.dim.R;
import com.dreamgyf.dim.database.entity.GroupMessage;
import com.dreamgyf.dim.database.entity.Message;
import com.dreamgyf.dim.database.entity.UserMessage;
import com.dreamgyf.dim.entity.Contact;
import com.dreamgyf.dim.entity.Friend;
import com.dreamgyf.dim.entity.Group;
import com.dreamgyf.dim.enums.MessageType;
import com.dreamgyf.dim.utils.imageloader.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class MessageRecyclerViewAdapter<T1 extends Contact, T2 extends Message> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private Context context;

	private T1 mContact;

	private final List<T2> mMessageList = new ArrayList<>();

	public MessageRecyclerViewAdapter(Context context, T1 contact) {
		super();
		this.context = context;
		this.mContact = contact;
	}

	@Override
	public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
		T2 message = mMessageList.get(position);
		if (mContact instanceof Friend && message instanceof UserMessage) {
			onBindViewHolder(holder, getItemViewType(position), (Friend) mContact, (UserMessage) message);
		} else if (mContact instanceof Group && message instanceof GroupMessage) {
			onBindViewHolder(holder, getItemViewType(position), (Group) mContact, (GroupMessage) message);
		}

	}

	private void onBindViewHolder(RecyclerView.ViewHolder holder, int viewType, Friend friend, UserMessage message) {
		switch (viewType) {
			case R.layout.recycler_view_message_send_text_item:
			case R.layout.recycler_view_message_receive_text_item:
				ImageLoader.with((Activity) context).loadAvatar(friend.getAvatarId()).into(((MessageRecyclerViewAdapter.TextViewHolder) holder).avatar);
				((MessageRecyclerViewAdapter.TextViewHolder) holder).message.setText(message.content);
				break;
		}
	}

	private void onBindViewHolder(RecyclerView.ViewHolder holder, int viewType, Group group, GroupMessage message) {
		switch (viewType) {
			case R.layout.recycler_view_message_send_text_item:
			case R.layout.recycler_view_message_receive_text_item:
				ImageLoader.with((Activity) context).loadAvatar(0).into(((MessageRecyclerViewAdapter.TextViewHolder) holder).avatar);
				((MessageRecyclerViewAdapter.TextViewHolder) holder).message.setText(message.content);
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
		int messageType = -1;
		T2 message = mMessageList.get(position);
		if (message instanceof UserMessage) {
			messageType = ((UserMessage) message).messageType;
		} else if (message instanceof GroupMessage) {
			messageType = ((GroupMessage) message).messageType;
		}

		switch (messageType) {
			case MessageType.RECEIVE_TEXT:
				return R.layout.recycler_view_message_receive_text_item;
			case MessageType.SEND_TEXT:
				return R.layout.recycler_view_message_send_text_item;
			default:
				return -1;
		}
	}

	@Override
	public int getItemCount() {
		return mMessageList.size();
	}

	private boolean canMessageAdd(T2 message) {
		return (mContact instanceof Friend && message instanceof UserMessage)
				|| (mContact instanceof Group && message instanceof GroupMessage);
	}

	public void addMessage(T2 message) {
		if (canMessageAdd(message)) {
			synchronized (mMessageList) {
				mMessageList.add(message);
				notifyDataSetChanged();
			}
		}
	}

	public void addMessage(List<T2> messageList) {
		synchronized (mMessageList) {
			for (T2 message : messageList) {
				if (canMessageAdd(message)) {
					mMessageList.add(message);
				}
			}
			notifyDataSetChanged();
		}
	}

	public void loadMessage(List<T2> messageList) {
		synchronized (mMessageList) {
			int index = 0;
			for (T2 message : messageList) {
				if (canMessageAdd(message)) {
					mMessageList.add(index++, message);
				}
			}
			notifyItemRangeInserted(0, index);
		}
	}

	private static class TextViewHolder extends RecyclerView.ViewHolder {
		private ImageView avatar;
		private TextView message;

		private TextViewHolder(@NonNull View itemView) {
			super(itemView);
			avatar = itemView.findViewById(R.id.avatar);
			message = itemView.findViewById(R.id.message);
		}
	}
}

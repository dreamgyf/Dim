package com.dreamgyf.dim.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dreamgyf.dim.R;
import com.dreamgyf.dim.data.StaticData;
import com.dreamgyf.dim.entity.Conversation;

public class MessagePageListViewAdapter extends BaseAdapter {

    class ViewHolder {
        ImageView avatar;
        TextView name;
        TextView message;
    }

    private Context context;

    public MessagePageListViewAdapter(Context context) {
        super();
        this.context = context;
    }

    @Override
    public int getCount() {
        return StaticData.conversationList.size();
    }

    @Override
    public Object getItem(int position) {
        return StaticData.conversationList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.listview_message_page,null);
            viewHolder = new ViewHolder();
            viewHolder.avatar = convertView.findViewById(R.id.avatar);
            viewHolder.name = convertView.findViewById(R.id.name);
            viewHolder.message = convertView.findViewById(R.id.message);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        synchronized (StaticData.conversationListLock) {
            Conversation conversation = StaticData.conversationList.get(position);
            String username;
            if(conversation.getUser().getRemark() != null) {
                username = conversation.getUser().getRemark();
            }
            else if(conversation.getUser().getNickname() != null) {
                username = conversation.getUser().getNickname();
            }
            else {
                username = conversation.getUser().getUsername();
            }
            if(conversation.getGroup() != null) {
                viewHolder.name.setText(conversation.getGroup().getName());
                viewHolder.message.setText(username + ":" + conversation.getCurrentMessage());
            }
            else {
                viewHolder.name.setText(username);
                viewHolder.message.setText(conversation.getCurrentMessage());
            }
        }
        return convertView;
    }

    public void addConversation(Conversation conversation) {
        synchronized (StaticData.conversationListLock) {
            for(int i = 0;i < StaticData.conversationList.size();i++) {
                if(conversation.getUser().getId().equals(StaticData.conversationList.get(i).getUser().getId())) {
                    StaticData.conversationList.remove(i);
                    break;
                }
            }
            StaticData.conversationList.add(0,conversation);
        }
        notifyDataSetChanged();
    }
}

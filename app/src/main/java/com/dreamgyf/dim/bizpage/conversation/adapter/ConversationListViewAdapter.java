package com.dreamgyf.dim.bizpage.conversation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dreamgyf.dim.R;
import com.dreamgyf.dim.asynctask.GetAvatarTask;
import com.dreamgyf.dim.base.enums.ChatType;
import com.dreamgyf.dim.entity.Conversation;
import com.dreamgyf.dim.sharedpreferences.DataAccessUtils;
import com.dreamgyf.dim.utils.NameUtils;

import java.util.LinkedList;

public class ConversationListViewAdapter extends BaseAdapter {

    class ViewHolder {
        ImageView avatar;
        TextView name;
        TextView message;
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
        if(convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.listview_conversation_item,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.avatar = convertView.findViewById(R.id.avatar);
            viewHolder.name = convertView.findViewById(R.id.name);
            viewHolder.message = convertView.findViewById(R.id.message);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Conversation conversation = list.get(position);
        switch (conversation.getType()) {
            case ChatType.USER: {
                //设置头像
                GetAvatarTask getAvatarTask = new GetAvatarTask(context,viewHolder.avatar);
                getAvatarTask.execute(conversation.getFriend().getAvatarId());
                viewHolder.name.setText(NameUtils.getUsername(conversation.getFriend()));
                viewHolder.message.setText(conversation.getCurrentMessage());
                break;
            }
            case ChatType.GROUP: {
                viewHolder.name.setText(conversation.getGroup().getName());
                viewHolder.message.setText(NameUtils.getUsername(conversation.getFriend()) + ":" + conversation.getCurrentMessage());
                break;
            }
        }
        return convertView;
    }

    public void update(Conversation conversation) {
        synchronized (list) {
            switch (conversation.getType()) {
                case ChatType.USER: {
                    for(int i = 0;i < list.size();i++) {
                        if(conversation.getFriend().getId() == list.get(i).getFriend().getId()) {
                            list.remove(i);
                            break;
                        }
                    }
                    list.add(0,conversation);
                    break;
                }
                case ChatType.GROUP: {
                    break;
                }
            }
        }
        notifyDataSetChanged();
    }

    public void sync2Local() {
        DataAccessUtils.saveConversationList(context,list);
    }
}

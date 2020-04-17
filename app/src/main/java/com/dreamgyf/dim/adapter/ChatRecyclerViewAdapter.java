package com.dreamgyf.dim.adapter;

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
import com.dreamgyf.dim.data.StaticData;
import com.dreamgyf.dim.entity.Group;
import com.dreamgyf.dim.entity.Message;
import com.dreamgyf.dim.entity.User;

import java.util.List;

public class ChatRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static class Type {
        public static final int FRIEND = 0;
        public static final int GROUP = 1;
    }

    private Context context;

    private User user;

    private Group group;

    public ChatRecyclerViewAdapter(Context context, User user) {
        super();
        this.context = context;
        this.user = user;
    }

    public ChatRecyclerViewAdapter(Context context, Group group) {
        super();
        this.context = context;
        this.group = group;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case Message.Type.SEND_TEXT:
            case Message.Type.RECEIVE_TEXT:
                if(user != null) {
                    ((TextViewHolder) holder).message.setText(StaticData.friendMessageMap.get(user.getId()).get(position).getContent());
                    //设置头像
                    GetAvatarTask getAvatarTask = new GetAvatarTask(context,((TextViewHolder) holder).avatar);
                    getAvatarTask.execute(user.getAvatarId());
                }
                else {
                    ((TextViewHolder) holder).message.setText(StaticData.groupMessageMap.get(group.getId()).get(position).getContent());
                }
                break;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(viewType,parent,false);
        switch (viewType) {
            case Message.Type.SEND_TEXT:
            case Message.Type.RECEIVE_TEXT:
                return new TextViewHolder(view);
            default:
                return null;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(user != null) {
            List<Message> messageList = StaticData.friendMessageMap.get(user.getId());
            return messageList.get(position).getType();
        }
        else if(group != null) {
            List<Message> messageList = StaticData.groupMessageMap.get(group.getId());
            return messageList.get(position).getType();
        }
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        if(user != null)
            return StaticData.friendMessageMap.get(user.getId()).size();
        else if(group != null)
            return StaticData.groupMessageMap.get(group.getId()).size();
        return 0;
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

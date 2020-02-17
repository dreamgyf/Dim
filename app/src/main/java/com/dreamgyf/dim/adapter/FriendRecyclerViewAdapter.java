package com.dreamgyf.dim.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dreamgyf.dim.ChatActivity;
import com.dreamgyf.dim.R;
import com.dreamgyf.dim.data.StaticData;

public class FriendRecyclerViewAdapter extends RecyclerView.Adapter<FriendRecyclerViewAdapter.ViewHolder> {

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView avatar;
        private TextView name;
        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.avatar);
            name = itemView.findViewById(R.id.username);
        }
    }

    public FriendRecyclerViewAdapter() {
        super();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_friend_item,null,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String username;
        if(StaticData.friendList.get(position).getRemarkName() != null) {
            username = StaticData.friendList.get(position).getRemarkName();
        }
        else if(StaticData.friendList.get(position).getNickname() != null) {
            username = StaticData.friendList.get(position).getNickname();
        }
        else {
            username = StaticData.friendList.get(position).getUsername();
        }
        holder.name.setText(username);
        //点击事件
        holder.itemView.setOnClickListener((view) -> {
            Intent intent = new Intent(view.getContext(), ChatActivity.class);
            intent.putExtra("user",StaticData.friendList.get(position));
            view.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return StaticData.friendList.size();
    }
}

package com.dreamgyf.dim.bizpage.contacts.adapter;

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
import com.dreamgyf.dim.bizpage.userinfo.UserInfoActivity;
import com.dreamgyf.dim.data.StaticData;
import com.dreamgyf.dim.utils.imageloader.ImageLoader;

public class FriendRecyclerViewAdapter extends RecyclerView.Adapter<FriendRecyclerViewAdapter.ViewHolder> {

    private Context context;

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView avatar;
        private TextView name;
        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.avatar);
            name = itemView.findViewById(R.id.username);
        }
    }

    public FriendRecyclerViewAdapter(Context context) {
        super();
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_view_friend_item,parent,false);
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
        //设置头像
        ImageLoader.with((Activity) context).loadAvatar(StaticData.friendList.get(position).getAvatarId()).into(holder.avatar);
        //点击事件
        holder.itemView.setOnClickListener((view) ->
                context.startActivity(UserInfoActivity.createIntent(context, StaticData.friendList.get(position))));
    }

    @Override
    public int getItemCount() {
        return StaticData.friendList.size();
    }
}

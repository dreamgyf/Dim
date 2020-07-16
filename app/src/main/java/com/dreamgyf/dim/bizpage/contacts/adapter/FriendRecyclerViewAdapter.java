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
import com.dreamgyf.dim.entity.Friend;
import com.dreamgyf.dim.utils.NameUtils;
import com.dreamgyf.dim.utils.UserUtils;
import com.dreamgyf.dim.utils.imageloader.ImageLoader;

public class FriendRecyclerViewAdapter extends RecyclerView.Adapter<FriendRecyclerViewAdapter.ViewHolder> {

    private Context context;

    static class ViewHolder extends RecyclerView.ViewHolder {
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
        Friend friend = UserUtils.getFriend(position);
        ImageLoader.with((Activity) context).loadAvatar(friend.getAvatarId()).into(holder.avatar);
        holder.name.setText(NameUtils.getUsername(friend));
        holder.itemView.setOnClickListener((view) ->
                context.startActivity(UserInfoActivity.createIntent(context, friend)));
    }

    @Override
    public int getItemCount() {
        return UserUtils.friendCount();
    }
}

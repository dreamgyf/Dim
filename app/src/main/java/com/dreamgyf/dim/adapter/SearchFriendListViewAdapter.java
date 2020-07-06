package com.dreamgyf.dim.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dreamgyf.dim.R;
import com.dreamgyf.dim.asynctask.GetAvatarTask;
import com.dreamgyf.dim.entity.Friend;

import java.util.List;

public class SearchFriendListViewAdapter extends BaseAdapter {

    private Context context;

    private List<Friend> dataList;

    public SearchFriendListViewAdapter(Context context, List<Friend> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    private class ViewHolder {
        private ImageView avatar;
        private TextView userId;
        private TextView username;
        private TextView nickname;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.listview_search_friend_item,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.avatar = convertView.findViewById(R.id.avatar);
            viewHolder.userId = convertView.findViewById(R.id.userid);
            viewHolder.nickname = convertView.findViewById(R.id.nickname);
            viewHolder.username = convertView.findViewById(R.id.username);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        GetAvatarTask getAvatarTask = new GetAvatarTask(context,viewHolder.avatar);
        getAvatarTask.execute(dataList.get(position).getAvatarId());
        viewHolder.userId.setText("(ID:" + dataList.get(position).getId() + ")");
        viewHolder.nickname.setText(dataList.get(position).getNickname());
        viewHolder.username.setText(dataList.get(position).getUsername());
        return convertView;
    }
}

package com.dreamgyf.dim.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dreamgyf.dim.R;
import com.dreamgyf.dim.entity.User;

import java.util.List;

public class SearchFriendListViewAdapter extends BaseAdapter {

    private List<User> dataList;

    public SearchFriendListViewAdapter(List<User> dataList) {
        this.dataList = dataList;
    }

    private class ViewHolder {
        public TextView userid;
        public TextView username;
        public TextView nickname;
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
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_search_friend_item,null,false);
            viewHolder = new ViewHolder();
            viewHolder.userid = convertView.findViewById(R.id.userid);
            viewHolder.nickname = convertView.findViewById(R.id.nickname);
            viewHolder.username = convertView.findViewById(R.id.username);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.userid.setText("(ID:" + dataList.get(position).getId() + ")");
        viewHolder.nickname.setText(dataList.get(position).getNickname());
        viewHolder.username.setText(dataList.get(position).getUsername());
        return convertView;
    }
}

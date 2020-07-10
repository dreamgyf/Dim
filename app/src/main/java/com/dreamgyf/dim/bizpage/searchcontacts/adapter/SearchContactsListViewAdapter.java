package com.dreamgyf.dim.bizpage.searchcontacts.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dreamgyf.dim.R;
import com.dreamgyf.dim.entity.User;
import com.dreamgyf.dim.utils.imageloader.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class SearchContactsListViewAdapter extends BaseAdapter {

	public static class Type {
		public static final int FRIEND = 0;
		public static final int GROUP = 1;
	}

	private Context mContext;

	private int mType;

	private List mDataList;

	public SearchContactsListViewAdapter(Context context, int type) {
		this.mContext = context;
		mType = type;
		mDataList = new ArrayList();
	}

	private static class SearchViewHolder {

	}

	private static class SearchFriendViewHolder extends SearchViewHolder {
		private ImageView avatar;
		private TextView userId;
		private TextView username;
		private TextView nickname;
	}

	private static class SearchGroupViewHolder extends SearchViewHolder {

	}

	@Override
	public int getCount() {
		return mDataList.size();
	}

	@Override
	public Object getItem(int position) {
		return mDataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		SearchViewHolder viewHolder = null;
		if(convertView == null) {
			if(mType == Type.FRIEND) {
				convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_search_friend_item,parent,false);
			} else if(mType == Type.GROUP) {

			}
			viewHolder = mType == Type.FRIEND ? createSearchFriendViewHolder(convertView) : createSearchGroupViewHolder();
			convertView.setTag(viewHolder);
		}
		else {
			viewHolder = (SearchViewHolder) convertView.getTag();
		}
		if(mType == Type.FRIEND) {
			renderSearchFriendView((SearchFriendViewHolder) viewHolder, (User) mDataList.get(position));
		} else if(mType == Type.GROUP) {
			renderSearchGroupView((SearchGroupViewHolder) viewHolder, position);
		}
		return convertView;
	}

	private SearchFriendViewHolder createSearchFriendViewHolder(View convertView) {
		SearchFriendViewHolder viewHolder = new SearchFriendViewHolder();
		viewHolder.avatar = convertView.findViewById(R.id.avatar);
		viewHolder.userId = convertView.findViewById(R.id.userid);
		viewHolder.nickname = convertView.findViewById(R.id.nickname);
		viewHolder.username = convertView.findViewById(R.id.username);
		return viewHolder;
	}

	private SearchGroupViewHolder createSearchGroupViewHolder() {
		SearchGroupViewHolder viewHolder = new SearchGroupViewHolder();
		return viewHolder;
	}

	private void renderSearchFriendView(SearchFriendViewHolder viewHolder, User user) {
		List<User> list = mDataList;
		ImageLoader.with((Activity) mContext).loadAvatar(user.getAvatarId()).into(viewHolder.avatar);
		viewHolder.userId.setText("(ID:" + user.getId() + ")");
		viewHolder.nickname.setText(user.getNickname());
		viewHolder.username.setText(user.getUsername());
	}

	private void renderSearchGroupView(SearchGroupViewHolder viewHolder, int position) {

	}

	public void setData(List dataList) {
		mDataList.clear();
		mDataList.addAll(dataList);
		notifyDataSetChanged();
	}

}

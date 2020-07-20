package com.dreamgyf.dim.bizpage.userrequest.adapter;

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
import com.dreamgyf.dim.base.http.HttpObserver;
import com.dreamgyf.dim.database.entity.UserRequest;
import com.dreamgyf.dim.entity.User;
import com.dreamgyf.dim.enums.UserRequestStatus;
import com.dreamgyf.dim.utils.NameUtils;
import com.dreamgyf.dim.utils.UserUtils;
import com.dreamgyf.dim.utils.imageloader.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class UserRequestRecyclerViewAdapter extends RecyclerView.Adapter<UserRequestRecyclerViewAdapter.ViewHolder> {

	static class ViewHolder extends RecyclerView.ViewHolder {

		ImageView mIvAvatar;
		TextView mTvTitle;
		TextView mTvSubtitle;
		ViewGroup mContainerOperable;
		TextView mTvAccept;
		TextView mTvRefuse;
		ViewGroup mContainerInoperable;
		TextView mTvStatus;

		public ViewHolder(@NonNull View itemView) {
			super(itemView);
			mIvAvatar = itemView.findViewById(R.id.iv_avatar);
			mTvTitle = itemView.findViewById(R.id.tv_title);
			mTvSubtitle = itemView.findViewById(R.id.tv_subtitle);
			mContainerOperable = itemView.findViewById(R.id.container_operable);
			mTvAccept = itemView.findViewById(R.id.tv_accept);
			mTvRefuse = itemView.findViewById(R.id.tv_refuse);
			mContainerInoperable = itemView.findViewById(R.id.container_inoperable);
			mTvStatus = itemView.findViewById(R.id.tv_status);
		}

	}
	private Context mContext;

	private final List<UserRequest> mRequestList = new ArrayList<>();

	private OnUserRequestItemListener mListener;

	public UserRequestRecyclerViewAdapter(Context context) {
		mContext = context;
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(mContext).inflate(R.layout.recyclerview_user_request_item, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
		UserRequest request = mRequestList.get(position);
		UserUtils.getUser(request.receiverId == UserUtils.my().getId() ? request.senderId : request.receiverId)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new HttpObserver<User>() {
					@Override
					public void onSuccess(User user) throws Throwable {
						ImageLoader.with((Activity) mContext).loadAvatar(user.getAvatarId()).into(holder.mIvAvatar);
						holder.mTvTitle.setText(NameUtils.getUsername(user));
						holder.mTvSubtitle.setText(request.verify);
						switch (request.status) {
							case UserRequestStatus.WAIT: {
								if(request.receiverId == UserUtils.my().getId()) {
									holder.mContainerOperable.setVisibility(View.VISIBLE);
									holder.mContainerInoperable.setVisibility(View.GONE);
								} else {
									holder.mContainerOperable.setVisibility(View.GONE);
									holder.mContainerInoperable.setVisibility(View.VISIBLE);
									holder.mTvStatus.setText("等待验证");
								}
							}
							break;
							case UserRequestStatus.ACCEPT: {
								holder.mContainerOperable.setVisibility(View.GONE);
								holder.mContainerInoperable.setVisibility(View.VISIBLE);
								holder.mTvStatus.setText("已同意");
							}
							break;
							case UserRequestStatus.REFUSE: {
								holder.mContainerOperable.setVisibility(View.GONE);
								holder.mContainerInoperable.setVisibility(View.VISIBLE);
								holder.mTvStatus.setText("已拒绝");
							}
						}
						//点击事件
						holder.itemView.setOnClickListener(v -> {
							if(mListener != null) {
								mListener.onItemClick(holder.itemView, position, request);
							}
						});
						holder.mTvAccept.setOnClickListener(v -> {
							if(mListener != null) {
								mListener.onItemAccept(holder.itemView, position, request);
							}
						});
						holder.mTvRefuse.setOnClickListener(v -> {
							if(mListener != null) {
								mListener.onItemRefuse(holder.itemView, position, request);
							}
						});
					}

					@Override
					public void onFailed(Throwable t) throws Throwable {
						onCaughtThrowable(t);
					}

					@Override
					public void onCaughtThrowable(Throwable t) {
						removeRequest(position);
					}
				});
	}

	@Override
	public int getItemCount() {
		return mRequestList.size();
	}

	public void setOnUserRequestItemListener(OnUserRequestItemListener listener) {
		mListener = listener;
	}

	public void loadRequest(List<UserRequest> list) {
		mRequestList.addAll(0,list);
		notifyItemRangeInserted(0,list.size());
	}

	public void addRequest(UserRequest userRequest) {
		mRequestList.add(userRequest);
	}

	public void removeRequest(int position) {
		mRequestList.remove(position);
	}

	public interface OnUserRequestItemListener {
		void onItemClick(View itemView, int position, UserRequest userRequest);
		void onItemAccept(View itemView, int position, UserRequest userRequest);
		void onItemRefuse(View itemView, int position, UserRequest userRequest);
	}
}

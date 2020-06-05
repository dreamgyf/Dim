package com.dreamgyf.dim.my.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.dreamgyf.dim.R;

public class MyView implements IMyView {

	private Context mContext;

	private View mView;

	public MyView(Context context) {
		this.mContext = context;
		init();
	}

	private void init() {
		mView = LayoutInflater.from(mContext).inflate(R.layout.main_viewpager_my,null,false);
	}

	@Override
	public View getView() {
		return mView;
	}

	@Override
	public String getTitle() {
		return "我";
	}

	@Override
	public void onSelected() {

	}
}

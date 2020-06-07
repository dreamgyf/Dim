package com.dreamgyf.dim.my.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.dreamgyf.dim.R;
import com.dreamgyf.dim.contacts.presenter.ContactsPresenter;
import com.dreamgyf.dim.my.presenter.MyPresenter;

public class MyView implements IMyView {

	private Context mContext;

	private MyPresenter mPresenter;

	private View mView;

	public void bindPresenter(MyPresenter presenter) {
		this.mPresenter = presenter;
	}

	public void init() {
		mContext = mPresenter.getContext();
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

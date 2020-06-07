package com.dreamgyf.dim.my.presenter;

import android.content.Context;

import com.dreamgyf.dim.base.mvp.presenter.BasePresenter;
import com.dreamgyf.dim.my.model.MyModel;
import com.dreamgyf.dim.my.view.MyView;

public class MyPresenter extends BasePresenter<MyModel, MyView> implements IMyPresenter {

	private Context mContext;

	private MyView mView;

	private MyModel mModel;

	public MyPresenter(Context context) {
		super(new MyView());
		this.mContext = context;
	}

	@Override
	protected void onAttach() {
		mView = getView();
		mView.bindPresenter(this);
		mView.init();
	}

	@Override
	protected void onDetach() {

	}

	@Override
	protected MyModel bindModel() {
		return mModel = new MyModel();
	}

	public Context getContext() {
		return mContext;
	}

	@Override
	public void onSelected() {
		mView.onSelected();
	}
}

package com.dreamgyf.dim.my.presenter;

import com.dreamgyf.dim.base.mvp.presenter.BasePresenter;
import com.dreamgyf.dim.my.model.MyModel;
import com.dreamgyf.dim.my.view.MyView;

public class MyPresenter extends BasePresenter<MyModel, MyView> implements IMyPresenter {

	private MyView mView;

	private MyModel mModel;

	public MyPresenter(MyView view) {
		super(view);
	}

	@Override
	protected void onAttach() {
		mView = getView();
	}

	@Override
	protected void onDetach() {

	}

	@Override
	protected MyModel bindModel() {
		return mModel = new MyModel();
	}

	@Override
	public void onSelected() {
		mView.onSelected();
	}
}

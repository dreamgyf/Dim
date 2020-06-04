package com.dreamgyf.dim.base.mvp.presenter;

import com.dreamgyf.dim.base.mvp.model.IBaseModel;
import com.dreamgyf.dim.base.mvp.view.IBaseView;

public abstract class BasePresenter<M extends IBaseModel, V extends IBaseView> implements IBasePresenter<M,V> {

	private M mModel;

	private V mView;

	public BasePresenter(V view) {
		this.mView = view;
	}

	@Override
	public void attach() {
		mModel = bindModel();
		onAttach();
	}

	@Override
	public void detach() {
		onDetach();
	}

	protected abstract void onAttach();

	protected abstract void onDetach();

	protected abstract M bindModel();

	public M getModel() {
		return mModel;
	}

	public V getView() {
		return mView;
	}
}

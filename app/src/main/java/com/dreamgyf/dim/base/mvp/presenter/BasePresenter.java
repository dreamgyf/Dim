package com.dreamgyf.dim.base.mvp.presenter;

import android.content.Context;

import com.dreamgyf.dim.MainApplication;
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

	@Override
	public void onPause() {

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

	public Context getContext() {
		if(mView instanceof Context) {
			return (Context) mView;
		}
		return MainApplication.getInstance().getApplicationContext();
	}
}

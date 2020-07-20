package com.dreamgyf.dim.base.mvp.presenter;

import android.content.Context;

import com.dreamgyf.dim.MainApplication;
import com.dreamgyf.dim.base.mvp.model.IBaseModel;
import com.dreamgyf.dim.base.mvp.view.IBaseView;

import java.lang.ref.WeakReference;

public abstract class BasePresenter<M extends IBaseModel, V extends IBaseView> implements IBasePresenter<M,V> {

	private WeakReference<M> mModelRef;

	private WeakReference<V> mViewRef;

	public BasePresenter(V view) {
		this.mViewRef = new WeakReference<>(view);
	}

	@Override
	public void attach() {
		mModelRef = new WeakReference<>(bindModel());
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
		return mModelRef.get();
	}

	public V getView() {
		return mViewRef.get();
	}

	public Context getContext() {
		if(mViewRef.get() instanceof Context) {
			return (Context) mViewRef.get();
		}
		return MainApplication.getInstance().getApplicationContext();
	}
}

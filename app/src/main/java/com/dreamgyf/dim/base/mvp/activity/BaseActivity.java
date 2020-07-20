package com.dreamgyf.dim.base.mvp.activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dreamgyf.dim.base.mvp.model.IBaseModel;
import com.dreamgyf.dim.base.mvp.presenter.BasePresenter;
import com.dreamgyf.dim.base.mvp.view.IBaseView;

public abstract class BaseActivity<M extends IBaseModel,V extends IBaseView,P extends BasePresenter<M,V>> extends AppCompatActivity implements IBaseView {

	private P mPresenter;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mPresenter = bindPresenter();
		mPresenter.attach();
	}

	@NonNull
	abstract public P bindPresenter();

	public P getPresenter() {
		return mPresenter;
	}

	@Override
	protected void onPause() {
		super.onPause();
		mPresenter.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mPresenter.detach();
		mPresenter = null;
	}
}

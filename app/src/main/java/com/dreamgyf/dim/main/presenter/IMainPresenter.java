package com.dreamgyf.dim.main.presenter;

import android.view.View;

import com.dreamgyf.dim.base.mvp.presenter.IBasePresenter;
import com.dreamgyf.dim.main.model.MainModel;
import com.dreamgyf.dim.main.view.MainActivity;

public interface IMainPresenter extends IBasePresenter<MainModel, MainActivity> {
	View getViewPagerView(int position);

	String getViewPagerTitle(int position);
}

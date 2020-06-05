package com.dreamgyf.dim.base.mvp.presenter;

import com.dreamgyf.dim.base.mvp.model.IBaseModel;
import com.dreamgyf.dim.base.mvp.view.IBaseView;

public interface IBaseViewPagePresenter<M extends IBaseModel, V extends IBaseView> extends IBasePresenter<M, V> {

	void onSelected();
}

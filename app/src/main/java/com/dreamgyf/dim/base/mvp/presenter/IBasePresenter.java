package com.dreamgyf.dim.base.mvp.presenter;

import com.dreamgyf.dim.base.mvp.model.IBaseModel;
import com.dreamgyf.dim.base.mvp.view.IBaseView;

public interface IBasePresenter<M extends IBaseModel, V extends IBaseView> {

	void attach();

	void detach();
}

package com.dreamgyf.dim.base.mvp.view;

import android.view.View;

public interface IBaseViewPagerView extends IBaseView {

	View getView();

	String getTitle();

	void onSelected();
}

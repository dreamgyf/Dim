package com.dreamgyf.dim.main.presenter;

import android.app.Activity;

import com.dreamgyf.dim.base.mqtt.MessageReceiveHandler;
import com.dreamgyf.dim.base.mvp.presenter.BasePresenter;
import com.dreamgyf.dim.main.model.MainModel;
import com.dreamgyf.dim.main.view.MainActivity;

public class MainPresenter extends BasePresenter<MainModel, MainActivity> implements IMainPresenter {

	private Activity mActivity;

	public MainPresenter(MainActivity view) {
		super(view);
	}

	@Override
	protected void onAttach() {
		mActivity = getView();
		//启动消息接收处理器
		MessageReceiveHandler.getInstance().start();
	}

	@Override
	protected void onDetach() {
		MessageReceiveHandler.getInstance().stop();
	}

	@Override
	protected MainModel bindModel() {
		return new MainModel();
	}
}

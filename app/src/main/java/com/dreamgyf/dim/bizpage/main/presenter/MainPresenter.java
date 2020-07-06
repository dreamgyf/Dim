package com.dreamgyf.dim.bizpage.main.presenter;

import android.app.Activity;
import android.view.View;

import com.dreamgyf.dim.MainApplication;
import com.dreamgyf.dim.base.mqtt.MessageReceiveHandler;
import com.dreamgyf.dim.base.mvp.presenter.BasePresenter;
import com.dreamgyf.dim.bizpage.contacts.presenter.ContactsPresenter;
import com.dreamgyf.dim.bizpage.conversation.presenter.ConversationPresenter;
import com.dreamgyf.dim.bizpage.main.model.MainModel;
import com.dreamgyf.dim.bizpage.main.view.MainActivity;
import com.dreamgyf.dim.bizpage.my.presenter.MyPresenter;

public class MainPresenter extends BasePresenter<MainModel, MainActivity> implements IMainPresenter {

	private Activity mActivity;

	private ConversationPresenter mConversationPresenter;

	private ContactsPresenter mContactsPresenter;

	private MyPresenter mMyPresenter;

	public MainPresenter(MainActivity view) {
		super(view);
	}

	@Override
	protected void onAttach() {
		mActivity = getView();
		initViewPager();
		//启动消息接收处理器
		MessageReceiveHandler.getInstance().start();
	}

	@Override
	protected void onDetach() {
		MessageReceiveHandler.getInstance().stop();
		MainApplication.getInstance().getDatabase().close();
		mConversationPresenter.detach();
		mContactsPresenter.detach();
		mMyPresenter.detach();
	}

	@Override
	public void onPause() {
		mConversationPresenter.onPause();
		mContactsPresenter.onPause();
		mMyPresenter.onPause();
	}

	@Override
	protected MainModel bindModel() {
		return new MainModel();
	}

	private void initViewPager() {
		mConversationPresenter = new ConversationPresenter(mActivity);
		mConversationPresenter.attach();
		mContactsPresenter = new ContactsPresenter(mActivity);
		mContactsPresenter.attach();
		mMyPresenter = new MyPresenter(mActivity);
		mMyPresenter.attach();
	}

	@Override
	public View getViewPagerView(int position) {
		switch (position) {
			case 0:
				return mConversationPresenter.getView().getView();
			case 1:
				return mContactsPresenter.getView().getView();
			case 2:
				return mMyPresenter.getView().getView();
		}
		return null;
	}

	@Override
	public String getViewPagerTitle(int position) {
		switch (position) {
			case 0:
				return mConversationPresenter.getView().getTitle();
			case 1:
				return mContactsPresenter.getView().getTitle();
			case 2:
				return mMyPresenter.getView().getTitle();
		}
		return "";
	}

	@Override
	public void onPageSelected(int position) {
		switch (position) {
			case 0:
				mConversationPresenter.onSelected();
				break;
			case 1:
				mContactsPresenter.onSelected();
				break;
			case 2:
				mMyPresenter.onSelected();
				break;
		}
	}
}

package com.dreamgyf.dim.main.presenter;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;

import com.dreamgyf.dim.base.broadcast.BroadcastActions;
import com.dreamgyf.dim.base.mqtt.MessageReceiveHandler;
import com.dreamgyf.dim.base.mvp.presenter.BasePresenter;
import com.dreamgyf.dim.contacts.presenter.ContactsPresenter;
import com.dreamgyf.dim.contacts.view.ContactsView;
import com.dreamgyf.dim.conversation.presenter.ConversationPresenter;
import com.dreamgyf.dim.conversation.view.ConversationView;
import com.dreamgyf.dim.entity.Conversation;
import com.dreamgyf.dim.main.model.MainModel;
import com.dreamgyf.dim.main.view.MainActivity;
import com.dreamgyf.dim.my.presenter.MyPresenter;
import com.dreamgyf.dim.my.view.MyView;

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

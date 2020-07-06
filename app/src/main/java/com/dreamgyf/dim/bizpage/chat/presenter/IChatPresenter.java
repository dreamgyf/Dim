package com.dreamgyf.dim.bizpage.chat.presenter;

import com.dreamgyf.dim.base.mvp.presenter.IBasePresenter;
import com.dreamgyf.dim.bizpage.chat.model.ChatModel;
import com.dreamgyf.dim.bizpage.chat.view.ChatActivity;

public interface IChatPresenter extends IBasePresenter<ChatModel, ChatActivity> {
	int getMessageItemCount();

	void sendMessage(String text);
}

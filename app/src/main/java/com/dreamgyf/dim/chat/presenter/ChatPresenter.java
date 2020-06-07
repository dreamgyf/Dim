package com.dreamgyf.dim.chat.presenter;

import android.app.Activity;

import com.dreamgyf.dim.base.mvp.presenter.BasePresenter;
import com.dreamgyf.dim.chat.model.ChatModel;
import com.dreamgyf.dim.chat.view.ChatActivity;

public class ChatPresenter extends BasePresenter<ChatModel, ChatActivity> implements IChatPresenter {

    private Activity mActivity;

    private ChatModel mModel;

    public ChatPresenter(ChatActivity view) {
        super(view);
    }

    @Override
    protected void onAttach() {
        mActivity = getView();
    }

    @Override
    protected void onDetach() {

    }

    @Override
    protected ChatModel bindModel() {
        return mModel = new ChatModel();
    }
}

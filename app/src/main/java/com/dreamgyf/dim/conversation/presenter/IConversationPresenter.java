package com.dreamgyf.dim.conversation.presenter;

import android.widget.AdapterView;

import com.dreamgyf.dim.base.mvp.presenter.IBaseViewPagePresenter;
import com.dreamgyf.dim.conversation.model.ConversationModel;
import com.dreamgyf.dim.conversation.view.ConversationView;
import com.dreamgyf.dim.entity.Conversation;

public interface IConversationPresenter extends IBaseViewPagePresenter<ConversationModel, ConversationView> {

    void updateList(Conversation conversation);

    void setOnItemClickListener(AdapterView.OnItemClickListener listener);

    void syncConversation();
}

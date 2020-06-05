package com.dreamgyf.dim.conversation.presenter;

import com.dreamgyf.dim.base.mvp.presenter.BasePresenter;
import com.dreamgyf.dim.conversation.model.ConversationModel;
import com.dreamgyf.dim.conversation.view.ConversationView;

public class ConversationPresenter extends BasePresenter<ConversationModel,ConversationView> implements IConversationPresenter {

	private ConversationView mView;

	private ConversationModel mModel;

	public ConversationPresenter(ConversationView view) {
		super(view);
	}

	@Override
	protected void onAttach() {
		mView = getView();
	}

	@Override
	protected void onDetach() {

	}

	@Override
	protected ConversationModel bindModel() {
		return mModel = new ConversationModel();
	}

	@Override
	public void onSelected() {
		mView.onSelected();
	}
}

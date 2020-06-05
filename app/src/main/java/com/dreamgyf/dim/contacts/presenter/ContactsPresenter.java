package com.dreamgyf.dim.contacts.presenter;

import com.dreamgyf.dim.base.mvp.presenter.BasePresenter;
import com.dreamgyf.dim.contacts.model.ContactsModel;
import com.dreamgyf.dim.contacts.view.ContactsView;

public class ContactsPresenter extends BasePresenter<ContactsModel,ContactsView> implements IContactsPresenter {

	private ContactsView mView;

	private ContactsModel mModel;

	public ContactsPresenter(ContactsView view) {
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
	protected ContactsModel bindModel() {
		return mModel = new ContactsModel();
	}

	@Override
	public void onSelected() {
		mView.onSelected();
	}
}

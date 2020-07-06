package com.dreamgyf.dim.bizpage.addcontacts.presenter;

import com.dreamgyf.dim.base.mvp.presenter.BasePresenter;
import com.dreamgyf.dim.bizpage.addcontacts.model.AddContactsModel;
import com.dreamgyf.dim.bizpage.addcontacts.view.AddContactsActivity;
import com.dreamgyf.dim.bizpage.addcontacts.view.IAddContactsView;

public class AddContactsPresenter extends BasePresenter<AddContactsModel, AddContactsActivity> implements IAddContactsPresenter {

	private AddContactsModel mModel;

	private IAddContactsView mView;

	public AddContactsPresenter(AddContactsActivity view) {
		super(view);
		mView = view;
	}

	@Override
	protected void onAttach() {

	}

	@Override
	protected void onDetach() {

	}

	@Override
	protected AddContactsModel bindModel() {
		return mModel = new AddContactsModel();
	}
}

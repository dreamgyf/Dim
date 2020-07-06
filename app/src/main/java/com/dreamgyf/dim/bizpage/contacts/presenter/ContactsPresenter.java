package com.dreamgyf.dim.bizpage.contacts.presenter;

import android.content.Context;

import com.dreamgyf.dim.base.mvp.presenter.BasePresenter;
import com.dreamgyf.dim.bizpage.contacts.adapter.FriendRecyclerViewAdapter;
import com.dreamgyf.dim.bizpage.contacts.model.ContactsModel;
import com.dreamgyf.dim.bizpage.contacts.view.ContactsView;

import androidx.recyclerview.widget.RecyclerView;

public class ContactsPresenter extends BasePresenter<ContactsModel,ContactsView> implements IContactsPresenter {

	private Context mContext;

	private ContactsView mView;

	private ContactsModel mModel;

	private RecyclerView mRecyclerView;

	public ContactsPresenter(Context context) {
		super(new ContactsView());
		this.mContext = context;
	}

	@Override
	protected void onAttach() {
		mView = getView();
		mView.bindPresenter(this);
		mView.init();
	}

	@Override
	protected void onDetach() {

	}

	@Override
	protected ContactsModel bindModel() {
		return mModel = new ContactsModel();
	}

	public Context getContext() {
		return mContext;
	}

	@Override
	public void onSelected() {
		mView.onSelected();
	}

	public void initRecyclerView(RecyclerView recyclerView) {
		this.mRecyclerView = recyclerView;
		mRecyclerView.setAdapter(new FriendRecyclerViewAdapter(mContext));
	}
}

package com.dreamgyf.dim.bizpage.contacts.presenter;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import com.dreamgyf.dim.base.mvp.presenter.BasePresenter;
import com.dreamgyf.dim.bizpage.contacts.adapter.FriendRecyclerViewAdapter;
import com.dreamgyf.dim.bizpage.contacts.model.ContactsModel;
import com.dreamgyf.dim.bizpage.contacts.view.ContactsView;
import com.dreamgyf.dim.eventbus.FriendUpdateEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class ContactsPresenter extends BasePresenter<ContactsModel,ContactsView> implements IContactsPresenter {

	private Context mContext;

	private ContactsView mView;

	private ContactsModel mModel;

	private RecyclerView mRecyclerView;

	private FriendRecyclerViewAdapter mAdapter;

	public ContactsPresenter(Context context) {
		super(new ContactsView());
		this.mContext = context;
	}

	@Override
	protected void onAttach() {
		mView = getView();
		mView.bindPresenter(this);
		mView.init();
		EventBus.getDefault().register(this);
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
		mRecyclerView.setAdapter(mAdapter = new FriendRecyclerViewAdapter(mContext));
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onFriendUpdateEvent(FriendUpdateEvent friendUpdateEvent) {
		mAdapter.notifyDataSetChanged();
	}
}

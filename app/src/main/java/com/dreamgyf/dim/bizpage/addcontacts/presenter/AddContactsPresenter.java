package com.dreamgyf.dim.bizpage.addcontacts.presenter;

import com.dreamgyf.dim.api.service.FriendApiService;
import com.dreamgyf.dim.api.service.GroupApiService;
import com.dreamgyf.dim.base.http.HttpObserver;
import com.dreamgyf.dim.base.mvp.presenter.BasePresenter;
import com.dreamgyf.dim.bizpage.addcontacts.listener.OnSendRequestListener;
import com.dreamgyf.dim.bizpage.addcontacts.model.AddContactsModel;
import com.dreamgyf.dim.bizpage.addcontacts.view.AddContactsActivity;
import com.dreamgyf.dim.bizpage.addcontacts.view.IAddContactsView;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class AddContactsPresenter extends BasePresenter<AddContactsModel, AddContactsActivity> implements IAddContactsPresenter {

	private AddContactsModel mModel;

	private IAddContactsView mView;

	private OnSendRequestListener mOnSendRequestListener;

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

	@Override
	public void setOnSendRequestListener(OnSendRequestListener listener) {
		mOnSendRequestListener = listener;
	}

	@Override
	public void sendFriendRequest(int id, String verifyText, String remarkText) {
		FriendApiService.getInstance()
				.checkIsFriend(id)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new HttpObserver<Boolean>() {
					@Override
					public void onSuccess(Boolean isFriend) throws Throwable {
						if (!isFriend) {
							mModel.setOnSendRequestListener(mOnSendRequestListener);
							mModel.addFriend(id, verifyText, remarkText);
						} else {
							mOnSendRequestListener.onFailure(new Throwable("已经是好友啦"));
						}
					}

					@Override
					public void onFailed(Throwable t) throws Throwable {
						mOnSendRequestListener.onFailure(t);
					}

					@Override
					public void onCaughtThrowable(Throwable t) {
						mOnSendRequestListener.onFailure(t);
					}
				});
	}

	@Override
	public void sendGroupRequest(int id, String verifyText, String remarkText) {
		GroupApiService.getInstance()
				.checkInGroup(id)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new HttpObserver<Boolean>() {
					@Override
					public void onSuccess(Boolean isFriend) throws Throwable {
						if (!isFriend) {
							mModel.setOnSendRequestListener(mOnSendRequestListener);
							mModel.addGroup(id, verifyText, remarkText);
						} else {
							mOnSendRequestListener.onFailure(new Throwable("已经是好友啦"));
						}
					}

					@Override
					public void onFailed(Throwable t) throws Throwable {
						mOnSendRequestListener.onFailure(t);
					}

					@Override
					public void onCaughtThrowable(Throwable t) {
						mOnSendRequestListener.onFailure(t);
					}
				});
	}
}

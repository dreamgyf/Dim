package com.dreamgyf.dim.bizpage.addcontacts.presenter;

import com.dreamgyf.dim.base.mvp.presenter.IBasePresenter;
import com.dreamgyf.dim.bizpage.addcontacts.listener.OnSendRequestListener;
import com.dreamgyf.dim.bizpage.addcontacts.model.AddContactsModel;
import com.dreamgyf.dim.bizpage.addcontacts.view.AddContactsActivity;

public interface IAddContactsPresenter extends IBasePresenter<AddContactsModel, AddContactsActivity> {
	void setOnSendRequestListener(OnSendRequestListener listener);

	void sendFriendRequest(int id, String verifyText, String remarkText);

	void sendGroupRequest(int id, String verifyText, String remarkText);
}

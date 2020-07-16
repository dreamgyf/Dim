package com.dreamgyf.dim.bizpage.addcontacts.model;

import com.dreamgyf.dim.base.mvp.model.IBaseModel;
import com.dreamgyf.dim.bizpage.addcontacts.listener.OnSendRequestListener;

public interface IAddContactsModel extends IBaseModel {
	void setOnSendRequestListener(OnSendRequestListener listener);

	void addFriend(int id, String verifyText, String remarkText);

	void addGroup(int id, String verifyText, String remarkText);
}

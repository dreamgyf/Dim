package com.dreamgyf.dim.bizpage.userrequest.model;

import com.dreamgyf.dim.base.mvp.model.IBaseModel;
import com.dreamgyf.dim.database.entity.UserRequest;

public interface IUserRequestModel extends IBaseModel {
	void setOnUserRequestHandleListener(OnUserRequestHandleListener listener);
	void acceptRequest(UserRequest userRequest, String remarkOfUser);
	void refuseRequest(UserRequest userRequest);

	interface OnUserRequestHandleListener {
		void onAcceptSuccess(int userId);
		void onRefuseSuccess(int userId);
		void onAcceptFailure(int userId);
		void onRefuseFailure(int userId);
	}
}

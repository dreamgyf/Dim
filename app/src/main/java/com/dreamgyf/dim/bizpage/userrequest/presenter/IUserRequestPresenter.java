package com.dreamgyf.dim.bizpage.userrequest.presenter;

import android.view.View;

import com.dreamgyf.dim.base.mvp.presenter.IBasePresenter;
import com.dreamgyf.dim.bizpage.userrequest.model.IUserRequestModel;
import com.dreamgyf.dim.bizpage.userrequest.view.IUserRequestView;
import com.dreamgyf.loadingrecyclerview.LoadingRecyclerView;

public interface IUserRequestPresenter extends IBasePresenter<IUserRequestModel, IUserRequestView> {
	void setOnUserRequestHandleListener(OnUserRequestHandleListener listener);
	void initRecyclerView(LoadingRecyclerView recyclerView);
	int getRequestItemCount();

	interface OnUserRequestHandleListener {
		void onAcceptSuccess(View itemView, int userId);
		void onRefuseSuccess(View itemView, int userId);
		void onAcceptFailure(View itemView, int userId);
		void onRefuseFailure(View itemView, int userId);
	}
}

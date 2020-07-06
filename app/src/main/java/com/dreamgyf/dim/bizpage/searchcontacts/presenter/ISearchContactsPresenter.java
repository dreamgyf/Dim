package com.dreamgyf.dim.bizpage.searchcontacts.presenter;

import android.widget.ListView;

import com.dreamgyf.dim.base.mvp.presenter.IBasePresenter;
import com.dreamgyf.dim.entity.User;
import com.dreamgyf.dim.bizpage.searchcontacts.listener.OnSearchListener;
import com.dreamgyf.dim.bizpage.searchcontacts.model.ISearchContactsModel;
import com.dreamgyf.dim.bizpage.searchcontacts.view.ISearchContactsView;

public interface ISearchContactsPresenter extends IBasePresenter<ISearchContactsModel, ISearchContactsView> {

	void searchFriend(String query);

	void initUserListView(ListView friendListView);

	void setOnSearchFriendListener(OnSearchListener onSearchListener);

	User getUserItem(int position);
}

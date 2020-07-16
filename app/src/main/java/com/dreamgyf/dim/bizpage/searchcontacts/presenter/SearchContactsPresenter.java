package com.dreamgyf.dim.bizpage.searchcontacts.presenter;

import android.widget.ListView;

import com.dreamgyf.dim.base.http.HttpObserver;
import com.dreamgyf.dim.base.mvp.presenter.BasePresenter;
import com.dreamgyf.dim.bizpage.searchcontacts.adapter.SearchContactsListViewAdapter;
import com.dreamgyf.dim.bizpage.searchcontacts.listener.OnSearchListener;
import com.dreamgyf.dim.bizpage.searchcontacts.model.ISearchContactsModel;
import com.dreamgyf.dim.bizpage.searchcontacts.model.SearchContactsModel;
import com.dreamgyf.dim.bizpage.searchcontacts.view.ISearchContactsView;
import com.dreamgyf.dim.entity.User;
import com.dreamgyf.dim.utils.UserUtils;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SearchContactsPresenter extends BasePresenter<ISearchContactsModel, ISearchContactsView> implements ISearchContactsPresenter {

	private ISearchContactsModel mModel;

	private ISearchContactsView mView;

	private ListView mSearchFriendListView;

	private ListView mSearchGroupListView;

	private SearchContactsListViewAdapter mSearchFriendAdapter;

	private SearchContactsListViewAdapter mSearchGroupAdapter;

	private OnSearchListener mOnSearchFriendListener;

	public SearchContactsPresenter(ISearchContactsView view) {
		super(view);
	}

	@Override
	protected ISearchContactsModel bindModel() {
		return mModel = new SearchContactsModel();
	}

	@Override
	protected void onAttach() {
		mView = getView();
	}

	@Override
	protected void onDetach() {

	}

	@Override
	public void initUserListView(ListView friendListView) {
		mSearchFriendListView = friendListView;
		friendListView.setAdapter(mSearchFriendAdapter = new SearchContactsListViewAdapter(getContext(), SearchContactsListViewAdapter.Type.FRIEND));
	}

	@Override
	public void searchFriend(String query) {
		mModel.searchFriend(UserUtils.my().getId(),query)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new HttpObserver<List<User>>() {
					@Override
					public void onSuccess(List<User> userEntities) throws Throwable {
						mSearchFriendAdapter.setData(userEntities);
						if(mOnSearchFriendListener != null) {
							mOnSearchFriendListener.onSuccess();
						}
					}

					@Override
					public void onFailed(Throwable t) throws Throwable {
						if(mOnSearchFriendListener != null) {
							mOnSearchFriendListener.onFailure();
						}
					}

					@Override
					public void onCaughtThrowable(Throwable t) {
						if(mOnSearchFriendListener != null) {
							mOnSearchFriendListener.onFailure();
						}
					}
				});
	}

	@Override
	public void setOnSearchFriendListener(OnSearchListener onSearchListener) {
		mOnSearchFriendListener = onSearchListener;
	}

	@Override
	public User getUserItem(int position) {
		return (User) mSearchFriendAdapter.getItem(position);
	}
}

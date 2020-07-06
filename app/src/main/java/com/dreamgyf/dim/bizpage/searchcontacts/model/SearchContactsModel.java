package com.dreamgyf.dim.bizpage.searchcontacts.model;

import com.dreamgyf.dim.bizpage.searchcontacts.api.SearchContactsApiService;
import com.dreamgyf.dim.entity.User;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;

public class SearchContactsModel implements ISearchContactsModel {

	@Override
	public Observable<List<User>> searchFriend(int myId, String query) {
		return SearchContactsApiService.getInstance().searchUser(myId, query);
	}
}

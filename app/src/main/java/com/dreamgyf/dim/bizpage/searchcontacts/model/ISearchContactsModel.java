package com.dreamgyf.dim.bizpage.searchcontacts.model;

import com.dreamgyf.dim.base.mvp.model.IBaseModel;
import com.dreamgyf.dim.entity.User;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;

public interface ISearchContactsModel extends IBaseModel {
	Observable<List<User>> searchFriend(int id, String query);
}

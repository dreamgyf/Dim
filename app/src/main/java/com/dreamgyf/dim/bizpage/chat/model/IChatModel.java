package com.dreamgyf.dim.bizpage.chat.model;

import com.dreamgyf.dim.base.mvp.model.IBaseModel;

public interface IChatModel extends IBaseModel {
	void sendMessage(int chatType, int toId, String text) throws Exception;
}

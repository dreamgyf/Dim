package com.dreamgyf.dim.chat.presenter;

import android.app.Activity;

import androidx.recyclerview.widget.RecyclerView;

import com.dreamgyf.dim.MainApplication;
import com.dreamgyf.dim.base.enums.ChatType;
import com.dreamgyf.dim.base.mvp.presenter.BasePresenter;
import com.dreamgyf.dim.chat.adapter.GroupMessageRecyclerViewAdapter;
import com.dreamgyf.dim.chat.adapter.UserMessageRecyclerViewAdapter;
import com.dreamgyf.dim.chat.listener.OnMessageReceivedListener;
import com.dreamgyf.dim.chat.listener.OnMessageSendListener;
import com.dreamgyf.dim.chat.model.ChatModel;
import com.dreamgyf.dim.chat.view.ChatActivity;
import com.dreamgyf.dim.data.StaticData;
import com.dreamgyf.dim.database.entity.Message;
import com.dreamgyf.dim.database.entity.UserMessage;
import com.dreamgyf.dim.entity.Conversation;
import com.dreamgyf.dim.entity.Group;
import com.dreamgyf.dim.entity.httpresp.User;
import com.dreamgyf.dim.eventbus.event.ConversationEvent;
import com.dreamgyf.dim.eventbus.event.UserMessageEvent;
import com.dreamgyf.dim.framework.loadingrecyclerview.LoadingRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ChatPresenter extends BasePresenter<ChatModel, ChatActivity> implements IChatPresenter {

	private Activity mActivity;

	private ChatModel mModel;

	private LoadingRecyclerView mRecyclerView;

	private RecyclerView.Adapter mAdapter;

	private int mType;

	private User mUser;

	private Group mGroup;

	private OnMessageReceivedListener mOnMessageReceivedListener;

	private EventBus mEventBus = EventBus.getDefault();

	public ChatPresenter(ChatActivity view) {
		super(view);
	}

	@Override
	protected void onAttach() {
		mActivity = getView();
		mEventBus.register(this);
		mModel.setOnMessageSendListener(new OnMessageSendListener() {
			@Override
			public void onSendSuccess(int chatType, Message message) {
				//更新聊天数据
				if (chatType == ChatType.USER) {
					//更新会话
					Conversation conversation = new Conversation();
					conversation.setType(ChatType.USER);
					conversation.setUser(mUser);
					conversation.setCurrentMessage(((UserMessage) message).content);
					EventBus.getDefault().post(new ConversationEvent(conversation));
					//更新聊天页面
					MainApplication.getInstance().getDatabase().userMessageDao().insertUserMessage((UserMessage) message);
					mEventBus.post(new UserMessageEvent((UserMessage) message));
				}
			}

			@Override
			public void onSendError(Throwable t) {

			}
		});
	}

	@Override
	protected void onDetach() {
		mEventBus.unregister(this);
	}

	@Override
	protected ChatModel bindModel() {
		return mModel = new ChatModel();
	}

	public void initRecyclerView(LoadingRecyclerView recyclerView) {
		this.mRecyclerView = recyclerView;
		mType = getView().getType();
		if (mType == ChatType.USER) {
			mUser = getView().getUser();
			mRecyclerView.setAdapter(mAdapter = new UserMessageRecyclerViewAdapter(mActivity, mUser));
		} else if (mType == ChatType.GROUP) {
			mGroup = getView().getGroup();
			mRecyclerView.setAdapter(mAdapter = new GroupMessageRecyclerViewAdapter(mActivity, mGroup));
		}
		mRecyclerView.setLoadingListener(new LoadingRecyclerView.LoadingListener() {
			@Override
			public void onLoadMore(int direction, int offset) {
				if(direction == LoadingRecyclerView.Direction.START) {
					loadMessageData(offset,10);
				}
			}
		});
		loadMessageData(0,10);
	}

	private void loadMessageData(int offset,int limit) {
		if (mType == ChatType.USER) {
			Observable.create(new ObservableOnSubscribe<List<UserMessage>>() {
				@Override
				public void subscribe(@NonNull ObservableEmitter<List<UserMessage>> emitter) throws Throwable {
					emitter.onNext(MainApplication.getInstance().getDatabase().userMessageDao().getUserMessageListByOffset(StaticData.my.getId(), mUser.getId(),offset,limit));
					emitter.onComplete();
				}
			})
					.subscribeOn(Schedulers.io())
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(new Observer<List<UserMessage>>() {
						@Override
						public void onSubscribe(@NonNull Disposable d) {

						}

						@Override
						public void onNext(@NonNull List<UserMessage> userMessages) {
							((UserMessageRecyclerViewAdapter) mAdapter).loadUserMessageRecord(userMessages);
							if(userMessages.size() < limit) {
								mRecyclerView.disableLoad();
							}
						}

						@Override
						public void onError(@NonNull Throwable e) {

						}

						@Override
						public void onComplete() {
							mRecyclerView.onLoadFinish();
						}
					});
		}
	}

	public void addUserMessage(UserMessage userMessage) {
		if (mType == ChatType.USER && userMessage.myId == StaticData.my.getId() && userMessage.userId == mUser.getId()) {
			if (mAdapter instanceof UserMessageRecyclerViewAdapter) {
				((UserMessageRecyclerViewAdapter) mAdapter).addUserMessage(userMessage);
				if (mOnMessageReceivedListener != null) {
					mOnMessageReceivedListener.onReceived(userMessage.content);
				}
			}
		}
	}

	@Override
	public int getMessageItemCount() {
		return mAdapter.getItemCount();
	}

	@Override
	public void sendMessage(String text) {
		mModel.sendMessage(mType, mType == ChatType.USER ? mUser.getId() : mGroup.getId(), text);
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onUserMessageEvent(UserMessageEvent event) {
		UserMessage userMessage = event.getUserMessage();
		addUserMessage(userMessage);
	}

	public void setOnMessageReceivedListener(OnMessageReceivedListener onMessageReceivedListener) {
		mOnMessageReceivedListener = onMessageReceivedListener;
	}
}

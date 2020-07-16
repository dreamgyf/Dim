package com.dreamgyf.dim.bizpage.chat.presenter;

import com.dreamgyf.dim.MainApplication;
import com.dreamgyf.dim.base.mvp.presenter.BasePresenter;
import com.dreamgyf.dim.bizpage.chat.adapter.MessageRecyclerViewAdapter;
import com.dreamgyf.dim.bizpage.chat.listener.OnMessageReceivedListener;
import com.dreamgyf.dim.bizpage.chat.listener.OnMessageSendListener;
import com.dreamgyf.dim.bizpage.chat.model.ChatModel;
import com.dreamgyf.dim.bizpage.chat.view.ChatActivity;
import com.dreamgyf.dim.bizpage.chat.view.IChatView;
import com.dreamgyf.dim.database.entity.GroupMessage;
import com.dreamgyf.dim.database.entity.Message;
import com.dreamgyf.dim.database.entity.UserMessage;
import com.dreamgyf.dim.entity.Conversation;
import com.dreamgyf.dim.entity.Friend;
import com.dreamgyf.dim.entity.Group;
import com.dreamgyf.dim.enums.ChatType;
import com.dreamgyf.dim.enums.ConversationType;
import com.dreamgyf.dim.utils.NameUtils;
import com.dreamgyf.dim.utils.UserUtils;
import com.dreamgyf.loadingrecyclerview.LoadingRecyclerView;

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

	private IChatView mView;

	private ChatModel mModel;

	private LoadingRecyclerView mRecyclerView;

	private MessageRecyclerViewAdapter mAdapter;

	private int mType;

	private Friend mFriend;

	private Group mGroup;

	private OnMessageReceivedListener mOnMessageReceivedListener;

	private EventBus mEventBus = EventBus.getDefault();

	public ChatPresenter(ChatActivity view) {
		super(view);
	}

	@Override
	protected void onAttach() {
		mView = getView();
		mEventBus.register(this);
		mModel.setOnMessageSendListener(new OnMessageSendListener() {
			@Override
			public void onSendSuccess(int chatType, Message message) {
				//更新聊天数据
				if (chatType == ChatType.USER) {
					//更新会话
					Conversation conversation = new Conversation();
					conversation.setType(ConversationType.FRIEND_CHAT);
					conversation.setId(mFriend.getId());
					conversation.setAvatarId(mFriend.getAvatarId());
					conversation.setTitle(NameUtils.getUsername(mFriend));
					conversation.setSubtitle(((UserMessage) message).content);
					EventBus.getDefault().post(conversation);
					//更新聊天页面
					MainApplication.getInstance().getDatabase().userMessageDao().insertUserMessage((UserMessage) message);
					mEventBus.post((UserMessage) message);
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
			mFriend = getView().getFriend();
			mRecyclerView.setAdapter(mAdapter = new MessageRecyclerViewAdapter<Friend, UserMessage>(getContext(), mFriend));
		} else if (mType == ChatType.GROUP) {
			mGroup = getView().getGroup();
			mRecyclerView.setAdapter(mAdapter = new MessageRecyclerViewAdapter<Group, GroupMessage>(getContext(), mGroup));
		}
		mRecyclerView.setLoadingListener(new LoadingRecyclerView.LoadingListener() {
			@Override
			public void onLoadMore(int direction, int offset) {
				if (direction == LoadingRecyclerView.Direction.START) {
					loadMessageData(offset, 10);
				}
			}
		});
		loadMessageData(0, 10);
	}

	private void loadMessageData(int offset, int limit) {
		if (mType == ChatType.USER) {
			Observable.create(new ObservableOnSubscribe<List<UserMessage>>() {
				@Override
				public void subscribe(@NonNull ObservableEmitter<List<UserMessage>> emitter) throws Throwable {
					emitter.onNext(MainApplication.getInstance().getDatabase().userMessageDao().getUserMessageListByOffset(UserUtils.my().getId(), mFriend.getId(), offset, limit));
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
							mAdapter.loadUserMessageRecord(userMessages);
							if(offset == 0) {
								mView.scrollToBottom(false);
							}
							if (userMessages.size() < limit) {
								mRecyclerView.disableLoad(LoadingRecyclerView.Direction.START);
							}
						}

						@Override
						public void onError(@NonNull Throwable e) {

						}

						@Override
						public void onComplete() {
							mRecyclerView.loadFinish(LoadingRecyclerView.Direction.START);
						}
					});
		}
	}

	public void addUserMessage(UserMessage userMessage) {
		if (mType == ChatType.USER && userMessage.myId == UserUtils.my().getId() && userMessage.userId == mFriend.getId()) {
			mAdapter.addMessage(userMessage);
			if (mOnMessageReceivedListener != null) {
				mOnMessageReceivedListener.onReceived(userMessage.content);
			}
		}
	}

	public void addGroupMessage(GroupMessage groupMessage) {
		if (mType == ChatType.USER && groupMessage.myId == UserUtils.my().getId() && groupMessage.groupId == mGroup.getId()) {
			mAdapter.addMessage(groupMessage);
			if (mOnMessageReceivedListener != null) {
				mOnMessageReceivedListener.onReceived(groupMessage.content);
			}
		}
	}

	@Override
	public int getMessageItemCount() {
		return mAdapter.getItemCount();
	}

	@Override
	public void sendMessage(String text) {
		mModel.sendMessage(mType, mType == ChatType.USER ? mFriend.getId() : mGroup.getId(), text);
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onUserMessageEvent(UserMessage userMessage) {
		addUserMessage(userMessage);
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onGroupMessageEvent(GroupMessage groupMessage) {
		addGroupMessage(groupMessage);
	}

	public void setOnMessageReceivedListener(OnMessageReceivedListener onMessageReceivedListener) {
		mOnMessageReceivedListener = onMessageReceivedListener;
	}
}

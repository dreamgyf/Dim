package com.dreamgyf.dim.bizpage.userrequest.presenter;

import android.content.DialogInterface;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;

import com.dreamgyf.dim.MainApplication;
import com.dreamgyf.dim.base.mvp.presenter.BasePresenter;
import com.dreamgyf.dim.bizpage.userrequest.adapter.UserRequestRecyclerViewAdapter;
import com.dreamgyf.dim.bizpage.userrequest.model.IUserRequestModel;
import com.dreamgyf.dim.bizpage.userrequest.model.UserRequestModel;
import com.dreamgyf.dim.bizpage.userrequest.view.IUserRequestView;
import com.dreamgyf.dim.database.entity.UserRequest;
import com.dreamgyf.dim.utils.UserUtils;
import com.dreamgyf.loadingrecyclerview.LoadingRecyclerView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class UserRequestPresenter extends BasePresenter<IUserRequestModel, IUserRequestView> implements IUserRequestPresenter {

	private IUserRequestModel mModel;

	private IUserRequestView mView;

	private final static int ITEM_LIMIT = 10;

	private LoadingRecyclerView mRvUserRequest;

	private UserRequestRecyclerViewAdapter mAdapter;

	private EditText mEtRemark = new EditText(getContext());

	private AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(getContext())
			.setTitle("设置备注")
			.setView(mEtRemark);

	private OnUserRequestHandleListener mOnUserRequestHandleListener;

	public UserRequestPresenter(IUserRequestView view) {
		super(view);
	}

	@Override
	protected void onAttach() {
		mModel = getModel();
		mView = getView();
	}

	@Override
	protected void onDetach() {

	}

	@Override
	protected IUserRequestModel bindModel() {
		return new UserRequestModel();
	}

	@Override
	public void initRecyclerView(LoadingRecyclerView recyclerView) {
		mRvUserRequest = recyclerView;
		mRvUserRequest.setAdapter(mAdapter = new UserRequestRecyclerViewAdapter(getContext()));
		mAdapter.setOnUserRequestItemListener(new UserRequestRecyclerViewAdapter.OnUserRequestItemListener() {
			@Override
			public void onItemClick(View itemView, int position, UserRequest userRequest) {

			}

			@Override
			public void onItemAccept(View itemView, int position, UserRequest userRequest) {
				mDialogBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						acceptRequest(itemView, userRequest, mEtRemark.getText().toString());
					}
				}).setNegativeButton("跳过", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						acceptRequest(itemView, userRequest, "");
					}
				}).show();
			}

			@Override
			public void onItemRefuse(View itemView, int position, UserRequest userRequest) {
				refuseRequest(itemView, userRequest);
			}
		});
		mRvUserRequest.setLoadingListener((direction, offset) -> {
			if (direction == LoadingRecyclerView.Direction.START) {
				loadRequest(offset, ITEM_LIMIT);
			}
		});
		loadRequest(0, ITEM_LIMIT);
	}

	private void acceptRequest(View itemView, UserRequest userRequest, String remarkOfUser) {
		mModel.setOnUserRequestHandleListener(new IUserRequestModel.OnUserRequestHandleListener() {
			@Override
			public void onAcceptSuccess(int userId) {
				if(mOnUserRequestHandleListener != null) {
					mOnUserRequestHandleListener.onAcceptSuccess(itemView, userId);
				}
			}

			@Override
			public void onRefuseSuccess(int userId) {
				if(mOnUserRequestHandleListener != null) {
					mOnUserRequestHandleListener.onRefuseSuccess(itemView, userId);
				}
			}

			@Override
			public void onAcceptFailure(int userId) {
				if(mOnUserRequestHandleListener != null) {
					mOnUserRequestHandleListener.onAcceptFailure(itemView, userId);
				}
			}

			@Override
			public void onRefuseFailure(int userId) {
				if(mOnUserRequestHandleListener != null) {
					mOnUserRequestHandleListener.onRefuseFailure(itemView, userId);
				}
			}
		});
		mModel.acceptRequest(userRequest, remarkOfUser);
	}

	private void refuseRequest(View itemView, UserRequest userRequest) {
		mModel.setOnUserRequestHandleListener(new IUserRequestModel.OnUserRequestHandleListener() {
			@Override
			public void onAcceptSuccess(int userId) {
				if(mOnUserRequestHandleListener != null) {
					mOnUserRequestHandleListener.onAcceptSuccess(itemView, userId);
				}
			}

			@Override
			public void onRefuseSuccess(int userId) {
				if(mOnUserRequestHandleListener != null) {
					mOnUserRequestHandleListener.onRefuseSuccess(itemView, userId);
				}
			}

			@Override
			public void onAcceptFailure(int userId) {
				if(mOnUserRequestHandleListener != null) {
					mOnUserRequestHandleListener.onAcceptFailure(itemView, userId);
				}
			}

			@Override
			public void onRefuseFailure(int userId) {
				if(mOnUserRequestHandleListener != null) {
					mOnUserRequestHandleListener.onRefuseFailure(itemView, userId);
				}
			}
		});
		mModel.refuseRequest(userRequest);
	}

	@Override
	public void setOnUserRequestHandleListener(OnUserRequestHandleListener listener) {
		mOnUserRequestHandleListener = listener;
	}

	private void loadRequest(int offset, int limit) {
		Observable.<List<UserRequest>>create(emitter -> {
			emitter.onNext(MainApplication.getInstance().getDatabase().userRequestDao()
					.getUserRequestListByOffset(UserUtils.my().getId(), offset, limit));
			emitter.onComplete();
		})
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Observer<List<UserRequest>>() {
					@Override
					public void onSubscribe(@NonNull Disposable d) {

					}

					@Override
					public void onNext(@NonNull List<UserRequest> list) {
						mAdapter.loadRequest(list);
						if (offset == 0) {
							mView.scrollToBottom(false);
						}
						if (list.size() < limit) {
							mRvUserRequest.disableLoad(LoadingRecyclerView.Direction.START);
						}
					}

					@Override
					public void onError(@NonNull Throwable e) {

					}

					@Override
					public void onComplete() {
						mRvUserRequest.loadFinish(LoadingRecyclerView.Direction.START);
					}
				});
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onUserRequestEvent(UserRequest userRequest) {
		mAdapter.addRequest(userRequest);
	}

	@Override
	public int getRequestItemCount() {
		return mAdapter.getItemCount();
	}
}

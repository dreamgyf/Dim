package com.dreamgyf.dim.utils.imageloader;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.dreamgyf.dim.base.http.HttpObserver;
import com.dreamgyf.dim.cache.disk.AvatarDiskCache;
import com.dreamgyf.dim.cache.memory.AvatarMemoryCache;
import com.dreamgyf.dim.data.StaticData;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class AvatarLoader {

	private ImageLoader mImageLoader;

	private Observable<Bitmap> mObservable;

	AvatarLoader(ImageLoader imageLoader) {
		mImageLoader = imageLoader;
	}

	AvatarLoader load(int id) {
		Observable observable = Observable.create((emitter) -> {
			emitter.onNext(AvatarMemoryCache.getAvatar(id));
		})
				.onErrorResumeNext((t) -> {
					Log.i("AvatarLoader", t.getMessage());
					return Observable.create((emitter) -> {
						Bitmap avatar = AvatarDiskCache.getAvatar(mImageLoader.getContext(), id);
						AvatarMemoryCache.saveAvatar(id, avatar);
						emitter.onNext(avatar);
					});
				})
				.onErrorResumeNext((t) -> {
					Log.i("AvatarLoader", t.getMessage());
					return Observable.create(emitter -> {
						URL url = new URL(StaticData.DOMAIN + "/image/avatar/get/" + id);
						HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
						if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
							InputStream is = httpURLConnection.getInputStream();
							Bitmap avatar = AvatarDiskCache.saveAvatar(mImageLoader.getContext(), id, is);
							AvatarMemoryCache.saveAvatar(id, avatar);
							emitter.onNext(avatar);
						}
					});
				});
		mObservable = observable;
		return this;
	}

	public void into(ImageView imageView) {
		mObservable.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new HttpObserver<Bitmap>() {
					@Override
					public void onSubscribe(@NonNull Disposable d) {
						mImageLoader.addLifecycleListener(new ImageLoaderLifecycleListener() {
							@Override
							public void onStart() {

							}

							@Override
							public void onResume() {

							}

							@Override
							public void onPause() {

							}

							@Override
							public void onStop() {

							}

							@Override
							public void onDestroy() {
								if(d != null && !d.isDisposed()) {
									d.dispose();
								}
								Log.i("Observable","取消订阅");
							}
						});
					}

					@Override
					public void onSuccess(Bitmap bitmap) throws Throwable {
						imageView.setImageBitmap(bitmap);
					}

					@Override
					public void onFailed(Throwable t) throws Throwable {
						Log.i("AvatarLoader", "avatar load failed, message:" + t.getMessage());
					}

					@Override
					public void onCaughtThrowable(Throwable t) {
						Log.i("AvatarLoader", "avatar load failed, message:" + t.getMessage());
					}
				});
	}
}

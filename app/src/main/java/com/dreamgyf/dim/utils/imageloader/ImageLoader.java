package com.dreamgyf.dim.utils.imageloader;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.rxjava3.plugins.RxJavaPlugins;

public class ImageLoader {

	private final static Map<FragmentManager, ImageLoaderFragment> imageLoaderFragments = new HashMap<>();

	private final static Map<ImageLoaderFragment, ImageLoader> imageLoaders = new HashMap<>();

	private ImageLoaderFragment mImageLoaderFragment;

	private Context mContext;

	private ImageLoader(Context context) {
		mContext = context;
	}

	public static ImageLoader with(Activity activity) {
		if(activity == null) {
			throw new IllegalArgumentException("You cannot start a load on a null Context");
		}
		FragmentManager fm = activity.getFragmentManager();
		ImageLoaderFragment imageLoaderFragment = (ImageLoaderFragment) fm.findFragmentByTag(ImageLoaderFragment.TAG);
		if(imageLoaderFragment == null) {
			imageLoaderFragment = imageLoaderFragments.get(fm);
			if(imageLoaderFragment == null) {
				imageLoaderFragment = new ImageLoaderFragment();
				fm.beginTransaction().add(imageLoaderFragment, ImageLoaderFragment.TAG).commitAllowingStateLoss();
				imageLoaderFragments.put(fm, imageLoaderFragment);
			}
		}

		ImageLoader imageLoader = imageLoaders.get(imageLoaderFragment);
		if(imageLoader == null) {
			imageLoader = new ImageLoader(activity);
			imageLoader.mImageLoaderFragment = imageLoaderFragment;
			ImageLoaderFragment finalImageLoaderFragment = imageLoaderFragment;
			imageLoader.addLifecycleListener(new ImageLoaderLifecycleListener() {
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
					imageLoaders.remove(finalImageLoaderFragment);
					imageLoaderFragments.remove(fm);
				}
			});
			imageLoaders.put(imageLoaderFragment, imageLoader);
		}
		if(RxJavaPlugins.getErrorHandler() == null) {
			RxJavaPlugins.setErrorHandler((t) -> {});
		}
		return imageLoader;
	}

	public Context getContext() {
		return mContext;
	}

	void addLifecycleListener(ImageLoaderLifecycleListener listener) {
		mImageLoaderFragment.addLifecycleListener(listener);
	}

	void removeLifecycleListener(ImageLoaderLifecycleListener listener) {
		mImageLoaderFragment.removeLifecycleListener(listener);
	}

	public AvatarLoader loadAvatar(int id) {
		AvatarLoader avatarLoader = new AvatarLoader(this);
		return avatarLoader.load(id);
	}

}

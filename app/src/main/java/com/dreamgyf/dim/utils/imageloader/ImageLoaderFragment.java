package com.dreamgyf.dim.utils.imageloader;

import android.app.Fragment;
import android.util.Log;

public class ImageLoaderFragment extends Fragment {

	static final String TAG = "com.dreamgyf.imageloader.fragment";

	private ImageLoaderLifecycle mImageLoaderLifecycle = new ImageLoaderLifecycle();

	public ImageLoaderLifecycle getImageLoaderLifecycle() {
		return mImageLoaderLifecycle;
	}

	public void addLifecycleListener(ImageLoaderLifecycleListener listener) {
		mImageLoaderLifecycle.addListener(listener);
	}

	public void removeLifecycleListener(ImageLoaderLifecycleListener listener) {
		mImageLoaderLifecycle.removeListener(listener);
	}

	@Override
	public void onStart() {
		super.onStart();
		if(mImageLoaderLifecycle != null) {
			mImageLoaderLifecycle.onStart();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if(mImageLoaderLifecycle != null) {
			mImageLoaderLifecycle.onResume();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if(mImageLoaderLifecycle != null) {
			mImageLoaderLifecycle.onPause();
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		if(mImageLoaderLifecycle != null) {
			mImageLoaderLifecycle.onStop();
		}
	}

	@Override
	public void onDestroy() {
		Log.i("ImageLoader", "Activity被销毁");
		super.onDestroy();
		if(mImageLoaderLifecycle != null) {
			mImageLoaderLifecycle.onDestroy();
		}
	}
}

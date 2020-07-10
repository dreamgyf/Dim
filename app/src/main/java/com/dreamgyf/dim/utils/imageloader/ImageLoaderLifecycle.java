package com.dreamgyf.dim.utils.imageloader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class ImageLoaderLifecycle {

	private List<ImageLoaderLifecycleListener> mListeners = Collections.synchronizedList(new ArrayList<>());

	void addListener(ImageLoaderLifecycleListener listener) {
		mListeners.add(listener);
	}

	void removeListener(ImageLoaderLifecycleListener listener) {
		mListeners.remove(listener);
	}

	void onStart() {
		for(ImageLoaderLifecycleListener listener : mListeners) {
			listener.onStart();
		}
	}

	void onResume() {
		for(ImageLoaderLifecycleListener listener : mListeners) {
			listener.onResume();
		}
	}

	void onPause() {
		for(ImageLoaderLifecycleListener listener : mListeners) {
			listener.onPause();
		}
	}

	void onStop() {
		for(ImageLoaderLifecycleListener listener : mListeners) {
			listener.onStop();
		}
	}

	void onDestroy() {
		for(ImageLoaderLifecycleListener listener : mListeners) {
			listener.onDestroy();
		}
	}
}

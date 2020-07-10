package com.dreamgyf.dim.utils.imageloader;

interface ImageLoaderLifecycleListener {
	void onStart();
	void onResume();
	void onPause();
	void onStop();
	void onDestroy();
}

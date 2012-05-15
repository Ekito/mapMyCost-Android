package com.ekito.mapmycost;

import android.app.Application;
import android.httpimage.FileSystemPersistence;
import android.httpimage.HttpImageManager;

public class MMCApplication extends Application {

	public static final String BASEDIR = "/sdcard/MapMyCost/cache";

	@Override
	public void onCreate() {
		super.onCreate();

		// init HttpImageManager manager.
		mHttpImageManager = new HttpImageManager(new FileSystemPersistence(BASEDIR));
	}


	public HttpImageManager getHttpImageManager() {
		return mHttpImageManager;
	}


	//////PRIVATE
	private HttpImageManager mHttpImageManager; 
}

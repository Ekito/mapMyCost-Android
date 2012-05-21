package com.ekito.mapmycost;

import com.zubhium.ZubhiumSDK;
import com.zubhium.ZubhiumSDK.CrashReportingMode;

import android.app.Application;
import android.httpimage.FileSystemPersistence;
import android.httpimage.HttpImageManager;

public class MMCApplication extends Application {

	public static final String BASEDIR = "/sdcard/MapMyCost/cache";

	@Override
	public void onCreate() {
		super.onCreate();

		// init zubhium
		ZubhiumSDK sdk = ZubhiumSDK.getZubhiumSDKInstance(this, "2a1ae0d4b7bd023858cc8518858694");
		//  overide the ugly Force Close message and improve user experience
		sdk.setCrashReportingMode(CrashReportingMode.SILENT);
		
		// init HttpImageManager manager.
		mHttpImageManager = new HttpImageManager(new FileSystemPersistence(BASEDIR));
	}


	public HttpImageManager getHttpImageManager() {
		return mHttpImageManager;
	}

	public void clearCache() {
		new FileSystemPersistence(BASEDIR).clear();
	}

	//////PRIVATE
	private HttpImageManager mHttpImageManager; 
}

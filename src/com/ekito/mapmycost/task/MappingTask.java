package com.ekito.mapmycost.task;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.ekito.mapmycost.communication.RequestHandler;
import com.ekito.mapmycost.communication.RestClient;

public class MappingTask extends AsyncTask<String, String, String> {
	
	private static final String TAG = "SendPictureTask";
	
	private String mId;
	private Float mLatitude;
	private Float mLongitude;
	private Bitmap mBmp;
	private RequestHandler mHandler;
	
	public MappingTask(String id, Float latitude, Float longitude, Bitmap bmp, RequestHandler handler) {
		mId = id;
		mLatitude = latitude;
		mLongitude = longitude;
		mBmp = bmp;
		mHandler = handler;
	}

	@Override
	protected String doInBackground(String... params) {

		RestClient.putMapping(mId, mLatitude, mLongitude, mBmp, mHandler);

		// free bitmap memory to avoid
		// OutOfMemoryException
		mBmp.recycle();
		mBmp = null;
		System.gc();
		
		return null;
	}
}

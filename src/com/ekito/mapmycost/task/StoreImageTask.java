package com.ekito.mapmycost.task;

import android.os.AsyncTask;

import com.ekito.mapmycost.activity.CameraActivity;
import com.ekito.mapmycost.tools.FileManager;

public class StoreImageTask extends AsyncTask<byte[], String, String> {
	
	CameraActivity mContext;
	
	public StoreImageTask(CameraActivity context) {
		mContext = context;
	}

	@Override
	protected String doInBackground(byte[]... params) {
		return FileManager.saveImageToAlbum(mContext,params[0]);
	}

	@Override
	protected void onPostExecute(String result) {
		mContext.shotCallback(result);
		super.onPostExecute(result);
	}
}

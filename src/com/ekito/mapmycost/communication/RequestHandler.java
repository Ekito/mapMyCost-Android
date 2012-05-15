package com.ekito.mapmycost.communication;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;

public abstract class RequestHandler extends JsonHttpResponseHandler {

	private static String TAG = "RequestHandler";

	private Context mContext;
	private ProgressDialog progress;
	private String mMessage;

	public RequestHandler(Context context, String message) {
		mContext = context;
		mMessage = message;
		progress = new ProgressDialog(mContext);
	}

	@Override
	public void onFailure(Throwable arg0, String arg1) {
		super.onFailure(arg0, arg1);

		Log.e(TAG,"request error");

		progress.dismiss();
	}

	@Override
	public void onSuccess(JSONArray arg0) {
		progress.dismiss();
		super.onSuccess(arg0);
	}
	
	@Override
	public void onSuccess(JSONObject arg0) {
		progress.dismiss();
		super.onSuccess(arg0);
	}
	
	
	@Override
	public void onSuccess(String arg0) {
		progress.dismiss();
		super.onSuccess(arg0);
	}

	@Override
	public void onStart() {
		super.onStart();

		progress.setMessage(mMessage);
		progress.setCancelable(false);
		progress.show();
	}
}

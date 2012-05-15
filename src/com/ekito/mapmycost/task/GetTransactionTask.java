package com.ekito.mapmycost.task;

import com.ekito.mapmycost.communication.RequestHandler;
import com.ekito.mapmycost.communication.RestClient;

import android.os.AsyncTask;

public class GetTransactionTask extends AsyncTask<String, String, String> {
	
	private RequestHandler mHandler;
	
	public GetTransactionTask(RequestHandler handler) {
		mHandler = handler;
	}

	@Override
	protected String doInBackground(String... params) {
		RestClient.getTransaction(params[0],mHandler);
		return null;
	}
}

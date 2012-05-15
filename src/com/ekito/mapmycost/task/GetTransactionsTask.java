package com.ekito.mapmycost.task;

import com.ekito.mapmycost.communication.RequestHandler;
import com.ekito.mapmycost.communication.RestClient;

import android.os.AsyncTask;

public class GetTransactionsTask extends AsyncTask<String, String, String> {
	
	private RequestHandler mHandler;
	
	public GetTransactionsTask(RequestHandler handler) {
		mHandler = handler;
	}

	@Override
	protected String doInBackground(String... params) {
		RestClient.getTransactions(mHandler);
		return null;
	}
}

package com.ekito.mapmycost.communication;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class RestClient {

	private static final String BASE_URL = "http://mapmycost.herokuapp.com/";

	private static AsyncHttpClient client = new AsyncHttpClient();

	private static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		client.get(getAbsoluteUrl(url), params, responseHandler);
	}

	private static String getAbsoluteUrl(String relativeUrl) {
		return BASE_URL + relativeUrl;
	}
	
	public static void getTransactions(AsyncHttpResponseHandler responseHandler) {
		get("transactions", null, responseHandler);
	}
}

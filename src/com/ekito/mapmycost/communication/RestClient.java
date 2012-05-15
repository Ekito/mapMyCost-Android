package com.ekito.mapmycost.communication;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class RestClient {

	public static final String BASE_URL = "http://mapmycost.herokuapp.com";

	private static AsyncHttpClient client = new AsyncHttpClient();

	private static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		client.get(getAbsoluteUrl(url), params, responseHandler);
	}
	
	private static void put(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		client.put(getAbsoluteUrl(url), params, responseHandler);
	}

	private static String getAbsoluteUrl(String relativeUrl) {
		return BASE_URL + relativeUrl;
	}
	
	public static void getTransactions(AsyncHttpResponseHandler responseHandler) {
		get("/transactions", null, responseHandler);
	}

	public static void getTransaction(String id, AsyncHttpResponseHandler responseHandler) {
		get("/transactions/"+id, null, responseHandler);
	}
	
	public static void putMapping(String id, Float latitude, Float longitude, Bitmap picture, RequestHandler handler) {
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		picture.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		
		RequestParams params = new RequestParams();
		params.put("id", id);
		params.put("latitude", Float.toString(latitude));
		params.put("longitude", Float.toString(longitude));
		params.put("picture", new ByteArrayInputStream(baos.toByteArray()));	
		put("/transactions/mapping",params,handler);
	}
}

package com.ekito.mapmycost.activity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.ekito.mapmycost.R;
import com.ekito.mapmycost.communication.RestClient;

public class HeatMapActivity extends SherlockActivity {
	
	WebView mWebView;
	ProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.heat_map);
		
		dialog = new ProgressDialog(this);
		dialog.setMessage("loading heat map");
		dialog.setCancelable(false);
		
		WebViewClient client = new WebViewClient() {
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				dialog.show();
				super.onPageStarted(view, url, favicon);
			}
			@Override
			public void onPageFinished(WebView view, String url) {
				dialog.dismiss();
				super.onPageFinished(view, url);
			}
			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				dialog.dismiss();
				Toast.makeText(HeatMapActivity.this, "A problem occured while loading page", Toast.LENGTH_LONG).show();
				HeatMapActivity.this.finish();
				super.onReceivedError(view, errorCode, description, failingUrl);
			}
		};
		
		mWebView = (WebView) findViewById(R.id.heat_map);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.setWebViewClient(client);
		mWebView.loadUrl(RestClient.BASE_URL+"/phonemap");
	}
}

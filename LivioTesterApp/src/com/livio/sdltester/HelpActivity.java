package com.livio.sdltester;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class HelpActivity extends Activity {

	private static final String ASSET_URL_FORMAT = "file:///android_asset/";
	public static final String EXTRA_ASSET_FILENAME = "com.livio.sdltester.HelpActivity.extraAssetFilename";
	private static final String INDEX_FILENAME = "index.html";
	
	private WebView webView;
	private boolean atIndex = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
		
		ActionBar actionBar = getActionBar();
		if(actionBar != null){
			actionBar.setTitle("SDL Help");
		}
		
		webView = (WebView) findViewById(R.id.wv_help);
		webView.setWebViewClient(new WebViewClient(){
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				if(!url.equals(makeUrl(INDEX_FILENAME))){
					atIndex = false;
				}
				else{
					atIndex = true;
				}
			}

			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				Toast.makeText(HelpActivity.this, "An error occurred.", Toast.LENGTH_LONG).show();
				navigateToIndex();
			}
			
		});
		
		Intent incomingIntent = getIntent();
		if(incomingIntent.hasExtra(EXTRA_ASSET_FILENAME)){
			String fileName = incomingIntent.getStringExtra(EXTRA_ASSET_FILENAME);
			if(fileName != null && fileName.length() > 0){
				navigateToFileName(fileName);
			}
			else{
				navigateToIndex();
			}
		}
		else{
			navigateToIndex();
		}
	}
	
	private void navigateToFileName(String fileName){
		webView.loadUrl(makeUrl(fileName));
	}
	
	private void navigateToIndex(){
		webView.loadUrl(makeUrl(INDEX_FILENAME));
	}
	
	@Override
	public void onBackPressed() {
		if(atIndex){
			finish();
		}
		else{
			navigateToIndex();
		}
	}

	private static String makeUrl(String fileName){
		return new StringBuilder().append(ASSET_URL_FORMAT).append(fileName).toString();
	}

}

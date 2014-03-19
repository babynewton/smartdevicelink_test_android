package com.livio.sdltester;

import java.util.Stack;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class HelpActivity extends Activity {

	public static final String EXTRA_ASSET_FILENAME = "com.livio.sdltester.HelpActivity.extraAssetFilename";
	
	private static final String ASSET_URL_FORMAT = "file:///android_asset/help_docs/html/";
	private static final String INDEX_FILENAME = "index.html";
	
	private WebView webView;
	private Stack<String> urlStack;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
		
		ActionBar actionBar = getActionBar();
		if(actionBar != null){
			actionBar.setTitle("SDL Help");
		}
		
		urlStack = new Stack<String>();
		
		webView = (WebView) findViewById(R.id.wv_help);
		webView.setWebViewClient(new WebViewClient(){
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				urlStack.push(url);
			}

			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				Toast.makeText(HelpActivity.this, "An error occurred.", Toast.LENGTH_LONG).show();
				
				// the current URL didn't work, so remove it from the url stack
				urlStack.pop();
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
		if(urlStack == null || urlStack.size() <= 1){
			// if something went awry with the stack or if we're back at index when back is pressed, we'll just finish the activity
			finish();
		}
		else{
			// since we are going back, we don't need the current URL anymore
			urlStack.pop();
			
			// retrieve the last-visited page (it will be added back to the stack automatically when the page loads)
			String urlToLoad = urlStack.pop();
			webView.loadUrl(urlToLoad);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_help, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		android.view.MenuItem helpHome = (android.view.MenuItem) menu.findItem(R.id.menu_help_home);
		
		// show/hide connect/disconnect menu items
		boolean atIndex = (urlStack == null || urlStack.size() <= 1);
		helpHome.setVisible(!atIndex); // if we're not at the index, show the home button
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		int menuItemId = item.getItemId();
		switch(menuItemId){
		case R.id.menu_help_home:
			urlStack.clear();
			navigateToIndex();
			return true;
		case R.id.menu_help_close:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private static String makeUrl(String fileName){
		return new StringBuilder().append(ASSET_URL_FORMAT).append(fileName).toString();
	}

}

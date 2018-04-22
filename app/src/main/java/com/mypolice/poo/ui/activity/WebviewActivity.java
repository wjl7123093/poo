package com.mypolice.poo.ui.activity;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.mypolice.poo.R;
import com.mypolice.poo.widget.CenterDialog;
import com.mypolice.poo.widget.TitleBarView;
import com.mypolice.poo.widget.WebProgressBar;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * @Title: WebviewActivity.java
 * @Package com.mypolice.poo.ui.activity
 * @Description: Webview页面（便民二级页面 - 内嵌wap）
 * @author wangjl
 * @crdate 2017-9-4
 * @update 
 * @version v2.0.0(2)
 */
@ContentView(R.layout.activity_webview)
public class WebviewActivity extends Activity {
    private final String mPageName = "WebviewActivity";

	/** TitleBarView 顶部标题栏 */
	@ViewInject(R.id.titleWebView)
	private TitleBarView mTitleWebview;

	/** {@link WebProgressBar} 网页加载进度条 */
	@ViewInject(R.id.webProgressBar)
	private WebProgressBar mWebProgressBar;
	/** {@link WebView} 网页浏览控件 */
	@ViewInject(R.id.webView)
	private WebView mWebView;	
	
	private String mTitle = "";
	private String mWebSite = "";
	
	/** 加载进度条 */
    private CenterDialog centerDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		
		Bundle bundle = getIntent().getExtras();
		mTitle = bundle.getString("title");
		mWebSite = bundle.getString("website");
		initView();
		loadWebpage(mWebSite);
	}
	
	/** 初始化视图 */
	private void initView() {
		centerDialog = new CenterDialog(WebviewActivity.this, R.layout.dialog_wap_loading, 
				new int[]{});
		centerDialog.show();

		mTitleWebview.setText(mTitle);
	}
	
	/**
	 * 加载网页
	 * @param website
	 */
	private void loadWebpage(String website) {
		/** 以下属性基本全部设置，可以解决某些网页打不开的问题 */
		mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);//设置js可以直接打开窗口，如window.open()，默认为false
		mWebView.getSettings().setJavaScriptEnabled(true);//是否允许执行js，默认为false。设置true时，会提醒可能造成XSS漏洞
//		mWebView.getSettings().setSupportZoom(true);//是否可以缩放，默认true
//		mWebView.getSettings().setBuiltInZoomControls(true);//是否显示缩放按钮，默认false
//		mWebView.getSettings().setUseWideViewPort(true);//设置此属性，可任意比例缩放。大视图模式
		mWebView.getSettings().setLoadWithOverviewMode(true);//和setUseWideViewPort(true)一起解决网页自适应问题
		mWebView.getSettings().setAppCacheEnabled(true);//是否使用缓存
		mWebView.getSettings().setDomStorageEnabled(true);//DOM Storage
		
		/**
		 * 缓存模式说明: 
      	 * LOAD_CACHE_ONLY: 不使用网络，只读取本地缓存数据
         * LOAD_NO_CACHE: 不使用缓存，只从网络获取数据.
         * LOAD_DEFAULT: （默认）根据cache-control决定是否从网络上取数据。
         * LOAD_CACHE_ELSE_NETWORK，只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据
		 */
		mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		
		mWebView.loadUrl(website);
		mWebView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);
				mWebProgressBar.setProgress(newProgress);
			}
		});
		mWebView.setWebViewClient(new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (null != centerDialog && !centerDialog.isShowing())
					centerDialog.show();
				view.loadUrl(url);
				
				return true;
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				if (null != centerDialog && centerDialog.isShowing())
					centerDialog.cancel();
				
			}

		});
	}
	
}

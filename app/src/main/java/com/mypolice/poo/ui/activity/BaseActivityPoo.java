package com.mypolice.poo.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;

import com.mypolice.poo.R;
import com.mypolice.poo.application.PooApplication;
import com.mypolice.poo.util.CommonFuncUtil;
import com.mypolice.poo.util.GPSUtils;
import com.mypolice.poo.util.StatusBarTools;
import com.umeng.message.PushAgent;

/**
 * @Title: BaseActivity.java
 * @Package com.mypolice.poo.ui.activity
 * @Description: 父窗体UI页面
 * @author wangjl
 * @crdate 2017-8-21
 * @update 2017-9-7
 * @version v2.0.1(3)
 */
public class BaseActivityPoo extends Activity {

	public PooApplication mApplication;
	private BaseActivityPoo mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 沉浸式状态栏
		StatusBarTools.setWindowStatusBarColor(BaseActivityPoo.this,
				getResources().getColor(R.color.app_main_green));

		PushAgent.getInstance(BaseActivityPoo.this).onAppStart();
		if (null == mApplication)
			mApplication = (PooApplication) getApplication();

		mContext = this;
		addActivity();
	}

	/** 初始化试图 */
	public void initView() {
		
	}

	// 添加Activity方法
	public void addActivity() {
		mApplication.addActivity_(mContext);// 调用myApplication的添加Activity方法
	}
	//销毁当个Activity方法
	public void removeActivity() {
		mApplication.removeActivity_(mContext);// 调用myApplication的销毁单个Activity方法
	}
	//销毁所有Activity方法
	public void removeALLActivity() {
		mApplication.removeALLActivity_();// 调用myApplication的销毁所有Activity方法
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 判断是否开启 GPS
//		if (!GPSUtils.isOPen(BaseActivityPoo.this)) {  // 未开启 gps
//			CommonFuncUtil.getToast(BaseActivityPoo.this, "未开启GPS, 请先打开GPS");
//
//			// 跳转到 gps 设置页面
//			Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//			settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			BaseActivityPoo.this.startActivity(settingsIntent);
//		}
	}
}

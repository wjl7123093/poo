package com.mypolice.poo.ui.activity;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.mypolice.poo.R;
import com.mypolice.poo.application.GlobalSet;
import com.mypolice.poo.application.PooApplication;
import com.mypolice.poo.util.CommonFuncUtil;
import com.mypolice.poo.util.NetUtils;
import com.mypolice.poo.util.StatusBarTools;
import com.umeng.message.PushAgent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

/**   
 * @Title: StartActivity.java 
 * @Package com.mypolice.poo.ui.activity
 * @Description: 启动页面
 * @author wangjl  
 * @crdate 2017-8-21
 * @update 2017-9-7
 * @version v2.0.0(2)
 */
//@ContentView(R.layout.activity_start)
public class StartActivity extends AppCompatActivity {

	private PooApplication mApplication;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		ViewUtils.inject(this);
//		//隐藏状态栏
//		//定义全屏参数
//		int flag= WindowManager.LayoutParams.FLAG_FULLSCREEN;
//		//设置当前窗体为全屏显示
//		getWindow().setFlags(flag, flag);

		PushAgent.getInstance(StartActivity.this).onAppStart();
		if (null == mApplication)
			mApplication = (PooApplication) getApplication();

//		mHandler.postDelayed(r, 1000);

		mHandler.post(r);
	}

	private Handler mHandler = new Handler();

	Runnable r = new Runnable() {

		@Override
		public void run() {
			if (mApplication.isLogin()) {
				if (!NetUtils.checkNetworkInfo(StartActivity.this)) {	// 网络不可用
					CommonFuncUtil.getToast(StartActivity.this, GlobalSet.NetError);
//					mApplication.setLogin(false);

					// 跳转到 登录页面
					CommonFuncUtil.goNextActivityWithNoArgs(StartActivity.this,
							LoginActivity.class, true);
				} else {
					CommonFuncUtil.goNextActivityWithNoArgs(StartActivity.this,
							MainActivity.class, true);
				}
			} else {
				CommonFuncUtil.goNextActivityWithNoArgs(StartActivity.this,
						LoginActivity.class, true);
			}
		}
	};

}

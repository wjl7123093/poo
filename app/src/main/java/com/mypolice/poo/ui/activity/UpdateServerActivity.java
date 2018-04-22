package com.mypolice.poo.ui.activity;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mypolice.poo.R;
import com.mypolice.poo.application.GlobalSet;
import com.mypolice.poo.util.CommonFuncUtil;
import com.mypolice.poo.util.RegexUtil;
import com.mypolice.poo.widget.TitleBarView;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**   
 * @Title: UpdateServerActivity.java 
 * @Package com.mypolice.poo.ui.activity
 * @Description: 修改服务器地址页面
 * @author wangjl  
 * @crdate 2017-8-21
 * @update 2017-9-71
 * @version v2.0.0(2)
 */
@ContentView(R.layout.activity_update_server)
public class UpdateServerActivity extends BaseActivityPoo {

	/** TitleBarView 顶部标题栏 */
	@ViewInject(R.id.titleUpdateServer)
	private TitleBarView mTitleUpdateServer;

	/** 原服务器地址 */
	@ViewInject(R.id.tvOldServer)
	private TextView mTvOldServer;
	/** 新服务器地址 */
	@ViewInject(R.id.edtTxtNewServer)
	private EditText mEdtTxtNewServer;
	/** 确认修改按钮 */
	@ViewInject(R.id.btnUpdateServer)
	private Button mBtnUpdate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);

		initView();
	}
	
	@Override
	public void initView() {	
		super.initView();
		mTitleUpdateServer.setText("修改服务器");

		mTvOldServer.setText(mApplication.getAppServer());
	}

	/**
	 * 修改服务器 点击事件
	 * @param v
	 */
	@OnClick(R.id.btnUpdateServer)
	public void onBtnUpdateServerClick(View v) {
//		String oldServer = mEdtTxtOldServer.getText().toString().trim();
		String newServer = mEdtTxtNewServer.getText().toString().trim();
		// 判空
		if (TextUtils.isEmpty(newServer)) {
			CommonFuncUtil.getToast(UpdateServerActivity.this, "服务器地址不能为空");
			return;
		}
		// 正则验证
		if (!RegexUtil.isWebSite(newServer)) {
			CommonFuncUtil.getToast(UpdateServerActivity.this, "服务器地址格式不正确");
			return;
		}

		GlobalSet.APP_SERVER = newServer;
		mApplication.setAppServer(newServer);
		GlobalSet.APP_SERVER_URL = mApplication.getAppServer() + "/api/";
		GlobalSet.APP_IMAGE_URL = mApplication.getAppServer() + "/static/uploads/";
		GlobalSet.APP_DOWNLOAD_URL = mApplication.getAppServer() + "/app/update.xml";
		GlobalSet.APP_DETAIL_URL = mApplication.getAppServer() + "/index/wx/detail.html?id=";
		CommonFuncUtil.getToast(UpdateServerActivity.this, "修改成功");

		removeALLActivity();
		CommonFuncUtil.goNextActivityWithNoArgs(UpdateServerActivity.this, LoginActivity.class, false);

	}
	
}

package com.mypolice.poo.ui.activity;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mypolice.poo.R;
import com.mypolice.poo.application.GlobalSet;
import com.mypolice.poo.bean.AppVersion;
import com.mypolice.poo.bean.DragUserInfo;
import com.mypolice.poo.notification.NotificationActivity;
import com.mypolice.poo.service.KeepLiveService;
import com.mypolice.poo.util.CommonFuncUtil;
import com.mypolice.poo.util.UpdateVersion;
import com.mypolice.poo.widget.CenterDialog;
import com.mypolice.poo.widget.TitleBarView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.raphets.roundimageview.RoundImageView;

import okhttp3.Call;

/**   
 * @Title: PersonalActivity.java 
 * @Package com.mypolice.poo.ui.activity
 * @Description: 个人中心页面
 * @author wangjl  
 * @crdate 2017-8-21
 * @update 2017-11-15
 * @version v2.1.2(14)
 */
@ContentView(R.layout.activity_personal)
public class PersonalActivity extends BaseActivityPoo {

	private UpdateVersion 	mUpdateVersion;
	private UpdateVersion.DownCallBack mDownCallBack;


	@ViewInject(R.id.ivHeaderImg)
	private RoundImageView mIvHeader;
	@ViewInject(R.id.tvUserName)
	private TextView mTvUserName;
	@ViewInject(R.id.tvSecretaryName)
	private TextView mTvSecretaryName;
	@ViewInject(R.id.tvRecoveryName)
	private TextView mTvRecoveryName;
	@ViewInject(R.id.tvCommnuintyName)
	private TextView mTvCommunityName;

	/** Include Layout1 */
	@ViewInject(R.id.layout1)
	private LinearLayout mLayout1;
	/** Include Layout2 */
	@ViewInject(R.id.layout2)
	private LinearLayout mLayout2;
	/** Include Layout3 */
	@ViewInject(R.id.layout3)
	private LinearLayout mLayout3;
	/** Include Layout4 */
	@ViewInject(R.id.layout4)
	private LinearLayout mLayout4;
	/** Include Layout5 测试功能 1 */
	@ViewInject(R.id.layout5)
	private LinearLayout mLayout5;
	/** Include Layout5 测试功能 2 */
	@ViewInject(R.id.layout6)
	private LinearLayout mLayout6;
	/** Include Layout5 测试功能 3 */
	@ViewInject(R.id.layout7)
	private LinearLayout mLayout7;

	@ViewInject(R.id.btnLogout)
	private Button mBtnLogout;

	/** 加载进度条 */
	private CenterDialog centerDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);

		initView();
		getOwnerInfo();
	}
	
	@Override
	public void initView() {
		super.initView();
		initLayout(mLayout1, "修改密码", R.mipmap.ic_update_pwd, false);
//		initLayout(mLayout2, "修改服务器", false);
		initLayout(mLayout3, "关于我们", R.mipmap.ic_about_us, false);
		initLayout(mLayout4, "检查更新", R.mipmap.ic_update_version, true);
//		initLayout(mLayout5, "手写签名", false);	// 测试新功能1（手写签名）
//		initLayout(mLayout6, "消息推送", false);	// 测试新功能2（消息推送）
//		initLayout(mLayout7, "读取通讯录", false);	// 测试新功能3（读取通讯录）
		mLayout5.setVisibility(View.GONE);	// 测试功能1 默认隐藏
		mLayout6.setVisibility(View.GONE);	// 测试功能2 默认隐藏
		mLayout7.setVisibility(View.GONE);	// 测试功能3 默认隐藏
		mLayout2.setVisibility(View.GONE);

		centerDialog = new CenterDialog(PersonalActivity.this, R.layout.dialog_wap_loading,
				new int[]{});
		centerDialog.show();
	}

	/** 修改密码 */
	@OnClick(R.id.layout1)
	public void onLayout1Click(View v) {
		CommonFuncUtil.goNextActivityWithNoArgs(PersonalActivity.this,
				UpdatePwdActivity.class, false);
	}

	/** 修改服务器 */
	@OnClick(R.id.layout2)
	public void onLayout2Click(View v) {
		CommonFuncUtil.goNextActivityWithNoArgs(PersonalActivity.this,
				UpdateServerActivity.class, false);
	}

	/** 检查更新 */
	@OnClick(R.id.layout4)
	public void onLayout3Click(View v) {
//		CommonFuncUtil.goNextActivityWithNoArgs(PersonalActivity.this,
//				CheckVersionActivity.class, false);

		/**
		 * 程序更新
		 */
		// 获取版本信息
		AppVersion appVersion = CommonFuncUtil.getVersion(this);
		// 初始化回调函数
		initcallback();
		mUpdateVersion = new UpdateVersion(this, appVersion.getVersionCode(),
				mDownCallBack);
		// 检查版本
//		String path = getResources().getString(R.string.update_url);
		String path = GlobalSet.APP_DOWNLOAD_URL;
		mUpdateVersion.checkVersion(path);
	}

	/** 关于我们 */
	@OnClick(R.id.layout3)
	public void onLayout4Click(View v) {
		CommonFuncUtil.goNextActivityWithNoArgs(PersonalActivity.this,
				AboutUsActivity.class, false);
//		CommonFuncUtil.goNextActivityWithNoArgs(PersonalActivity.this,
//				SportsActivity.class, false);
	}

	/** 测试新功能1 手写签名 */
	@OnClick(R.id.layout5)
	public void onLayout5Click(View v) {
		// 手写签名
		CommonFuncUtil.goNextActivityWithNoArgs(PersonalActivity.this,
				SignaturepadActivity.class, false);
	}

	/** 测试新功能2 消息推送 */
	@OnClick(R.id.layout6)
	public void onLayout6Click(View v) {
		// 消息推送
		CommonFuncUtil.goNextActivityWithNoArgs(PersonalActivity.this,
				NotificationActivity.class, false);
	}

	/** 测试新功能3 读取通讯录 */
	@OnClick(R.id.layout7)
	public void onLayout7Click(View v) {
		// 读取通讯录
		CommonFuncUtil.goNextActivityWithNoArgs(PersonalActivity.this,
				ContactsListActivity.class, false);
	}

	/** 退出登录 */
	@OnClick(R.id.btnLogout)
	public void onLogoutClick(View v) {
		String acc = mApplication.getAcc();
//		mApplication.clearSharePreferenceData();
		mApplication.setLogin(false);

		// 退出 Activity 栈
		removeALLActivity();
		// 跳转到登陆界面
		CommonFuncUtil.goNextActivityWithNoArgs(PersonalActivity.this, LoginActivity.class, false);

		// 停止服务
		Intent intent = new Intent(PersonalActivity.this, KeepLiveService.class);
		stopService(intent);
	}

	/**
	 * 获取 用户信息
	 */
	private void getOwnerInfo() {
		String url = GlobalSet.APP_SERVER_URL + "drug_user/getDrugUser";
//		String url = GlobalSet.APP_SERVER_URL + "index/getOwnerInfo";
		OkHttpUtils.post().url(url)
				.addParams("token", mApplication.getToken())
				.addParams("drug_user_id", mApplication.getUserID() + "")
				.build()
				.execute(new StringCallback() {
					@Override
					public void onError(Call call, Exception e, int id) {
						centerDialog.cancel();
						CommonFuncUtil.getToast(PersonalActivity.this, e.getMessage());
					}

					@Override
					public void onResponse(String response, int id) {
//						Toast.makeText(PersonalActivity.this, response, Toast.LENGTH_LONG).show();
						centerDialog.cancel();
						try {
							JSONObject jsonResponse = new JSONObject(response);
							if (jsonResponse.getInt("code") == 0
									|| jsonResponse.getInt("code") == 200) {
								DragUserInfo user = JSON.parseObject(jsonResponse.getString("data"), DragUserInfo.class);
//								Toast.makeText(PersonalActivity.this, user.toString(), Toast.LENGTH_LONG).show();
								bindDataToUI(user);
							} else if (jsonResponse.getInt("code") == 1007) {
								// token 失效，踢出当前用户，退到登录页面
								CommonFuncUtil.getToast(PersonalActivity.this,
										"当前用户已在别处登录，请重新登录");
								removeALLActivity();
								CommonFuncUtil.goNextActivityWithNoArgs(PersonalActivity.this,
										LoginActivity.class, false);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
	}

	/**
	 * 初始化功能项视图
	 * @param layout
	 * @param funcName
	 */
	private void initLayout(LinearLayout layout, String funcName, int resId, boolean isShowVersion) {
		ImageView ivFuncItem = (ImageView) layout.findViewById(R.id.iconFuncItem);
		TextView tvFuncName = (TextView) layout.findViewById(R.id.tvFuncName);
		TextView tvVersion = (TextView)layout.findViewById(R.id.tvVersion);
		ivFuncItem.setImageResource(resId);
		tvVersion.setText("V" + CommonFuncUtil
				.getVersion(PersonalActivity.this).getVersionName());
		if(isShowVersion){
			tvVersion.setVisibility(View.VISIBLE);
		}else{
			tvVersion.setVisibility(View.GONE);
		}
		tvFuncName.setText(funcName);
	}

	/**
	 * 绑定数据到 UI
	 * @param user
     */
	private void bindDataToUI(DragUserInfo user) {
		mApplication.mBtimapUtils.display(mIvHeader, GlobalSet.APP_IMAGE_URL + user.getAvatar_url());
		mTvUserName.setText(user.getDrug_name());
		mTvSecretaryName.setText("专干: " + user.getSecretary_name());
		mTvRecoveryName.setText("类别: " + user.getRecovery_name());
		mTvCommunityName.setText("社区: " + user.getCommunity_name());
	}

	/**
	 * 初始化检查更新 回调函数
	 */
	protected void initcallback() {

		mDownCallBack = new UpdateVersion.DownCallBack() {
			@Override
			public void onDownCallBack(int cmd, Object data) {
				// TODO Auto-generated method stub

				switch (cmd) {
					case UpdateVersion.UPDATA_NONEED:
						// 版本号相同无需升级
						CommonFuncUtil.getToast(PersonalActivity.this, "当前已经是最新版 !");
						break;
					case UpdateVersion.UPDATA_CLIENT:
						// 版本号不同更新程序
						// mUpdateVersion.downLoadApk();
						mUpdateVersion.downLoadApk2();
						break;
					case UpdateVersion.GET_UNDATAINFO_ERROR:
						// 服务器超时

						break;
					case UpdateVersion.SDCARD_NOMOUNTED:
						// sdcard不可用

						break;
					case UpdateVersion.DOWN_ERROR:
						// 下载apk失败

						break;
					default:

						break;
				}
			}
		};

	}
	
}

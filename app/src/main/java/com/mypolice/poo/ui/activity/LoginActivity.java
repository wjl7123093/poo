package com.mypolice.poo.ui.activity;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mypolice.poo.R;
import com.mypolice.poo.application.ApiCode;
import com.mypolice.poo.application.GlobalSet;
import com.mypolice.poo.application.PooApplication;
import com.mypolice.poo.bean.UserEntity;
import com.mypolice.poo.util.CommonFuncUtil;
import com.mypolice.poo.util.NetUtils;
import com.mypolice.poo.util.encrypt.RSAUtil;
import com.mypolice.poo.widget.CenterDialog;
import com.umeng.message.PushAgent;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;
import sun.misc.BASE64Encoder;

/**   
 * @Title: LoginActivity.java 
 * @Package com.mypolice.poo.ui.activity
 * @Description: 登陆页面
 * @author wangjl  
 * @crdate 2017-8-21
 * @update 2017-9-9
 * @version v2.0.0(2)
 */
@ContentView(R.layout.activity_login)
public class LoginActivity extends BaseActivityPoo {

	/** Edt 账号 */
	@ViewInject(R.id.edtTxtAcc)
	private EditText mEdtTxtAcc;
	/** Edt 密码 */
	@ViewInject(R.id.edtTxtPwd)
	private EditText mEdtTxtPwd;
	/** Btn 登录 */
	@ViewInject(R.id.btnLogin)
	private Button mBtnLogin;
	/** Btn 修改服务器 */
	@ViewInject(R.id.btnUpdateServer)
	private Button mBtnUpdateServer;

	private String mAcc;
	private String mPwd;

	/** 加载进度条 */
	private SweetAlertDialog pDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		initView();
	}
	
	@Override
	public void initView() {
		super.initView();

//		if (TextUtils.isEmpty(mApplication.getAppServer())) {
//			mApplication.setAppServer(GlobalSet.APP_SERVER);
//		}
//		GlobalSet.APP_SERVER_URL = mApplication.getAppServer() + "/api/";
//		GlobalSet.APP_IMAGE_URL = mApplication.getAppServer() + "/static/uploads/";
//		GlobalSet.APP_DOWNLOAD_URL = mApplication.getAppServer() + "/app/update.xml";
//		GlobalSet.APP_DETAIL_URL = mApplication.getAppServer() + "/index/wx/detail.html?id=";

		mAcc = mApplication.getAcc();
		mEdtTxtAcc.setText(mAcc);

		pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
		pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
		pDialog.setTitleText("正在登录...");
		pDialog.setCancelable(false);
//		pDialog.show();
	}

	/**
	 * 登录
	 * @param v
     */
	@OnClick(R.id.btnLogin)
	public void onLoginClick(View v) {
		mAcc = mEdtTxtAcc.getText().toString().trim();
		mPwd = mEdtTxtPwd.getText().toString().trim();
		if (TextUtils.isEmpty(mAcc) || TextUtils.isEmpty(mPwd)) {
			CommonFuncUtil.getToast(LoginActivity.this, "账号密码不能为空");
			return;
		}
		if (!NetUtils.checkNetworkInfo(LoginActivity.this)) {
			CommonFuncUtil.getToast(LoginActivity.this, GlobalSet.NetError);
			return;
		}

//		String acc = "421221197803141573";
//		String pwd = "123456";
		String loginData = String.format("{\"username\":\"%1$s\","
						+ "\"password\":\"%2$s\"}", mAcc, mPwd);
		String data = "";
		try {
			data = encrypt(loginData);
		} catch (Exception e) {
			Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}

		pDialog.show();
//		doLogin(data);
		doLoginNew(data);

	}

	/**
	 * 跳转到 修改服务器页面
	 * @param v
     */
	@OnClick(R.id.btnUpdateServer)
	public void onUpdateServerClick(View v) {
		CommonFuncUtil.goNextActivityWithNoArgs(LoginActivity.this,
				UpdateServerActivity.class, false);
	}

	/**
	 * 登陆（HTTP）
	 * @param data
     */
	private void doLogin(final String data) {
		String url = GlobalSet.APP_SERVER_URL + "index/login";
		OkHttpUtils.post().url(url)
				.addParams("app_id", GlobalSet.APP_ID)
				.addParams("user", data)
				.addParams("registrationID", PushAgent.getInstance(LoginActivity.this).getRegistrationId() + ",0")	// 0 代表 Android
				.build()
				.execute(new StringCallback() {
					@Override
					public void onError(Call call, Exception e, int id) {
						pDialog.dismiss();

						// http 访问不成功，就访问 https
						doLoginHttps(data);
					}

					@Override
					public void onResponse(String response, int id) {
						pDialog.dismiss();
						try {
							JSONObject jsonResponse = new JSONObject(response);
							if (jsonResponse.getInt("code") == 0
									|| jsonResponse.getInt("code") == 200) {
								loadDataToApplication(mApplication, jsonResponse.getString("data"));
								CommonFuncUtil.goNextActivityWithNoArgs(LoginActivity.this,
										MainActivity.class, true);
							} else {
								CommonFuncUtil.getToast(LoginActivity.this, "登录失败，请检查账号密码");
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
	}

	/**
	 * 登陆（HTTP）
	 * @param data
	 */
	private void doLoginNew(final String data) {
		String url = GlobalSet.APP_SERVER_URL + "app.passport/login";
		OkHttpUtils.post().url(url)
				.addParams("type", GlobalSet.APP_TYPE + "")
				.addParams("user", data)
				.addParams("registrationID", PushAgent.getInstance(LoginActivity.this).getRegistrationId() + ",0")	// 0 代表 Android
				.build()
				.execute(new StringCallback() {
					@Override
					public void onError(Call call, Exception e, int id) {
						pDialog.dismiss();
						CommonFuncUtil.getToast(LoginActivity.this, e.getMessage());

						// http 访问不成功，就访问 https
//						doLoginHttps(data);
					}

					@Override
					public void onResponse(String response, int id) {
						pDialog.dismiss();
						try {
							JSONObject jsonResponse = new JSONObject(response);
							if (jsonResponse.getInt("code") == GlobalSet.APP_SUCCESS) {
								loadDataToApplication(mApplication, jsonResponse.getString("data"));
								CommonFuncUtil.goNextActivityWithNoArgs(LoginActivity.this,
										MainActivity.class, true);
							} else {
								CommonFuncUtil.getToast(LoginActivity.this, "登录失败，请检查账号密码");
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
	}

	/**
	 * 登陆（HTTPS）
	 * @param data
     */
	private void doLoginHttps(String data) {
		String url = GlobalSet.APP_SERVER_URL_HTTPS + "index/login";
		OkHttpUtils.post().url(url)
				.addParams("app_id", GlobalSet.APP_ID)
				.addParams("user", data)
				.addParams("registrationID", PushAgent.getInstance(LoginActivity.this).getRegistrationId() + ",0")	// 0 代表 Android
				.build()
				.execute(new StringCallback() {
					@Override
					public void onError(Call call, Exception e, int id) {
						pDialog.dismiss();
						CommonFuncUtil.getToast(LoginActivity.this, e.getMessage());
					}

					@Override
					public void onResponse(String response, int id) {
						pDialog.dismiss();
						try {
							JSONObject jsonResponse = new JSONObject(response);
							if (jsonResponse.getInt("code") == ApiCode.CODE_SUCCESS) {
								loadDataToApplication(mApplication, jsonResponse.getString("data"));
								CommonFuncUtil.goNextActivityWithNoArgs(LoginActivity.this,
										MainActivity.class, true);

								// 将服务地址 换成 https的
								GlobalSet.APP_SERVER_URL = GlobalSet.APP_SERVER_URL_HTTPS;
								GlobalSet.APP_DETAIL_URL = GlobalSet.APP_DETAIL_URL_HTTPS;
								GlobalSet.APP_IMAGE_URL = GlobalSet.APP_IMAGE_URL_HTTPS;
								GlobalSet.APP_DOWNLOAD_URL = GlobalSet.APP_DOWNLOAD_URL_HTTPS;
							} else {
								CommonFuncUtil.getToast(LoginActivity.this, "登录失败，请检查账号密码");
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
	}

	/**
	 * RSA 加密
	 * @param data 加密字符串
	 * @return
	 * @throws Exception
	 */
	private String encrypt(String data) throws Exception {

//		String s1 ="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCzA+mdwLl/Y/QPy9xTVsvrNt0B7h" +
//				"CVRwu+Abt3ebgQTOMy66iS/zPkl3Cx2H9lAaFl4UkaRN3hrVJ3O70fnzvTR1P7Cx+UXye" +
//				"M1IPew1YTWQtaWIxaKbYxiCpMJsZ5KDs+POFRPF6CDvf6gsCu5QwdauxFbhBTut6ncrOY" +
//				"PCB3GwIDAQAB";
		byte[] bt_cipher = RSAUtil.encrypt(RSAUtil.getPublicKey(
				GlobalSet.APP_SECRET), data.getBytes());
		String encryptStr = new BASE64Encoder().encodeBuffer(bt_cipher);
		System.out.println("加密后："+ encryptStr);
		return encryptStr;
	}

	/**
	 * 加载数据到 Application里保存
	 */
	private void loadDataToApplication(PooApplication application, String data) {
		application.setLogin(true);
		try {
			JSONObject json = new JSONObject(data);
			UserEntity user = JSON.parseObject(json.getString("user"), UserEntity.class);

			application.setToken(json.getString("token"));
			application.setAcc(mAcc);
			application.setPwd(mPwd);
			application.setUserID(user.getUser_id());
			application.setUserName(user.getName());
			application.setAvatarUrl(user.getAvatar_url());
			application.setStatus(user.getStatus());
			application.setSecretary(user.getSecretary());
			application.setCommunity(user.getCommunity());

//			application.setToken(json.getString("ticket"));
//			application.setAcc(mAcc);
//			application.setPwd(mPwd);
//			application.setUserID(json.getInt("id"));
//			application.setUserName(json.getString("name"));
//			application.setGroupID(json.getInt("group"));
//			application.setRoleID(json.getString("role"));
//			application.setMobile(json.getString("mobile"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}


}
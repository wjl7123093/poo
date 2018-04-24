package com.mypolice.poo.ui.activity;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mypolice.poo.R;
import com.mypolice.poo.application.GlobalSet;
import com.mypolice.poo.util.CommonFuncUtil;
import com.mypolice.poo.widget.TitleBarView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;

/**   
 * @Title: UpdatePwdActivity.java 
 * @Package com.mypolice.poo.ui.activity
 * @Description: 修改密码页面
 * @author wangjl  
 * @crdate 2017-8-21
 * @update 
 * @version v2.0.0(2)
 */
@ContentView(R.layout.activity_update_pwd)
public class UpdatePwdActivity extends BaseActivityPoo {

	/** TitleBarView 顶部标题栏 */
	@ViewInject(R.id.titleUpdatePwd)
	private TitleBarView mTitleUpdatePwd;

	/** 原密码 */
	@ViewInject(R.id.edtTxtOldPwd)
	private EditText mEdtTxtOldPwd;
	/** 新密码 */
	@ViewInject(R.id.edtTxtNewPwd)
	private EditText mEdtTxtNewPwd;
	/** 确认新密码 */
	@ViewInject(R.id.edtTxtNewPwd2)
	private EditText mEdtTxtNewPwd2;
	/** 确认修改按钮 */
	@ViewInject(R.id.btnUpdatePwd)
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
		mTitleUpdatePwd.setText("修改密码");
	}

	/**
	 * 修改密码 点击事件
	 * @param v
     */
	@OnClick(R.id.btnUpdatePwd)
	public void onBtnUpdatePwdClick(View v) {
		String oldPwd = mEdtTxtOldPwd.getText().toString().trim();
		String newPwd = mEdtTxtNewPwd.getText().toString().trim();
		String newPwd2 = mEdtTxtNewPwd2.getText().toString().trim();
		if (TextUtils.isEmpty(oldPwd) || TextUtils.isEmpty(newPwd)
				|| TextUtils.isEmpty(newPwd2)) {
			CommonFuncUtil.getToast(UpdatePwdActivity.this, "密码不能为空");
			return;
		} else if (!newPwd.equals(newPwd2)) {
			CommonFuncUtil.getToast(UpdatePwdActivity.this, "两次新密码输入不一致");
			return;
		} else if (newPwd.length() < 6 || newPwd2.length() < 6) {
			CommonFuncUtil.getToast(UpdatePwdActivity.this, "新密码长度不能小于6位");
			return;
		}

		String url = GlobalSet.APP_SERVER_URL + "app.drug_user/updatePassword";
		OkHttpUtils.post().url(url)
				.addHeader("jwt-token", mApplication.getToken())
				.addParams("password", oldPwd)
				.addParams("new_password", newPwd)
				.addParams("verify_password", newPwd)
				.build()
				.execute(new StringCallback() {
					@Override
					public void onError(Call call, Exception e, int id) {
						CommonFuncUtil.getToast(UpdatePwdActivity.this, e.getMessage());
					}

					@Override
					public void onResponse(String response, int id) {
//						Toast.makeText(UpdatePwdActivity.this, response, Toast.LENGTH_LONG).show();
						try {
							JSONObject jsonResponse = new JSONObject(response);
							if (jsonResponse.getInt("code") == GlobalSet.APP_SUCCESS) {
								CommonFuncUtil.getToast(UpdatePwdActivity.this, jsonResponse.getString("info"));
								removeALLActivity();
								CommonFuncUtil.goNextActivityWithNoArgs(UpdatePwdActivity.this,
										LoginActivity.class, false);
							} else if (jsonResponse.getInt("code") == 1007) {
								// token 失效，踢出当前用户，退到登录页面
								CommonFuncUtil.getToast(UpdatePwdActivity.this,
										"当前用户已在别处登录，请重新登录");
								removeALLActivity();
								CommonFuncUtil.goNextActivityWithNoArgs(UpdatePwdActivity.this,
										LoginActivity.class, false);
							}

						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
	}
	
}

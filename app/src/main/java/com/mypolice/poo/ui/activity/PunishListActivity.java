package com.mypolice.poo.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.mypolice.poo.R;
import com.mypolice.poo.adapter.CommonAdapter;
import com.mypolice.poo.adapter.ViewHolder;
import com.mypolice.poo.application.GlobalSet;
import com.mypolice.poo.bean.LeaveItemBean;
import com.mypolice.poo.bean.PunishBean;
import com.mypolice.poo.util.CommonFuncUtil;
import com.mypolice.poo.widget.CenterDialog;
import com.mypolice.poo.widget.TitleBarView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**   
 * @Title: PunishListActivity.java
 * @Package com.mypolice.poo.ui.activity
 * @Description: 违反协议处置列表页面
 * @author wangjl  
 * @crdate 2017-10-27
 * @update
 * @version v2.1.0(12)
 */
@ContentView(R.layout.activity_punish_list)
public class PunishListActivity extends BaseActivityPoo {

	private static final int REQUEST_CODE_LEAVE = 0x101;

	/** TitleBarView 顶部标题栏 */
	@ViewInject(R.id.titleLeaveList)
	private TitleBarView mTitleLeaveList;

	@ViewInject(R.id.lvPunish)
	private ListView mLvPunish;

	private CommonAdapter mAdapter;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

	/** 加载进度条 */
	private CenterDialog centerDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);

		initView();
		loadData();
	}
	
	@Override
	public void initView() {
		super.initView();
		mTitleLeaveList.setText("消息通知");

		centerDialog = new CenterDialog(PunishListActivity.this, R.layout.dialog_wap_loading,
				new int[]{});
		centerDialog.show();
	}

	private void loadData() {
		getPunishList();
	}

	/**
	 * 保存已读状态
	 */
	private void saveIsRead() {
		String url = GlobalSet.APP_SERVER_URL + "community_punish/saveIsRead";
		OkHttpUtils.post().url(url)
				.addParams("token", mApplication.getToken())
				.addParams("drug_user_id", mApplication.getUserID() + "")
				.build()
				.execute(new StringCallback() {
					@Override
					public void onError(Call call, Exception e, int id) {
						centerDialog.cancel();
						CommonFuncUtil.getToast(PunishListActivity.this, e.getMessage());
					}

					@Override
					public void onResponse(String response, int id) {
//						CommonFuncUtil.getToast(LeaveListActivity.this, response);
						centerDialog.cancel();
						try {
							JSONObject jsonResponse = new JSONObject(response);
							if (jsonResponse.getInt("code") == 0
									|| jsonResponse.getInt("code") == 200) {
								Log.d("IS_Read", "is read true");

							} else if (jsonResponse.getInt("code") == 1007) {
								// token 失效，踢出当前用户，退到登录页面
								CommonFuncUtil.getToast(PunishListActivity.this,
										"当前用户已在别处登录，请重新登录");
								removeALLActivity();
								CommonFuncUtil.goNextActivityWithNoArgs(PunishListActivity.this,
										LoginActivity.class, false);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});

	}

	/**
	 * 获取违反协议处置列表
	 */
	private void getPunishList() {
		String url = GlobalSet.APP_SERVER_URL + "community_punish/getPunishList";
		OkHttpUtils.post().url(url)
				.addParams("token", mApplication.getToken())
				.addParams("drug_user_id", mApplication.getUserID() + "")
				.build()
				.execute(new StringCallback() {
					@Override
					public void onError(Call call, Exception e, int id) {
						centerDialog.cancel();
						CommonFuncUtil.getToast(PunishListActivity.this, e.getMessage());
					}

					@Override
					public void onResponse(String response, int id) {
//						CommonFuncUtil.getToast(LeaveListActivity.this, response);
						centerDialog.cancel();
						try {
							JSONObject jsonResponse = new JSONObject(response);
							if (jsonResponse.getInt("code") == 0
									|| jsonResponse.getInt("code") == 200) {
								org.json.JSONArray array = jsonResponse.getJSONArray("data");
								if (array.length() == 0)
									return;

								List<PunishBean> punishList = new ArrayList<PunishBean>();
								PunishBean punishBean = null;
								for (int i = 0; i < array.length(); i++) {
									punishBean = JSON.parseObject(array.getString(i), PunishBean.class);
									punishList.add(punishBean);
								}
								bindDataToUI(punishList);
								saveIsRead();
							} else if (jsonResponse.getInt("code") == 1007) {
								// token 失效，踢出当前用户，退到登录页面
								CommonFuncUtil.getToast(PunishListActivity.this,
										"当前用户已在别处登录，请重新登录");
								removeALLActivity();
								CommonFuncUtil.goNextActivityWithNoArgs(PunishListActivity.this,
										LoginActivity.class, false);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});

	}

	/**
	 * 绑定数据到 UI
	 * @param punishList
     */
	private void bindDataToUI(List<PunishBean> punishList) {
		mAdapter = new CommonAdapter<PunishBean>(PunishListActivity.this,
				punishList, R.layout.item_lv_punish) {
			@Override
			public void convert(ViewHolder helper, final PunishBean item) {

				helper.setText(R.id.tvItemPunishText, item.getPunish_type_text());
				helper.setText(R.id.tvItemPunishTime, item.getReg_time());

			}
		};
		mLvPunish.setAdapter(mAdapter);
		mLvPunish.setVisibility(View.VISIBLE);

	}
	
}

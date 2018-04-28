package com.mypolice.poo.ui.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.mypolice.poo.R;
import com.mypolice.poo.adapter.CommonAdapter;
import com.mypolice.poo.adapter.ViewHolder;
import com.mypolice.poo.application.ApiCode;
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

import cn.pedant.SweetAlert.SweetAlertDialog;
import lib.kingja.switchbutton.SwitchMultiButton;
import okhttp3.Call;

/**   
 * @Title: PunishListActivity.java
 * @Package com.mypolice.poo.ui.activity
 * @Description: 消息通知列表页面
 * @author wangjl  
 * @crdate 2017-10-27
 * @update 2018-4-24	更新新版UI
 * @version v2.1.0(12)
 */
@ContentView(R.layout.activity_punish_list)
public class PunishListActivity extends BaseActivityPoo {

	private static final int REQUEST_CODE_LEAVE = 0x101;

	/** TitleBarView 顶部标题栏 */
	@ViewInject(R.id.titleLeaveList)
	private TitleBarView mTitleLeaveList;
	@ViewInject(R.id.switchmultibutton)
	private SwitchMultiButton mSwitchButton;

	@ViewInject(R.id.lvPunish)
	private ListView mLvPunish;

	private CommonAdapter mAdapter;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
	private List<PunishBean.NoticeBean> noticeList = new ArrayList<>();

	/** 加载进度条 */
	private SweetAlertDialog pDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);

		initView();
//		getPunishList(2);	// 获取全部
	}
	
	@Override
	public void initView() {
		super.initView();
		mTitleLeaveList.setText("消息通知");

		pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
		pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
		pDialog.setTitleText("正在加载...");
		pDialog.setCancelable(false);
		pDialog.show();

		mSwitchButton.setOnSwitchListener(new SwitchMultiButton.OnSwitchListener() {
			@Override
			public void onSwitch(int position, String tabText) {
				pDialog.show();
				switch (position) {
					case 0:	// 全部
						getPunishList(2);
						break;
					case 1:	// 未读
						getPunishList(0);
						break;
					case 2: // 已读
						getPunishList(1);
						break;
				}

//				Toast.makeText(PunishListActivity.this, tabText, Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();

		mSwitchButton.setSelectedTab(0);
		getPunishList(2);	// 获取全部
	}

	/**
	 * 获取违反协议处置列表
	 */
	private void getPunishList(int status) {
		String url = GlobalSet.APP_SERVER_URL + "app.message/allMessage";
		OkHttpUtils.get().url(url)
				.addHeader(GlobalSet.APP_TOKEN_KEY, mApplication.getToken())
				.addParams("type", "1")	// 1 吸毒端，2 管控端
				.addParams("read", status + "")
				.build()
				.execute(new StringCallback() {
					@Override
					public void onError(Call call, Exception e, int id) {
						pDialog.dismiss();
						CommonFuncUtil.getToast(PunishListActivity.this, e.getMessage());
					}

					@Override
					public void onResponse(String response, int id) {
						pDialog.dismiss();
						try {
							JSONObject jsonResponse = new JSONObject(response);
							if (jsonResponse.getInt("code") == ApiCode.CODE_SUCCESS) {
//								org.json.JSONArray array = jsonResponse.getJSONArray("data");
								PunishBean punishBean = JSON.parseObject(jsonResponse.getString("data"), PunishBean.class);
								if (punishBean.getData().size() == 0) {
									CommonFuncUtil.getToast(PunishListActivity.this,
											"当前暂无消息");
									clearList();
									return;
								}

								noticeList = punishBean.getData();
								bindDataToUI(noticeList);
//								saveIsRead();
							} else if (jsonResponse.getInt("code") == ApiCode.CODE_TOKEN_EXPIRED) {
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
     */
	private void bindDataToUI(List<PunishBean.NoticeBean> noticeList) {

		mAdapter = new CommonAdapter<PunishBean.NoticeBean>(PunishListActivity.this,
				noticeList, R.layout.item_lv_punish) {
			@Override
			public void convert(ViewHolder helper, final PunishBean.NoticeBean item) {

				helper.setText(R.id.tvItemPunishText, item.getTopic());
				helper.setText(R.id.tvItemPunishTime, item.getCreate_time());
				helper.setText(R.id.tvItemStatus, item.getIs_read() == 0 ? "未读" : "已读");
				if (item.getIs_read() == 0)	// 未读
					((TextView) helper.getView(R.id.tvItemStatus)).setTextColor(Color.parseColor("#ffa400"));
				else	// 已读
					((TextView) helper.getView(R.id.tvItemStatus)).setTextColor(Color.parseColor("#999999"));

				helper.getView(R.id.ll_container).setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Bundle bundle = new Bundle();
						bundle.putInt("msg_id", item.getMsg_id());
						bundle.putString("title", item.getTopic());
						bundle.putString("time", item.getCreate_time());
						bundle.putString("content", item.getContent());
						bundle.putInt("is_read", item.getIs_read());
						CommonFuncUtil.goNextActivityWithArgs(PunishListActivity.this,
								NoticeDetailActivity.class, bundle, false);
					}
				});

			}
		};
		mLvPunish.setAdapter(mAdapter);
		mLvPunish.setVisibility(View.VISIBLE);

	}

	/** 清空列表 */
	private void clearList() {
		noticeList.clear();
		if (null != mAdapter)
			mAdapter.notifyDataSetChanged();
	}
	
}

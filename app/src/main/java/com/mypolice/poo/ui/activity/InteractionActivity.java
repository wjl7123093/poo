package com.mypolice.poo.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mypolice.poo.R;
import com.mypolice.poo.adapter.CommonAdapter;
import com.mypolice.poo.adapter.ViewHolder;
import com.mypolice.poo.application.ApiCode;
import com.mypolice.poo.application.GlobalSet;
import com.mypolice.poo.bean.WorkBean;
import com.mypolice.poo.service.KeepLiveService;
import com.mypolice.poo.util.CommonFuncUtil;
import com.mypolice.poo.widget.CenterDialog;
import com.mypolice.poo.widget.MyListView;
import com.mypolice.poo.widget.TitleBarView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;

/**   
 * @Title: InteractionActivity.java 
 * @Package com.mypolice.poo.ui.activity
 * @Description: 交流互动页面
 * @author wangjl  
 * @crdate 2017-8-21
 * @update 2017-10-26
 * @version v2.1.0(12)
 */
@ContentView(R.layout.activity_interaction)
public class InteractionActivity extends BaseActivityPoo {

	/** TitleBarView 顶部标题栏 */
	@ViewInject(R.id.titleInteraction)
	private TitleBarView mTitleInteraction;

	@ViewInject(R.id.lvInteraction)
	private MyListView mLvInteraction;
	@ViewInject(R.id.lvInteractionLog)
	private MyListView mLvInteractionPre;
//	@ViewInject(R.id.llExpandSwitch)
//	private LinearLayout mLlExpandSwitch;
//	@ViewInject(R.id.ivSwitch)
//	private ImageView mIvSwitch;

	@ViewInject(R.id.tv_notice)
	private TextView mTvNotice;
	@ViewInject(R.id.tv_count)
	private TextView mTvCounts;

	private CommonAdapter mAdapter;
	private CommonAdapter mAdapterPre;

	private boolean isExpand = false;	// 是否展开[默认关闭]

	/** 加载进度条 */
	private SweetAlertDialog pDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		
		initView();
//		getInteractionList();
		getInteractionData();
	}

	@Override
	public void initView() {
		super.initView();
		mTitleInteraction.setText("交流互动");

		pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
		pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
		pDialog.setTitleText("正在加载...");
		pDialog.setCancelable(false);
		pDialog.show();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 判断服务是否停止运行，停止则重启
		if (!mApplication.isServiceRunning()) {
			Intent intent = new Intent(InteractionActivity.this, KeepLiveService.class);
			this.startService(intent);
		}
	}

	/** 展开/合并开关 点击事件 */
//	@OnClick(R.id.llExpandSwitch)
//	public void onViewExpandSwitchClick(View v) {
//		if (isExpand) {
//			isExpand = false;
//			mIvSwitch.setImageResource(R.mipmap.ic_arrow_down);
//			mLvInteractionPre.setVisibility(View.GONE);
//		} else {
//			isExpand = true;
//			mIvSwitch.setImageResource(R.mipmap.ic_arrow_up);
//			mLvInteractionPre.setVisibility(View.VISIBLE);
//		}
//	}

	/**
	 * 获取交流互动数据 [六安]
	 * 2018-4-24
	 */
	private void getInteractionData() {
		String url = GlobalSet.APP_SERVER_URL + "app.drug_user/workView";
		OkHttpUtils.post().url(url)
				.addHeader(GlobalSet.APP_TOKEN_KEY, mApplication.getToken())
				.addParams("type", "2")	// 2 -> 家访
				.build()
				.execute(new StringCallback() {
					@Override
					public void onError(Call call, Exception e, int id) {
						pDialog.dismiss();
						CommonFuncUtil.getToast(InteractionActivity.this, e.getMessage());
					}

					@Override
					public void onResponse(String response, int id) {
						pDialog.dismiss();
						try {
							JSONObject jsonResponse = new JSONObject(response);
							if (jsonResponse.getInt("code") == ApiCode.CODE_SUCCESS) {
								JSONArray arrWorkLog = jsonResponse.getJSONObject("data").getJSONArray("work_log");
								String work = jsonResponse.getJSONObject("data").getString("work");
								String workNum = jsonResponse.getJSONObject("data").getString("work_num");

								if (!TextUtils.isEmpty(work) && !work.equals("{}")
										&& !work.equals("null")) {	// 当前任务
									WorkBean workBean = JSON.parseObject(work, WorkBean.class);
									bindWorkDataToUI(workBean);
								}
								if (!TextUtils.isEmpty(workNum) && !workNum.equals("{}")
										&& !workNum.equals("null")) {	// 任务数量
									WorkBean workNumBean = JSON.parseObject(workNum, WorkBean.class);
									bindWorkNumDataToUI(workNumBean);
								}
								if (arrWorkLog.length() > 0) {	// 家访记录
									List<WorkBean> workBeanPreList = new ArrayList<WorkBean>();
									WorkBean workPre = null;
									for (int i = 0; i < arrWorkLog.length(); i++) {
										workPre = JSON.parseObject(arrWorkLog.getString(i), WorkBean.class);
										workBeanPreList.add(workPre);
									}
									bindDataToUIPre(workBeanPreList);
								}

							} else if (jsonResponse.getInt("code") == ApiCode.CODE_TOKEN_EXPIRED) {
								// token 失效，踢出当前用户，退到登录页面
								CommonFuncUtil.isTokenExpired(InteractionActivity.this);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
	}

	/**
	 * 绑定数据到 UI
	 * @param workBeanList
	 */
	private void bindDataToUI(List<WorkBean> workBeanList) {
		mAdapter = new CommonAdapter<WorkBean>(InteractionActivity.this, workBeanList,
				R.layout.item_lv_interaction) {
			@Override
			public void convert(ViewHolder helper, WorkBean item) {
				helper.setText(R.id.tvItemDeadtime, GlobalSet.WORK_TIME_HEAD
						+ item.getWork_time());
				helper.setText(R.id.tvItemDescription, GlobalSet.WORK_DESCRIPTION_HEAD
						+ item.getRemark());
				helper.setText(R.id.tvItemStatus, GlobalSet.WORK_STATUS_HEAD
						+ (item.getWork_tag() == 0 ? "未登记" : "已登记"));
			}
		};
		mLvInteraction.setAdapter(mAdapter);
	}

	/**
	 * 绑定数据到 UI
	 * @param workBeanList
	 */
	private void bindDataToUIPre(List<WorkBean> workBeanList) {
		mAdapterPre = new CommonAdapter<WorkBean>(InteractionActivity.this, workBeanList,
				R.layout.item_lv_pre_work) {
			@Override
			public void convert(ViewHolder helper, final WorkBean item) {
				if (item == getItem(0)) {
					helper.getView(R.id.view_1).setVisibility(View.INVISIBLE);
				}

				/*helper.setText(R.id.tvItemDeadtime, "访谈时间: " + item.getWork_time());
				helper.setText(R.id.tvItemDescription, GlobalSet.WORK_DESCRIPTION_HEAD + item.getRemark());*/
				((TextView) helper.getView(R.id.tvItemDeadtime)).setText(Html.fromHtml("访谈时间: "
						+ "<font color=\"#999999\">" + item.getWork_time() + "</font>"));
				((TextView) helper.getView(R.id.tvItemDescription)).setText(Html.fromHtml(GlobalSet.WORK_DESCRIPTION_HEAD
						+ "<font color=\"#999999\">" + item.getRemark() + "</font>"));

				String status = "";
				int resId = R.mipmap.ic_warning;
				switch (item.getWork_tag()) {
					case 0:
						status = "<font color=\"#ffa400\">新任务</font>";
						resId = R.mipmap.ic_warning;
						break;
					case 1:
						status = "<font color=\"#ffa400\">待审核</font>";
						resId = R.mipmap.ic_warning;
						break;
					case 2:
						status = "<font color=\"#53d656\">已完成</font>";
						resId = R.mipmap.ic_ok;
						break;
					case 3:
						status = "<font color=\"#f14f4f\">未通过</font>";
						resId = R.mipmap.ic_error;
						break;
				}
				helper.setImageResource(R.id.iconItemStatus, resId);
				((TextView) helper.getView(R.id.tvItemStatus)).setText(Html.fromHtml("任务状态: " + status));
				helper.setText(R.id.tvItemTimes, item.getThe_first_year() + "第" + item.getNum() + "次");

			}
		};
		mLvInteractionPre.setAdapter(mAdapterPre);
	}

	/** 绑定当前家访数据 */
	private void bindWorkDataToUI(WorkBean workBean) {
		mTvNotice.setText("亲～" + workBean.getWork_time() + "有一次访谈沟通，届时工作人员会与你取得联系，请提前做好准备哦～～");
	}

	/** 绑定任务数量数据 */
	private void bindWorkNumDataToUI(WorkBean workBean) {
		mTvCounts.setText("已完成:" + workBean.getFinish() + "次  |  共需完成:" + workBean.getSum() + "次");
	}
	
}

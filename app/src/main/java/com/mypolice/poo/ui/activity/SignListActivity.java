package com.mypolice.poo.ui.activity;

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

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;

/**   
 * @Title: SignListActivity.java 
 * @Package com.mypolice.poo.ui.activity
 * @Description: 签到列表页面
 * @author wangjl  
 * @crdate 2017-8-21
 * @update 2017-11-14
 * @version v2.1.2(14)
 */
@ContentView(R.layout.activity_sign_list_new)
public class SignListActivity extends BaseActivityPoo {

	private static final int REQUES_CODE_SIGN = 0x301;

	/** TitleBarView 顶部标题栏 */
	@ViewInject(R.id.titleSignList)
	private TitleBarView mTitleSignList;

	@ViewInject(R.id.lvSign)
	private MyListView mLvSign;
	@ViewInject(R.id.lvSignPre)
	private MyListView mLvSignPre;
	@ViewInject(R.id.llExpandSwitch)
	private LinearLayout mLlExpandSwitch;
//	@ViewInject(R.id.ivSwitch)
//	private ImageView mIvSwitch;

	@ViewInject(R.id.ll_sign_btn)
	private LinearLayout mLlSignBtn;
	@ViewInject(R.id.ll_sign_pre_notice_title)
	private LinearLayout mLlSignPreWorkTitle;
	@ViewInject(R.id.ll_sign_pre_notice_nowork)
	private LinearLayout mLlSignPreWorkNowork;

	@ViewInject(R.id.tv_sign_time)
	private TextView mTvSignTime;
	@ViewInject(R.id.tv_sign_type)
	private TextView mTvSignType;
	@ViewInject(R.id.btn_sign)
	private Button mBtnSign;
	@ViewInject(R.id.tv_count)
	private TextView mTvCounts;

	private int mWorkId = 0;	// 任务ID
	private String mWorkTime = "";	// 签到时间

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
		getSignList();
	}
	
	@Override
	public void initView() {
		super.initView();
		mTitleSignList.setText("定期签到");

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
			Intent intent = new Intent(SignListActivity.this, KeepLiveService.class);
			this.startService(intent);
		}
	}

	/** 跳转到签到页面 */
	@OnClick(R.id.btn_sign)
	public void onBtnSignClick(View v) {
		Bundle bundle = new Bundle();
		bundle.putInt("taskId", mWorkId);
		bundle.putString("time", mWorkTime);
		CommonFuncUtil.goNextActivityWithArgsForResult(SignListActivity.this,
				SignActivity.class, bundle, REQUES_CODE_SIGN);
	}

	/**
	 * 获取签到列表 [New]
	 */
	private void getSignList() {
		String url = GlobalSet.APP_SERVER_URL + "app.sign/mySign";
		OkHttpUtils.post().url(url)
				.addHeader(GlobalSet.APP_TOKEN_KEY, mApplication.getToken())
				.build()
				.execute(new StringCallback() {
					@Override
					public void onError(Call call, Exception e, int id) {
						pDialog.dismiss();
						CommonFuncUtil.getToast(SignListActivity.this, e.getMessage());
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
//									// 显示提示区
//									setUranNoticeBlockVisibility(View.VISIBLE);
								} else {
									// 隐藏尿检操作区
//									setUranNoticeBlockGone();
//									setNoTaskVisible();
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
								CommonFuncUtil.getToast(SignListActivity.this,
										"当前用户已在别处登录，请重新登录");
								removeALLActivity();
								CommonFuncUtil.goNextActivityWithNoArgs(SignListActivity.this,
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
	 * @param workBeanList
	 */
	private void bindDataToUI(List<WorkBean> workBeanList) {
		mAdapter = new CommonAdapter<WorkBean>(SignListActivity.this, workBeanList,
				R.layout.item_lv_sign) {
			@Override
			public void convert(ViewHolder helper, final WorkBean item) {
				helper.setText(R.id.tvItemDeadtime, "签到时间: "
						+ item.getReality_time());
				/*helper.setText(R.id.tvItemDescription, GlobalSet.WORK_DESCRIPTION_HEAD
						+ item.getRemark());*/

				switch (item.getWork_tag()) {
					case 0:
						helper.setText(R.id.tvItemStatus, "任务状态: 新任务");
						break;
					case 1:
						helper.setText(R.id.tvItemStatus, "任务状态: 待审核");
						break;
					case 2:
						helper.setText(R.id.tvItemStatus, "任务状态: 已完成");
						break;
				}

			}
		};
		mLvSign.setAdapter(mAdapter);
		mLvSign.setVisibility(View.VISIBLE);
	}

	/**
	 * 绑定数据到 UI
	 * @param workBeanList
	 */
	private void bindDataToUIPre(List<WorkBean> workBeanList) {
		mAdapterPre = new CommonAdapter<WorkBean>(SignListActivity.this, workBeanList,
				R.layout.item_lv_pre_work) {
			@Override
			public void convert(ViewHolder helper, final WorkBean item) {
				if (item == getItem(0)) {
					helper.getView(R.id.view_1).setVisibility(View.INVISIBLE);
				}

				/*helper.setText(R.id.tvItemDeadtime, GlobalSet.WORK_TIME_HEAD + item.getWork_time());
				helper.setText(R.id.tvItemDescription, GlobalSet.WORK_DESCRIPTION_HEAD + item.getRemark());*/
				((TextView) helper.getView(R.id.tvItemDeadtime)).setText(Html.fromHtml(GlobalSet.WORK_TIME_HEAD
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
//				helper.setText(R.id.tvItemTimes, item.getThe_first_year() + "第" + item.getNum() + "次");
				helper.setVisibility(R.id.tvItemTimes, View.GONE);

			}
		};
		mLvSignPre.setAdapter(mAdapterPre);
	}

	/** 绑定当前尿检数据 */
	private void bindWorkDataToUI(WorkBean workBean) {
		mWorkId = workBean.getWork_id();
		mWorkTime = workBean.getWork_time();
//		mTvNotice.setText("亲～" + workBean.getWork_time() + "要完成本期签到检任务哦～～");
		mTvSignTime.setText("签到时间  " + workBean.getWork_time());
		mTvSignType.setText("任务说明  " + workBean.getRemark());

		List<WorkBean> workList = new ArrayList<>();
		workList.add(workBean);
		switch (workBean.getWork_tag()) {
			case 0: // 新任务
				mLvSign.setVisibility(View.GONE);
				break;
			case 1:	// 待审核
			case 2: // 已完成
				bindDataToUI(workList);
				setUranNoticeBlockGone();
				break;
		}
	}

	/** 绑定任务数量数据 */
	private void bindWorkNumDataToUI(WorkBean workBean) {
		mTvCounts.setText("已完成:" + workBean.getFinish() + "次  |  共需完成:" + workBean.getSum() + "次");
	}

	/** 隐藏 任务提示区 */
	private void setUranNoticeBlockGone() {
//		mLlSignNotice.setVisibility(View.GONE);
		mLlSignBtn.setVisibility(View.GONE);
	}

	/** 显示 无任务区 */
	private void setNoTaskVisible() {
		mLlSignPreWorkTitle.setVisibility(View.VISIBLE);
		mLlSignPreWorkNowork.setVisibility(View.VISIBLE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUES_CODE_SIGN && resultCode == SignActivity.RESULT_CODE_SIGN) {
			pDialog.show();
			// 刷新列表
//			getCommunityWork();
			getSignList();
		}
	}
}

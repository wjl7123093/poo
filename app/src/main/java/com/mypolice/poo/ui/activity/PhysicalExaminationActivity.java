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
 * @Title: PysicalExaminationActivity.java 
 * @Package com.mypolice.poo.ui.activity
 * @Description: 定期体检页面
 * @author wangjl  
 * @crdate 2017-8-21
 * @update 2017-11-14
 * @version v2.1.2(14)
 */
@ContentView(R.layout.activity_physical_examination)
public class PhysicalExaminationActivity extends BaseActivityPoo {

	private static final int REQUES_CODE_EXAMINATION = 0x201;

	/** TitleBarView 顶部标题栏 */
	@ViewInject(R.id.titleExamination)
	private TitleBarView mTitleExamination;

	@ViewInject(R.id.lvExamination)
	private MyListView mLvExamination;
	@ViewInject(R.id.lvExaminationPre)
	private MyListView mLvExaminationPre;
	@ViewInject(R.id.llExpandSwitch)
	private LinearLayout mLlExpandSwitch;
//	@ViewInject(R.id.ivSwitch)
//	private ImageView mIvSwitch;

	@ViewInject(R.id.ll_uran_notice)
	private LinearLayout mLlUranNotice;
	@ViewInject(R.id.ll_uran_btn)
	private LinearLayout mLlUranBtn;
	@ViewInject(R.id.ll_uran_pre_notice_title)
	private LinearLayout mLlUranPreWorkTitle;
	@ViewInject(R.id.ll_uran_pre_notice_nowork)
	private LinearLayout mLlUranPreWorkNowork;

	@ViewInject(R.id.btn_uran)
	private Button mBtnUran;
	@ViewInject(R.id.tv_notice)
	private TextView mTvNotice;
	@ViewInject(R.id.tv_count)
	private TextView mTvCounts;

	private int mWorkId = 0;	// 任务ID

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
//		getExaminationList();
		getExaminationData();
	}
	
	@Override
	public void initView() {	
		super.initView();
		mTitleExamination.setText("定期体检");

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
			Intent intent = new Intent(PhysicalExaminationActivity.this, KeepLiveService.class);
			this.startService(intent);
		}
	}

	/** 跳转到尿检页面 */
	@OnClick(R.id.btn_uran)
	public void onBtnUranClick(View v) {
		Bundle bundle = new Bundle();
		bundle.putInt("taskId", mWorkId);
		CommonFuncUtil.goNextActivityWithArgs(PhysicalExaminationActivity.this, URANActivity.class,
				bundle, false);
	}

	/**
	 * 获取体检列表 [New]
	 */
	private void getExaminationList() {
		String url = GlobalSet.APP_SERVER_URL + "community_work/getWorkListBy";
		OkHttpUtils.post().url(url)
				.addHeader("token", mApplication.getToken())
				.addParams("drug_user_id", mApplication.getUserID() + "")
				.addParams("work_type", "1")	// 1 -> 体检
				.build()
				.execute(new StringCallback() {
					@Override
					public void onError(Call call, Exception e, int id) {
						pDialog.dismiss();
						CommonFuncUtil.getToast(PhysicalExaminationActivity.this, e.getMessage());
					}

					@Override
					public void onResponse(String response, int id) {
//						CommonFuncUtil.getToast(SignActivity.this, response);
						pDialog.dismiss();
						mLlExpandSwitch.setVisibility(View.VISIBLE);
						try {
							JSONObject jsonResponse = new JSONObject(response);
							if (jsonResponse.getInt("code") == 0
									|| jsonResponse.getInt("code") == 200) {
								JSONArray arrNow = jsonResponse.getJSONObject("data").getJSONArray("nowList");
								JSONArray arrPre = jsonResponse.getJSONObject("data").getJSONArray("preList");

								List<WorkBean> workBeanList = new ArrayList<WorkBean>();
								WorkBean work = null;
								for (int i = 0; i < arrNow.length(); i++) {
									work = JSON.parseObject(arrNow.getString(i), WorkBean.class);
									workBeanList.add(work);
								}
								List<WorkBean> workBeanPreList = new ArrayList<WorkBean>();
								WorkBean workPre = null;
								for (int i = 0; i < arrPre.length(); i++) {
									workPre = JSON.parseObject(arrPre.getString(i), WorkBean.class);
									workBeanPreList.add(workPre);
								}

//								if (workBeanList.size() > 0)
									bindDataToUI(workBeanList);

//								if (workBeanPreList.size() > 0)
									bindDataToUIPre(workBeanPreList);

							} else if (jsonResponse.getInt("code") == 1007) {
								// token 失效，踢出当前用户，退到登录页面
								CommonFuncUtil.getToast(PhysicalExaminationActivity.this,
										"当前用户已在别处登录，请重新登录");
								removeALLActivity();
								CommonFuncUtil.goNextActivityWithNoArgs(PhysicalExaminationActivity.this,
										LoginActivity.class, false);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
	}

	/**
	 * 获取尿检数据 [六安]
	 * 2018-4-24
	 */
	private void getExaminationData() {
		String url = GlobalSet.APP_SERVER_URL + "app.drug_user/workView";
		OkHttpUtils.get().url(url)
				.addHeader(GlobalSet.APP_TOKEN_KEY, mApplication.getToken())
				.addParams("type", "1")	// 1 -> 尿检
				.build()
				.execute(new StringCallback() {
					@Override
					public void onError(Call call, Exception e, int id) {
						pDialog.dismiss();
						CommonFuncUtil.getToast(PhysicalExaminationActivity.this, e.getMessage());
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
									setUranNoticeBlockGone();
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
								CommonFuncUtil.getToast(PhysicalExaminationActivity.this,
										"当前用户已在别处登录，请重新登录");
								removeALLActivity();
								CommonFuncUtil.goNextActivityWithNoArgs(PhysicalExaminationActivity.this,
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
		mAdapter = new CommonAdapter<WorkBean>(PhysicalExaminationActivity.this, workBeanList,
				R.layout.item_lv_examination) {
			@Override
			public void convert(ViewHolder helper, final WorkBean item) {
				helper.setText(R.id.tvItemDeadtime, GlobalSet.WORK_TIME_HEAD
						+ item.getWork_time());
				helper.setText(R.id.tvItemDescription, GlobalSet.WORK_DESCRIPTION_HEAD
						+ item.getRemark());
//				helper.setText(R.id.tvItemStatus, GlobalSet.WORK_STATUS_HEAD
//						+ (item.getWork_tag() == 0 ? "未登记" : "已登记(待审核)"));
				helper.setText(R.id.tvItemStatus, GlobalSet.WORK_STATUS_HEAD
						+ item.getWork_tag_text());
				helper.setText(R.id.btnItemUpdateWork, item.getWork_tag() == 0 ? "登记" : "修改");
				helper.setOnClickListener(R.id.btnItemUpdateWork, new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Bundle bundle = new Bundle();
						bundle.putInt("taskId", item.getId());
						CommonFuncUtil.goNextActivityWithArgsForResult(PhysicalExaminationActivity.this,
								URANActivity.class, bundle, REQUES_CODE_EXAMINATION);
					}
				});
			}
		};
		mLvExamination.setAdapter(mAdapter);
	}

	/**
	 * 绑定数据到 UI
	 * @param workBeanList
	 */
	private void bindDataToUIPre(List<WorkBean> workBeanList) {
		mAdapterPre = new CommonAdapter<WorkBean>(PhysicalExaminationActivity.this, workBeanList,
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
				helper.setText(R.id.tvItemTimes, item.getThe_first_year() + "第" + item.getNum() + "次");
			}
		};
		mLvExaminationPre.setAdapter(mAdapterPre);
	}

	/** 绑定当前尿检数据 */
	private void bindWorkDataToUI(WorkBean workBean) {
		mWorkId = workBean.getWork_id();
		mTvNotice.setText("亲～" + workBean.getWork_time() + "要完成本月尿检任务哦～～");
	}

	/** 绑定任务数量数据 */
	private void bindWorkNumDataToUI(WorkBean workBean) {
		mTvCounts.setText("已完成:" + workBean.getFinish() + "次  |  共需完成:" + workBean.getSum() + "次");
	}

	/** 设置提示区显示状态 */
	private void setUranNoticeBlockVisibility(int visibility) {
		mLlUranNotice.setVisibility(visibility);
		mLlUranBtn.setVisibility(visibility);
		mLlUranPreWorkTitle.setVisibility(visibility == View.VISIBLE ? View.GONE : View.VISIBLE);
		mLlUranPreWorkNowork.setVisibility(visibility == View.VISIBLE ? View.GONE : View.VISIBLE);
	}

	/** 设置提示区显示状态 */
	private void setUranNoticeBlockGone() {
		mLlUranBtn.setVisibility(View.GONE);
		mTvNotice.setText("");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUES_CODE_EXAMINATION
				&& resultCode == URANActivity.RESULT_CODE_URAN) {
			// 刷新列表
//			getExaminationList();
			getExaminationData();
		}
	}
}

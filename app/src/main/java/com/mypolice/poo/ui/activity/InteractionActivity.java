package com.mypolice.poo.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mypolice.poo.R;
import com.mypolice.poo.adapter.CommonAdapter;
import com.mypolice.poo.adapter.ViewHolder;
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

import java.util.ArrayList;
import java.util.List;

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
	@ViewInject(R.id.lvInteractionPre)
	private MyListView mLvInteractionPre;
	@ViewInject(R.id.llExpandSwitch)
	private LinearLayout mLlExpandSwitch;
	@ViewInject(R.id.ivSwitch)
	private ImageView mIvSwitch;

	private CommonAdapter mAdapter;
	private CommonAdapter mAdapterPre;

	private boolean isExpand = false;	// 是否展开[默认关闭]

	/** 加载进度条 */
	private CenterDialog centerDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		
		initView();
//		getCommunityWork();
		getInteractionList();
	}

	@Override
	public void initView() {
		super.initView();
		mTitleInteraction.setText("交流互动");
		mLlExpandSwitch.setVisibility(View.GONE);
		mLvInteractionPre.setVisibility(View.GONE);

		centerDialog = new CenterDialog(InteractionActivity.this, R.layout.dialog_wap_loading,
				new int[]{});
		centerDialog.show();
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
	@OnClick(R.id.llExpandSwitch)
	public void onViewExpandSwitchClick(View v) {
		if (isExpand) {
			isExpand = false;
			mIvSwitch.setImageResource(R.mipmap.ic_arrow_down);
			mLvInteractionPre.setVisibility(View.GONE);
		} else {
			isExpand = true;
			mIvSwitch.setImageResource(R.mipmap.ic_arrow_up);
			mLvInteractionPre.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 获取 特定吸毒者所有家访记录
	 */
	private void getCommunityWork() {
		String url = GlobalSet.APP_SERVER_URL + "community_work/conditionGet";
		OkHttpUtils.post().url(url)
				.addHeader("token", mApplication.getToken())
				.addParams("conditions", "(work_tag=0 or work_tag=1) and (work_type=2) and drug_user_id=" + mApplication.getUserID())
				.addParams("fileds", "id,work_type,work_time,remark,work_tag")
//				.addParams("id", mApplication.getUserID() + "")
				.build()
				.execute(new StringCallback() {
					@Override
					public void onError(Call call, Exception e, int id) {
						centerDialog.cancel();
						CommonFuncUtil.getToast(InteractionActivity.this, e.getMessage());
					}

					@Override
					public void onResponse(String response, int id) {
//						CommonFuncUtil.getToast(PhysicalExaminationActivity.this, response);
						centerDialog.cancel();
						try {
							JSONObject jsonResponse = new JSONObject(response);
							if (jsonResponse.getInt("code") == 0
									|| jsonResponse.getInt("code") == 200) {
								org.json.JSONArray array = jsonResponse.getJSONObject("data").getJSONArray("data");
								List<WorkBean> workBeanList = new ArrayList<WorkBean>();
								WorkBean work = null;
								for (int i = 0; i < array.length(); i++) {
									work = JSON.parseObject(array.getString(i), WorkBean.class);
//									CommonFuncUtil.getToast(InteractionActivity.this, work.toString());
									workBeanList.add(work);
								}
								bindDataToUI(workBeanList);
							} else if (jsonResponse.getInt("code") == 1007) {
								// token 失效，踢出当前用户，退到登录页面
								CommonFuncUtil.getToast(InteractionActivity.this,
										"当前用户已在别处登录，请重新登录");
								removeALLActivity();
								CommonFuncUtil.goNextActivityWithNoArgs(InteractionActivity.this,
										LoginActivity.class, false);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
	}

	/**
	 * 获取交流互动列表 [New]
	 */
	private void getInteractionList() {
		String url = GlobalSet.APP_SERVER_URL + "community_work/getWorkListBy";
		OkHttpUtils.post().url(url)
				.addHeader("token", mApplication.getToken())
				.addParams("drug_user_id", mApplication.getUserID() + "")
				.addParams("work_type", "2")	// 2 -> 家访
				.build()
				.execute(new StringCallback() {
					@Override
					public void onError(Call call, Exception e, int id) {
						CommonFuncUtil.getToast(InteractionActivity.this, e.getMessage());
					}

					@Override
					public void onResponse(String response, int id) {
//						CommonFuncUtil.getToast(SignActivity.this, response);
						centerDialog.cancel();
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

								if (workBeanList.size() > 0)
									bindDataToUI(workBeanList);
								if (workBeanPreList.size() > 0)
									bindDataToUIPre(workBeanPreList);

							} else if (jsonResponse.getInt("code") == 1007) {
								// token 失效，踢出当前用户，退到登录页面
								CommonFuncUtil.getToast(InteractionActivity.this,
										"当前用户已在别处登录，请重新登录");
								removeALLActivity();
								CommonFuncUtil.goNextActivityWithNoArgs(InteractionActivity.this,
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
				helper.setText(R.id.tvItemDeadtime, GlobalSet.WORK_TIME_HEAD
						+ item.getWork_time());
				helper.setText(R.id.tvItemDescription, GlobalSet.WORK_DESCRIPTION_HEAD
						+ item.getRemark());
				helper.setIcon(R.id.iconItemStatus,
						item.getWork_tag() == 2 ? getString(R.string.icon_sign_ok)
								: getString(R.string.icon_sign_err),
						item.getWork_tag() == 2 ? getResources().getColor(R.color.app_main_green)
								: getResources().getColor(R.color.app_main_red));

			}
		};
		mLvInteractionPre.setAdapter(mAdapterPre);
	}
	
}

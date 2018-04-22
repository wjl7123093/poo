package com.mypolice.poo.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
import com.mypolice.poo.bean.LeaveBean;
import com.mypolice.poo.bean.LeaveItemBean;
import com.mypolice.poo.util.CommonFuncUtil;
import com.mypolice.poo.widget.CenterDialog;
import com.mypolice.poo.widget.TitleBarView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**   
 * @Title: LeaveHistoryListActivity.java
 * @Package com.mypolice.poo.ui.activity
 * @Description: 历史请假列表页面
 * @author wangjl  
 * @crdate 2017-10-26
 * @update
 * @version v2.1.0(12)
 */
@ContentView(R.layout.activity_leave_history_list)
public class LeaveHistoryListActivity extends BaseActivityPoo {

	private static final int REQUEST_CODE_LEAVE = 0x101;

	/** TitleBarView 顶部标题栏 */
	@ViewInject(R.id.titleLeaveList)
	private TitleBarView mTitleLeaveList;

	@ViewInject(R.id.lvLeave)
	private ListView mLvLeave;

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
		mTitleLeaveList.setText("历史请假记录");

		centerDialog = new CenterDialog(LeaveHistoryListActivity.this, R.layout.dialog_wap_loading,
				new int[]{});
		centerDialog.show();
	}

	private void loadData() {
		getLeaveData();
	}

	/**
	 * 获取历史请假数据
	 */
	private void getLeaveData() {
		String url = GlobalSet.APP_SERVER_URL + "community_leave/getPreLeaveList";
		OkHttpUtils.post().url(url)
				.addParams("token", mApplication.getToken())
				.addParams("drug_user_id", mApplication.getUserID() + "")
				.build()
				.execute(new StringCallback() {
					@Override
					public void onError(Call call, Exception e, int id) {
						centerDialog.cancel();
						CommonFuncUtil.getToast(LeaveHistoryListActivity.this, e.getMessage());
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

								List<LeaveItemBean> leaveList = new ArrayList<LeaveItemBean>();
								LeaveItemBean leave = null;
								for (int i = 0; i < array.length(); i++) {
									leave = JSON.parseObject(array.getString(i), LeaveItemBean.class);
//									CommonFuncUtil.getToast(LeaveListActivity.this, leave.toString());
									leaveList.add(leave);
								}
								bindDataToUI(leaveList);
							} else if (jsonResponse.getInt("code") == 1007) {
								// token 失效，踢出当前用户，退到登录页面
								CommonFuncUtil.getToast(LeaveHistoryListActivity.this,
										"当前用户已在别处登录，请重新登录");
								removeALLActivity();
								CommonFuncUtil.goNextActivityWithNoArgs(LeaveHistoryListActivity.this,
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
	 * @param leaveList
     */
	private void bindDataToUI(List<LeaveItemBean> leaveList) {
//		mLeaveBean = leave;

		mAdapter = new CommonAdapter<LeaveItemBean>(LeaveHistoryListActivity.this,
				leaveList, R.layout.item_lv_leave_history) {
			@Override
			public void convert(ViewHolder helper, final LeaveItemBean item) {
				if (4 == item.getLeave_type()) {	// 已销假
					helper.setIcon(R.id.iconItemStatus, getString(R.string.icon_leave_status1),
							getResources().getColor(R.color.app_main_green));
				} else {	// 审核未通过
					helper.setIcon(R.id.iconItemStatus, getString(R.string.icon_leave_status2),
							getResources().getColor(R.color.app_main_red));
				}

//				helper.setText(R.id.tvItemCreateTime, "创建时间: " + item.getReg_time());
				helper.setText(R.id.tvItemStartTime, "开始时间: " + item.getStart_time());
				helper.setText(R.id.tvItemEndTime, "结束时间: " + item.getEnd_time());
				helper.setText(R.id.tvItemLeaveReason, "请假事由: " + item.getReason());
				helper.setText(R.id.tvItemDestination, "目的地: " + item.getDestination());

			}
		};
		mLvLeave.setAdapter(mAdapter);
		mLvLeave.setVisibility(View.VISIBLE);

	}

	/**
	 * LeaveItemBean -> LeaveBean
	 * @param leaveItemBean
	 * @return
     */
	private LeaveBean doTransferLeaveItemToLeave(LeaveItemBean leaveItemBean) {
		LeaveBean leaveBean = new LeaveBean();
		leaveBean.setDestination(leaveItemBean.getDestination());
		leaveBean.setReason(leaveItemBean.getReason());
		try {
			leaveBean.setStart_time(sdf.parse(leaveItemBean.getStart_time()).getTime() / 1000);
			leaveBean.setEnd_time(sdf.parse(leaveItemBean.getEnd_time()).getTime() / 1000);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		leaveBean.setLeave_num(leaveItemBean.getLeave_num());
		leaveBean.setVisitor_name(leaveItemBean.getVisitor_name());
		leaveBean.setVisitor_relation(leaveItemBean.getVisitor_relation());
		leaveBean.setVisitor_profession(leaveItemBean.getVisitor_profession());
		leaveBean.setVisitor_company(leaveItemBean.getVisitor_company());
		leaveBean.setVisitor_address(leaveItemBean.getVisitor_address());
		leaveBean.setVisitor_tel(leaveItemBean.getVisitor_tel());
		leaveBean.setDrug_user_id(leaveItemBean.getDrug_user_id());
		leaveBean.setLeave_type(leaveItemBean.getLeave_type());
		leaveBean.setCommunity_drug_reg_id(leaveItemBean.getCommunity_drug_reg_id());
		return leaveBean;
	}
	
}

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
import com.mypolice.poo.bean.LeaveBean;
import com.mypolice.poo.bean.LeaveItemBean;
import com.mypolice.poo.util.CommonFuncUtil;
import com.mypolice.poo.widget.CenterDialog;
import com.mypolice.poo.widget.TitleBarView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

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
 * @Title: LeaveListActivity.java 
 * @Package com.mypolice.poo.ui.activity
 * @Description: 请假列表页面
 * @author wangjl  
 * @crdate 2017-8-21
 * @update 2017-9-9
 * @version v2.0.0(2)
 */
@ContentView(R.layout.activity_leave_list)
public class LeaveListActivity extends BaseActivityPoo {

	private static final int REQUEST_CODE_LEAVE = 0x101;

	/** TitleBarView 顶部标题栏 */
	@ViewInject(R.id.titleLeaveList)
	private TitleBarView mTitleLeaveList;

//	@ViewInject(R.id.llAppForLeave)
//	private LinearLayout mLlAppForLeave;

	@ViewInject(R.id.btn_leave)
	private Button mBtnLeave;
	@ViewInject(R.id.lvLeave)
	private ListView mLvLeave;
	@ViewInject(R.id.lvLeavePre)
	private ListView mLvLeavePre;

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
		// 获取请假记录数据
		getLeaveLogNew();
	}
	
	@Override
	public void initView() {
		super.initView();
		mTitleLeaveList.setText("请假申请");
		mTitleLeaveList.setRightBtnIcon(getString(R.string.icon_leave_history));
		mTitleLeaveList.setRightBtnOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CommonFuncUtil.goNextActivityWithNoArgs(LeaveListActivity.this,
						LeaveHistoryListActivity.class, false);
			}
		});

		centerDialog = new CenterDialog(LeaveListActivity.this, R.layout.dialog_wap_loading,
				new int[]{});
		centerDialog.show();
	}

	private void loadData() {
//		getLeaveData();
		getLeaveDataNew();
	}

	/**
	 * 跳转到 请假申请页面
	 * @param v
     */
	@OnClick(R.id.btn_leave)
	public void onBtnLeaveClick(View v) {
		Bundle bundle = new Bundle();
		bundle.putString("from", "create");
		CommonFuncUtil.goNextActivityWithArgsForResult(LeaveListActivity.this,
				ApplicationForLeaveActivity.class, bundle, REQUEST_CODE_LEAVE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQUEST_CODE_LEAVE
				&& resultCode == ApplicationForLeaveActivity.RESULT_CODE_LEAVE) {
			centerDialog.show();
			// 刷新列表
			loadData();
		}
	}

	/**
	 * 获取请假数据
	 */
	private void getLeaveData() {
		String url = GlobalSet.APP_SERVER_URL + "community_leave/conditionGet";
		OkHttpUtils.post().url(url)
				.addParams("token", mApplication.getToken())
				.addParams("conditions", "drug_user_id=" + mApplication.getUserID() + " and leave_type<3")
				.build()
				.execute(new StringCallback() {
					@Override
					public void onError(Call call, Exception e, int id) {
						centerDialog.cancel();
						CommonFuncUtil.getToast(LeaveListActivity.this, e.getMessage());
					}

					@Override
					public void onResponse(String response, int id) {
//						CommonFuncUtil.getToast(LeaveListActivity.this, response);
						centerDialog.cancel();
						try {
							JSONObject jsonResponse = new JSONObject(response);
							if (jsonResponse.getInt("code") == 0
									|| jsonResponse.getInt("code") == 200) {
								org.json.JSONArray array = jsonResponse.getJSONObject("data").getJSONArray("data");
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
								CommonFuncUtil.getToast(LeaveListActivity.this,
										"当前用户已在别处登录，请重新登录");
								removeALLActivity();
								CommonFuncUtil.goNextActivityWithNoArgs(LeaveListActivity.this,
										LoginActivity.class, false);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});

	}

	/**
	 * 获取请假数据[六安]
	 * 2018-4-24
	 */
	private void getLeaveDataNew() {
		String url = GlobalSet.APP_SERVER_URL + "app.drug_user/getLeaveStatus";
		OkHttpUtils.get().url(url)
				.addHeader(GlobalSet.APP_TOKEN_KEY, mApplication.getToken())
				.build()
				.execute(new StringCallback() {
					@Override
					public void onError(Call call, Exception e, int id) {
						centerDialog.cancel();
						CommonFuncUtil.getToast(LeaveListActivity.this, e.getMessage());
					}

					@Override
					public void onResponse(String response, int id) {
//						CommonFuncUtil.getToast(LeaveListActivity.this, response);
						centerDialog.cancel();
						try {
							JSONObject jsonResponse = new JSONObject(response);
							if (jsonResponse.getInt("code") == GlobalSet.APP_SUCCESS) {
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
							} else if (jsonResponse.getInt("code") == ApiCode.CODE_EMPTY_DATA) {
								// 空数据，不做处理

							} else if (jsonResponse.getInt("code") == 1007) {
								// token 失效，踢出当前用户，退到登录页面
								CommonFuncUtil.getToast(LeaveListActivity.this,
										"当前用户已在别处登录，请重新登录");
								removeALLActivity();
								CommonFuncUtil.goNextActivityWithNoArgs(LeaveListActivity.this,
										LoginActivity.class, false);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});

	}

	/**
	 * 获取请假记录[六安]
	 * 2018-4-24
	 */
	private void getLeaveLogNew() {
		String url = GlobalSet.APP_SERVER_URL + "app.drug_user/getLeaveLog";
		OkHttpUtils.get().url(url)
				.addHeader(GlobalSet.APP_TOKEN_KEY, mApplication.getToken())
				.build()
				.execute(new StringCallback() {
					@Override
					public void onError(Call call, Exception e, int id) {
						centerDialog.cancel();
						CommonFuncUtil.getToast(LeaveListActivity.this, e.getMessage());
					}

					@Override
					public void onResponse(String response, int id) {
//						CommonFuncUtil.getToast(LeaveListActivity.this, response);
						centerDialog.cancel();
						try {
							JSONObject jsonResponse = new JSONObject(response);
							if (jsonResponse.getInt("code") == ApiCode.CODE_SUCCESS) {
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
								bindLogDataToUI(leaveList);
							} else if (jsonResponse.getInt("code") == ApiCode.CODE_EMPTY_DATA) {
								// 空数据，不做处理

							} else if (jsonResponse.getInt("code") == ApiCode.CODE_TOKEN_EXPIRED) {
								// token 失效，踢出当前用户，退到登录页面
								CommonFuncUtil.getToast(LeaveListActivity.this,
										"当前用户已在别处登录，请重新登录");
								removeALLActivity();
								CommonFuncUtil.goNextActivityWithNoArgs(LeaveListActivity.this,
										LoginActivity.class, false);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});

	}

	/**
	 * 提交已有请假数据
	 */
	private void putLeaveData(LeaveBean leaveBean, int leaveId) {
		String url = GlobalSet.APP_SERVER_URL + "community_leave/" + leaveId;
		OkHttpUtils.put().url(url)
				.addHeader("token", mApplication.getToken())
				.requestBody(RequestBody.create(MediaType.parse("application/json"), JSON.toJSONString(leaveBean)))
				.build()
				.execute(new StringCallback() {
					@Override
					public void onError(Call call, Exception e, int id) {
						centerDialog.cancel();
						CommonFuncUtil.getToast(LeaveListActivity.this, e.getMessage());
					}

					@Override
					public void onResponse(String response, int id) {
//						CommonFuncUtil.getToast(LeaveListActivity.this, response);
						centerDialog.cancel();
						try {
							JSONObject jsonResponse = new JSONObject(response);
							if (jsonResponse.getInt("code") == 0
									|| jsonResponse.getInt("code") == 200) {
								int result = jsonResponse.getInt("data");
								if (1 == result) {
									CommonFuncUtil.getToast(LeaveListActivity.this, "提交成功");
									loadData();	// 刷新列表
								}
							} else if (jsonResponse.getInt("code") == 1007) {
								// token 失效，踢出当前用户，退到登录页面
								CommonFuncUtil.getToast(LeaveListActivity.this,
										"当前用户已在别处登录，请重新登录");
								removeALLActivity();
								CommonFuncUtil.goNextActivityWithNoArgs(LeaveListActivity.this,
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

		mAdapter = new CommonAdapter<LeaveItemBean>(LeaveListActivity.this,
				leaveList, R.layout.item_lv_leave) {
			@Override
			public void convert(ViewHolder helper, final LeaveItemBean item) {
//				if (0 == item.getLeave_type()) {	// 草稿
////					helper.setText(R.id.tvItemTitle, "请假草稿");
////					helper.setVisibility(R.id.rlItemEdit, View.VISIBLE);
//					helper.setText(R.id.tvItemStatus, "存为草稿");
//				} else if (1 == item.getLeave_type()) {	// 待审核
////					helper.setText(R.id.tvItemTitle, "已提交申请");
////					helper.setVisibility(R.id.rlItemEdit, View.GONE);
//					helper.setText(R.id.tvItemStatus, "待审核");
//				} else if (2 == item.getLeave_type()) {	// 已审核，待销假
////					helper.setText(R.id.tvItemTitle, "已提交申请");
////					helper.setVisibility(R.id.rlItemEdit, View.GONE);
//					helper.setText(R.id.tvItemStatus, "已审核，待销假");
//				}

				helper.setText(R.id.tvItemStatus, "请假状态: " + item.getLeave_type_text());
				helper.setText(R.id.tvItemCreateTime, "创建时间:  " + item.getReg_time());
				helper.setText(R.id.tvItemStartTime, "开始时间:  " + item.getStart_time());
				helper.setText(R.id.tvItemEndTime, "结束时间:  " + item.getEnd_time());
				helper.setText(R.id.tvItemLeaveReason, "请假事由:  " + item.getReason());
				helper.setText(R.id.tvItemDestination, "外出地址:  " + item.getDestination());

				// 编辑
				/*helper.setOnClickListener(R.id.btnItemEdit, new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Bundle bundle = new Bundle();
						bundle.putString("from", "edit");
						bundle.putParcelable("leaveBean", item);
						CommonFuncUtil.goNextActivityWithArgsForResult(LeaveListActivity.this,
								ApplicationForLeaveActivity.class, bundle, REQUEST_CODE_LEAVE);
					}
				});
				// 提交
				helper.setOnClickListener(R.id.btnItemSubmit, new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						centerDialog.show();
						item.setLeave_type(1);
						putLeaveData(doTransferLeaveItemToLeave(item), item.getId());
					}
				});*/
			}
		};
		mLvLeave.setAdapter(mAdapter);
		mLvLeave.setVisibility(View.VISIBLE);

	}

	/**
	 * 绑定请假记录数据到 UI [六安]
	 * 2018-4-24
	 * @param leaveList
	 */
	private void bindLogDataToUI(List<LeaveItemBean> leaveList) {
		mAdapter = new CommonAdapter<LeaveItemBean>(LeaveListActivity.this,
				leaveList, R.layout.item_lv_leave_history) {
			@Override
			public void convert(ViewHolder helper, final LeaveItemBean item) {
				helper.setText(R.id.tvItemCreateTime, "创建时间:  " + item.getReg_time());
				helper.setText(R.id.tvItemStartTime, "请假时间:  " + item.getStart_time());
				helper.setText(R.id.tvItemStatus, "请假状态: " + item.getLeave_type_text());
			}
		};
		mLvLeavePre.setAdapter(mAdapter);
//		mLvLeavePre.setVisibility(View.VISIBLE);

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

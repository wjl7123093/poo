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
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**   
 * @Title: LeaveListActivity.java 
 * @Package com.mypolice.poo.ui.activity
 * @Description: 请假列表页面
 * @author wangjl  
 * @crdate 2017-8-21
 * @update 2018-5-2		优化UI
 * @version v1.0.3(4)[六安]
 */
@ContentView(R.layout.activity_leave_list)
public class LeaveListActivity extends BaseActivityPoo {

	private static final int REQUEST_CODE_LEAVE = 0x101;

	/** TitleBarView 顶部标题栏 */
	@ViewInject(R.id.titleLeaveList)
	private TitleBarView mTitleLeaveList;

//	@ViewInject(R.id.llAppForLeave)
//	private LinearLayout mLlAppForLeave;

	// 当前无请假 - 提示区
	@ViewInject(R.id.ll_no_leave_title)
	private LinearLayout mLlNoLeaveTitle;
	@ViewInject(R.id.ll_no_leave_btn)
	private LinearLayout mLlNoLeaveBtn;
	@ViewInject(R.id.tvItemStatus)
	private TextView mTvItemStatus;

	@ViewInject(R.id.btn_leave)
	private Button mBtnLeave;
	@ViewInject(R.id.lvLeave)
	private ListView mLvLeave;
	@ViewInject(R.id.lvLeavePre)
	private ListView mLvLeavePre;
	@ViewInject(R.id.tv_count)
	private TextView mTvCounts;

	private CommonAdapter mAdapter;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

	/** 加载进度条 */
    private SweetAlertDialog pDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);

		initView();
		loadData();
		// 获取请假记录数据
//		getLeaveLogNew();
	}
	
	@Override
	public void initView() {
		super.initView();
		mTitleLeaveList.setText("请假申请");
//		mTitleLeaveList.setRightBtnIcon(getString(R.string.icon_leave_history));
//		mTitleLeaveList.setRightBtnOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				CommonFuncUtil.goNextActivityWithNoArgs(LeaveListActivity.this,
//						LeaveHistoryListActivity.class, false);
//			}
//		});

        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("正在加载...");
        pDialog.setCancelable(false);
        pDialog.show();
	}

	private void loadData() {
		getLeaveData();
//		getLeaveDataNew();
	}

	/** 设置无请假提示区的显示状态 */
	private void setNoLeaveBlockVisibility(int visibility) {
//		mLlNoLeaveTitle.setVisibility(visibility);
		mLlNoLeaveBtn.setVisibility(visibility);
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
			pDialog.show();
			// 刷新列表
			loadData();
		}
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
						pDialog.dismiss();
						CommonFuncUtil.getToast(LeaveListActivity.this, e.getMessage());
					}

					@Override
					public void onResponse(String response, int id) {
                        pDialog.dismiss();
						try {
							JSONObject jsonResponse = new JSONObject(response);
							if (jsonResponse.getInt("code") == GlobalSet.APP_SUCCESS) {
								List<LeaveItemBean> leaveList = new ArrayList<LeaveItemBean>();
								LeaveItemBean leave = JSON.parseObject(jsonResponse.getString("data"), LeaveItemBean.class);
								leaveList.add(leave);
								bindDataToUI(leaveList);
								// 隐藏无请假 - 提示区
								setNoLeaveBlockVisibility(View.GONE);
							} else if (jsonResponse.getInt("code") == ApiCode.CODE_EMPTY_DATA) {
								// 空数据，不做处理

							} else if (jsonResponse.getInt("code") == ApiCode.CODE_TOKEN_EXPIRED) {
								// token 失效，踢出当前用户，退到登录页面
								CommonFuncUtil.isTokenExpired(LeaveListActivity.this);
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
                        pDialog.dismiss();
						CommonFuncUtil.getToast(LeaveListActivity.this, e.getMessage());
					}

					@Override
					public void onResponse(String response, int id) {
						pDialog.dismiss();
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
								CommonFuncUtil.isTokenExpired(LeaveListActivity.this);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});

	}

	/**
	 * 一次性获取请假状态，记录[六安]
	 * 2018-4-24
	 */
	private void getLeaveData() {
		String url = GlobalSet.APP_SERVER_URL + "app.drug_user/leaveView";
		OkHttpUtils.get().url(url)
				.addHeader(GlobalSet.APP_TOKEN_KEY, mApplication.getToken())
				.build()
				.execute(new StringCallback() {
					@Override
					public void onError(Call call, Exception e, int id) {
                        pDialog.dismiss();
						CommonFuncUtil.getToast(LeaveListActivity.this, e.getMessage());
					}

					@Override
					public void onResponse(String response, int id) {
						pDialog.dismiss();
						try {
							JSONObject jsonResponse = new JSONObject(response);
							if (jsonResponse.getInt("code") == ApiCode.CODE_SUCCESS) {
								String statusData = jsonResponse.getJSONObject("data").getString("leave");
								org.json.JSONArray arrayLog = jsonResponse.getJSONObject("data").getJSONArray("leave_list");

								if (!TextUtils.isEmpty(statusData) && !statusData.equals("{}")
										&& !statusData.equals("null")) {
									List<LeaveItemBean> leaveList = new ArrayList<LeaveItemBean>();
									LeaveItemBean leave = JSON.parseObject(statusData, LeaveItemBean.class);
									leaveList.add(leave);
									bindDataToUI(leaveList);
									// 隐藏无请假 - 提示区
									setNoLeaveBlockVisibility(View.GONE);
								}
								if (arrayLog.length() > 0) {
									List<LeaveItemBean> leaveList = new ArrayList<LeaveItemBean>();
									LeaveItemBean leave = null;
									for (int i = 0; i < arrayLog.length(); i++) {
										leave = JSON.parseObject(arrayLog.getString(i), LeaveItemBean.class);
//									CommonFuncUtil.getToast(LeaveListActivity.this, leave.toString());
										leaveList.add(leave);
									}
									bindLogDataToUI(leaveList);
								} else {
									mTvCounts.setText("共0条");
								}


							} else if (jsonResponse.getInt("code") == ApiCode.CODE_EMPTY_DATA) {
								// 空数据，不做处理

							} else if (jsonResponse.getInt("code") == ApiCode.CODE_TOKEN_EXPIRED) {
								// token 失效，踢出当前用户，退到登录页面
								CommonFuncUtil.isTokenExpired(LeaveListActivity.this);
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
		mTvItemStatus.setText(leaveList.get(0).getLeave_type_text());

		mAdapter = new CommonAdapter<LeaveItemBean>(LeaveListActivity.this,
				leaveList, R.layout.item_lv_leave) {
			@Override
			public void convert(ViewHolder helper, final LeaveItemBean item) {

//				helper.setText(R.id.tvItemStatus, item.getLeave_type_text());
				helper.setText(R.id.tvItemCreateTime, "创建时间:  " + item.getReg_time());
				helper.setText(R.id.tvItemStartTime, "开始时间:  " + item.getStart_time());
				helper.setText(R.id.tvItemEndTime, "结束时间:  " + item.getEnd_time());
				helper.setText(R.id.tvItemLeaveReason, "请假事由:  " + item.getReason());
				helper.setText(R.id.tvItemDestination, "外出地址:  " + item.getDestination());
				helper.setText(R.id.tvItemDestinStation, "目的地派出所:  " + item.getDestin_police());

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
		mTvCounts.setText("共" + leaveList.size() + "条");

		mAdapter = new CommonAdapter<LeaveItemBean>(LeaveListActivity.this,
				leaveList, R.layout.item_lv_leave_history) {
			@Override
			public void convert(ViewHolder helper, final LeaveItemBean item) {
				if (item == getItem(0)) {
					helper.getView(R.id.view_1).setVisibility(View.INVISIBLE);
				}

				/*helper.setText(R.id.tvItemCreateTime, "创建时间:  " + item.getReg_time());
				helper.setText(R.id.tvItemStartTime, "请假时间:  " + item.getStart_time());*/
				((TextView) helper.getView(R.id.tvItemCreateTime)).setText(Html.fromHtml("创建时间: "
						+ "<font color=\"#999999\">" + item.getReg_time() + "</font>"));
				((TextView) helper.getView(R.id.tvItemStartTime)).setText(Html.fromHtml("请假时间: "
						+ "<font color=\"#999999\">" + item.getStart_time() + "</font>"));

				String status = "";
				int resId = R.mipmap.ic_warning;
				switch (item.getLeave_type()) {
					case 2:	// 已审批
					case 4:	// 已销假
						status = "<font color=\"#53d656\">" + item.getLeave_type_text() + "</font>";
						resId = R.mipmap.ic_ok;
						break;
					case 3: // 审批未通过
						status = "<font color=\"#f14f4f\">" + item.getLeave_type_text() + "</font>";
						resId = R.mipmap.ic_error;
						break;
					default:// 其他（草稿/关闭）
						status = "<font color=\"#ffa400\">" + item.getLeave_type_text() + "</font>";
						resId = R.mipmap.ic_warning;
						break;
				}
				helper.setImageResource(R.id.iconItemStatus, resId);
				((TextView) helper.getView(R.id.tvItemStatus)).setText(Html.fromHtml("请假状态: " + status));
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

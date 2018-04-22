package com.mypolice.poo.ui.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mypolice.poo.R;
import com.mypolice.poo.application.GlobalSet;
import com.mypolice.poo.bean.LeaveBean;
import com.mypolice.poo.bean.LeaveItemBean;
import com.mypolice.poo.util.CommonFuncUtil;
import com.mypolice.poo.util.DateTimeUtil;
import com.mypolice.poo.util.RegexUtil;
import com.mypolice.poo.widget.CenterDialog;
import com.mypolice.poo.widget.CustomDatePicker;
import com.mypolice.poo.widget.TitleBarView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**   
 * @Title: ApplicationForLeaveActivity.java 
 * @Package com.mypolice.poo.ui.activity
 * @Description: 请假申请页面
 * @author wangjl  
 * @crdate 2017-8-21
 * @update 2017-9-14
 * @version v2.0.5(7)
 */
@ContentView(R.layout.activity_application_for_leave)
public class ApplicationForLeaveActivity extends BaseActivityPoo {

	public static final int RESULT_CODE_LEAVE = 0x102;

	/** TitleBarView 顶部标题栏 */
	@ViewInject(R.id.titleApplicationForLeave)
	private TitleBarView mTitleApplicationForLeave;

	/** EditText 外出目的地 */
	@ViewInject(R.id.edtTxtOutwardDestination)
	private EditText mEdtTxtDestination;
	/** EditText 请假事由 */
	@ViewInject(R.id.edtTxtLeaveReason)
	private EditText mEdtTxtLeaveReason;
	/** EditText 开始日期 */
	@ViewInject(R.id.tvStartDate)
	private TextView mTvStartDate;
	/** EditText 结束日期 */
	@ViewInject(R.id.tvEndDate)
	private TextView mTvEndDate;
	/** TextView 请假总天数 */
	@ViewInject(R.id.tvTotalDays)
	private TextView mTvTotoalDays;
	/** LinearLayout 被访人情况 标题栏 */
	@ViewInject(R.id.llRespondentsConditionsTitle)
	private LinearLayout mLlRespondentsConditionsTitle;
	/** ImageView 上下指示箭头 */
	@ViewInject(R.id.ivArrow)
	private ImageView mIvArrow;
	/** LinearLayout 被访人情况 区域 */
	@ViewInject(R.id.llRespondentsConditions)
	private LinearLayout mLlRespondentsConditions;
	/** EditText 姓名 */
	@ViewInject(R.id.edtTxtName)
	private EditText mEdtTxtName;
	/** EditText 关系 */
	@ViewInject(R.id.edtTxtRelationship)
	private EditText mEdtTxtRelationship;
	/** EditText 职业 */
	@ViewInject(R.id.edtTxtJob)
	private EditText mEdtTxtJob;
	/** EditText 单位 */
	@ViewInject(R.id.edtTxtUnit)
	private EditText mEdtTxtUnit;
	/** EditText 地址 */
	@ViewInject(R.id.edtTxtAddress)
	private EditText mEdtTxtAddress;
	/** EditText 电话 */
	@ViewInject(R.id.edtTxtPhone)
	private EditText mEdtTxtPhone;
	/** EditText 备注 */
	@ViewInject(R.id.edtTxtRemarks)
	private EditText mEdtTxtRemarks;

	/** Button 存为草稿 */
	@ViewInject(R.id.btnSaveDraft)
	private Button mBtnSaveDraft;
	/** Button 提交请假 */
	@ViewInject(R.id.btnSubmitLeave)
	private Button mBtnSubmitLeave;

	private boolean mIsDisplay = false;

	/** 时间选择器 */
	private CustomDatePicker customDatePicker1, customDatePicker2;
	private Date mDateStart = null;
	private Date mDateEnd = null;

	private String from = "";
	private LeaveItemBean mLeaveItemBean = new LeaveItemBean();
	private LeaveBean mLeaveBean = new LeaveBean();
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private String wraningInfo = "结束日期不能小于起始日期";

	/** 加载进度条 */
	private CenterDialog centerDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);

		Bundle bundle = getIntent().getExtras();
		from = bundle.getString("from");
		if (from.equals("edit")) {
			mLeaveItemBean = bundle.getParcelable("leaveBean");
			mLeaveBean = doTransferLeaveItemToLeave(mLeaveItemBean);
		}

		initView();
		initDatePicker();
		initData();
	}
	
	@Override
	public void initView() {
		super.initView();
		centerDialog = new CenterDialog(ApplicationForLeaveActivity.this, R.layout.dialog_wap_loading,
				new int[]{});

		mTitleApplicationForLeave.setText("请假申请");
		mLlRespondentsConditions.setVisibility(View.GONE);

	}

	/**
	 * 初始化数据（仅编辑状态下）
	 */
	private void initData() {
		if (from.equals("edit") && null != mLeaveItemBean) {
			// mLeaveItemBean 里的 start_time 和 end_time 是 yyyy/MM/dd 格式，
			// 需要转换为 日期选择器所需的 yyyy-MM-dd 格式
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
			try {
				mDateStart = sdf.parse(mLeaveItemBean.getStart_time());
				mDateEnd = sdf.parse(mLeaveItemBean.getStart_time());
			} catch (ParseException e) {
				e.printStackTrace();
			}

			mEdtTxtDestination.setText(mLeaveItemBean.getDestination());
			mEdtTxtLeaveReason.setText(mLeaveItemBean.getReason());
			mTvStartDate.setText(sdf2.format(mDateStart));
			mTvEndDate.setText(sdf2.format(mDateEnd));
			mTvTotoalDays.setText(mLeaveItemBean.getLeave_num() + "");
			mEdtTxtName.setText(mLeaveItemBean.getVisitor_name());
			mEdtTxtRelationship.setText(mLeaveItemBean.getVisitor_relation());
			mEdtTxtJob.setText(mLeaveItemBean.getVisitor_profession());
			mEdtTxtUnit.setText(mLeaveItemBean.getVisitor_company());
			mEdtTxtAddress.setText(mLeaveItemBean.getVisitor_address());
			mEdtTxtPhone.setText(mLeaveItemBean.getVisitor_tel());
			mEdtTxtRemarks.setText(mLeaveItemBean.getRemark());

		}
	}

	/** 初始化时间选择器 */
	private void initDatePicker() {
		// sdf 为 时间选择器所需要的格式， sdf2 为 poo 程序具体所需格式
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
		final SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
		String now = sdf.format(new Date());
		mTvStartDate.setText(now.split(" ")[0]);
		mTvEndDate.setText(now.split(" ")[0]);
		try {
			mDateStart = sdf2.parse(now.split(" ")[0]);
			mDateEnd = sdf2.parse(now.split(" ")[0]);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		customDatePicker1 = new CustomDatePicker(this, new CustomDatePicker.ResultHandler() {
			@Override
			public void handle(String time) { // 回调接口，获得选中的时间
				String startTime = time.split(" ")[0];
				mTvStartDate.setText(startTime);
				try {
					mDateStart = sdf2.parse(startTime);
					if (((mDateEnd.getTime()-mDateStart.getTime())/(60*60*1000*24)) < 0) {
						// 起始日期 < 结束日期
						CommonFuncUtil.getToast(ApplicationForLeaveActivity.this, wraningInfo);
						return;
					} else {
						// 计算间隔天数
						mTvTotoalDays.setText(
								((mDateEnd.getTime()-mDateStart.getTime())/(60*60*1000*24) + 1) + "");
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}, now, "2099-01-01 00:00"); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
		customDatePicker1.showSpecificTime(false); // 不显示时和分
		customDatePicker1.setIsLoop(false); // 不允许循环滚动

		customDatePicker2 = new CustomDatePicker(this, new CustomDatePicker.ResultHandler() {
			@Override
			public void handle(String time) { // 回调接口，获得选中的时间
				String endTime = time.split(" ")[0];
				mTvEndDate.setText(endTime);
				try {
					mDateEnd = sdf2.parse(endTime);
					if (((mDateEnd.getTime()-mDateStart.getTime())/(60*60*1000*24)) < 0) {
						// 起始日期 < 结束日期
						CommonFuncUtil.getToast(ApplicationForLeaveActivity.this, wraningInfo);
						return;
					} else {
						// 计算间隔天数
						mTvTotoalDays.setText(
								((mDateEnd.getTime()-mDateStart.getTime())/(60*60*1000*24) + 1) + "");
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}, now, "2099-01-01 00:00"); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
		customDatePicker2.showSpecificTime(false); // 不显示时和分
		customDatePicker2.setIsLoop(false); // 不允许循环滚动
	}

	/**
	 * 点击 显示/隐藏 被访人区域
	 * @param v
     */
	@OnClick(R.id.llRespondentsConditionsTitle)
	public void onLlRespondentsConditionsTitleClick(View v) {
		if (mIsDisplay) {
			mIvArrow.setImageResource(R.mipmap.ic_down_arrow);
			mLlRespondentsConditionsTitle.setBackgroundColor(getResources().getColor(R.color.white));
			mLlRespondentsConditions.setVisibility(View.GONE);
			mIsDisplay = false;
		} else {
			mIvArrow.setImageResource(R.mipmap.ic_up_arrow);
			mLlRespondentsConditionsTitle.setBackgroundColor(getResources().getColor(R.color.GREY_L));
			mLlRespondentsConditions.setVisibility(View.VISIBLE);
			mIsDisplay = true;
		}
	}

	@OnClick(R.id.tvStartDate)
	public void onTvStartDataClick(View v) {
		// 日期格式为yyyy-MM-dd
		customDatePicker1.show(mTvStartDate.getText().toString());
	}

	@OnClick(R.id.tvEndDate)
	public void onTvEndDataClick(View v) {
		// 日期格式为yyyy-MM-dd
		customDatePicker2.show(mTvEndDate.getText().toString());
	}

	/**
	 * 点击 保存草稿
	 * @param v
     */
	@OnClick(R.id.btnSaveDraft)
	public void onBtnSaveDraftClick(View v) {

		// 验证
		if (!checkIsLegal()) return;

		setLeaveData();
		mLeaveBean.setLeave_type(0);

		centerDialog.show();
		if (from.equals("edit"))
			putLeaveData(mLeaveItemBean.getId());
		else
			postLeaveData();
	}

	/**
	 * 点击 提交请假
	 * @param v
     */
	@OnClick(R.id.btnSubmitLeave)
	public void onBtnSubmitLeaveClick(View v) {

		// 验证
		if (!checkIsLegal()) return;

		setLeaveData();
		mLeaveBean.setLeave_type(1);

		centerDialog.show();
		if (from.equals("edit"))
			putLeaveData(mLeaveItemBean.getId());
		else
			postLeaveData();
	}

	/**
	 * 新增请假数据
     */
	private void postLeaveData() {
		String url = GlobalSet.APP_SERVER_URL + "community_leave";
		OkHttpUtils.post().url(url)
				.addHeader("token", mApplication.getToken())
//				.addParams("token", mApplication.getToken())
				.addParams("destination", mLeaveBean.getDestination())
				.addParams("reason", mLeaveBean.getReason())
				.addParams("start_time", mLeaveBean.getStart_time() + "")
				.addParams("end_time", mLeaveBean.getEnd_time() + "")
				.addParams("leave_num", mLeaveBean.getLeave_num() + "")
				.addParams("visitor_name", mLeaveBean.getVisitor_name())
				.addParams("visitor_profession", mLeaveBean.getVisitor_profession())
				.addParams("visitor_relation", mLeaveBean.getVisitor_relation())
				.addParams("visitor_company", mLeaveBean.getVisitor_company())
				.addParams("visitor_address", mLeaveBean.getVisitor_address())
				.addParams("visitor_tel", mLeaveBean.getVisitor_tel())
				.addParams("remark", mLeaveBean.getRemark())
				.addParams("drug_user_id", mLeaveBean.getDrug_user_id() + "")
				.addParams("leave_type", mLeaveBean.getLeave_type() + "")
				.addParams("community_drug_reg_id", mLeaveBean.getCommunity_drug_reg_id() + "")
				.build()
				.execute(new StringCallback() {
					@Override
					public void onError(Call call, Exception e, int id) {
						centerDialog.cancel();
						CommonFuncUtil.getToast(ApplicationForLeaveActivity.this, e.getMessage());
					}

					@Override
					public void onResponse(String response, int id) {
//						CommonFuncUtil.getToast(ApplicationForLeaveActivity.this, response);
						centerDialog.cancel();
						try {
							JSONObject jsonResponse = new JSONObject(response);
							if (jsonResponse.getInt("code") == 0
									|| jsonResponse.getInt("code") == 200) {
								int leaveId = jsonResponse.getInt("data");
								setResult(RESULT_CODE_LEAVE);
								ApplicationForLeaveActivity.this.finish();
							} else if (jsonResponse.getInt("code") == 1007) {
								// token 失效，踢出当前用户，退到登录页面
								CommonFuncUtil.getToast(ApplicationForLeaveActivity.this,
										"当前用户已在别处登录，请重新登录");
								removeALLActivity();
								CommonFuncUtil.goNextActivityWithNoArgs(ApplicationForLeaveActivity.this,
										LoginActivity.class, false);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
	}

	/**
	 * 修改请假数据
	 */
	private void putLeaveData(int leaveId) {
		String url = GlobalSet.APP_SERVER_URL + "community_leave/" + leaveId;
		OkHttpUtils.put().url(url)
				.addHeader("token", mApplication.getToken())
				.requestBody(RequestBody.create(MediaType.parse("application/json"), JSON.toJSONString(mLeaveBean)))
				.build()
				.execute(new StringCallback() {
					@Override
					public void onError(Call call, Exception e, int id) {
						centerDialog.cancel();
						CommonFuncUtil.getToast(ApplicationForLeaveActivity.this, e.getMessage());
					}

					@Override
					public void onResponse(String response, int id) {
//						CommonFuncUtil.getToast(ApplicationForLeaveActivity.this, response);
						centerDialog.cancel();
						try {
							JSONObject jsonResponse = new JSONObject(response);
							if (jsonResponse.getInt("code") == 0
									|| jsonResponse.getInt("code") == 200) {
								int result = jsonResponse.getInt("data");
								if (1 == result) {
									CommonFuncUtil.getToast(ApplicationForLeaveActivity.this, "修改成功");
									setResult(RESULT_CODE_LEAVE);
									ApplicationForLeaveActivity.this.finish();
								} else if (jsonResponse.getInt("code") == 1007) {
									// token 失效，踢出当前用户，退到登录页面
									CommonFuncUtil.getToast(ApplicationForLeaveActivity.this,
											"当前用户已在别处登录，请重新登录");
									removeALLActivity();
									CommonFuncUtil.goNextActivityWithNoArgs(ApplicationForLeaveActivity.this,
											LoginActivity.class, false);
								}
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
	}

	/**
	 * 绑定数据到 LeaveData
	 */
	private void setLeaveData() {
		mLeaveBean.setDestination(mEdtTxtDestination.getText().toString().trim());
		mLeaveBean.setReason(mEdtTxtLeaveReason.getText().toString().trim());
		try {
			mLeaveBean.setStart_time(sdf.parse(mTvStartDate.getText().toString()).getTime() / 1000);
			mLeaveBean.setEnd_time(sdf.parse(mTvEndDate.getText().toString()).getTime() / 1000);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		mLeaveBean.setLeave_num(Integer.parseInt(mTvTotoalDays.getText().toString()));
		mLeaveBean.setVisitor_name(mEdtTxtName.getText().toString().trim());
		mLeaveBean.setVisitor_relation(mEdtTxtRelationship.getText().toString().trim());
		mLeaveBean.setVisitor_profession(mEdtTxtJob.getText().toString().trim());
		mLeaveBean.setVisitor_company(mEdtTxtUnit.getText().toString().trim());
		mLeaveBean.setVisitor_address(mEdtTxtAddress.getText().toString().trim());
		mLeaveBean.setVisitor_tel(mEdtTxtPhone.getText().toString().trim());
		mLeaveBean.setRemark(mEdtTxtRemarks.getText().toString().trim());
		mLeaveBean.setDrug_user_id(mApplication.getUserID());
		mLeaveBean.setCommunity_drug_reg_id(mApplication.getGroupID());
	}

	/**
	 * 检测数据是否合法
	 */
	private boolean checkIsLegal() {
		boolean isLegal = true;

		// 日期判断
		try {
			mDateStart = sdf.parse(mTvStartDate.getText().toString());
			mDateEnd = sdf.parse(mTvEndDate.getText().toString());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (((mDateEnd.getTime()-mDateStart.getTime())/(60*60*1000*24)) < 0) {
			// 结束日期 < 起始日期
			CommonFuncUtil.getToast(ApplicationForLeaveActivity.this, wraningInfo);
			return false;
		}

		// 手机号判断
		String phone = mEdtTxtPhone.getText().toString().trim();
		if (!TextUtils.isEmpty(phone)
				&& (!RegexUtil.isMobileNumber(phone) && !RegexUtil.isPhoneNumber(phone))) {
			CommonFuncUtil.getToast(ApplicationForLeaveActivity.this,
					"手机/电话格式不正确");
			return false;
		}
		return true;

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

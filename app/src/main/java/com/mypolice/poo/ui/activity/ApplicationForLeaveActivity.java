package com.mypolice.poo.ui.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mypolice.poo.R;
import com.mypolice.poo.application.ApiCode;
import com.mypolice.poo.application.GlobalSet;
import com.mypolice.poo.bean.LeaveBean;
import com.mypolice.poo.bean.LeaveItemBean;
import com.mypolice.poo.util.CommonFuncUtil;
import com.mypolice.poo.util.DateTimeUtil;
import com.mypolice.poo.util.FileUtils;
import com.mypolice.poo.util.ImageTools;
import com.mypolice.poo.util.RegexUtil;
import com.mypolice.poo.widget.CenterDialog;
import com.mypolice.poo.widget.CustomDatePicker;
import com.mypolice.poo.widget.IconView;
import com.mypolice.poo.widget.TitleBarView;
import com.yixia.camera.demo.ui.record.VideoPlayerActivity;
import com.yixia.camera.demo.util.Constant;
import com.yixia.weibo.sdk.VCamera;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
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
	@ViewInject(R.id.edt_police_station)
	private EditText mEdtTxtDestinPolice;
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
//	@ViewInject(R.id.tvTotalDays)
//	private TextView mTvTotoalDays;
	/** LinearLayout 被访人情况 标题栏 */
//	@ViewInject(R.id.llRespondentsConditionsTitle)
//	private LinearLayout mLlRespondentsConditionsTitle;
	/** ImageView 上下指示箭头 */
//	@ViewInject(R.id.ivArrow)
//	private ImageView mIvArrow;
	/** LinearLayout 被访人情况 区域 */
//	@ViewInject(R.id.llRespondentsConditions)
//	private LinearLayout mLlRespondentsConditions;
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

	@ViewInject(R.id.llPhoto1)
	private LinearLayout mLlPhoto1;
	@ViewInject(R.id.rlPhoto1)
	private RelativeLayout mRlPhoto1;
	@ViewInject(R.id.ivPhoto1)
	private ImageView mivPhoto1;
	@ViewInject(R.id.iconDel1)
	private IconView mIconDel1;

	/** 是否已拍摄 标识 */
	private boolean isPhoto1 = false;	// 照片1是否已经存在
	private boolean isTakingPhoto1 = false;	// 是否正在拍摄照片1
	// 路径
	private String mBmpPath1 = "";
	// 文件
	private File mFileBmp1 = null;
	// 文件名
	private String mFileNameBmp1 = "";
	// 临时保存图片
	private Bitmap mBitmap1 = null;

	// 拍照
	public static final int IMAGE_CAPTURE = 1;

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
	private SweetAlertDialog pDialog;

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

		// 获取 onSaveInstanseState() 保存的值
		if (null != savedInstanceState) {

			// 获取 拍摄照片翻转后 Activity 销毁之前保存的数据
			isTakingPhoto1 = savedInstanceState.getBoolean("isTakingPhoto1");
			if (isTakingPhoto1) {
				mBitmap1 = (Bitmap) savedInstanceState.getParcelable("bitmap");
				mFileNameBmp1 = savedInstanceState.getString("fileName");
				mBmpPath1 = savedInstanceState.getString("filePath");

				mivPhoto1.setImageBitmap(mBitmap1);
				mLlPhoto1.setVisibility(View.GONE);
				mRlPhoto1.setVisibility(View.VISIBLE);
				isPhoto1 = true;
			}
		}
	}
	
	@Override
	public void initView() {
		super.initView();
		pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
		pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
		pDialog.setTitleText("正在提交...");
		pDialog.setCancelable(false);

		mTitleApplicationForLeave.setText("请假申请");
//		mLlRespondentsConditions.setVisibility(View.GONE);

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
//			mTvTotoalDays.setText(mLeaveItemBean.getLeave_num() + "");
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
//						mTvTotoalDays.setText(
//								((mDateEnd.getTime()-mDateStart.getTime())/(60*60*1000*24) + 1) + "");
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
//						mTvTotoalDays.setText(
//								((mDateEnd.getTime()-mDateStart.getTime())/(60*60*1000*24) + 1) + "");
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
	/*@OnClick(R.id.llRespondentsConditionsTitle)
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
	}*/

	@OnClick(R.id.llPhoto1)
	public void onLlPhoto1Click(View v) {
		isTakingPhoto1 = true;
//		isTakingPhoto2 = false;
		captureImage(FileUtils.SDPATH);
	}

	@OnClick(R.id.iconDel1)
	public void onIconDel1Click(View v) {
		mLlPhoto1.setVisibility(View.VISIBLE);
		mRlPhoto1.setVisibility(View.GONE);

		mBmpPath1 = "";
		FileUtils.deleteFile(new File(mBmpPath1));
	}

	@OnClick(R.id.ivPhoto1)
	public void onIvPhoto1Click(View v) {
		goToImagePreviewActivity(mBmpPath1);
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

		pDialog.show();
		if (from.equals("edit"))
			putLeaveData(mLeaveItemBean.getId());
		else
			postLeaveDataNew();
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

		pDialog.show();
		if (from.equals("edit"))
			putLeaveData(mLeaveItemBean.getId());
		else
			postLeaveDataNew();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK && resultCode != RESULT_CANCELED) {	// 拍照/相册

			String fileName;
			switch (requestCode) {
				case IMAGE_CAPTURE:// 拍照返回

					Bitmap newBitmap = null;
					try {
						newBitmap = ImageTools.revitionImageSize(Environment.getExternalStorageDirectory() + "/image.jpg");
						int degree = getBitmapDegree(Environment.getExternalStorageDirectory() + "/image.jpg");
						if (degree != 0) {
							newBitmap = rotateBitmapByDegree(newBitmap, degree);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
//					// 将保存在本地的图片取出并缩小后显示在界面上
//					Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/image.jpg");
//					Bitmap newBitmap = ImageTools.zoomBitmap(bitmap,bitmap.getWidth() / SCALE, bitmap.getHeight() / SCALE);
					// 由于Bitmap内存占用较大，这里需要回收内存，否则会报out of memory异常
//					bitmap.recycle();

					// 生成一个图片文件名
					fileName = String.valueOf(System.currentTimeMillis());
					// 将处理过的图片添加到缩略图列表并保存到本地
					ImageTools.savePhotoToSDCard(newBitmap, FileUtils.SDPATH,fileName);


					if (isTakingPhoto1) {
						mBitmap1 = newBitmap;
						mFileNameBmp1 = fileName + ".jpg";
						mBmpPath1 = FileUtils.SDPATH + mFileNameBmp1;
						mFileBmp1 = new File(mBmpPath1);

						mivPhoto1.setImageBitmap(newBitmap);
						mLlPhoto1.setVisibility(View.GONE);
						mRlPhoto1.setVisibility(View.VISIBLE);
						isPhoto1 = true;
					}

					break;

				default:
					break;
			}
		}
	}

	/**
	 * 点击图片 跳转到预览页面
	 */
	private void goToImagePreviewActivity(String bmpPath) {
		Bundle bundle = new Bundle();
		bundle.putString("bmpPath", bmpPath);
		CommonFuncUtil.goNextActivityWithArgs(ApplicationForLeaveActivity.this,
				ImagePreviewActivity.class, bundle, false);
	}

	/**
	 * 拍照
	 *
	 * @param path
	 *            照片存放的路径
	 */
	public void captureImage(String path) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// 指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
		Uri uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "image.jpg"));
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		startActivityForResult(intent, IMAGE_CAPTURE);
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
						pDialog.dismiss();
						CommonFuncUtil.getToast(ApplicationForLeaveActivity.this, e.getMessage());
					}

					@Override
					public void onResponse(String response, int id) {
						pDialog.dismiss();
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
	 * 新增请假数据[六安]
	 * 2018-4-24
	 */
	private void postLeaveDataNew() {
		String url = GlobalSet.APP_SERVER_URL + "app.drug_user/addLeave";
		OkHttpUtils.post().url(url)
				.addHeader(GlobalSet.APP_TOKEN_KEY, mApplication.getToken())
				.addFile("photo_url", mFileNameBmp1, new File(mBmpPath1))
				.addParams("destination", mLeaveBean.getDestination())
				.addParams("reason", mLeaveBean.getReason())
				.addParams("start_time", mLeaveBean.getStart_time() + "")
				.addParams("end_time", mLeaveBean.getEnd_time() + "")
				.addParams("destin_police", mLeaveBean.getDestin_police() + "")
				.addParams("visitor_name", mLeaveBean.getVisitor_name())
				.addParams("visitor_profession", mLeaveBean.getVisitor_profession())
				.addParams("visitor_relation", mLeaveBean.getVisitor_relation())
				.addParams("visitor_company", mLeaveBean.getVisitor_company())
				.addParams("visitor_address", mLeaveBean.getVisitor_address())
				.addParams("visitor_tel", mLeaveBean.getVisitor_tel())
				.build()
				.execute(new StringCallback() {
					@Override
					public void onError(Call call, Exception e, int id) {
						pDialog.dismiss();
						CommonFuncUtil.getToast(ApplicationForLeaveActivity.this, e.getMessage());
					}

					@Override
					public void onResponse(String response, int id) {
						pDialog.dismiss();
						try {
							JSONObject jsonResponse = new JSONObject(response);
							if (jsonResponse.getInt("code") == ApiCode.CODE_SUCCESS) {
//								int leaveId = jsonResponse.getInt("data");
								setResult(RESULT_CODE_LEAVE);
								ApplicationForLeaveActivity.this.finish();
							} else if (jsonResponse.getInt("code") == ApiCode.CODE_TOKEN_EXPIRED	) {
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
						pDialog.dismiss();
						CommonFuncUtil.getToast(ApplicationForLeaveActivity.this, e.getMessage());
					}

					@Override
					public void onResponse(String response, int id) {
						pDialog.dismiss();
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
		mLeaveBean.setDestin_police(mEdtTxtDestinPolice.getText().toString().trim());
		mLeaveBean.setReason(mEdtTxtLeaveReason.getText().toString().trim());
		try {
			mLeaveBean.setStart_time(sdf.parse(mTvStartDate.getText().toString()).getTime() / 1000);
			mLeaveBean.setEnd_time(sdf.parse(mTvEndDate.getText().toString()).getTime() / 1000);
		} catch (ParseException e) {
			e.printStackTrace();
		}
//		mLeaveBean.setLeave_num(Integer.parseInt(mTvTotoalDays.getText().toString()));
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

		if (TextUtils.isEmpty(mEdtTxtLeaveReason.getText().toString().trim())
				|| TextUtils.isEmpty(mEdtTxtDestination.getText().toString().trim())
				|| TextUtils.isEmpty(mEdtTxtDestinPolice.getText().toString().trim())) {
			CommonFuncUtil.getToast(ApplicationForLeaveActivity.this,
					"请完善请假信息");
			return false;
		}

		if (TextUtils.isEmpty(mBmpPath1)) {
			CommonFuncUtil.getToast(ApplicationForLeaveActivity.this,
					"请上传请假条照片");
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

	/**
	  * 读取图片的旋转的角度
	  *
	  * @param path
	  *            图片绝对路径
	  * @return 图片的旋转角度
	  */
	private int getBitmapDegree(String path) {
		int degree = 0;
		try {
			// 从指定路径下读取图片，并获取其EXIF信息
			ExifInterface exifInterface = new ExifInterface(path);
			// 获取图片的旋转信息
			int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					degree = 90;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					degree = 180;
					break;
				case ExifInterface.ORIENTATION_ROTATE_270:
					degree = 270;
					break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	/**
	  * 将图片按照某个角度进行旋转
	  *
	  * @param bm
	  *            需要旋转的图片
	  * @param degree
	  *            旋转角度
	  * @return 旋转后的图片
	  */
	public static Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
		Bitmap returnBm = null;

		// 根据旋转角度，生成旋转矩阵
		Matrix matrix = new Matrix();
		matrix.postRotate(degree);
		try {
			// 将原始图片按照旋转矩阵进行旋转，并得到新的图片
			returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
		} catch (OutOfMemoryError e) {
		}
		if (returnBm == null) {
			returnBm = bm;
		}
		if (bm != returnBm) {
			bm.recycle();
		}
		return returnBm;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		// 防止部分 三星手机 因拍摄照片后，屏幕翻转导致 Activity 周期重建从而导致的数据丢失
		// 故在因翻转导致 Activity 销毁之前保存 拍照后的数据
		outState.putBoolean("isTakingPhoto1", isTakingPhoto1);
		outState.putParcelable("bitmap", mBitmap1);
		outState.putString("fileName", mFileNameBmp1);
		outState.putString("filePath", mBmpPath1);

	}

}

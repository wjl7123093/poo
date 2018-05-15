package com.mypolice.poo.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.map.Text;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mypolice.poo.R;
import com.mypolice.poo.application.ApiCode;
import com.mypolice.poo.application.GlobalSet;
import com.mypolice.poo.bean.FileBean;
import com.mypolice.poo.bean.LeaveBean;
import com.mypolice.poo.bean.LeaveItemBean;
import com.mypolice.poo.service.KeepLiveService;
import com.mypolice.poo.service.LocationService;
import com.mypolice.poo.util.CommonFuncUtil;
import com.mypolice.poo.util.FileUtils;
import com.mypolice.poo.util.GPSUtils;
import com.mypolice.poo.util.ImageTools;
import com.mypolice.poo.util.keeping.ScreenManager;
import com.mypolice.poo.widget.CenterDialog;
import com.mypolice.poo.widget.CustomDatePicker;
import com.mypolice.poo.widget.IconView;
import com.mypolice.poo.widget.TitleBarView;
import com.yixia.camera.demo.ui.record.MediaRecorderActivity;
import com.yixia.camera.demo.ui.record.VideoPlayerActivity;
import com.yixia.camera.demo.util.Constant;
import com.yixia.weibo.sdk.VCamera;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
//import com.yixia.camera.demo.*;
//import com.yixia.camera.demo.ui.record.MediaRecorderActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**   
 * @Title: AboutUsActivity.java 
 * @Package com.mypolice.poo.ui.activity
 * @Description: 尿检页面
 * @author wangjl  
 * @crdate 2017-8-22
 * @update 2017-11-24
 * @version v2.1.2(14)
 */
@ContentView(R.layout.activity_uran)
public class URANActivity extends BaseActivityPoo {

	public static final int RESULT_CODE_URAN = 0x202;
	public static final int REQUEST_CODE_SIGNATURE = 0x301;	// 请求手写签名页面 code

	/*private static URANActivity instance;

	public URANActivity() {}

	//单例模式中获取唯一的MyApplication实例
	public synchronized static URANActivity getInstance()
	{
		if(null == instance)
		{
			instance = new URANActivity();
		}
		return instance;

	}*/

	private LocationService locationService;
	private double mLongitude = 0.0f;
	private double mLatitude = 0.0f;
	private String mAddress = "";

	private static final int REQ_CODE = 10001;

	/** TitleBarView 顶部标题栏 */
	@ViewInject(R.id.titleURAN)
	private TitleBarView mTitleURAN;

	/** EditText 尿检日期 */
	@ViewInject(R.id.ll_datetime)
	private LinearLayout mLlDatetime;
	@ViewInject(R.id.ll_result)
	private LinearLayout mLlResult;
	@ViewInject(R.id.tv_datetime)
	private TextView mTvStartDate;
	@ViewInject(R.id.tv_result)
	private TextView mTvResult;
	@ViewInject(R.id.edt_department)
	private EditText mEdtDepartment;
	@ViewInject(R.id.edt_department_username)
	private EditText mEdtDepartmentUserName;
	@ViewInject(R.id.edt_remark)
	private EditText mEdtRemark;

	@ViewInject(R.id.llPhoto1)
	private LinearLayout mLlPhoto1;
	@ViewInject(R.id.llPhoto2)
	private LinearLayout mLlPhoto2;
	@ViewInject(R.id.llVideo)
	private LinearLayout mLlVideo;
	@ViewInject(R.id.rlPhoto1)
	private RelativeLayout mRlPhoto1;
	@ViewInject(R.id.rlPhoto2)
	private RelativeLayout mRlPhoto2;
	@ViewInject(R.id.rlVideo)
	private RelativeLayout mRlVideo;
	@ViewInject(R.id.ivPhoto1)
	private ImageView mivPhoto1;
	@ViewInject(R.id.ivPhoto2)
	private ImageView mivPhoto2;
	@ViewInject(R.id.ivVideo)
	private ImageView mivVideo;
	@ViewInject(R.id.iconDel1)
	private IconView mIconDel1;
	@ViewInject(R.id.iconDel2)
	private IconView mIconDel2;
	@ViewInject(R.id.iconDel3)
	private IconView mIconDel3;
	@ViewInject(R.id.ivPlay)
	private ImageView mIvPlay;
//	@ViewInject(R.id.ivSignatureImage)
//	private ImageView mIvSignatureImage;

//	@ViewInject(R.id.btnSignature)
//	private Button mBtnSignature;
	@ViewInject(R.id.btn_upload)
	private Button mBtnUpload;
	@ViewInject(R.id.btnSendReport)
	private Button mBtnSendReport;

	private int mTaskId;	// 任务 ID

	/** 是否已拍摄 标识 */
	private boolean isPhoto1 = false;	// 照片1是否已经存在
	private boolean isPhoto2 = false;	// 照片2是否已经存在
	private boolean isVideo = false;	// 视频是否已经存在
	private boolean isTakingPhoto1 = false;	// 是否正在拍摄照片1
	private boolean isTakingPhoto2 = false;	// 是否正在拍摄照片2
	// 路径
	private String mBmpPath1 = "";
	private String mBmpPath2 = "";
	private String mVideoPath = "";		// 视频路径
	private String mSignatureBmpPath = "";
	private String mCoverPath = "";		// 缩略图路径
	private String mVideoDirPath = "";	// video 存放的文件夹路径
	private String mFilePath = "";		// 文件路径
	// 文件
	private File mFileBmp1 = null;
	private File mFileBmp2 = null;
	private File mFileVideo = null;
	private File mFileSignatureBmp = null;
	// 文件名
	private String mFileNameBmp1 = "";
	private String mFileNameBmp2 = "";
	private String mFileNameVideo = "";
	private String mFileNameSignatureBmp = "test_sign.jpg";
	// 临时保存图片
	private Bitmap mBitmap1 = null;
	private Bitmap mBitmap2 = null;

	private String uploadPhoto = "";
	private String uploadVideo = "";

	// 拍照
	public static final int IMAGE_CAPTURE = 1;
	// 从相册选择
	public static final int IMAGE_SELECT = 2;
	// 从视频库中选择
	public static final int VIDEO_SELECT = 3;
	// 从文件管理器选择
	public static final int FILE_SELECT = 0x400;
	// 照片缩小比例
	private static final int SCALE = 5;

	/** 时间选择器 */
	private CustomDatePicker customDatePicker1;
	private Date mDateStart = null;

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private String wraningInfo = "结束日期不能小于起始日期";

	private int mResult = 0;	// 0 阴性，1 阳性

	/** 加载进度条 */
	private CenterDialog centerDialog;
	private SweetAlertDialog pDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);

		Bundle bundle = getIntent().getExtras();
		mTaskId = bundle.getInt("taskId");

		initView();
		initDatePicker();

		// 获取 onSaveInstanseState() 保存的值
		if (null != savedInstanceState) {

			mSignatureBmpPath = savedInstanceState.getString("filePathSignature");
			// 获取 拍摄照片翻转后 Activity 销毁之前保存的数据
			isTakingPhoto1 = savedInstanceState.getBoolean("isTakingPhoto1");
			isTakingPhoto2 = savedInstanceState.getBoolean("isTakingPhoto2");

			if (isTakingPhoto1 && !isTakingPhoto2) {
				mBitmap1 = (Bitmap) savedInstanceState.getParcelable("bitmap1");
				mFileNameBmp1 = savedInstanceState.getString("fileName1");
				mBmpPath1 = savedInstanceState.getString("filePath1");

				mivPhoto1.setImageBitmap(mBitmap1);
				mLlPhoto1.setVisibility(View.GONE);
				mRlPhoto1.setVisibility(View.VISIBLE);
			} else if (!isTakingPhoto1 && isTakingPhoto2) {
				mBitmap2 = (Bitmap) savedInstanceState.getParcelable("bitmap2");
				mFileNameBmp2 = savedInstanceState.getString("fileName2");
				mBmpPath2 = savedInstanceState.getString("filePath2");

				mivPhoto2.setImageBitmap(mBitmap2);
				mLlPhoto2.setVisibility(View.GONE);
				mRlPhoto2.setVisibility(View.VISIBLE);
			}

		}
	}
	
	@Override
	public void initView() {
		super.initView();
		mTitleURAN.setText("尿检");

		pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
		pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
		pDialog.setTitleText("正在提交...");
		pDialog.setCancelable(false);
//		pDialog.show();
	}

	/**
	 * 签名
	 * @param v
     */
	/*@OnClick(R.id.btnSignature)
	public void onBtnSignature(View v) {
		CommonFuncUtil.goNextActivityWithNoArgsForResult(URANActivity.this,
				SignaturepadActivity.class, REQUEST_CODE_SIGNATURE);
	}*/

	/**
	 * 上传文件
	 */
	@OnClick(R.id.btn_upload)
	public void onBtnUpload(View v) {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		//intent.setType(“image/*”);//选择图片
		//intent.setType(“audio/*”); //选择音频
		//intent.setType(“video/*”); //选择视频 （mp4 3gp 是android支持的视频格式）
		//intent.setType(“video/*;image/*”);//同时选择视频和图片
		intent.setType("*/*");//无类型限制
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		startActivityForResult(intent, FILE_SELECT);

	}

	/**
	 * 提交 尿检 信息
	 */
	@OnClick(R.id.btnSendReport)
	public void onBtnSendReport(View v) {
//		doUploadFile();
		if (TextUtils.isEmpty(mBmpPath1) || TextUtils.isEmpty(mBmpPath2)
				|| TextUtils.isEmpty(mVideoPath)
				|| TextUtils.isEmpty(mTvResult.getText().toString().trim())
				|| TextUtils.isEmpty(mEdtDepartment.getText().toString().trim())
				|| TextUtils.isEmpty(mEdtDepartmentUserName.getText().toString().trim())
				|| TextUtils.isEmpty(mEdtRemark.getText().toString().trim())) {
			CommonFuncUtil.getToast(URANActivity.this, "请完善信息后上传");
			return;
		}

		if (!GPSUtils.isOPen(URANActivity.this)) {  // 未开启 gps
			CommonFuncUtil.getToast(URANActivity.this, "未开启GPS, 请先打开GPS再上传");

			// 跳转到 gps 设置页面
			Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			URANActivity.this.startActivity(settingsIntent);

		} else {    // 开启GPS，运行服务
			pDialog.show();
			// 先执行定位操作
			doLoc();
		}
	}

	@OnClick(R.id.llPhoto1)
	public void onLlPhoto1Click(View v) {
		isTakingPhoto1 = true;
		isTakingPhoto2 = false;
		captureImage(FileUtils.SDPATH);
	}

	@OnClick(R.id.llPhoto2)
	public void onLlPhoto2Click(View v) {
		isTakingPhoto1 = false;
		isTakingPhoto2 = true;
		captureImage(FileUtils.SDPATH);
	}

	@OnClick(R.id.llVideo)
	public void onLlVideoClick(View v) {
//		MediaRecorderActivity.launchActivity(URANActivity.this,REQ_CODE);

		Intent intent = new Intent(URANActivity.this, MediaRecorderActivity.class);
//		CommonFuncUtil.getToast(URANActivity.this, intent == null ? "NULL":"xxxx");
		URANActivity.this.startActivityForResult(intent, this.REQ_CODE);
	}

	@OnClick(R.id.iconDel1)
	public void onIconDel1Click(View v) {
		mLlPhoto1.setVisibility(View.VISIBLE);
		mRlPhoto1.setVisibility(View.GONE);

		mBmpPath1 = "";
		FileUtils.deleteFile(new File(mBmpPath1));
	}

	@OnClick(R.id.iconDel2)
	public void onIconDel2Click(View v) {
		mLlPhoto2.setVisibility(View.VISIBLE);
		mRlPhoto2.setVisibility(View.GONE);

		mBmpPath2 = "";
		FileUtils.deleteFile(new File(mBmpPath2));
	}

	@OnClick(R.id.iconDel3)
	public void onIconDel3Click(View v) {
		mLlVideo.setVisibility(View.VISIBLE);
		mRlVideo.setVisibility(View.GONE);

		mVideoPath = "";
		// 删除视频文件夹
		FileUtils.deleteDir(mVideoDirPath);
	}

	@OnClick(R.id.ivPhoto1)
	public void onIvPhoto1Click(View v) {
		goToImagePreviewActivity(mBmpPath1);
	}

	@OnClick(R.id.ivPhoto2)
	public void onIvPhoto2Click(View v) {
		goToImagePreviewActivity(mBmpPath2);
	}

	@OnClick(R.id.ivVideo)
	public void onIvVideoClick(View v) {
//		goToVideoPlayActivity();
	}

	@OnClick(R.id.ivPlay)
	public void onIvPlayClick(View v) {
		goToVideoPlayActivity();
	}

	@OnClick(R.id.ll_datetime)
	public void onTvStartDateClick(View v) {
		// 日期格式为yyyy-MM-dd
		customDatePicker1.show(mTvStartDate.getText().toString());
	}

	@OnClick(R.id.ll_result)
	public void onTvResultClick(View v) {

		centerDialog = new CenterDialog(URANActivity.this, R.layout.dialog_select_type,
				new int[]{R.id.btnCancel, R.id.btnOK});
		centerDialog.show();
		final RadioGroup radioGroup = (RadioGroup) centerDialog.findViewById(R.id.rdo_grp_select);
		radioGroup.check(R.id.rdo_yin);
		centerDialog.setOnCenterItemClickListener(new CenterDialog.OnCenterItemClickListener() {
			@Override
			public void OnCenterItemClick(CenterDialog dialog, View view) {
				switch (view.getId()) {
					case R.id.btnCancel:
						mTvResult.setText("阴性");
						dialog.dismiss();
						break;
					case R.id.btnOK:
						if (R.id.rdo_yin == radioGroup.getCheckedRadioButtonId()) {
							mResult = 0;
						} else {
							mResult = 1;
						}
						mTvResult.setText(mResult == 0 ? "阴性" : "阳性");
						dialog.dismiss();
						break;
				}
			}
		});
	}

	/** 初始化时间选择器 */
	private void initDatePicker() {
		// sdf 为 时间选择器所需要的格式， sdf2 为 poo 程序具体所需格式
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
		final SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
		String now = sdf.format(new Date());
		mTvStartDate.setText(now.split(" ")[0]);
		try {
			mDateStart = sdf2.parse(now.split(" ")[0]);
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

				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}, now, "2099-01-01 00:00"); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
		customDatePicker1.showSpecificTime(false); // 不显示时和分
		customDatePicker1.setIsLoop(false); // 不允许循环滚动
	}

	/**
	 * 点击图片 跳转到预览页面
     */
	private void goToImagePreviewActivity(String bmpPath) {
		Bundle bundle = new Bundle();
		bundle.putString("bmpPath", bmpPath);
		CommonFuncUtil.goNextActivityWithArgs(URANActivity.this,
				ImagePreviewActivity.class, bundle, false);
	}

	/**
	 * 点击播放 跳转到视频播放页面
	 */
	private void goToVideoPlayActivity() {
		Bundle bundle = new Bundle();
		bundle.putString(Constant.RECORD_VIDEO_PATH, mVideoPath);
		bundle.putString(Constant.RECORD_VIDEO_CAPTURE, mVideoPath);
		CommonFuncUtil.goNextActivityWithArgs(URANActivity.this,
				VideoPlayerActivity.class, bundle, false);
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


					if (isTakingPhoto1 && !isTakingPhoto2) {
						mBitmap1 = newBitmap;
						mFileNameBmp1 = fileName + ".jpg";
						mBmpPath1 = FileUtils.SDPATH + mFileNameBmp1;
						mFileBmp1 = new File(mBmpPath1);

						mivPhoto1.setImageBitmap(newBitmap);
						mLlPhoto1.setVisibility(View.GONE);
						mRlPhoto1.setVisibility(View.VISIBLE);
						isPhoto1 = true;
					} else if (!isTakingPhoto1 && isTakingPhoto2) {
						mBitmap2 = newBitmap;
						mFileNameBmp2 = fileName + ".jpg";
						mBmpPath2 = FileUtils.SDPATH + mFileNameBmp2;
						mFileBmp2 = new File(mBmpPath2);

						mivPhoto2.setImageBitmap(newBitmap);
						mLlPhoto2.setVisibility(View.GONE);
						mRlPhoto2.setVisibility(View.VISIBLE);
						isPhoto2 = true;
					}

					/*// 上传图片流
					try {
						uploadPhoto += Base64Utils.encodeFile(FileUtils.SDPATH + fileName + ".jpg") + ",";
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}*/

					break;

				case FILE_SELECT:	// 文件选择
					Uri uri = data.getData();
					if ("file".equalsIgnoreCase(uri.getScheme())){//使用第三方应用打开
						mFilePath = uri.getPath();
//						tv.setText(mFilePath);
						Toast.makeText(this, mFilePath +"11111",Toast.LENGTH_SHORT).show();
						return;
					}
					if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4以后
						mFilePath = FileUtils.getPath(this, uri);
//						tv.setText(path);
						Toast.makeText(this,mFilePath,Toast.LENGTH_SHORT).show();
					} else {//4.4以下下系统调用方法
						mFilePath = FileUtils.getRealPathFromURI(URANActivity.this, uri);
//						tv.setText(path);
						Toast.makeText(URANActivity.this, mFilePath+"222222", Toast.LENGTH_SHORT).show();
					}

					break;

				default:
					break;
			}
		} else if (requestCode == REQ_CODE) {
			// && resultCode == MediaRecorderActivity.RESULT_RECODE

			if (null == GlobalSet.intentData)
				return;

			Intent data1 = GlobalSet.intentData;
			mVideoPath = data1.getStringExtra("videoPath");
			mCoverPath = data1.getStringExtra("coverPath");
			mVideoDirPath = VCamera.getVideoCachePath() + mVideoPath.split("/")[mVideoPath.split("/").length - 2];
			mFileNameVideo = mVideoPath.split("/")[mVideoPath.split("/").length - 1];
			mFileVideo = new File(mVideoPath);

			mApplication.mBtimapUtils.display(mivVideo, mCoverPath);
			mRlVideo.setVisibility(View.VISIBLE);
//					play(videoPath);

			// 重置
			GlobalSet.intentData = null;

		} else if (requestCode == REQUEST_CODE_SIGNATURE
				&& resultCode == SignaturepadActivity.RESULT_CODE_SIGNATURE) {	// 手写签名页面
			mSignatureBmpPath = FileUtils.SDPATH + "test_sign.jpg";
			mFileSignatureBmp = new File(mSignatureBmpPath);

//			mIvSignatureImage.setImageBitmap(null);
//			mIvSignatureImage.setImageBitmap(BitmapFactory.decodeFile(mSignatureBmpPath));
//			mIvSignatureImage.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 尿检登记[六安]
	 * 2018-4-25
	 */
	private void doUploadDataNew() {
		String url = GlobalSet.APP_SERVER_URL + "app.drug_user/addUrine";
		OkHttpUtils.post()
				.addHeader(GlobalSet.APP_TOKEN_KEY, mApplication.getToken())
				.addFile("photo1_url", mFileNameBmp1, new File(mBmpPath1))
				.addFile("photo2_url", mFileNameBmp2, new File(mBmpPath2))
				.addFile("video_url", mFileNameVideo, new File(mVideoPath))
				.addParams("work_id",  mTaskId + "")
				.addParams("examination", "" + mResult)	// 尿检结果 0 阴性 1 阳性
				.addParams("longitude", mLongitude + "")
				.addParams("latitude", mLatitude + "")
				.addParams("department_name", mEdtDepartment.getText().toString().trim())
				.addParams("department_username", mEdtDepartmentUserName.getText().toString().trim())
				.addParams("sign_time", mTvStartDate.getText().toString().trim())
				.addParams("remark", mEdtRemark.getText().toString().trim())
				.url(url)
				.build()
				.execute(new StringCallback() {
					@Override
					public void onError(Call call, Exception e, int id) {
						pDialog.dismiss();
						CommonFuncUtil.getToast(URANActivity.this, e.getMessage());
					}

					@Override
					public void onResponse(String response, int id) {
						pDialog.dismiss();
						try {
							JSONObject jsonResponse = new JSONObject(response);
							if (jsonResponse.getInt("code") == ApiCode.CODE_SUCCESS) {	// Success
								// 删除视频文件夹
								FileUtils.deleteDir(mVideoDirPath);
								// 删除照片
								FileUtils.deleteFile(new File(mBmpPath1));
								FileUtils.deleteFile(new File(mBmpPath2));
								FileUtils.deleteFile(new File(mSignatureBmpPath));
								CommonFuncUtil.getToast(URANActivity.this, "提交成功");

								URANActivity.this.setResult(RESULT_CODE_URAN);
								URANActivity.this.finish();
							} else if (jsonResponse.getInt("code") == ApiCode.CODE_TOKEN_EXPIRED) {
								// token 失效，踢出当前用户，退到登录页面
								CommonFuncUtil.isTokenExpired(URANActivity.this);
							} else {
								CommonFuncUtil.getToast(URANActivity.this, jsonResponse.getString("info"));
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
	}

	/**
	 * 开始定位
	 */
	private void doLoc() {
		locationService.start();
	}

	/**
	 * 准备定位
	 */
	@Override
	protected void onStart() {
		super.onStart();
		//获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
		// -----------location config ------------
		locationService = mApplication.locationService;
		locationService.registerListener(mListener);
		locationService.setLocationOption(locationService.getDefaultLocationClientOption());
	}

	/**
	 * 结束定位
	 */
	@Override
	protected void onPause() {
		super.onPause();
		locationService.unregisterListener(mListener); //注销掉监听
		locationService.stop(); //停止定位服务
		mApplication.setServiceRunning(false);
	}

	/**
	 * 结束定位[弃用]
	 */
	@Override
	protected void onStop() {
//		locationService.unregisterListener(mListener); //注销掉监听
//		locationService.stop(); //停止定位服务
		super.onStop();
	}

	/*****
	 * 定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
	 *
	 */
	private BDLocationListener mListener = new BDLocationListener() {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// TODO Auto-generated method stub
			if (null != location && location.getLocType() != BDLocation.TypeServerError) {
//				CommonFuncUtil.getToast(URANActivity.this, location.getLatitude() + " " + location.getLongitude());

				// 判断是否是无效数据，是就舍弃
				if (String.valueOf(location.getLatitude()).contains("E")
						|| String.valueOf(location.getLongitude()).contains("E")) {

					ScreenManager.getScreenManagerInstance(URANActivity.this).finishActivity();
					return;
				}

				mLongitude = location.getLongitude();
				mLatitude = location.getLatitude();
				mAddress = location.getAddrStr();
				locationService.stop();

//				doUploadData();
				doUploadDataNew();

			}
		}

		public void onConnectHotSpotMessage(String s, int i){
		}
	};

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

	/**
	 * 验证服务端返回的文件数据是否正确（服务端是否全部保存成功）
	 * @param fileList
	 * @return
     */
	private boolean checkFileList(List<FileBean> fileList) {
		if (null == fileList || fileList.size() < 3) {
			return false;
		} else {
			if (fileList.get(0).getFile().equals(fileList.get(1).getFile())) {
				return false;
			} else {
				return true;
			}
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		// 防止部分 三星手机 因拍摄照片后，屏幕翻转导致 Activity 周期重建从而导致的数据丢失
		// 故在因翻转导致 Activity 销毁之前保存 拍照后的数据
		outState.putBoolean("isTakingPhoto1", isTakingPhoto1);
		outState.putBoolean("isTakingPhoto2", isTakingPhoto2);
		outState.putParcelable("bitmap1", mBitmap1);
		outState.putString("fileName1", mFileNameBmp1);
		outState.putString("filePath1", mBmpPath1);
		outState.putParcelable("bitmap2", mBitmap2);
		outState.putString("fileName2", mFileNameBmp2);
		outState.putString("filePath2", mBmpPath2);
		outState.putString("filePathSignture", mFileNameSignatureBmp);

	}

}

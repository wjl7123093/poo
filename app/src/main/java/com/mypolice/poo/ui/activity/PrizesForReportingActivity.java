package com.mypolice.poo.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.map.Text;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mypolice.poo.R;
import com.mypolice.poo.application.GlobalSet;
import com.mypolice.poo.bean.FileBean;
import com.mypolice.poo.service.LocationService;
import com.mypolice.poo.util.CommonFuncUtil;
import com.mypolice.poo.util.FileUtils;
import com.mypolice.poo.util.GPSUtils;
import com.mypolice.poo.util.ImageTools;
import com.mypolice.poo.widget.CenterDialog;
import com.mypolice.poo.widget.IconView;
import com.mypolice.poo.widget.TitleBarView;
import com.yixia.camera.demo.ui.record.MediaRecorderActivity;
import com.yixia.camera.demo.ui.record.VideoPlayerActivity;
import com.yixia.camera.demo.util.Constant;
import com.yixia.weibo.sdk.VCamera;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * @Title: PrizesForReportingActivity.java
 * @Package com.mypolice.poo.ui.activity
 * @Description: 举报有奖页面
 * @author wangjl
 * @crdate 2017-11-24
 * @update
 * @version v2.1.2(14)
 */
@ContentView(R.layout.activity_prizes_for_reporting)
public class PrizesForReportingActivity extends BaseActivityPoo {

    public static final int RESULT_CODE_SIGN = 0x302;

    private LocationService locationService;
    private double mLongitude = 0.0f;
    private double mLatitude = 0.0f;
    private String mAddress = "";

    private static final int REQ_CODE = 10001;

    /** TitleBarView 顶部标题栏 */
    @ViewInject(R.id.titlePrizesForReporting)
    private TitleBarView mTitlePrizesForReporting;

    @ViewInject(R.id.llPhoto1)
    private LinearLayout mLlPhoto1;
    //	@ViewInject(R.id.llPhoto2)
//	private LinearLayout mLlPhoto2;
    @ViewInject(R.id.llVideo)
    private LinearLayout mLlVideo;
    @ViewInject(R.id.rlPhoto1)
    private RelativeLayout mRlPhoto1;
    //	@ViewInject(R.id.rlPhoto2)
//	private RelativeLayout mRlPhoto2;
    @ViewInject(R.id.rlVideo)
    private RelativeLayout mRlVideo;
    @ViewInject(R.id.ivPhoto1)
    private ImageView mivPhoto1;
    //	@ViewInject(R.id.ivPhoto2)
//	private ImageView mivPhoto2;
    @ViewInject(R.id.ivVideo)
    private ImageView mivVideo;
    @ViewInject(R.id.iconDel1)
    private IconView mIconDel1;
    //	@ViewInject(R.id.iconDel2)
//	private IconView mIconDel2;
    @ViewInject(R.id.iconDel3)
    private IconView mIconDel3;
    @ViewInject(R.id.ivPlay)
    private ImageView mIvPlay;

    @ViewInject(R.id.edtTxtReportInfo)
    private EditText mEdtTxtReportInfo;

    @ViewInject(R.id.btnSendReport)
    private Button mBtnSendReport;

//    private int mTaskId;	// 任务 ID

    /** 是否已拍摄 标识 */
    private boolean isPhoto1 = false;	// 照片1是否已经存在
    //	private boolean isPhoto2 = false;	// 照片2是否已经存在
    private boolean isVideo = false;	// 视频是否已经存在
    private boolean isTakingPhoto1 = false;	// 是否正在拍摄照片1
    //	private boolean isTakingPhoto2 = false;	// 是否正在拍摄照片2
    // 路径
    private String mBmpPath1 = "";
    //	private String mBmpPath2 = "";
    private String mVideoPath = "";		// 视频路径
    private String mCoverPath = "";		// 缩略图路径
    private String mVideoDirPath = "";	// video 存放的文件夹路径
    // 文件
    private File mFileBmp1 = null;
    //	private File mFileBmp2 = null;
    private File mFileVideo = null;
    // 文件名
    private String mFileNameBmp1 = "";
    //	private String mFileNameBmp2 = "";
    private String mFileNameVideo = "";
    // 临时保存图片
    private Bitmap mBitmap1 = null;

    // 拍照
    public static final int IMAGE_CAPTURE = 1;

    /** 加载进度条 */
    private CenterDialog centerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewUtils.inject(this);

        Bundle bundle = getIntent().getExtras();

        initView();

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
        mTitlePrizesForReporting.setText("有奖举报");

        centerDialog = new CenterDialog(PrizesForReportingActivity.this, R.layout.dialog_uploading,
                new int[]{});
//		centerDialog.show();
    }

    /**
     * 上传操作
     */
    @OnClick(R.id.btnSendReport)
    public void onBtnSendReport(View v) {
//		doUploadFile();
        if (TextUtils.isEmpty(mBmpPath1)
                && TextUtils.isEmpty(mVideoPath) && TextUtils.isEmpty(mEdtTxtReportInfo.getText().toString().trim())) {
            CommonFuncUtil.getToast(PrizesForReportingActivity.this, "请至少上传一种证据信息进行举报");
            return;
        }
        // 判断是否开启 GPS
        if (!GPSUtils.isOPen(PrizesForReportingActivity.this)) {  // 未开启 gps
            CommonFuncUtil.getToast(PrizesForReportingActivity.this, "未开启GPS, 请先打开GPS");

            // 跳转到 gps 设置页面
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PrizesForReportingActivity.this.startActivity(settingsIntent);
        } else {
            centerDialog.show();
            // 先执行定位操作
            doLoc();
        }
    }

    @OnClick(R.id.llPhoto1)
    public void onLlPhoto1Click(View v) {
        isTakingPhoto1 = true;
//		isTakingPhoto2 = false;
        captureImage(FileUtils.SDPATH);
    }

	/*@OnClick(R.id.llPhoto2)
	public void onLlPhoto2Click(View v) {
		isTakingPhoto1 = false;
		isTakingPhoto2 = true;
		captureImage(FileUtils.SDPATH);
	}*/

    @OnClick(R.id.llVideo)
    public void onLlVideoClick(View v) {
//		MediaRecorderActivity.launchActivity(URANActivity.this,REQ_CODE);

        Intent intent = new Intent(PrizesForReportingActivity.this, MediaRecorderActivity.class);
//		CommonFuncUtil.getToast(SignActivity.this, intent == null ? "NULL":"xxxx");
        PrizesForReportingActivity.this.startActivityForResult(intent, this.REQ_CODE);
    }

    @OnClick(R.id.iconDel1)
    public void onIconDel1Click(View v) {
        mLlPhoto1.setVisibility(View.VISIBLE);
        mRlPhoto1.setVisibility(View.GONE);

        mBmpPath1 = "";
        FileUtils.deleteFile(new File(mBmpPath1));
    }

	/*@OnClick(R.id.iconDel2)
	public void onIconDel2Click(View v) {
		mLlPhoto2.setVisibility(View.VISIBLE);
		mRlPhoto2.setVisibility(View.GONE);
	}*/

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

	/*@OnClick(R.id.ivPhoto2)
	public void onIvPhoto2Click(View v) {
		goToImagePreviewActivity(mBmpPath2);
	}*/

    @OnClick(R.id.ivVideo)
    public void onIvVideoClick(View v) {
//		goToVideoPlayActivity();
    }

    @OnClick(R.id.ivPlay)
    public void onIvPlayClick(View v) {
        goToVideoPlayActivity();
    }

    /**
     * 点击图片 跳转到预览页面
     */
    private void goToImagePreviewActivity(String bmpPath) {
        Bundle bundle = new Bundle();
        bundle.putString("bmpPath", bmpPath);
        CommonFuncUtil.goNextActivityWithArgs(PrizesForReportingActivity.this,
                ImagePreviewActivity.class, bundle, false);
    }

    /**
     * 点击播放 跳转到视频播放页面
     */
    private void goToVideoPlayActivity() {
        Bundle bundle = new Bundle();
        bundle.putString(Constant.RECORD_VIDEO_PATH, mVideoPath);
        bundle.putString(Constant.RECORD_VIDEO_CAPTURE, mVideoPath);
        CommonFuncUtil.goNextActivityWithArgs(PrizesForReportingActivity.this,
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

        }
    }

    /**
     * 上传举报数据
     */
    private void doUploadData() {
        String url = GlobalSet.APP_SERVER_URL + "drug_Inform/uploadSave";

        if (!TextUtils.isEmpty(mBmpPath1) && !TextUtils.isEmpty(mVideoPath)) {  // Photo & Video
            OkHttpUtils.post()
                    .addHeader("token", mApplication.getToken())
                    .addFile("mFile1", mFileNameBmp1, new File(mBmpPath1))
                    .addFile("mFile3", mFileNameVideo, new File(mVideoPath))
                    .addParams("user_id", mApplication.getUserID() + "")
                    .addParams("user_type", "0")   // 0 吸毒端，1 管控端
                    .addParams("content", mEdtTxtReportInfo.getText().toString().trim())
                    .addParams("longitude", mLongitude + "")
                    .addParams("latitude", mLatitude + "")
                    .addParams("address", mAddress + "")
                    .url(url)
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            CommonFuncUtil.getToast(PrizesForReportingActivity.this, e.getMessage());
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            dealWithResponseData(response);
                        }
                    });

        } else if (TextUtils.isEmpty(mBmpPath1) && !TextUtils.isEmpty(mVideoPath)) { // !Photo & Video
            OkHttpUtils.post()
                    .addHeader("token", mApplication.getToken())
                    .addFile("mFile3", mFileNameVideo, new File(mVideoPath))
                    .addParams("user_id", mApplication.getUserID() + "")
                    .addParams("user_type", "0")   // 0 吸毒端，1 管控端
                    .addParams("content", mEdtTxtReportInfo.getText().toString().trim())
                    .addParams("longitude", mLongitude + "")
                    .addParams("latitude", mLatitude + "")
                    .addParams("address", mAddress + "")
                    .url(url)
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            CommonFuncUtil.getToast(PrizesForReportingActivity.this, e.getMessage());
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            dealWithResponseData(response);
                        }
                    });

        } else if (!TextUtils.isEmpty(mBmpPath1) && TextUtils.isEmpty(mVideoPath)) { // Photo & !Video
            OkHttpUtils.post()
                    .addHeader("token", mApplication.getToken())
                    .addFile("mFile1", mFileNameBmp1, new File(mBmpPath1))
                    .addParams("user_id", mApplication.getUserID() + "")
                    .addParams("user_type", "0")   // 0 吸毒端，1 管控端
                    .addParams("content", mEdtTxtReportInfo.getText().toString().trim())
                    .addParams("longitude", mLongitude + "")
                    .addParams("latitude", mLatitude + "")
                    .addParams("address", mAddress + "")
                    .url(url)
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            CommonFuncUtil.getToast(PrizesForReportingActivity.this, e.getMessage());
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            dealWithResponseData(response);
                        }
                    });

        } else if (TextUtils.isEmpty(mBmpPath1) && TextUtils.isEmpty(mVideoPath)) { // !Photo & !Video
            OkHttpUtils.post()
                    .addHeader("token", mApplication.getToken())
                    .addParams("user_id", mApplication.getUserID() + "")
                    .addParams("user_type", "0")   // 0 吸毒端，1 管控端
                    .addParams("content", mEdtTxtReportInfo.getText().toString().trim())
                    .addParams("longitude", mLongitude + "")
                    .addParams("latitude", mLatitude + "")
                    .addParams("address", mAddress + "")
                    .url(url)
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            CommonFuncUtil.getToast(PrizesForReportingActivity.this, e.getMessage());
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            dealWithResponseData(response);
                        }
                    });

        }


    }


    /**
     * 上传举报数据[六安]
     */
    private void doUploadDataNew() {
        String url = GlobalSet.APP_SERVER_URL + "app.drug_user/addReport";

        if (!TextUtils.isEmpty(mBmpPath1) && !TextUtils.isEmpty(mVideoPath)) {  // Photo & Video
            OkHttpUtils.post()
                    .addHeader(GlobalSet.APP_TOKEN_KEY, mApplication.getToken())
                    .addFile("photo_url", mFileNameBmp1, new File(mBmpPath1))
                    .addFile("video_url", mFileNameVideo, new File(mVideoPath))
                    .addParams("content", mEdtTxtReportInfo.getText().toString().trim())
                    .addParams("longitude", mLongitude + "")
                    .addParams("latitude", mLatitude + "")
                    .url(url)
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            CommonFuncUtil.getToast(PrizesForReportingActivity.this, e.getMessage());
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            dealWithResponseData(response);
                        }
                    });

        } else if (TextUtils.isEmpty(mBmpPath1) && !TextUtils.isEmpty(mVideoPath)) { // !Photo & Video
            OkHttpUtils.post()
                    .addHeader(GlobalSet.APP_TOKEN_KEY, mApplication.getToken())
                    .addFile("video_url", mFileNameVideo, new File(mVideoPath))
                    .addParams("content", mEdtTxtReportInfo.getText().toString().trim())
                    .addParams("longitude", mLongitude + "")
                    .addParams("latitude", mLatitude + "")
                    .url(url)
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            CommonFuncUtil.getToast(PrizesForReportingActivity.this, e.getMessage());
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            dealWithResponseData(response);
                        }
                    });

        } else if (!TextUtils.isEmpty(mBmpPath1) && TextUtils.isEmpty(mVideoPath)) { // Photo & !Video
            OkHttpUtils.post()
                    .addHeader(GlobalSet.APP_TOKEN_KEY, mApplication.getToken())
                    .addFile("photo_url", mFileNameBmp1, new File(mBmpPath1))
                    .addParams("content", mEdtTxtReportInfo.getText().toString().trim())
                    .addParams("longitude", mLongitude + "")
                    .addParams("latitude", mLatitude + "")
                    .url(url)
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            CommonFuncUtil.getToast(PrizesForReportingActivity.this, e.getMessage());
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            dealWithResponseData(response);
                        }
                    });

        } else if (TextUtils.isEmpty(mBmpPath1) && TextUtils.isEmpty(mVideoPath)) { // !Photo & !Video
            OkHttpUtils.post()
                    .addHeader(GlobalSet.APP_TOKEN_KEY, mApplication.getToken())
                    .addParams("content", mEdtTxtReportInfo.getText().toString().trim())
                    .addParams("longitude", mLongitude + "")
                    .addParams("latitude", mLatitude + "")
                    .url(url)
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            CommonFuncUtil.getToast(PrizesForReportingActivity.this, e.getMessage());
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            dealWithResponseData(response);
                        }
                    });

        }


    }

    /**
     * 处理返回数据
     * @param response
     */
    private void dealWithResponseData(String response) {
        try {
            JSONObject jsonResponse = new JSONObject(response);
            if (jsonResponse.getInt("code") == 0
                    || jsonResponse.getInt("code") == 200) {	// Success
                // 删除视频文件夹
                if (!TextUtils.isEmpty(mVideoDirPath))
                    FileUtils.deleteDir(mVideoDirPath);
                // 删除照片
                if (!TextUtils.isEmpty(mBmpPath1))
                    FileUtils.deleteFile(new File(mBmpPath1));
                CommonFuncUtil.getToast(PrizesForReportingActivity.this, "举报成功");

//                PrizesForReportingActivity.this.setResult(RESULT_CODE_SIGN);
                PrizesForReportingActivity.this.finish();
            } else if (jsonResponse.getInt("code") == 1007) {
                // token 失效，踢出当前用户，退到登录页面
                CommonFuncUtil.getToast(PrizesForReportingActivity.this,
                        "当前用户已在别处登录，请重新登录");
                removeALLActivity();
                CommonFuncUtil.goNextActivityWithNoArgs(PrizesForReportingActivity.this,
                        LoginActivity.class, false);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
        mApplication.setServiceRunning(false);
        locationService.unregisterListener(mListener); //注销掉监听
        locationService.stop(); //停止定位服务
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
     * @see copy funtion to you project
     * 定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
     *
     */
    private BDLocationListener mListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // TODO Auto-generated method stub
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {

                // 由于 服务端数据库 longitude 和 latitude字段弄反了，所以客户端要把字段相反传上去
                mLongitude = location.getLongitude();
                mLatitude = location.getLatitude();
                mAddress = location.getAddrStr();
                locationService.stop();
//				doUploadFile();
//                doUploadData();
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
        if (null == fileList || fileList.size() < 2) {
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
        outState.putParcelable("bitmap", mBitmap1);
        outState.putString("fileName", mFileNameBmp1);
        outState.putString("filePath", mBmpPath1);

    }
}

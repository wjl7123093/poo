package com.mypolice.poo.service;

import android.annotation.TargetApi;
import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClientOption;
import com.mypolice.poo.application.GlobalSet;
import com.mypolice.poo.application.PooApplication;
import com.mypolice.poo.bean.UploadServiceBean;
import com.mypolice.poo.bean.UploadServiceBeanNew;
import com.mypolice.poo.broadcast.KeepLiveReceiver;
import com.mypolice.poo.ui.activity.MainActivity;
import com.mypolice.poo.ui.activity.SportsActivity;
import com.mypolice.poo.util.CommonFuncUtil;
import com.mypolice.poo.util.DateTimeUtil;
import com.mypolice.poo.util.FileUtils;
import com.mypolice.poo.util.NetUtils;
import com.mypolice.poo.util.keeping.Contants;
import com.mypolice.poo.util.keeping.JobSchedulerManager;
import com.mypolice.poo.util.keeping.SystemUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**JobService，支持5.0以上forcestop依然有效
 *
 * Created by jianddongguo on 2017/7/10.
 */
@TargetApi(21)
public class AliveJobLocService extends JobService {

    private PooApplication mApplication;
    private LocationService locationService;
    private LocationClientOption mOption;

    private List<UploadServiceBean> mUploadServiceBeanList = new ArrayList<UploadServiceBean>();
    private List<UploadServiceBeanNew> mUploadServiceBeanListNew = new ArrayList<UploadServiceBeanNew>();
    private String mStrCachePath = "";
    private String mFileName = "LocInfo.txt";

    private final static String TAG = "KeepAliveService";
    // 告知编译器，这个变量不能被优化
    private volatile static Service mKeepAliveService = null;

    public static boolean isJobServiceAlive(){
        return mKeepAliveService != null;
    }

    private static final int MESSAGE_ID_TASK = 0x01;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            // 具体任务逻辑
            if(SystemUtils.isAPPALive(getApplicationContext(), Contants.PACKAGE_NAME)){
                Toast.makeText(getApplicationContext(), "APP活着的", Toast.LENGTH_SHORT)
                        .show();

                locationService.start();// 开启定位SDK
                mApplication.setServiceRunning(true);
            }else{
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "APP被杀死，重启...", Toast.LENGTH_SHORT)
                        .show();
            }
            // 通知系统任务执行结束
            jobFinished( (JobParameters) msg.obj, false );

//            JobSchedulerManager.getJobSchedulerInstance(AliveJobLocService.this).startJobScheduler();
//            if (Build.VERSION.SDK_INT >= 24) {  // Android N 以后
//                jobFinished((JobParameters) msg.obj, true);
//            } else {
//                jobFinished((JobParameters) msg.obj, false);
//            }

            return true;
        }
    });

    @Override
    public boolean onStartJob(JobParameters params) {
        if(Contants.DEBUG)
            Log.d(TAG,"KeepAliveService----->JobService服务被启动...");
        mKeepAliveService = this;

        mApplication = (PooApplication) getApplication();

        mStrCachePath = AliveJobLocService.this.getExternalCacheDir().getPath();
        locationService = mApplication.locationService;
        //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
        locationService.registerListener(mListener);
        locationService.setLocationOption(getLocationClientOption());

        // 返回false，系统假设这个方法返回时任务已经执行完毕；
        // 返回true，系统假定这个任务正要被执行
        Message msg = Message.obtain(mHandler, MESSAGE_ID_TASK, params);
        mHandler.sendMessage(msg);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        mHandler.removeMessages(MESSAGE_ID_TASK);
        if(Contants.DEBUG)
            Log.d(TAG,"KeepAliveService----->JobService服务被关闭");

        mApplication.setServiceRunning(false);
        locationService.stop();// 停止定位SDK

        return false;
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
                StringBuffer sb = new StringBuffer(256);
                sb.append("time : ");

				/*// 先删除之前的，再重新保存最新的
				FileUtils.deleteFile(new File(mStrCachePath + mFileName));
				FileUtils.writeTxtToFile(getJsonArrayNew(location), mStrCachePath, mFileName);
				CommonFuncUtil.getToast(KeepLiveService.this, getJsonArrayNew(location));*/

                // 判断是否是无效数据，是就舍弃
                if (String.valueOf(location.getLatitude()).contains("E")
                        || String.valueOf(location.getLongitude()).contains("E")) {
                    return;
                }

				/*// 判断是否开启 GPS	[根据项目隐蔽需求，隐藏掉此功能]
				if (!GPSUtils.isOPen(KeepLiveService.this)) {  // 未开启 gps
					CommonFuncUtil.getToast(KeepLiveService.this, "未开启GPS, 请先打开GPS");

					// 跳转到 gps 设置页面
					Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
					settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					KeepLiveService.this.startActivity(settingsIntent);
				}*/

                // 判断网络是否通畅
                if (NetUtils.checkNetworkInfo(AliveJobLocService.this)) {	// 有网络
//					CommonFuncUtil.getToast(KeepLiveService.this, location.getLatitude() + " " + location.getLongitude());
                    // 上传位置信息
                    doUploadLocation(location);

                    // 判断离线文件里是否有内容
                    String jsonContent = FileUtils.readFileSdcard(mStrCachePath + mFileName);
                    if (!TextUtils.isEmpty(jsonContent)) {
                        try {
                            org.json.JSONArray array = new org.json.JSONArray(jsonContent);
                            if (array.length() > 0)	// 内容不为空，则上传内容
                                doUploadLocationLineOff(jsonContent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                } else {	// 无网络
                    // 保存 经纬度坐标至缓存文件中
//					FileUtils.writeTxtToFile(location.getLatitude() + "," + location.getLongitude()
//							+ "," + DateTimeUtil.getDateCN() + "\n " , mStrCachePath, "LocInfo.txt");

                    // 先删除之前的，再重新保存最新的
                    FileUtils.deleteFile(new File(mStrCachePath + mFileName));
                    FileUtils.writeTxtToFile(getJsonArrayNew(location), mStrCachePath, mFileName);
                }


            }
        }

        public void onConnectHotSpotMessage(String s, int i){
        }
    };

    /***
     *
     * @return DefaultLocationClientOption
     */
    public LocationClientOption getLocationClientOption(){
        if(mOption == null){
            mOption = new LocationClientOption();
            mOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
            mOption.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
            mOption.setScanSpan(1000 * 60 * 10);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
//			mOption.setScanSpan(1000 * 3);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
            mOption.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
            mOption.setIsNeedLocationDescribe(true);//可选，设置是否需要地址描述
            mOption.setNeedDeviceDirect(false);//可选，设置是否需要设备方向结果
            mOption.setLocationNotify(false);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
            mOption.setIgnoreKillProcess(true);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
            mOption.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
            mOption.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
            mOption.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集

            mOption.setIsNeedAltitude(false);//可选，默认false，设置定位时是否需要海拔信息，默认不需要，除基础定位版本都可用

//			mOption.setOpenGps(true); // 打开gps
//			mOption.setCoorType("bd09ll"); // 设置坐标类型
//			mOption.setScanSpan(1000 * 3);

        }
        return mOption;
    }

    /**
     * 后台上传位置信息
     * @param location
     */
    private void doUploadLocation(BDLocation location) {
//		String url = GlobalSet.APP_SERVER_URL + "community_dynamic/multSave";
        String url = GlobalSet.APP_SERVER_URL + "community_dynamic/dynamicSaveAll";
        OkHttpUtils.post().url(url)
                .addHeader("token", mApplication.getToken())
                .addParams("isContinue", "true")
                .addParams("rows", getJsonArrayNew(location))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        CommonFuncUtil.getToast(AliveJobLocService.this, e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
//						CommonFuncUtil.getToast(KeepLiveService.this, response);
                        // 清空 List，防止上传重复数据
                        mUploadServiceBeanListNew.clear();
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            if (jsonResponse.getInt("code") == 0
                                    || jsonResponse.getInt("code") == 200) {
                                Log.d("UploadService", "Success");
                            } else {
                                CommonFuncUtil.getToast(AliveJobLocService.this, jsonResponse.toString());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 后台上传位置信息 [离线文件中保存的内容]
     */
    private void doUploadLocationLineOff(String json) {
//		String url = GlobalSet.APP_SERVER_URL + "community_dynamic/multSave";
        String url = GlobalSet.APP_SERVER_URL + "community_dynamic/dynamicSaveAll";
        OkHttpUtils.post().url(url)
                .addHeader("token", mApplication.getToken())
                .addParams("isContinue", "true")
                .addParams("rows", json)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        CommonFuncUtil.getToast(AliveJobLocService.this, e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
//						CommonFuncUtil.getToast(KeepLiveService.this, response);
                        // 删除已经上传的离线文件，避免重复提交
                        FileUtils.deleteFile(new File(mStrCachePath + mFileName));
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            if (jsonResponse.getInt("code") == 0
                                    || jsonResponse.getInt("code") == 200) {
                                Log.d("UploadService_LineOff", "Success");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 组装 上传服务 Json
     * @param location
     * @return
     */
    private String getJsonArrayNew(BDLocation location) {
        UploadServiceBeanNew uploadServiceBean = new UploadServiceBeanNew();
        uploadServiceBean.setDrug_user_id(mApplication.getUserID());

        uploadServiceBean.setLongitude(location.getLongitude());
        uploadServiceBean.setLatitude(location.getLatitude());

        uploadServiceBean.setEnd_time(DateTimeUtil.getDateENToDate2().getTime() / 1000);
        mUploadServiceBeanListNew.add(uploadServiceBean);
        return JSON.toJSONString(mUploadServiceBeanListNew);
    }
}

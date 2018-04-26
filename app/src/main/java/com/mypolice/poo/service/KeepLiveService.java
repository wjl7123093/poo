package com.mypolice.poo.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.mypolice.poo.R;
import com.mypolice.poo.application.GlobalSet;
import com.mypolice.poo.application.PooApplication;
import com.mypolice.poo.bean.UploadServiceBean;
import com.mypolice.poo.bean.UploadServiceBeanNew;
import com.mypolice.poo.broadcast.KeepLiveReceiver;
import com.mypolice.poo.ui.activity.ApplicationForLeaveActivity;
import com.mypolice.poo.ui.activity.MainActivity;
import com.mypolice.poo.util.CommonFuncUtil;
import com.mypolice.poo.util.DateTimeUtil;
import com.mypolice.poo.util.FileUtils;
import com.mypolice.poo.util.GPSUtils;
import com.mypolice.poo.util.KeepLiveManager;
import com.mypolice.poo.util.NetUtils;
import com.mypolice.poo.util.keeping.ScreenManager;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * POO 定位服务
 * 2017-9-4
 * wangjl
 *
 * 备注：要实现 息屏状态下继续持续定位功能 有如下两种方式：
 * 1、在 LocService 的 onStartCommand() 和 onDestory() 里
 *    分别实现如下功能：
 *    onStartCommand() -> acquireWakeLock()
 *    onDestory() -> releaseWakeLock()
 *
 *   【推荐】
 *
 * 2、直接将 onDestory() 里的 startForeground(true) 和
 *    locationService.stop() 注释掉即可。即实现如下效果：
 *    // startForeground(true);
 *    // locationService.stop();
 *
 *    [No 推荐]
 */

/**
 * @Title: KeepLiveService.java
 * @Package com.mypolice.poo.ui.service
 * @Description: 后台定位服务
 * @author wangjl
 * @crdate 2017-9-4
 * @update 2017-9-6
 * @version v2.0.0(2)
 */
public class KeepLiveService extends Service {
	private static final String TAG = KeepLiveService.class.getSimpleName();
	static KeepLiveService sKeepLiveService;
	public KeepLiveService() {
	}

	private KeepLiveReceiver mReceiver = null;

	private PooApplication mApplication;
	private LocationService locationService;
	private LocationClientOption mOption;

	private List<UploadServiceBean> mUploadServiceBeanList = new ArrayList<UploadServiceBean>();
	private List<UploadServiceBeanNew> mUploadServiceBeanListNew = new ArrayList<UploadServiceBeanNew>();
	private String mStrCachePath = "";
	private String mFileName = "LocInfo.txt";

	private long mRegTime = 0;
	
	public final IBinder mBinder = new LocalBinder();

	public class LocalBinder extends Binder {
		// 在Binder中定义一个自定义的接口用于数据交互
		// 这里直接把当前的服务传回给宿主
		public KeepLiveService getService() {
			return KeepLiveService.this;
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}

	@Override
	public void onCreate() {
		Log.e("LocService", "onCreate...");
		sKeepLiveService = this;
		mApplication = (PooApplication) getApplication();

		mStrCachePath = KeepLiveService.this.getExternalCacheDir().getPath();
		locationService = mApplication.locationService;
		//获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
		locationService.registerListener(mListener);
		locationService.setLocationOption(getLocationClientOption());
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		// 获取电源锁，保持该服务在屏幕熄灭时仍然获取CPU时，保持运行
		CommonFuncUtil.acquireWakeLock(KeepLiveService.this);

//		if (null == mReceiver)
//			initBroadcastReceiver();

//		if (!mApplication.isServiceRunning()) {
			locationService.start();// 开启定位SDK
			startService(new Intent(this, InnerService.class));
			mApplication.setServiceRunning(true);
//		}
		return START_STICKY;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mApplication.setServiceRunning(false);

		// 释放设备电源锁
		CommonFuncUtil.releaseWakeLock();

		// 注释掉下面2行代码，可以实现 手机息屏后 定位服务依然运行的功能
		locationService.stop();// 停止定位SDK

//		if (null != mReceiver) {
//			unregisterReceiver(mReceiver);
//			mReceiver = null;
//		}
	}

	public static class InnerService extends Service {

		@Override
		public int onStartCommand(Intent intent,int flags, int startId) {
			KeepLiveManager.getInstance().setForegroundService(sKeepLiveService, this);
			return super.onStartCommand(intent, flags, startId);
		}

		@Nullable
		@Override
		public IBinder onBind(Intent intent) {
			return null;
		}
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

					ScreenManager.getScreenManagerInstance(KeepLiveService.this).finishActivity();
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
				if (NetUtils.checkNetworkInfo(KeepLiveService.this)) {	// 有网络
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

					ScreenManager.getScreenManagerInstance(KeepLiveService.this).finishActivity();
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
//			mOption.setScanSpan(1000 * 10);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
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
	 * 动态注册广播
	 */
	private void initBroadcastReceiver() {
		//实例化过滤器；
		IntentFilter intentFilter = new IntentFilter();
		//添加过滤的Action值；
		intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
		intentFilter.addAction(Intent.ACTION_SCREEN_ON);
		intentFilter.addAction(Intent.ACTION_USER_BACKGROUND);
		intentFilter.addAction(Intent.ACTION_USER_PRESENT);

		//实例化广播监听器；
		mReceiver = new KeepLiveReceiver();

		//将广播监听器和过滤器注册在一起；
		registerReceiver(mReceiver, intentFilter);
	}

	/**
	 * 后台上传位置信息
	 * @param location
     */
	private void doUploadLocation(BDLocation location) {
		String url = GlobalSet.APP_SERVER_URL + "app.upload/dynamicSaveAll";
		OkHttpUtils.post().url(url)
				.addHeader(GlobalSet.APP_TOKEN_KEY, mApplication.getToken())
				.addParams("isContinue", "true")
				.addParams("rows", getJsonArrayNew(location))
				.build()
				.execute(new StringCallback() {
					@Override
					public void onError(Call call, Exception e, int id) {
						CommonFuncUtil.getToast(KeepLiveService.this, e.getMessage());

						ScreenManager.getScreenManagerInstance(KeepLiveService.this).finishActivity();
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
								CommonFuncUtil.getToast(KeepLiveService.this, jsonResponse.toString());
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}

						ScreenManager.getScreenManagerInstance(KeepLiveService.this).finishActivity();
					}
				});
	}

	/**
	 * 后台上传位置信息 [离线文件中保存的内容]
	 */
	private void doUploadLocationLineOff(String json) {
		String url = GlobalSet.APP_SERVER_URL + "app.upload/dynamicSaveAll";
		OkHttpUtils.post().url(url)
				.addHeader(GlobalSet.APP_TOKEN_KEY, mApplication.getToken())
				.addParams("isContinue", "true")
				.addParams("rows", json)
				.build()
				.execute(new StringCallback() {
					@Override
					public void onError(Call call, Exception e, int id) {
						CommonFuncUtil.getToast(KeepLiveService.this, e.getMessage());

						ScreenManager.getScreenManagerInstance(KeepLiveService.this).finishActivity();
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

						ScreenManager.getScreenManagerInstance(KeepLiveService.this).finishActivity();
					}
				});
	}

	/**
	 * 组装 上传服务 Json
	 * @param location
	 * @return
     */
	private String getJsonArray(BDLocation location) {
		UploadServiceBean uploadServiceBean = new UploadServiceBean();
		uploadServiceBean.setDrug_user_id(mApplication.getUserID());
		uploadServiceBean.setId_code("");
		uploadServiceBean.setUser_tel("");
		uploadServiceBean.setFrom_id("");

		// 由于 服务端数据库 longitude 和 latitude字段弄反了，所以客户端要把字段相反传上去
		uploadServiceBean.setLongitude(location.getLatitude());
		uploadServiceBean.setLatitude(location.getLongitude());

		uploadServiceBean.setAddress(location.getAddrStr() == null ? "" : location.getAddrStr());
		uploadServiceBean.setReg_time(DateTimeUtil.getDateENToDate2().getTime() / 1000);
		mUploadServiceBeanList.add(uploadServiceBean);
		return JSON.toJSONString(mUploadServiceBeanList);
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

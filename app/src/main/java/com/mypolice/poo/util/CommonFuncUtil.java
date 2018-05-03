package com.mypolice.poo.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mypolice.poo.application.PooApplication;
import com.mypolice.poo.bean.AppVersion;
import com.mypolice.poo.ui.activity.ApplicationForLeaveActivity;
import com.mypolice.poo.ui.activity.BaseActivityPoo;
import com.mypolice.poo.ui.activity.LoginActivity;
import com.mypolice.poo.util.filecache.DataLoader;

/**   
 * @Title: CommonFuncUtil.java 
 * @Package com.mytv.rtmedia.util
 * @Description: 公共方法类
 * @author wangjl  
 * @crdate 2017-4-5
 * @update 2017-6-7
 * @version v1.1.3(5)
 */
public class CommonFuncUtil {

	/**
	 * 动态设置listview的高度
	 * 
	 * @param listView
	 */
	public static void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}

		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		if (params.height > 300) {
			params.height = 300;
		}
		listView.setLayoutParams(params);
	}

	/**
	 * 动态设置Gridview的高度
	 * 
	 * @param gridView
	 * @param colnum
	 */
	public static void setGridViewHeightBasedOnChildren(GridView gridView,
			int colnum) {
		ListAdapter listAdapter = gridView.getAdapter();
		if (listAdapter == null) {
			return;
		}

		int totalHeight = 0;
		for (int i = 0; i < ((listAdapter.getCount() - 1) / colnum) + 1; i++) {
			View gridItem = listAdapter.getView(i, null, gridView);
			gridItem.measure(0, 0);
			totalHeight += gridItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = gridView.getLayoutParams();
		params.height = totalHeight;
		/*
		 * if (params.height > 300) { params.height = 300; }
		 */
		gridView.setLayoutParams(params);
	}

	/**
	 * 获取程序版本信息
	 * 
	 * @return
	 */
	public static AppVersion getVersion(Context context) {
		AppVersion version = new AppVersion();
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			// 版本号
			version.setVersionCode(info.versionCode);
			// 版本名称
			version.setVersionName(info.versionName);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			version.setVersionCode(-1);
			version.setVersionName("版本号未知!");
			e.printStackTrace();
		}
		return version;
	}

	/**
	 * 跳转到下一页面 [不带参数]
	 * @param activity	当前页面
	 * @param cls 下一页面
	 * @param isFinish 当前页面是否关闭
	 */
	public static void goNextActivityWithNoArgs(Activity activity, 
			Class<?> cls, boolean isFinish) {
		Intent intent = new Intent(activity, cls);
		activity.startActivity(intent);
		if (isFinish)
			activity.finish();
	}

	/**
	 * 跳转到下一页面 [不带参数]
	 * @param activity	当前页面
	 * @param cls 下一页面
	 * @param requestCode 请求码
	 */
	public static void goNextActivityWithNoArgsForResult(Activity activity, 
			Class<?> cls, int requestCode) {
		Intent intent = new Intent(activity, cls);
		activity.startActivityForResult(intent, requestCode);
	}

	/**
	 * 跳转到下一页面 [带参数]
	 * @param activity	当前页面
	 * @param cls 下一页面
	 * @param bundle 参数
	 * @param isFinish 当前页面是否关闭
	 */
	public static void goNextActivityWithArgs(Activity activity, 
			Class<?> cls, Bundle bundle, boolean isFinish) {
		Intent intent = new Intent(activity, cls);
		intent.putExtras(bundle);
		activity.startActivity(intent);
		if (isFinish)
			activity.finish();
	}

	/**
	 * 跳转到下一页面 [带参数]
	 * @param activity	当前页面
	 * @param cls 下一页面
	 * @param bundle 参数
	 * @param requestCode 请求码
	 */
	public static void goNextActivityWithArgsForResult(Activity activity, 
			Class<?> cls, Bundle bundle, int requestCode) {
		Intent intent = new Intent(activity, cls);
		intent.putExtras(bundle);
		activity.startActivityForResult(intent, requestCode);
	}

	public static boolean hasSDCard() {
		String status = Environment.getExternalStorageState();
		if (!status.equals(Environment.MEDIA_MOUNTED)) {
			return false;
		}
		return true;
	}
	
	/**  
	 * 弹出提示信息
	 * @param context
	 * @param msg
	 */
	public static void getToast(Context context, String msg) {
		if (null != context)
			Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}
	
	/**  
	 * 弹出提示信息
	 * @param context
	 * @param msg
	 */
	public static void getToastLong(Context context, String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_LONG).show(); 
	}
	
	/**
	 * 设置字体图标
	 * @param context
	 * @param tv
	 */
	public static void setIconFont(Context context, TextView tv) {
		Typeface iconfont = Typeface.createFromAsset(context.getAssets(), 
				"iconfont/iconfont.ttf");
		tv.setTypeface(iconfont);                                          
	}

	/**
	 * 获取SD卡缓存JSON数据
	 * 
	 * @return 缓存的json数据，无数据返回""
	 */
	public static String getDataFromSDCard(DataLoader dataLoader, String key) {
		String jsonData = "";
		if (hasSDCard()) {
			jsonData = dataLoader.getDataFromDisk(key);
		}
		return jsonData;
	}
	
	/**
	 * 判断字符串是否为空
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str) {
		if (null == str || str.equals("") || str.equals("null"))
			return true;
		return false;
	}

	/**
	 * 获取 手机 IP 地址
	 * 
	 * @param context
	 * @return
	 */
	public static String getIPAddress(Context context) {
		NetworkInfo info = ((ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE))
				.getActiveNetworkInfo();
		if (info != null && info.isConnected()) {
			if (info.getType() == ConnectivityManager.TYPE_MOBILE) {// 当前使用2G/3G/4G网络
				try {
					// Enumeration<NetworkInterface>
					// en=NetworkInterface.getNetworkInterfaces();
					for (Enumeration<NetworkInterface> en = NetworkInterface
							.getNetworkInterfaces(); en.hasMoreElements();) {
						NetworkInterface intf = en.nextElement();
						for (Enumeration<InetAddress> enumIpAddr = intf
								.getInetAddresses(); enumIpAddr
								.hasMoreElements();) {
							InetAddress inetAddress = enumIpAddr.nextElement();
							if (!inetAddress.isLoopbackAddress()
									&& inetAddress instanceof Inet4Address) {
								return inetAddress.getHostAddress();
							}
						}
					}
				} catch (SocketException e) {
					e.printStackTrace();
				}

			} else if (info.getType() == ConnectivityManager.TYPE_WIFI) {// 当前使用无线网络
				WifiManager wifiManager = (WifiManager) context
						.getSystemService(Context.WIFI_SERVICE);
				WifiInfo wifiInfo = wifiManager.getConnectionInfo();
				String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());// 得到IPV4地址
				return ipAddress;
			}
		} else {
			// 当前无网络连接,请在设置中打开网络
		}
		return null;
	}

	/**
	 * 将得到的int类型的IP转换为String类型
	 * 
	 * @param ip
	 * @return
	 */
	public static String intIP2StringIP(int ip) {
		return (ip & 0xFF) + "." + ((ip >> 8) & 0xFF) + "."
				+ ((ip >> 16) & 0xFF) + "." + (ip >> 24 & 0xFF);
	}

	private static PowerManager.WakeLock wakeLock = null;

	/**
	 * 获取电源锁，保持该服务在屏幕熄灭时仍然获取CPU时，保持运行
	 * @param context
     */
	public static void acquireWakeLock(Context context) {
		if (null == wakeLock) {
			PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
			wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE,
					context.getClass().getCanonicalName());
			if (null != wakeLock) {
				Log.i("WAKE_ACQUIRE", "call acquireWakeLock");
				wakeLock.acquire();
			}
		}
	}

	/**
	 * 释放设备电源锁
	 */
	public static void releaseWakeLock() {
		if (null != wakeLock && wakeLock.isHeld()) {
			Log.i("WAKE_RELEASE", "call releaseWakeLock");
			wakeLock.release();
			wakeLock = null;
		}
	}

	/**
	 * Token 失效
	 */
	public static void isTokenExpired(BaseActivityPoo activity) {
		// token 失效，踢出当前用户，退到登录页面
		CommonFuncUtil.getToast(activity,
				"当前用户已在别处登录，请重新登录");
		activity.removeALLActivity();
		CommonFuncUtil.goNextActivityWithNoArgs(activity,
				LoginActivity.class, false);

		activity.mApplication.setLogin(false);
	}
	
}

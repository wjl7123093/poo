package com.mypolice.poo.application;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.os.Handler;

/**   
 * @Title: GlobalSet.java 
 * @Package com.mypolice.poo.application
 * @Description: 全局设置信息类
 * @author wangjl  
 * @crdate 2017-8-22
 * @update 2017-9-7
 * @version v1.0.0(1)
 */
public class GlobalSet {

	public static String APP_ID = "druggy";
	public static String APP_SECRET = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCzA+m" +
			"dwLl/Y/QPy9xTVsvrNt0B7hCVRwu+Abt3ebgQTOMy66iS/zPkl3Cx2H9lAaFl4UkaRN3h" +
			"rVJ3O70fnzvTR1P7Cx+UXyeM1IPew1YTWQtaWIxaKbYxiCpMJsZ5KDs+POFRPF6CDvf6g" +
			"sCu5QwdauxFbhBTut6ncrOYPCB3GwIDAQAB";

	// =================== 六安新版 地址 ==============================
	public static int APP_TYPE = 2;
    public static int APP_SUCCESS = 1;
	/** Token Key */
	public static String APP_TOKEN_KEY = "jwt-token";
	/** 服务端接口地址 */
	public static String APP_SERVER_URL = "http://111.39.142.240:55555/doc//";
	/** 服务端图片地址 */
	public static String APP_IMAGE_URL = "http://111.39.142.240:55555/static/uploads/";
	/** 更新下载 apk 地址 */
	public static String APP_DOWNLOAD_URL = "http://111.39.142.240:55555/static/app/shapp_update.xml";



//	public static String APP_SERVER = "http://www.myxdgk.com:18080";
//	/** 服务端接口地址 */
//	public static String APP_SERVER_URL = "http://www.myxdgk.com:18080/api/";
//	/** 服务端接口地址 https */
//	public static String APP_SERVER_URL_HTTPS = "https://www.myxdgk.com:18080/api/";
//	/** 服务端图片地址 */
//	public static String APP_IMAGE_URL = "http://www.myxdgk.com:18080/static/uploads/";
//	/** 服务端图片地址 https */
//	public static String APP_IMAGE_URL_HTTPS = "https://www.myxdgk.com:18080/static/uploads/";
//	/** 宣传教育 - 新闻详情页地址 */
//	public static String APP_DETAIL_URL = "http://www.myxdgk.com:18080/index/wx/detail.html?id=";
//	/** 宣传教育 - 新闻详情页地址 https */
//	public static String APP_DETAIL_URL_HTTPS = "https://www.myxdgk.com:18080/index/wx/detail.html?id=";
//	/** 更新下载 apk 地址 */
//	public static String APP_DOWNLOAD_URL = "http://www.myxdgk.com:18080/app/update.xml";
//	/** 更新下载 apk 地址 https */
//	public static String APP_DOWNLOAD_URL_HTTPS = "https://www.myxdgk.com:18080/app/update.xml";
//	/** 下载 apk 名称 */
//	public static String APP_DOWNLOAD_NAME = "drugapp.apk";



	// =================== 演示环境 地址 ==============================

	public static String APP_SERVER = "http://police.keywaysoft.com/";
//	/** 服务端接口地址 */
//	public static String APP_SERVER_URL = "http://police.keywaysoft.com/api/";
	/** 服务端接口地址 https */
	public static String APP_SERVER_URL_HTTPS = "https://police.keywaysoft.com/api/";
//	/** 服务端图片地址 */
//	public static String APP_IMAGE_URL = "http://police.keywaysoft.com/static/uploads/";
	/** 服务端图片地址 https */
	public static String APP_IMAGE_URL_HTTPS = "https://police.keywaysoft.com/static/uploads/";
	/** 宣传教育 - 新闻详情页地址 */
	public static String APP_DETAIL_URL = "http://police.keywaysoft.com/index/wx/detail.html?id=";
//	/** 宣传教育 - 新闻详情页地址 */
//	public static String APP_DETAIL_URL = "http://police.keywaysoft.com/api/portal_article/getPortalArticleContent/";
	/** 宣传教育 - 新闻详情页地址 https */
	public static String APP_DETAIL_URL_HTTPS = "https://police.keywaysoft.com/index/wx/detail.html?id=";
//	/** 更新下载 apk 地址 */
//	public static String APP_DOWNLOAD_URL = "http://police.keywaysoft.com/app/update.xml";
	/** 更新下载 apk 地址 https */
	public static String APP_DOWNLOAD_URL_HTTPS = "https://police.keywaysoft.com/app/update.xml";
	/** 下载 apk 名称 */
	public static String APP_DOWNLOAD_NAME = "drugapp.apk";


	public static String WORK_TIME_HEAD = "截止时间: ";
	public static String WORK_DESCRIPTION_HEAD = "任务说明: ";
	public static String WORK_STATUS_HEAD = "任务状态: ";

	// 为了避免红米/魅族手机 调用 setResult() 方法后，不回调 onActivityResult() 方法
	// 保存拍摄视频后的 数据
	public static Intent intentData = null;

	// =====================================================================

	// 操作系统版本号
	public static String VERSION_OS = android.os.Build.VERSION.RELEASE;	

	// wcf获取数据异常,超时
	public static final int HTTPRESPONSEEXCEPTION = 100;
	// wcf获取数据异常
	public static final int IOEXCEPTION = 101;
	// xml处理异常
	public static final int XMLPULLPARSEREXCEPTION = 102;

	// 网络异常提示信息
	public static String NetError = "当前网络不可用";
	public static String LogTAG = "DEBUG";
	public static String Toast_NotGetCats = "未获取到分类数据，请稍后再试";
	public static String Toast_NetError = "当前网络不可用，请联网后再试";
	
	// 缓存数据key值
	public static final String KEY_HOME_HOT_DATA = "home_hot_data";
	
}

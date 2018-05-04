package com.mypolice.poo.application;

import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.widget.RemoteViews;
import android.widget.Toast;
//import android.support.multidex.MultiDexApplication;

import com.baidu.mapapi.SDKInitializer;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.factory.BitmapFactory;
import com.mypolice.poo.R;
import com.mypolice.poo.notification.CustomNotificationHandler;
import com.mypolice.poo.notification.MyPushIntentService;
import com.mypolice.poo.notification.UmengNotificationService;
import com.mypolice.poo.service.LocationService;
import com.mypolice.poo.util.CrashHandler;
import com.mypolice.poo.util.ImageTools;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.MsgConstant;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.common.UmLog;
import com.umeng.message.entity.UMessage;
import com.yixia.camera.demo.service.AssertService;
import com.yixia.weibo.sdk.VCamera;
import com.yixia.weibo.sdk.util.DeviceUtils;
import com.yixia.weibo.sdk.util.FileUtils;
import com.yixia.weibo.sdk.util.ToastUtils;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import me.leolin.shortcutbadger.ShortcutBadger;
import okhttp3.OkHttpClient;

/**
 * @Title: PooApplication.java
 * @Package com.mypolice.poo.application
 * @Description: 获取和设置全局变量，存在于整个生命周期
 * @author wangjl
 * @crdate 2017-8-21
 * @update 2017-9-7
 * @version v1.0.0
 */
public class PooApplication extends Application {
    private static PooApplication application;
    private List<Activity> mActivityList;//用于存放所有启动的Activity的集合

    private static final String TAG = PooApplication.class.getName();
    public static final String UPDATE_STATUS_ACTION = "com.umeng.message.example.action.UPDATE_STATUS";
    private Handler handler;

    /**
     * xutils 里 BitmapUtils 图片框架
     */
    public static BitmapUtils mBtimapUtils;

    private static PooApplication instance;

    public static PooApplication getInstance() {
        if (null == instance)
            instance = new PooApplication();
        return instance;
    }

    /**
     * SharePreferences 全局配置参数存储类
     */
    private SharedPreferences spRTApp;
    private SharedPreferences.Editor spEditor;
    private static String SP_NAME = "POO_Config";

    private static String KEY_TOKEN = "token";
    private static String KEY_ACC = "acc";
    private static String KEY_PWD = "pwd";
    private static String KEY_USER_ID = "userID";
    private static String KEY_USER_NAME = "userName";
    private static String KEY_GROUP_ID = "groupID";
    private static String KEY_ROLE_ID = "roleID";
    private static String KEY_MOBILE = "mobile";

    // 六安 - 新版
    private static String KEY_AVATAR_URL = "avatar_url";
    private static String KEY_STATUS = "status";
    private static String KEY_SECRETARY = "secretary";
    private static String KEY_COMMUNITY = "community";

    private static String KEY_IS_LOGIN = "isLogin";
    private static String KEY_APP_SERVER = "appserver";

    private String token;                   // token 用于验证
    private String acc;                     // 登录账号
    private String pwd;                     // 登录密码
    private int userID;                     // 用户id
    private String userName;                // 用户姓名
    private int groupID;                    // 分组 ID
    private String roleID;                  // 分组名称
    private String mobile;                  // 手机号

    // 六安 - 新版
    private String avatarUrl;               // 头像
    private String status;                  // 状态
    private String secretary;               // 所属专干
    private String community;               // 社区

    private boolean isLogin = false;		// 是否登录
    private boolean isServiceRunning = false;   // 服务是否运行中
    private String appServer = "";

    // 百度地图相关
    public LocationService locationService;
    public Vibrator mVibrator;

    @Override
    public void onCreate() {
        super.onCreate();
        mActivityList = new ArrayList<Activity>();

        /** 初始化 OkHttpClient */
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .addInterceptor(new LoggerInterceptor("TAG"))
                .connectTimeout(20000L, TimeUnit.MILLISECONDS)
                .readTimeout(20000L, TimeUnit.MILLISECONDS)
                //其他配置
                .build();

        OkHttpUtils.initClient(okHttpClient);


        /** 初始化 Umeng 消息推送 SDK */
        initNotificationSDK();

        /***
         * 初始化定位sdk，建议在Application中创建
         */
        locationService = new LocationService(getApplicationContext());
        mVibrator =(Vibrator)getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
        SDKInitializer.initialize(getApplicationContext());

        // 设置拍摄视频缓存路径
        File dcim = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        if (DeviceUtils.isZte()) {
            if (dcim.exists()) {
                VCamera.setVideoCachePath(dcim + "/Camera/POO/");
            } else {
                VCamera.setVideoCachePath(dcim.getPath().replace("/sdcard/", "/sdcard-ext/") + "/Camera/POO/");
            }
        } else {
            VCamera.setVideoCachePath(dcim + "/Camera/POO/");
        }
        // 开启log输出,ffmpeg输出到logcat
        VCamera.setDebugMode(true);
        // 初始化拍摄SDK，必须
        VCamera.initialize(this);

        //解压assert里面的文件
        startService(new Intent(this, AssertService.class));

        // 全局异常捕捉
        CrashHandler catchHandler = CrashHandler.getInstance();
        catchHandler.init(getApplicationContext());

        spRTApp = getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        spEditor = spRTApp.edit();

        this.token = spRTApp.getString(KEY_TOKEN, "");
        this.acc = spRTApp.getString(KEY_ACC, "");
        this.pwd = spRTApp.getString(KEY_PWD, "");
        this.userID = spRTApp.getInt(KEY_USER_ID, 0);
        this.userName = spRTApp.getString(KEY_USER_NAME, "");
        this.groupID = spRTApp.getInt(KEY_GROUP_ID, 0);
        this.roleID = spRTApp.getString(KEY_ROLE_ID, "");
        this.mobile = spRTApp.getString(KEY_MOBILE, "");
        this.isLogin = spRTApp.getBoolean(KEY_IS_LOGIN, false);
        this.appServer = spRTApp.getString(KEY_APP_SERVER, "");

        this.avatarUrl = spRTApp.getString(KEY_AVATAR_URL, "");
        this.status = spRTApp.getString(KEY_STATUS, "");
        this.secretary = spRTApp.getString(KEY_SECRETARY, "");
        this.community = spRTApp.getString(KEY_COMMUNITY, "");

        // 全局初始化 bimaputils
        mBtimapUtils = new BitmapUtils(this, "", 4 * 1024 * 1024, 20 * 1024 * 1024);
        mBtimapUtils.configDefaultBitmapConfig(Bitmap.Config.RGB_565);
        mBtimapUtils.configDefaultLoadingImage(R.mipmap.ic_launcher);
        mBtimapUtils.configDefaultLoadFailedImage(R.mipmap.ic_launcher);
    }

    /** 清空 SharePreference 数据 */
    public void clearSharePreferenceData() {
        spEditor.clear();
        spEditor.commit();
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
        spEditor.putString(KEY_AVATAR_URL, avatarUrl);
        spEditor.commit();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
        spEditor.putString(KEY_STATUS, status);
        spEditor.commit();
    }

    public String getSecretary() {
        return secretary;
    }

    public void setSecretary(String secretary) {
        this.secretary = secretary;
        spEditor.putString(KEY_SECRETARY, secretary);
        spEditor.commit();
    }

    public String getCommunity() {
        return community;
    }

    public void setCommunity(String community) {
        this.community = community;
        spEditor.putString(KEY_COMMUNITY, community);
        spEditor.commit();
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
        spEditor.putString(KEY_TOKEN, token);
        spEditor.commit();
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
        spEditor.putInt(KEY_USER_ID, userID);
        spEditor.commit();
    }

    public String getAcc() {
        return acc;
    }

    public void setAcc(String acc) {
        this.acc = acc;
        spEditor.putString(KEY_ACC, acc);
        spEditor.commit();
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
        spEditor.putString(KEY_PWD, pwd);
        spEditor.commit();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
        spEditor.putString(KEY_USER_NAME, userName);
        spEditor.commit();
    }

    public int getGroupID() {
        return groupID;
    }

    public void setGroupID(int groupID) {
        this.groupID = groupID;
        spEditor.putInt(KEY_GROUP_ID, groupID);
        spEditor.commit();
    }

    public String getRoleID() {
        return roleID;
    }

    public void setRoleID(String roleID) {
        this.roleID = roleID;
        spEditor.putString(KEY_ROLE_ID, roleID);
        spEditor.commit();
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
        spEditor.putString(KEY_MOBILE, mobile);
        spEditor.commit();
    }

    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean isLogin) {
        this.isLogin = isLogin;
        spEditor.putBoolean(KEY_IS_LOGIN, isLogin);
        spEditor.commit();
    }

    public String getAppServer() {
        return appServer;
    }

    public void setAppServer(String appServer) {
        this.appServer = appServer;
        spEditor.putString(KEY_APP_SERVER, appServer);
        spEditor.commit();
    }

    public boolean isServiceRunning() {
        return isServiceRunning;
    }

    public void setServiceRunning(boolean serviceRunning) {
        isServiceRunning = serviceRunning;
    }

    public static Context getContext() {
        return application;
    }

    public static File getGifCacheDirectory() {
        if (application != null)
            return FileUtils.getCacheDiskPath(application, "gif");//vineApplication.getExternalCacheDir() + "/cache/gif/";
        return null;
    }


    public final static int AVAILABLE_SPACE = 200;//M
    /**
     * 检测用户手机是否剩余可用空间200M以上
     * @return
     */
    public static boolean isAvailableSpace() {
        if (application == null) {
            return false;
        }
        //检测磁盘空间
        if (FileUtils.showFileAvailable(application) < AVAILABLE_SPACE) {
            ToastUtils.showToast(application, application.getString(R.string.record_check_available_faild, AVAILABLE_SPACE));
            return false;
        }

        return true;
    }

    /** 视频截图目录 */
    public static File getThumbCacheDirectory() {
        if (application != null)
            return FileUtils.getCacheDiskPath(application, "thumbs");//vineApplication.getExternalCacheDir() + "/cache/thumbs/";
        return null;
    }

    /**
     * 添加Activity
     */
    public void addActivity_(Activity activity) {
        // 判断当前集合中不存在该Activity
        if (!mActivityList.contains(activity)) {
            mActivityList.add(activity);//把当前Activity添加到集合中
        }
    }

    /**
     * 销毁单个Activity
     */
    public void removeActivity_(Activity activity) {
        //判断当前集合中存在该Activity
        if (mActivityList.contains(activity)) {
            mActivityList.remove(activity);//从集合中移除
            activity.finish();//销毁当前Activity
        }
    }

    /**
     * 销毁所有的Activity
     */
    public void removeALLActivity_() {
        //通过循环，把集合中的所有Activity销毁
        for (Activity activity : mActivityList) {
            activity.finish();
        }
    }

    /**
     * 初始化消息推送 SDK
     */
    private void initNotificationSDK() {
        PushAgent mPushAgent = PushAgent.getInstance(this);
        mPushAgent.setDebugMode(true);
        handler = new Handler();

        //sdk开启通知声音
        mPushAgent.setNotificationPlaySound(MsgConstant.NOTIFICATION_PLAY_SDK_ENABLE);
        // sdk关闭通知声音
//		mPushAgent.setNotificationPlaySound(MsgConstant.NOTIFICATION_PLAY_SDK_DISABLE);
        // 通知声音由服务端控制
//		mPushAgent.setNotificationPlaySound(MsgConstant.NOTIFICATION_PLAY_SERVER);

//		mPushAgent.setNotificationPlayLights(MsgConstant.NOTIFICATION_PLAY_SDK_DISABLE);
//		mPushAgent.setNotificationPlayVibrate(MsgConstant.NOTIFICATION_PLAY_SDK_DISABLE);


        UmengMessageHandler messageHandler = new UmengMessageHandler() {
            /**
             * 自定义消息的回调方法
             * */
            @Override
            public void dealWithCustomMessage(final Context context, final UMessage msg) {

                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        // 对自定义消息的处理方式，点击或者忽略
                        boolean isClickOrDismissed = true;
                        if (isClickOrDismissed) {
                            //自定义消息的点击统计
                            UTrack.getInstance(getApplicationContext()).trackMsgClick(msg);
                        } else {
                            //自定义消息的忽略统计
                            UTrack.getInstance(getApplicationContext()).trackMsgDismissed(msg);
                        }
                        Toast.makeText(context, msg.custom, Toast.LENGTH_LONG).show();
                    }
                });
            }

            /**
             * 自定义通知栏样式的回调方法
             * */
            @Override
            public Notification getNotification(Context context, UMessage msg) {
                switch (msg.builder_id) {
                    case 1:
                        Notification.Builder builder = new Notification.Builder(context);
                        RemoteViews myNotificationView = new RemoteViews(context.getPackageName(), R.layout.notification_view);
                        myNotificationView.setTextViewText(R.id.notification_title, msg.title);
                        myNotificationView.setTextViewText(R.id.notification_text, msg.text);
                        myNotificationView.setImageViewBitmap(R.id.notification_large_icon, getLargeIcon(context, msg));
                        myNotificationView.setImageViewResource(R.id.notification_small_icon, getSmallIconId(context, msg));
                        builder.setContent(myNotificationView)
                                .setSmallIcon(getSmallIconId(context, msg))
                                .setTicker(msg.ticker)
                                .setAutoCancel(true);


                        /** 未读数目 +1 */
                        ShortcutBadger.applyCount(context, 1);

                        return builder.getNotification();
                    default:
                        //默认为0，若填写的builder_id并不存在，也使用默认。
                        return super.getNotification(context, msg);
                }
            }
        };
        mPushAgent.setMessageHandler(messageHandler);

        /**
         * 自定义行为的回调处理，参考文档：高级功能-通知的展示及提醒-自定义通知打开动作
         * UmengNotificationClickHandler是在BroadcastReceiver中被调用，故
         * 如果需启动Activity，需添加Intent.FLAG_ACTIVITY_NEW_TASK
         * */
//        UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {
//            @Override
//            public void dealWithCustomAction(Context context, UMessage msg) {
//                Toast.makeText(context, msg.custom, Toast.LENGTH_LONG).show();
//            }
//        };
        //使用自定义的NotificationHandler，来结合友盟统计处理消息通知，参考http://bbs.umeng.com/thread-11112-1-1.html
        CustomNotificationHandler notificationClickHandler = new CustomNotificationHandler();
        mPushAgent.setNotificationClickHandler(notificationClickHandler);


        //注册推送服务 每次调用register都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback() {
            @Override
            public void onSuccess(String deviceToken) {
                UmLog.i(TAG, "device token: " + deviceToken);
                sendBroadcast(new Intent(UPDATE_STATUS_ACTION));
            }

            @Override
            public void onFailure(String s, String s1) {
                UmLog.i(TAG, "register failed: " + s + " " +s1);
                sendBroadcast(new Intent(UPDATE_STATUS_ACTION));
            }
        });

        //此处是完全自定义处理设置，两个例子，任选一种即可
        mPushAgent.setPushIntentServiceClass(MyPushIntentService.class);    // 自定义处理通知message
//        mPushAgent.setPushIntentServiceClass(UmengNotificationService.class);
    }

}

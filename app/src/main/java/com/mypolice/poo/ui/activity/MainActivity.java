package com.mypolice.poo.ui.activity;

import android.app.NotificationManager;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnItemClick;
import com.mypolice.poo.R;
import com.mypolice.poo.adapter.CommonAdapter;
import com.mypolice.poo.adapter.ViewHolder;
import com.mypolice.poo.application.GlobalSet;
import com.mypolice.poo.bean.AppVersion;
import com.mypolice.poo.bean.ContactBean;
import com.mypolice.poo.bean.GvFunctionBean;
import com.mypolice.poo.broadcast.KeepLiveReceiver;
import com.mypolice.poo.broadcast.ScreenReceiverUtil;
import com.mypolice.poo.doubleservice.KeepLiveLocalService;
import com.mypolice.poo.doubleservice.KeepLiveRemoteService;
import com.mypolice.poo.notification.MyPushIntentService;
import com.mypolice.poo.service.DaemonService;
import com.mypolice.poo.service.KeepLiveService;
import com.mypolice.poo.service.PlayerMusicService;
import com.mypolice.poo.util.CommonFuncUtil;
import com.mypolice.poo.util.FunctionManager;
import com.mypolice.poo.util.SystemUtil;
import com.mypolice.poo.util.UpdateVersion;
import com.mypolice.poo.util.keeping.HwPushManager;
import com.mypolice.poo.util.keeping.JobSchedulerManager;
import com.mypolice.poo.util.keeping.ScreenManager;
import com.mypolice.poo.widget.QuickAlphabeticBar;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import me.leolin.shortcutbadger.ShortcutBadger;
import okhttp3.Call;

/**
 * @Title: MainActivity.java
 * @Package com.mypolice.poo.ui.activity
 * @Description: 主页面
 * @author wangjl
 * @crdate 2017-8-21
 * @update 2017-10-27
 * @version v2.1.0(12)
 */
@ContentView(R.layout.activity_main)
public class MainActivity extends BaseActivityPoo {

    // ================== Start ==================

    private int timeSec;
    private int timeMin;
    private int timeHour;
    private Timer mRunTimer;
    private boolean isRunning = false;
    // 动态注册锁屏等广播
    private ScreenReceiverUtil mScreenListener;
    // 1像素Activity管理类
    private ScreenManager mScreenManager;
    // JobService，执行系统任务
    private JobSchedulerManager mJobManager;
    // 华为推送管理类
    private HwPushManager mHwPushManager;


    // ================== End =================

    private UpdateVersion 	mUpdateVersion;
    private UpdateVersion.DownCallBack mDownCallBack;

    private KeepLiveReceiver mReceiver;

    private FunctionManager mFuncManager;
    private CommonAdapter<GvFunctionBean> mAdapter;
    private int mClickPosition = 0;

    private List<ContactBean> list;
    private AsyncQueryHandler asyncQueryHandler; // 异步查询数据库类对象
    private QuickAlphabeticBar alphabeticBar; // 快速索引条

    private Map<Integer, ContactBean> contactIdMap = null;
    private Map<String, String> contacts = null;

    /** 退出程序时间 */
    private long mExitTime = 0;

    /** 未读标识（红点） */
    @ViewInject(R.id.viewIsRead)
    private View mViewIsRead;
    /** GridView 功能区 */
    @ViewInject(R.id.gvFunc)
    private GridView mGvFunc;

    private ScreenReceiverUtil.SreenStateListener mScreenListenerer = new ScreenReceiverUtil.SreenStateListener() {
        @Override
        public void onSreenOn() {
            // 亮屏，移除"1像素"
            mScreenManager.finishActivity();
        }

        @Override
        public void onSreenOff() {
            // 接到锁屏广播，将SportsActivity切换到可见模式
            // "咕咚"、"乐动力"、"悦动圈"就是这么做滴
//            Intent intent = new Intent(SportsActivity.this,SportsActivity.class);
//            startActivity(intent);
            // 如果你觉得，直接跳出SportActivity很不爽
            // 那么，我们就制造个"1像素"惨案
            mScreenManager.startActivity();
        }

        @Override
        public void onUserPresent() {
            // 解锁，暂不用，保留
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewUtils.inject(this);

        initView();
//        initBroadcastReceiver();

//        // 1. 注册锁屏广播监听器
//        mScreenListener = new ScreenReceiverUtil(this);
//        mScreenManager = ScreenManager.getScreenManagerInstance(this);
//        mScreenListener.setScreenReceiverListener(mScreenListenerer);
//        // 2. 启动系统任务
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {    // >= API21
//            mJobManager = JobSchedulerManager.getJobSchedulerInstance(this);
//            mJobManager.startJobScheduler();
//        } else {
//
//        }

//        // 3. 华为推送保活，允许接收透传
//        mHwPushManager = HwPushManager.getInstance(this);
//        mHwPushManager.startRequestToken();
//        mHwPushManager.isEnableReceiveNormalMsg(true);
//        mHwPushManager.isEnableReceiverNotifyMsg(true);

        // 4. 调用 服务
        getIsRead();
        postDeviceInfo();
        // 实例化
        asyncQueryHandler = new MainActivity.MyAsyncQueryHandler(getContentResolver());
        init();
    }

    @Override
    public void initView() {
        super.initView();

        initFunctionManager();
        bindGvFuncData();

        mViewIsRead.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 将未读消息数目设置为0
        MyPushIntentService.badgeCount = 0;
        ShortcutBadger.removeCount(MainActivity.this);
        // 清空消息通知
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotificationManager.cancelAll();

        // 开启GPS，运行服务
        // 如果 API < 21 那么则 定位服务直接运行在 KeepLiveLocalService 里。即已经在 startLive() 里运行
        if (!mApplication.isServiceRunning()) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)  // API >= 21
                startKeepLiveService();
        }

        // 检测版本更新
        checkUpdateVersion();

        // 5. 保活
//        startAlive();
//        startAliveNew();

    }

    /** 未读通知 点击事件 */
    @OnClick(R.id.iconNotification)
    public void onIconNotificationClick(View v) {
        CommonFuncUtil.goNextActivityWithNoArgs(MainActivity.this, PunishListActivity.class, false);
    }

    /** GridView 功能区 项点击事件 */
    @OnItemClick(R.id.gvFunc)
    public void onGvFuncItemClick(AdapterView<?> parent, View view,
                                  int position, long id) {
        mClickPosition = position;
        try {
            mFuncManager.lanchFunction(position);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

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

    /** 初始化 FunctionManager */
    private void initFunctionManager() {
        mFuncManager = FunctionManager.getInstance(MainActivity.this);
        mFuncManager.init();
    }

    /** 数据绑定 GridView */
    private void bindGvFuncData() {
        // 设置适配器
        mGvFunc.setAdapter(mAdapter = new CommonAdapter<GvFunctionBean>(
                MainActivity.this, mFuncManager.getBeans(), R.layout.item_gv_main) {

            @Override
            public void convert(ViewHolder helper, GvFunctionBean item) {
                helper.setBackground(R.id.llFuncBg1, MainActivity.this.getResources().getDrawable(
                        getResourceIdDrawable(item.getBg1())));
                helper.setBackground(R.id.llFuncBg2, MainActivity.this.getResources().getDrawable(
                        getResourceIdDrawable(item.getBg2())));
                helper.setText(R.id.tvFuncName, item.getDisplayName());
                helper.setIcon(R.id.iconFunc, mContext.getString(
                        getResourceIdString(item.getIcon())), getResources().getColor(R.color.WHITE_H));
            }

        });
    }

    /**
     * 获取是否有 未读的违反协议处置 [v2.1.0]
     */
    private void getIsRead() {
        String url = GlobalSet.APP_SERVER_URL + "community_punish/getIsRead";
        OkHttpUtils.post().url(url)
                .addHeader("token", mApplication.getToken())
                .addParams("drug_user_id", mApplication.getUserID() + "")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        CommonFuncUtil.getToast(MainActivity.this, e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
//						CommonFuncUtil.getToast(SignActivity.this, response);
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            if (jsonResponse.getInt("code") == 0
                                    || jsonResponse.getInt("code") == 200) {
                                int isRead = jsonResponse.getJSONObject("data").getInt("is_read");
                                if (0 == isRead)
                                    mViewIsRead.setVisibility(View.GONE);
                                else
                                    mViewIsRead.setVisibility(View.VISIBLE);

                            } else if (jsonResponse.getInt("code") == 1007) {
                                // token 失效，踢出当前用户，退到登录页面
                                CommonFuncUtil.getToast(MainActivity.this,
                                        "当前用户已在别处登录，请重新登录");
                                removeALLActivity();
                                CommonFuncUtil.goNextActivityWithNoArgs(MainActivity.this,
                                        LoginActivity.class, false);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 上传外联信息 [v2.1.0]
     */
    private void addContacts(String contactsMap) {
        String url = GlobalSet.APP_SERVER_URL + "drug_user_outreach/add";
        OkHttpUtils.post().url(url)
                .addHeader("token", mApplication.getToken())
                .addParams("drug_user_id", mApplication.getUserID() + "")
                .addParams("contacts", contactsMap)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        CommonFuncUtil.getToast(MainActivity.this, e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
//						CommonFuncUtil.getToast(SignActivity.this, response);
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            if (jsonResponse.getInt("code") == 0
                                    || jsonResponse.getInt("code") == 200) {
                                Log.i("CONTACTS", "SUCCESS");

                            } else if (jsonResponse.getInt("code") == 1007) {
                                // token 失效，踢出当前用户，退到登录页面
                                CommonFuncUtil.getToast(MainActivity.this,
                                        "当前用户已在别处登录，请重新登录");
                                removeALLActivity();
                                CommonFuncUtil.goNextActivityWithNoArgs(MainActivity.this,
                                        LoginActivity.class, false);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 上传设备信息 [v2.2.2]
     */
    private void postDeviceInfo() {
        String url = GlobalSet.APP_SERVER_URL + "device_code/saveDeviceCode";
        OkHttpUtils.post().url(url)
                .addHeader("token", mApplication.getToken())
                .addParams("drug_user_id", mApplication.getUserID() + "")
                .addParams("device_code", SystemUtil.getIMEI(MainActivity.this))
                .addParams("version", SystemUtil.getSystemVersion())
                .addParams("brand", SystemUtil.getDeviceBrand())
                .addParams("model", SystemUtil.getSystemModel())
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        CommonFuncUtil.getToast(MainActivity.this, e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
//						CommonFuncUtil.getToast(SignActivity.this, response);
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            if (jsonResponse.getInt("code") == 0
                                    || jsonResponse.getInt("code") == 200) {
                                Log.i("DEVICE_INFO", "UPLOAD SUCCESS");

                            } else if (jsonResponse.getInt("code") == 1007) {
                                // token 失效，踢出当前用户，退到登录页面
                                CommonFuncUtil.getToast(MainActivity.this,
                                        "当前用户已在别处登录，请重新登录");
                                removeALLActivity();
                                CommonFuncUtil.goNextActivityWithNoArgs(MainActivity.this,
                                        LoginActivity.class, false);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    // ======================= 检查更新 ===========================

    /**
     * 检测更新
     */
    private void checkUpdateVersion() {
        /**
         * 程序更新
         */
        // 获取版本信息
        AppVersion appVersion = CommonFuncUtil.getVersion(this);
        // 初始化回调函数
        initcallback();
        mUpdateVersion = new UpdateVersion(this, appVersion.getVersionCode(),
                mDownCallBack);
        // 检查版本
//		String path = getResources().getString(R.string.update_url);
        String path = GlobalSet.APP_DOWNLOAD_URL;
        mUpdateVersion.checkVersion(path);
    }

    /**
     * 初始化检查更新 回调函数
     */
    protected void initcallback() {

        mDownCallBack = new UpdateVersion.DownCallBack() {
            @Override
            public void onDownCallBack(int cmd, Object data) {
                // TODO Auto-generated method stub

                switch (cmd) {
                    case UpdateVersion.UPDATA_NONEED:
                        // 版本号相同无需升级
                        Log.d("UpdateVersion", "当前已经是最新版 !");
                        break;
                    case UpdateVersion.UPDATA_CLIENT:
                        // 版本号不同更新程序
                        // mUpdateVersion.downLoadApk();
                        mUpdateVersion.downLoadApk2();
                        break;
                    case UpdateVersion.GET_UNDATAINFO_ERROR:
                        // 服务器超时

                        break;
                    case UpdateVersion.SDCARD_NOMOUNTED:
                        // sdcard不可用

                        break;
                    case UpdateVersion.DOWN_ERROR:
                        // 下载apk失败

                        break;
                    default:

                        break;
                }
            }
        };

    }

    /**
     * 根据 icon 名称获取图片资源 ID
     * @param imgName 图片名称
     * @return 资源 ID
     */
    private int getResourceIdDrawable(String imgName)
    {
        Field field;
        int resId = R.mipmap.ic_launcher;
        try {
            field = R.drawable.class.getField(imgName);
            resId = (int) field.get(null);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        return resId;
    }

    /**
     * 根据 icon 名称获取字符串资源 ID
     * @param iconName 字体图标名称
     * @return 资源 ID
     */
    private int getResourceIdString(String iconName)
    {
        Field field;
        int resId = R.string.icon_main_examination;
        try {
            field = R.string.class.getField(iconName);
            resId = (int) field.get(null);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        return resId;
    }

    /**
     * 初始化数据库查询参数
     */
    private void init() {
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI; // 联系人Uri；
        // 查询的字段
        String[] projection = { ContactsContract.CommonDataKinds.Phone._ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.DATA1, "sort_key",
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.PHOTO_ID,
                ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY,
                ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER };
        // 按照sort_key升序查詢
        asyncQueryHandler.startQuery(0, null, uri, projection, null, null,
                "sort_key COLLATE LOCALIZED asc");
//        asyncQueryHandler.startQuery(0, null, uri, null, null, null, null);

    }

    /**
     *
     * @author Administrator
     *
     */
    private class MyAsyncQueryHandler extends AsyncQueryHandler {

        public MyAsyncQueryHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            if (cursor != null && cursor.getCount() > 0) {
                contactIdMap = new HashMap<Integer, ContactBean>();
                contacts = new HashMap<String, String>();
                list = new ArrayList<ContactBean>();
                cursor.moveToFirst(); // 游标移动到第一项
                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToPosition(i);
                    String name = cursor.getString(1);
                    String number = cursor.getString(2);
                    String sortKey = cursor.getString(3);
                    int contactId = cursor.getInt(4);
                    Long photoId = cursor.getLong(5);
                    String lookUpKey = cursor.getString(6);

                    // 保存联系人电话（包括多个电话）
                    contacts.put(number, name);

                    /*if (contactIdMap.containsKey(contactId)) {
                        // 无操作
                    } else {
                        // 创建联系人对象
                        ContactBean contact = new ContactBean();
                        contact.setDesplayName(name);
                        contact.setPhoneNum(number);
                        contact.setSortKey(sortKey);
                        contact.setPhotoId(photoId);
                        contact.setLookUpKey(lookUpKey);
                        list.add(contact);

                        contactIdMap.put(contactId, contact);
                        contacts.put(number, name);
                    }*/

                }
                /*if (list.size() > 0) {
                    setAdapter(list);
                }*/

                String jsonStr = com.alibaba.fastjson.JSONObject.toJSONString(contacts);
                addContacts(jsonStr);
            }

            super.onQueryComplete(token, cookie, cursor);
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 连按两次返回键退出程序
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) < 2000) {
                finish();
                System.exit(0);
            }
            mExitTime = System.currentTimeMillis();
            Toast.makeText(this, "再按一次退出程序！", Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        unregisterReceiver(mReceiver);
    }


    // ======================= 启动 保活服务 ==========================

    /**
     * 执行 保活程序
     */
    private void startAlive() {
        if(! isRunning){
//            startRunTimer();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {    // >= API21
                // 3. 启动前台Service
                startDaemonService();
                // 4. 启动播放音乐Service
                startPlayMusicService();

//                startKeepLiveService();
            } else {
                // 启动本地服务
                startLocalService();
                // 启动远程服务
                startRemoteService();
            }
        }else{
//            stopRunTimer();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {    // >= API21
                //关闭前台Service
                stopDaemonService();
                //关闭启动播放音乐Service
                stopPlayMusicService();
            } else {
                // 关闭本地服务
                stopLocalService();
                // 关闭远程服务
                stopRemoteService();
            }
        }
//        isRunning = !isRunning;
        isRunning = false;
    }

    private void stopPlayMusicService() {
        Intent intent = new Intent(MainActivity.this, PlayerMusicService.class);
        stopService(intent);
    }

    private void startPlayMusicService() {
        Intent intent = new Intent(MainActivity.this,PlayerMusicService.class);
        startService(intent);
    }

    private void startDaemonService() {
        Intent intent = new Intent(MainActivity.this, DaemonService.class);
//        Intent intent = new Intent(MainActivity.this, KeepLiveDaemonService.class);
        startService(intent);
    }

    private void stopDaemonService() {
        Intent intent = new Intent(MainActivity.this, DaemonService.class);
//        Intent intent = new Intent(MainActivity.this, KeepLiveDaemonService.class);
        stopService(intent);
    }

    private void startLocalService() {
//        startService(new Intent(this, LocalService.class));
        startService(new Intent(this, KeepLiveLocalService.class));
    }

    private void stopLocalService() {
//        stopService(new Intent(this, LocalService.class));
        stopService(new Intent(this, KeepLiveLocalService.class));
    }

    private void startRemoteService() {
//        startService(new Intent(this, RemoteService.class));
        startService(new Intent(this, KeepLiveRemoteService.class));
    }

    private void stopRemoteService() {
//        stopService(new Intent(this, RemoteService.class));
        stopService(new Intent(this, KeepLiveRemoteService.class));
    }

    private void startKeepLiveService() {
        startService(new Intent(this, KeepLiveService.class));

//        Intent keepService = new Intent(this, KeepLiveService.class);
//        keepService.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startService(keepService);

    }

    private void startRunTimer() {
        TimerTask mTask = new TimerTask() {
            @Override
            public void run() {
                timeSec++;
                if(timeSec == 60){
                    timeSec = 0;
                    timeMin++;
                }
                if(timeMin == 60){
                    timeMin = 0;
                    timeHour++;
                }
                if(timeHour == 24){
                    timeSec = 0;
                    timeMin = 0;
                    timeHour = 0;
                }
                // 更新UI
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        mTvRunTime.setText(timeHour+" : "+timeMin+" : "+timeSec);
                    }
                });
            }
        };
        mRunTimer = new Timer();
        // 每隔1s更新一下时间
        mRunTimer.schedule(mTask,1000,1000);
    }

    private void stopRunTimer(){
        if(mRunTimer != null){
            mRunTimer.cancel();
            mRunTimer = null;
        }
        timeSec = 0;
        timeMin = 0;
        timeHour = 0;
//        mTvRunTime.setText(timeHour+" : "+timeMin+" : "+timeSec);
    }

}

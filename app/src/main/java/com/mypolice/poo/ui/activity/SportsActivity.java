package com.mypolice.poo.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
//import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.mypolice.poo.R;
import com.mypolice.poo.broadcast.ScreenReceiverUtil;
import com.mypolice.poo.doubleservice.LocalService;
import com.mypolice.poo.doubleservice.RemoteService;
import com.mypolice.poo.service.DaemonService;
import com.mypolice.poo.service.PlayerMusicService;
import com.mypolice.poo.util.keeping.Contants;
import com.mypolice.poo.util.keeping.HwPushManager;
import com.mypolice.poo.util.keeping.JobSchedulerManager;
import com.mypolice.poo.util.keeping.ScreenManager;
import com.mypolice.poo.widget.TitleBarView;

import java.util.Timer;
import java.util.TimerTask;

/** 运动界面，处理各种保活逻辑
 *
 * Created by jianddongguo on 2017/7/7.
 * http://blog.csdn.net/andrexpert
 */

public class SportsActivity extends Activity {
    private static final String TAG = "SportsActivity";
//    private Toolbar mToolBar;
    private TextView mTvRunTime;
    private Button mBtnRun;

    /** TitleBarView 顶部标题栏 */
    @ViewInject(R.id.titleSports)
    private TitleBarView mTitleSports;

    private int timeSec;
    private int timeMin;
    private int timeHour;
    private Timer mRunTimer;
    private boolean isRunning;
    // 动态注册锁屏等广播
    private ScreenReceiverUtil mScreenListener;
    // 1像素Activity管理类
    private ScreenManager mScreenManager;
    // JobService，执行系统任务
    private JobSchedulerManager mJobManager;
    // 华为推送管理类
    private HwPushManager mHwPushManager;

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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sports);
        ViewUtils.inject(this);

        if(Contants.DEBUG)
            Log.d(TAG,"--->onCreate");
        // 1. 注册锁屏广播监听器
//        mScreenListener = new ScreenReceiverUtil(this);
//        mScreenManager = ScreenManager.getScreenManagerInstance(this);
//        mScreenListener.setScreenReceiverListener(mScreenListenerer);
        // 2. 启动系统任务
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {    // >= API21
            mJobManager = JobSchedulerManager.getJobSchedulerInstance(this);
            mJobManager.startJobScheduler();
        } else {

        }

        // 3. 华为推送保活，允许接收透传
//        mHwPushManager = HwPushManager.getInstance(this);
//        mHwPushManager.startRequestToken();
//        mHwPushManager.isEnableReceiveNormalMsg(true);
//        mHwPushManager.isEnableReceiverNotifyMsg(true);
        initView();
    }

    private void initView() {
        mTitleSports.setText("跑步啦");
        mTvRunTime = (TextView)findViewById(R.id.tv_run_time);
        mBtnRun = (Button)findViewById(R.id.btn_run);
    }

    public void onRunningClick(View v){
        if(! isRunning){
            mBtnRun.setText("停止跑步");
            startRunTimer();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {    // >= API21
                // 3. 启动前台Service
                startDaemonService();
                // 4. 启动播放音乐Service
                startPlayMusicService();
            } else {
                // 启动本地服务
                startLocalService();
                // 启动远程服务
                startRemoteService();
            }
        }else{
            mBtnRun.setText("开始跑步");
            stopRunTimer();

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
        isRunning = !isRunning;
    }

    private void stopPlayMusicService() {
        Intent intent = new Intent(SportsActivity.this, PlayerMusicService.class);
        stopService(intent);
    }

    private void startPlayMusicService() {
        Intent intent = new Intent(SportsActivity.this,PlayerMusicService.class);
        startService(intent);
    }

    private void startDaemonService() {
        Intent intent = new Intent(SportsActivity.this, DaemonService.class);
        startService(intent);
    }

    private void stopDaemonService() {
        Intent intent = new Intent(SportsActivity.this, DaemonService.class);
        stopService(intent);
    }

    private void startLocalService() {
        startService(new Intent(this, LocalService.class));
    }

    private void stopLocalService() {
        stopService(new Intent(this, LocalService.class));
    }

    private void startRemoteService() {
        startService(new Intent(this, RemoteService.class));
    }

    private void stopRemoteService() {
        stopService(new Intent(this, RemoteService.class));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 禁用返回键
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(isRunning){
                Toast.makeText(SportsActivity.this,"正在跑步",Toast.LENGTH_SHORT).show();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
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
                        mTvRunTime.setText(timeHour+" : "+timeMin+" : "+timeSec);
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
        mTvRunTime.setText(timeHour+" : "+timeMin+" : "+timeSec);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(Contants.DEBUG)
            Log.d(TAG,"--->onDestroy");
        stopRunTimer();
//        mScreenListener.stopScreenReceiverListener();
    }
}

package com.mypolice.poo.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.mypolice.poo.ui.activity.MainActivity;
import com.mypolice.poo.util.KeepLiveManager;

/**
 * 定义 保活广播，用于开启 1像素 Activity，保证锁屏后应用在最前端，拥有最高进程优先级
 * 2017-9-4
 * wangjl
 */
public class KeepLiveReceiver extends BroadcastReceiver {
    public static final String TAG = KeepLiveReceiver.class.getSimpleName();


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        Log.d(TAG, "onReceive: KeepLiveReceiver receive action =" + action);

        if (Intent.ACTION_SCREEN_OFF.equals(action) || Intent.ACTION_USER_BACKGROUND.equals(action)) {
            /** 屏幕锁屏的时候，打开 1像素 Activity */
            KeepLiveManager.getInstance().startKeepLiveActivity(context);
//            Intent intent1 = new Intent(context, MainActivity.class);
//            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(intent1);
        } else if (Intent.ACTION_SCREEN_ON.equals(action) || Intent.ACTION_USER_BACKGROUND.equals(action)) {
//            KeepLiveManager.getInstance().finishKeepLiveActivity();
        }
        KeepLiveManager.getInstance().startKeepLiveService(context);
    }
}

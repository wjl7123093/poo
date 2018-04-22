package com.mypolice.poo.util;

import android.app.Activity;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.mypolice.poo.service.KeepLiveService;
import com.mypolice.poo.ui.activity.KeepLiveActivity;

import java.lang.ref.WeakReference;

/**
 * 单例KeepLiveManager，用于初始化、启动、关闭一像素Activity
 * 2017-9-4
 * wangjl
 */
public class KeepLiveManager {
    public static final String TAG = KeepLiveManager.class.getSimpleName();

    public static KeepLiveManager sInstance = new KeepLiveManager();
    public WeakReference<KeepLiveActivity> mWeakActivityRef = null;

    private KeepLiveManager() {

    }

    public static KeepLiveManager getInstance() {
        return sInstance;
    }

    public void initKeepLiveActivity(KeepLiveActivity keepLiveActivity) {
        this.mWeakActivityRef = new WeakReference<>(keepLiveActivity);

        // 设置1像素透明窗口
        Window window = keepLiveActivity.getWindow();
        window.setGravity(Gravity.LEFT | Gravity.TOP);
        WindowManager.LayoutParams params = window.getAttributes();
        params.x = 0;
        params.y = 0;
        params.width = 1;
        params.height = 1;
        window.setAttributes(params);
    }

    /**
     * 启动一像素Activity
     * @param context
     */
    public void startKeepLiveActivity(Context context) {
        Intent intent = new Intent(context, KeepLiveActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 结束一像素Activity
     */
    public void finishKeepLiveActivity() {
        if (mWeakActivityRef != null) {
            Activity activity = mWeakActivityRef.get();
            if (activity != null && !activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    /**
     * 提升Service的优先级为前台Service
     */
    public void setForegroundService(final Service keepLiveService, final Service innerService) {
        final int foregroundPushId = 1;
        Log.d(TAG, "setForegroundService: KeepLiveService->setForegroundService: " + keepLiveService + ", innerService:" + innerService);
        if (keepLiveService != null) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
                keepLiveService.startForeground(foregroundPushId, new Notification());
            } else {
                keepLiveService.startForeground(foregroundPushId, new Notification());
                if (innerService != null) {
                    innerService.startForeground(foregroundPushId, new Notification());
                    innerService.stopSelf();
                }
            }
        }
    }

    /**
     * 启动一像素Activity
     * @param context
     */
    public void startKeepLiveService(Context context) {
        Intent intent = new Intent(context, KeepLiveService.class);
        context.startService(intent);
    }

}

package com.mypolice.poo.ui.activity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.mypolice.poo.util.KeepLiveManager;


/**
 * 定义一像素的Activity
 * 2017-9-4
 * wangjl
 */
public class KeepLiveActivity extends BaseActivityPoo {
    public static final String TAG = KeepLiveActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: KeepLiveActivity->onCreate");
        KeepLiveManager.getInstance().initKeepLiveActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: KeepLiveActivity->onResume");


        /**
         * 在屏幕点亮的时候，关闭 1像素 Activity
         *
         */
        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);

        boolean isScreenOn = pm.isScreenOn();//如果为true，则表示屏幕“亮”了，否则屏幕“暗”了。
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            if (pm.isInteractive()) {
                System.out.println("alarm screen ON");
                this.finish();
            }
        } else if(Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT_WATCH){
            if(pm.isScreenOn()){
                System.out.println("alarm screen ON");
                this.finish();
            }
        } else {
            System.out.println("alarm screen OFF");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: KeepLiveActivity->onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: KeepLiveActivity->onDestroy");
    }


}

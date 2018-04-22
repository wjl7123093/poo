package com.mypolice.poo.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.mypolice.poo.service.KeepLiveService;
import com.mypolice.poo.util.keeping.Contants;
import com.mypolice.poo.util.keeping.ScreenManager;
import com.mypolice.poo.util.keeping.SystemUtils;

/**1像素Activity
 *
 * Created by jianddongguo on 2017/7/8.
 */

public class SinglePixelActivity extends Activity {
    private static final String TAG = "SinglePixelActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Contants.DEBUG)
            Log.d(TAG,"onCreate--->启动1像素保活");
        Window mWindow = getWindow();
        mWindow.setGravity(Gravity.LEFT | Gravity.TOP);
        WindowManager.LayoutParams attrParams = mWindow.getAttributes();
        attrParams.x = 0;
        attrParams.y = 0;
        attrParams.height = 300;
        attrParams.width = 300;
        mWindow.setAttributes(attrParams);
        // 绑定SinglePixelActivity到ScreenManager
        ScreenManager.getScreenManagerInstance(this).setSingleActivity(this);

        /**
         * 在一像素Activity 里启动定位服务，只为与 JobService 绑定起来，达到保活目的
         */
        startKeepLiveService();
    }

    @Override
    protected void onDestroy() {
        if(Contants.DEBUG)
            Log.d(TAG,"onDestroy--->1像素保活被终止");
        if(! SystemUtils.isAPPALive(this,Contants.PACKAGE_NAME)){
            Intent intentAlive = new Intent(this, SportsActivity.class);
            intentAlive.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intentAlive);
            Log.i(TAG,"SinglePixelActivity---->APP被干掉了，我要重启它");
        }
        super.onDestroy();
    }

    private void startKeepLiveService() {
        startService(new Intent(this, KeepLiveService.class));

    }
}

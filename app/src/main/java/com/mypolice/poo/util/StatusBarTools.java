package com.mypolice.poo.util;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;

/**   
 * @Title: StatusBarUtil.java 
 * @Package com.hw.ics.util
 * @Description: 状态栏工具类
 * @author wangjl  
 * @crdate 2017-3-27
 * @update 
 * @version v1.0   
 */
public class StatusBarTools {

	public static void setWindowStatusBarColor(Activity activity, int colorResId) {
        try {
        	// 4.4及以上版本开启
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                setTranslucentStatus(activity, true);
            }

            SystemBarTintManager tintManager = new SystemBarTintManager(activity);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setNavigationBarTintEnabled(true);

            // 自定义颜色
//            tintManager.setTintColor(Color.parseColor("#049bdd"));
            tintManager.setTintColor(colorResId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    } 

    @TargetApi(19)
    private static void setTranslucentStatus(Activity activity, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
	
}

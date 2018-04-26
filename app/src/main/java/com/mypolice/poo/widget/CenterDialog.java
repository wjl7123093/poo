package com.mypolice.poo.widget;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.mypolice.poo.R;
import com.mypolice.poo.util.ScreenUtils;

/**   
 * @Title: CenterDialog.java 
 * @Package com.hw.ics.widget
 * @Description: 自定义居中弹出dialog
 * @author wangjl
 * @crdate 2017-3-17  
 * @update  
 * @version v1.0   
 */
public class CenterDialog extends Dialog implements View.OnClickListener {

    private Context context;

    private int layoutResID;

    /**
     * 要监听的控件id
     */
    private int[] listenedItems;

    private OnCenterItemClickListener listener;

    public CenterDialog(Context context, int layoutResID, int[] listenedItems) {
        super(context, R.style.dialog_custom);
        this.context = context;
        this.layoutResID = layoutResID;
        this.listenedItems = listenedItems;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setGravity(Gravity.CENTER); // 此处可以设置dialog显示的位置为居中
//        window.setWindowAnimations(R.style.bottom_menu_animation); // 添加动画效果
        setContentView(layoutResID);
        // 宽度全屏
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = ScreenUtils.getScreenWidth(context)*4/5; // 设置dialog宽度为屏幕的4/5
        lp.height = LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(lp);
        // 点击Dialog外部消失
        setCanceledOnTouchOutside(true);

        for (int id : listenedItems) {
            findViewById(id).setOnClickListener(this);
        }
    }

    public interface OnCenterItemClickListener {

        void OnCenterItemClick(CenterDialog dialog, View view);

    }

    public void setOnCenterItemClickListener(OnCenterItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        dismiss();
        listener.OnCenterItemClick(this, view);
    }
    
    /**
     * 重置窗体大小
     * @param width
     * @param height
     */
    public void setLayoutParams(int width, int height) {
        // 宽度全屏
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = width;
        lp.height = height;
        getWindow().setAttributes(lp);
    }
}


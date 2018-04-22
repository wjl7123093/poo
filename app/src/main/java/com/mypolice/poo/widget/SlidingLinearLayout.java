package com.mypolice.poo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * @Title: SlidingLinearLayout.java
 * @Package com.mypolice.app.widget
 * @Description: 侧边栏导航
 * @author wangjl
 * @crdate 2017-11-15
 * @update
 * @version v2.1.2(14)
 */
public class SlidingLinearLayout extends LinearLayout {

    public SlidingLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

}

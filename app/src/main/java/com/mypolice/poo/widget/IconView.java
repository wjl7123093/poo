package com.mypolice.poo.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**   
 * @Title: IconView.java 
 * @Package com.mypolice.poo.widget
 * @Description: 字体图标控件
 * @author wangjl
 * @crdate 2017-8-21
 * @update  
 * @version v1.0.0
 */
public class IconView extends TextView {

	public IconView(Context context) {
		super(context, null);
		init(context);
	}

	public IconView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);		
	}
	
	/** 设置字体图标 */
    private void init(Context context) {
    	Typeface iconfont = Typeface.createFromAsset(context.getAssets(), 
				"iconfont/iconfont.ttf");
    	this.setTypeface(iconfont);
    }

}

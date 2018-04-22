/**   
 * @Title: WebProgressBar.java 
 * @Description: WebView加载数据进度条。
 * @author qingxd   
 * @date 2015年4月21日 下午3:42:17 
 * @version V1.0   
 */

package com.mypolice.poo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.mypolice.poo.R;

public class WebProgressBar extends LinearLayout {

	private Context mContext;
	private ImageView mImageView;
	private final int MAX_PROGRESS = 100;

	public WebProgressBar(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		mContext = context;
		initView();
	}

	public WebProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mContext = context;
		initView();
	}

	public WebProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
		mContext = context;
		initView();
	}

	private void initView() {
		LayoutInflater.from(mContext).inflate(R.layout.progressbar_web, this);
		mImageView = (ImageView) findViewById(R.id.webprogress_iv);
	}

	/**
	 * 设置进度条
	 * 
	 * @param progress
	 */
	public void setProgress(int progress) {
		if (progress == MAX_PROGRESS) {
			setVisibility(View.GONE);
		}
		// 获取进度条总长度
		int width = getWidth();
		// 计算当前长度
		int progressWidth = (int) (((float) width / 100f) * (float) progress);
		LayoutParams lp = (LayoutParams) mImageView.getLayoutParams();
		lp.width = progressWidth;
		mImageView.setLayoutParams(lp);
	}

}

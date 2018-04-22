package com.mypolice.poo.widget;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mypolice.poo.R;

/**   
 * @Title: TitleBarView.java 
 * @Package com.mypolice.poo.widget
 * @Description: 界面共用顶部控件。包含左右两侧按钮盒标题
 * @author wangjl
 * @crdate 2017-8-21
 * @update 2017-10-26
 * @version v2.1.0(12)
 */
public class TitleBarView extends LinearLayout {

	private Context mContext;
	private RelativeLayout mRlTitle;
	private TextView mTvTitle;
	private TextView mTvMenu;
	private IconView mIconMenu;
	
	private ImageView mIvBack;

	/**
	 * 两个构造函数不能缺失
	 * 
	 * @param context
	 */
	public TitleBarView(Context context) {
		super(context);
		mContext = context;
		initView();
	}

	public TitleBarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		initView();
	}

	private void initView() {
		LayoutInflater.from(mContext).inflate(R.layout.view_title_bar, this);
		mRlTitle = (RelativeLayout) findViewById(R.id.rlTitle);
		mTvTitle = (TextView) findViewById(R.id.tvTitle);
		mTvMenu = (TextView) findViewById(R.id.tvMenu);
		mIconMenu = (IconView) findViewById(R.id.iconMenu);

		mIvBack = (ImageView) findViewById(R.id.ivBack);
		mIvBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				((Activity) mContext).finish();
			}
		});

		setRightBtnGONE();	// 默认屏蔽右侧按钮

	}

	/**
	 * 添加左侧按钮事件
	 * @param listener
	 */
	public void setLeftBtnOnClickListener(OnClickListener listener) {
		mIvBack.setOnClickListener(listener);
	}
	
	/**
	 * 设置左侧按钮是否显示
	 * @param isVisiable
	 */
	public void setLeftBtnVisibility(int isVisiable) {
		mIvBack.setVisibility(isVisiable);
	}

	/**
	 * 添加右侧按钮事件
	 * @param listener
	 */
	public void setRightBtnOnClickListener(OnClickListener listener) {
		if (View.VISIBLE == mTvMenu.getVisibility())
			mTvMenu.setOnClickListener(listener);
		else if (View.VISIBLE == mIconMenu.getVisibility())
			mIconMenu.setOnClickListener(listener);
	}

	/**
	 * 默认屏蔽右侧按钮
	 */
	public void setRightBtnGONE() {
		mTvMenu.setVisibility(View.GONE);
		mIconMenu.setVisibility(View.GONE);
	}

	/**
	 * 设置右侧按钮文字
	 * @param text
     */
	public void setRightBtnText(String text) {
		mTvMenu.setText(text);
		mTvMenu.setVisibility(View.VISIBLE);
		mIconMenu.setVisibility(View.GONE);
	}

	/**
	 * 设置右侧按钮图标
	 * @param text
     */
	public void setRightBtnIcon(String text) {
		mIconMenu.setText(text);
		mIconMenu.setVisibility(View.VISIBLE);
		mTvMenu.setVisibility(View.GONE);
	}

	/**
	 * 设置标题文字内容v
	 * @param text
	 */
	public void setText(String text) {
		mTvTitle.setText(text);
	}
	
	/**
	 * 设置标题背景色
	 * @param color
	 */
	public void setTitleBackground(int color) {
		mRlTitle.setBackgroundColor(color);
	}
}

package com.mypolice.poo.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mypolice.poo.application.PooApplication;
import com.mypolice.poo.widget.IconView;

/**   
 * @Title: ViewHolder.java 
 * @Package com.mypolice.poo.adapter
 * @Description: 通用的 ViewHolder
 * @author wangjl  
 * @crdate 2017-8-23
 * @update 2017-10-26
 * @version v2.1.0(12)
 */
public class ViewHolder {
	
	/* 
	 * 这里之所以用 SparseArray<E> 而不用 Map，
	 * 是因为 SparseArray<E> 为 Android 提供，
	 * 且效率高于 Map。只是其 Key 只能为 Integer。
	 */
	private final SparseArray<View> mViews;
	private int mPosition;
	private View mConvertView;
	
	private ViewHolder(Context context, ViewGroup parent,
			int layoutId, int position) {		
		this.mPosition = position;
		this.mViews = new SparseArray<View>();
		mConvertView = LayoutInflater.from(context).inflate(layoutId, 
				parent, false);
		
		// setTag
		mConvertView.setTag(this);
	}
	
	/**
	 * 拿到一个 ViewHolder 对象
	 * @param context
	 * @param convertView
	 * @param parent
	 * @param layoutId
	 * @param position
	 * @return ViewHolder 对象
	 */
	public static ViewHolder get(Context context, View convertView,
			ViewGroup parent, int layoutId, int position) {
		if (null == convertView) {
			return new ViewHolder(context, parent, layoutId, position);
		}
		return (ViewHolder) convertView.getTag();
	}
	
	/**
	 * 通过控件的 Id 获取对应的控件，如果没有则加入 views
	 * @param viewId
	 * @return
	 */
	public <T extends View> T getView(int viewId) {
		View view = mViews.get(viewId);
		if (null == view) {
			view = mConvertView.findViewById(viewId);
			mViews.put(viewId, view);
		}
		return (T) view;
	}
	
	/**
	 *  得到 ConvertView
	 * @return
	 */
	public View getConvertView() {
		return mConvertView;
	}
	
	/**
	 * 为 TextView 设置字符串
	 * @param viewId
	 * @param text
	 * @return
	 */
	public ViewHolder setText(int viewId, String text) {
		TextView view = getView(viewId);
		view.setText(text);
		return this;
	}
	
	/**
	 * 为 IconView 设置字体图标
	 * @param viewId
	 * @param text
	 * @return
	 */
	public ViewHolder setIcon(int viewId, String text, int color) {
		IconView view = getView(viewId);
		view.setText(text);
		view.setTextColor(color);
		return this;
	}
	
	/**
	 * 为 TextView 设置字符串
	 * @param viewId
	 * @param text
	 * @return
	 */
	public ViewHolder setText(int viewId, String text, Object tag) {
		TextView view = getView(viewId);
		view.setText(text);
		if (null != tag)
			view.setTag(tag);
		return this;
	}
	
	/**
	 * 为 ImageView 设置图片
	 * @param viewId
	 * @param drawableId
	 * @return
	 */
	public ViewHolder setImageResource(int viewId, int drawableId) {
		ImageView view = getView(viewId);
		view.setImageResource(drawableId);		
		return this;
	}
	
	/**
	 * 为 ImageView 设置图片
	 * @param viewId
	 * @param bm
	 * @return
	 */
	public ViewHolder setImageBitmap(int viewId, Bitmap bm) {
		ImageView view = getView(viewId);
		view.setImageBitmap(bm);		
		return this;
	}
	
	/**
	 * 为 ImageView 设置图片
	 * @param viewId
	 * @param bm
	 * @return
	 */
	public ViewHolder setImageByUrl(int viewId, String url) {
		PooApplication.getInstance().mBtimapUtils.display(getView(viewId), url);
		return this;
	}


	public ViewHolder setBackground(int viewId, Drawable drawable) {
		getView(viewId).setBackground(drawable);
		return this;
	}
	
	public int getPosition() {
		return mPosition;
	}

	public ViewHolder setOnClickListener(int viewId, View.OnClickListener onClickListener) {
		View view = getView(viewId);
		view.setOnClickListener(onClickListener);
		return this;
	}

	public ViewHolder setVisibility(int viewId, int visibility) {
		View view = getView(viewId);
		view.setVisibility(visibility);
		return this;
	}
	
}

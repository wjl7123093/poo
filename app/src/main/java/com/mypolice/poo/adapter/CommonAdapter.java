package com.mypolice.poo.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**   
 * @Title: CommonAdapter.java 
 * @Package com.mypolice.poo.adapter
 * @Description: 通用的 Adapter [abstract 类]
 * @author wangjl  
 * @crdate 2017-3-6
 * @update 
 * @version v1.0   
 */
public abstract class CommonAdapter<T> extends BaseAdapter {
	
	protected LayoutInflater mInflater;
	protected Context mContext;
	protected List<T> mDatas;
	protected final int mItemLayoutId;
	
	public CommonAdapter(Context context, List<T> mDatas, 
			int itemLayoutId) {
		mInflater = LayoutInflater.from(context);
		this.mContext = context;
		this.mDatas = mDatas;
		this.mItemLayoutId = itemLayoutId;
	}

	@Override
	public int getCount() {
		return mDatas.size();
	}

	@Override
	public T getItem(int position) {
		return mDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		final ViewHolder viewHolder = getViewHolder(position, convertView, 
				parent);
		convert(viewHolder, getItem(position));		
		return viewHolder.getConvertView();
	}
	
	/**
	 * 设置控件属性，绑定数据
	 * @param helper
	 * @param item
	 */
	public abstract void convert(ViewHolder helper, T item);
	
	private ViewHolder getViewHolder(int position, View convertView,
			ViewGroup parent) {
		return ViewHolder.get(mContext, convertView, parent, mItemLayoutId, 
				position);
	}

}

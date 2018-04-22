package com.mypolice.poo.util;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.mypolice.poo.bean.GvFunctionBean;

/**   
 * @Title: FunctionManager.java 
 * @Package com.mypolice.poo.adapter
 * @Description: 九宫格功能管理类
 * @author wangjl  
 * @crdate 2017-8-21
 * @update
 * @version v1.0.0(1)
 */
public class FunctionManager {

	private static volatile FunctionManager instance = null;
	private Context mContext;
	private FunctionXMLReader<GvFunctionBean> mXmlReader;
	private List<GvFunctionBean> mBeans;
	
	private FunctionManager(Context context) {
		this.mContext = context;
	}
	
	/** 单例 */
	public static FunctionManager getInstance(Context context) {
		if (null == instance) {
			synchronized (FunctionManager.class) {
				if (null == instance) {
					instance = new FunctionManager(context);
				}
			}
		}
		return instance;
	}
	
	/** 初始化 */
	public void init() {
		mXmlReader = new FunctionXMLReader<GvFunctionBean>(
				mContext, GvFunctionBean.class);
		mXmlReader.parse();
		mBeans = mXmlReader.getBeans();
	}
	
	/** 得到 GvFunctionBean */
	public List<GvFunctionBean> getBeans() {
		return mBeans;
	}
	
	/** 
	 * 执行每一项功能
	 * @param position 项索引
	 * @throws ClassNotFoundException 
	 */
	public void lanchFunction(int position)
			throws ClassNotFoundException {
		GvFunctionBean bean = mBeans.get(position);		
		String name = bean.getName();	// 功能名称
		String className = bean.getPackageName() + "." + bean.getActivityName();

		Intent intent = new Intent();
		intent.setClass(mContext, Class.forName(className));
		mContext.startActivity(intent);

	}
	
}

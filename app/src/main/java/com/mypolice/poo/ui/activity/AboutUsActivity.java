package com.mypolice.poo.ui.activity;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.mypolice.poo.R;
import com.mypolice.poo.util.CommonFuncUtil;
import com.mypolice.poo.widget.TitleBarView;

import android.os.Bundle;
import android.widget.TextView;

/**   
 * @Title: AboutUsActivity.java 
 * @Package com.mypolice.poo.ui.activity
 * @Description: 关于我们页面
 * @author wangjl  
 * @crdate 2017-8-21
 * @update 
 * @version v2.0.0(2)
 */
@ContentView(R.layout.activity_about_us)
public class AboutUsActivity extends BaseActivityPoo {

	/** TitleBarView 顶部标题栏 */
	@ViewInject(R.id.titleAboutUs)
	private TitleBarView mTitleAboutUs;
//	@ViewInject(R.id.tvVersion)
//	private TextView mTvVersion;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);

		initView();
	}
	
	@Override
	public void initView() {
		super.initView();
		mTitleAboutUs.setText("关于我们");
//		mTvVersion.setText("Version: " + CommonFuncUtil
//				.getVersion(AboutUsActivity.this).getVersionName());
	}
}

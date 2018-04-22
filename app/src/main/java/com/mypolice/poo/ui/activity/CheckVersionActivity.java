package com.mypolice.poo.ui.activity;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.mypolice.poo.R;
import com.mypolice.poo.widget.TitleBarView;

import android.os.Bundle;

/**   
 * @Title: CheckVersionActivity.java 
 * @Package com.mypolice.poo.ui.activity
 * @Description: 检查更新页面
 * @author wangjl  
 * @crdate 2017-8-21
 * @update 
 * @version v2.0.0(2)
 */
@ContentView(R.layout.activity_check_version)
public class CheckVersionActivity extends BaseActivityPoo {

	/** TitleBarView 顶部标题栏 */
	@ViewInject(R.id.titleCheckVersion)
	private TitleBarView mTitleCheckVersion;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);

		initView();
	}
	
	@Override
	public void initView() {
		super.initView();
		mTitleCheckVersion.setText("检查更新");
	}
	
}

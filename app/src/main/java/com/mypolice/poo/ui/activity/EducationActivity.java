package com.mypolice.poo.ui.activity;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.mypolice.poo.R;
import com.mypolice.poo.adapter.CommonAdapter;
import com.mypolice.poo.adapter.FragmentAdapter2;
import com.mypolice.poo.adapter.ViewHolder;
import com.mypolice.poo.application.GlobalSet;
import com.mypolice.poo.application.PooApplication;
import com.mypolice.poo.bean.ArticlesBean;
import com.mypolice.poo.bean.CategoryBean;
import com.mypolice.poo.bean.WorkBean;
import com.mypolice.poo.ui.fragment.Fragment1;
import com.mypolice.poo.ui.fragment.Fragment4;
import com.mypolice.poo.util.CommonFuncUtil;
import com.mypolice.poo.widget.CategoryTabStrip;
import com.mypolice.poo.widget.RefreshLayout;
import com.mypolice.poo.widget.TitleBarView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**   
 * @Title: EducationActivity.java 
 * @Package com.mypolice.poo.ui.activity
 * @Description: 宣传教育页面
 * @author wangjl  
 * @crdate 2017-8-21
 * @update 2017-9-4
 * @version v2.0.0(2)
 */
@ContentView(R.layout.activity_education)
public class EducationActivity extends FragmentActivity {

	/** TitleBarView 顶部标题栏 */
	@ViewInject(R.id.titleEducation)
	private TitleBarView mTitleEducation;

	@ViewInject(R.id.refreshContainer)
	private RefreshLayout mRefreshContainer;
	@ViewInject(R.id.pagerEducation)
	private ViewPager mPager;
	@ViewInject(R.id.categoryTabStrip)
	private CategoryTabStrip mTabs;
	@ViewInject(R.id.progressBar1)
	private ProgressBar mProgressBar;

	private PooApplication mApplication;

	private List<Fragment> mFragmentList = new ArrayList<Fragment>();
	private FragmentAdapter2 mFragmentAdapter;

	private Fragment1 mFg1;
	private Fragment4 mFg4;

	public final static String TAB_1 = "tab_1";
	public final static String TAB_4 = "tab_4";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);

		mApplication = (PooApplication) getApplication();
		initView();
		loadData();
	}

	public void initView() {
		mTitleEducation.setText("宣传教育");
		mProgressBar.setVisibility(View.VISIBLE);

		// 设置刷新加载进度动画颜色
		mRefreshContainer.setColorSchemeResources(R.color.app_main_green);
		initRefreshListeners();
	}

	private void loadData() {
		getPortalCategory();
	}

	/** 初始化刷新监听 */
	private void initRefreshListeners() {
		// 下拉刷新
		mRefreshContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

			@Override
			public void onRefresh() {
				mRefreshContainer.post(new Runnable() {

					@Override
					public void run() {
						getPortalCategory();
					}
				});
			}
		});
	}

	/**
	 * 获取分类数据
	 */
	private void getPortalCategory() {
//		String url = GlobalSet.APP_SERVER_URL + "portal_category/getPortalCategory";
		String url = GlobalSet.APP_SERVER_URL + "Dynamic/getPortalCategory";
		OkHttpUtils.get().url(url)
				.addHeader("token", mApplication.getToken())
				.build()
				.execute(new StringCallback() {
					@Override
					public void onError(Call call, Exception e, int id) {
						CommonFuncUtil.getToast(EducationActivity.this, e.getMessage());
					}

					@Override
					public void onResponse(String response, int id) {
//						CommonFuncUtil.getToast(EducationActivity.this, response);
						try {
							JSONObject jsonResponse = new JSONObject(response);
							if (jsonResponse.getInt("code") == 0
									|| jsonResponse.getInt("code") == 200) {
								org.json.JSONArray array = jsonResponse.getJSONArray("data");
								List<CategoryBean> categoryList = new ArrayList<CategoryBean>();
								CategoryBean category = null;
								for (int i = 0; i < array.length(); i++) {
									category = JSON.parseObject(array.getString(i), CategoryBean.class);
//									CommonFuncUtil.getToast(EducationActivity.this, category.toString());
									categoryList.add(category);
								}
								bindDataToUI(bindLastCategoryToList(categoryList));
							} else if (jsonResponse.getInt("code") == 1007) {
								// token 失效，踢出当前用户，退到登录页面
								CommonFuncUtil.getToast(EducationActivity.this,
										"当前用户已在别处登录，请重新登录");
								mApplication.removeALLActivity_();
								CommonFuncUtil.goNextActivityWithNoArgs(EducationActivity.this,
										LoginActivity.class, true);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});

	}

	/**
	 * 绑定数据到 UI
	 * @param categoryList
     */
	private void bindDataToUI(List<CategoryBean> categoryList) {
		initFragmentList(categoryList);

		// 填充数据到FragmentAdapter
		mFragmentAdapter = new FragmentAdapter2(getSupportFragmentManager(),
				mFragmentList, categoryList);
		mPager.setAdapter(mFragmentAdapter);
		mPager.setCurrentItem(0);
		mTabs.setViewPager(mPager);

		mPager.setVisibility(View.VISIBLE);
		mProgressBar.setVisibility(View.GONE);
	}

	/** 初始化 Fragment 列表 [ 根据分类集合 ] */
	private void initFragmentList(List<CategoryBean> categoryList) {
		// 排除最后一个（友情链接），其余都是列表Fragment
		for (int i = 0; i < categoryList.size() - 1; i++) {
			Fragment1 childFragment = Fragment1.newInstance(categoryList.get(i).getId());
			mFragmentList.add(childFragment);
		}

		Fragment4 fragment4 = new Fragment4();
		mFragmentList.add(fragment4);
	}

	/**
	 * 绑定最后一个分类（友情链接）
	 * @param categoryList
	 * @return
     */
	private List<CategoryBean> bindLastCategoryToList(List<CategoryBean> categoryList) {
		CategoryBean categoryBean = new CategoryBean();
		categoryBean.setId(999);
		categoryBean.setName("友情链接");
		categoryBean.setSort(999);
		categoryBean.setType(999);
		categoryBean.setType_text("link");
		categoryList.add(categoryBean);
		return categoryList;
	}
	
}

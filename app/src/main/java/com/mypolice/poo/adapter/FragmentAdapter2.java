package com.mypolice.poo.adapter;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mypolice.poo.bean.CategoryBean;

/**   
 * @Title: FragmentAdapter2.java 
 * @Package com.mypolice.poo.adapter
 * @Description: 通用的 FragmentPagerAdapter 
 * 							   ViewPager的适配器（嵌套Fragment页面）
 * @author wangjl  
 * @crdate 2017-8-30
 * @update
 * @version v1.0.0(1)
 */
public class FragmentAdapter2 extends FragmentPagerAdapter {

	private List<Fragment> mFragmentList;
    private List<CategoryBean> mCatList = null;

	public FragmentAdapter2(FragmentManager fm, List<Fragment> fragmentList,
			List<CategoryBean> catList) {
		super(fm);
		mFragmentList = fragmentList;
		mCatList = catList;	
	}

	public FragmentAdapter2(FragmentManager fm) {
		super(fm);
	}

    @Override
    public CharSequence getPageTitle(int position) {
        return mCatList.get(position).getName();
    }

	@Override
	public Fragment getItem(int position) {
		return mFragmentList.get(position);
	}

	@Override
	public int getCount() {
		return mCatList.size();
//		return mFragmentList.size();
	}

}

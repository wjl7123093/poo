package com.mypolice.poo.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mypolice.poo.R;
import com.mypolice.poo.adapter.CommonAdapter;
import com.mypolice.poo.adapter.ViewHolder;
import com.mypolice.poo.application.GlobalSet;
import com.mypolice.poo.bean.ArticlesBean;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * @Title: Fragment4.java
 * @Package com.mypolice.poo.ui.fragment
 * @Description: fragment4
 * @author wangjl
 * @crdate 2017-8-30
 * @update 2017-9-7
 * @version v1.0.0(1)
 */
public class Fragment4 extends BaseFragment {

    /** TextView 链接 */
    @ViewInject(R.id.tvLink)
    private TextView mTvLink;

    /** 页面视图 View */
    private View view = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        /** 防止 切换Fragment (repleaceFragment) 后重复加载 onCreateView 等方法 */
        if (null != view) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (null != parent) {
                parent.removeView(view);
            }
        } else {
            view = inflater.inflate(R.layout.fragment_4, container, false);
            ViewUtils.inject(this, view);

            initView();
            loadData();
        }

        return view;
    }

    @Override
    public void initView() {
        super.initView();
        mTvLink.setText(Html.fromHtml("<a href='http://www.626china.org/'>http://www.626china.org/</a>"));
    }

    @Override
    public void loadData() {
        super.loadData();
    }

    @OnClick(R.id.tvLink)
    public void onTvLinkClick(View v) {
        jumpToBrowser("http://www.626china.org/");
    }

    /**
     * 广告位点击跳转到浏览器里
     * @param url
     */
    private void jumpToBrowser(String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        getActivity().startActivity(intent);
    }

}

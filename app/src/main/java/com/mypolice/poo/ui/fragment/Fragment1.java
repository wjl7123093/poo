package com.mypolice.poo.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.mypolice.poo.R;
import com.mypolice.poo.adapter.CommonAdapter;
import com.mypolice.poo.adapter.ViewHolder;
import com.mypolice.poo.application.GlobalSet;
import com.mypolice.poo.bean.ArticlesBean;
import com.mypolice.poo.ui.activity.EducationActivity;
import com.mypolice.poo.ui.activity.LeaveListActivity;
import com.mypolice.poo.ui.activity.LoginActivity;
import com.mypolice.poo.ui.activity.WebviewActivity;
import com.mypolice.poo.util.CommonFuncUtil;
import com.mypolice.poo.widget.RefreshLayout;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * @Title: Fragment1.java
 * @Package com.mypolice.poo.ui.fragment
 * @Description: fragment1
 * @author wangjl
 * @crdate 2017-8-30
 * @update
 * @version v1.0.0(1)
 */
public class Fragment1 extends BaseFragment {

    private static int CATEGORY_ID = 26;

    @ViewInject(R.id.refreshContainer)
    private RefreshLayout mRefreshContainer;
    /** 宣传教育 新闻列表 */
    @ViewInject(R.id.lvEducation)
    private ListView mLvEducation;


    /** 页面视图 View */
    private View view = null;

    private CommonAdapter mAdapter;
    private int mCatID;
    private int mPage = 1;

    private List<ArticlesBean> mArticlesList = new ArrayList<ArticlesBean>();

    /**
     * 创建带参实例
     * @param catID
     * @return
     */
    public static Fragment1 newInstance(int catID){
        Bundle bundle = new Bundle();
        bundle.putInt("catID", catID);
        Fragment1 childFm = new Fragment1();
        childFm.setArguments(bundle);
        return childFm;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        if(bundle != null){
            mCatID = bundle.getInt("catID");
        }
    }

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
            view = inflater.inflate(R.layout.fragment_1, container, false);
            ViewUtils.inject(this, view);

            initView();
            loadData();
        }

        return view;
    }

    @Override
    public void initView() {
        super.initView();
        // 设置刷新加载进度动画颜色
        mRefreshContainer.setColorSchemeResources(R.color.app_main_green);
//        mRefreshContainer.setLoading(true);
        initRefreshListeners();
    }

    @Override
    public void loadData() {
        super.loadData();
        getArticlesData(1, mCatID);
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
                        mArticlesList.clear();
                        getArticlesData(1, mCatID);
                    }
                });
            }
        });
        mRefreshContainer.setOnLoadListener(new RefreshLayout.OnLoadListener() {
            @Override
            public void onLoad() {
                getArticlesData(++mPage, mCatID);
            }
        });
    }

    /**
     * 获取文章列表数据
     */
    private void getArticlesData(final int page, int catID) {
        mPage = page;
        String url = GlobalSet.APP_SERVER_URL + "category/" + catID;
        OkHttpUtils.get().url(url)
                .addHeader("token", mApplication.getToken())
                .addParams("page", page + "")
                .addParams("per_page", "10")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        CommonFuncUtil.getToast(getActivity(), e.getMessage());
                        if (mRefreshContainer.isRefreshing())
                            mRefreshContainer.setRefreshing(false);
                    }

                    @Override
                    public void onResponse(String response, int id) {
//                        CommonFuncUtil.getToast(getActivity(), response);
                        if (mRefreshContainer.isRefreshing())
                            mRefreshContainer.setRefreshing(false);
                        if (page > 1)
                            mRefreshContainer.setLoading(false);
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            if (jsonResponse.getInt("code") == 0
                                    || jsonResponse.getInt("code") == 200) {
                                org.json.JSONArray array = jsonResponse.getJSONObject("data")
                                        .getJSONObject("articles").getJSONArray("data");

                                // 判断 array 是否长度为0
                                if (array.length() == 0) {
                                    CommonFuncUtil.getToast(getActivity(), "已加载完毕");
                                    return;
                                }

                                ArticlesBean article = null;
                                for (int i = 0; i < array.length(); i++) {
                                    article = JSON.parseObject(array.getString(i), ArticlesBean.class);
//                                    CommonFuncUtil.getToast(getActivity(), article.toString());
                                    mArticlesList.add(article);
                                }
                                bindDataToUI(mArticlesList);
                            } else if (jsonResponse.getInt("code") == 1007) {
                                // token 失效，踢出当前用户，退到登录页面
                                CommonFuncUtil.getToast(getActivity(),
                                        "当前用户已在别处登录，请重新登录");
                                mApplication.removeALLActivity_();
                                CommonFuncUtil.goNextActivityWithNoArgs(getActivity(),
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
     * @param articlesList
     */
    private void bindDataToUI(List<ArticlesBean> articlesList) {
        if (null != getActivity()) {    // getActivity() 不为空再赋值
            mAdapter = new CommonAdapter<ArticlesBean>(getActivity(), articlesList,
                    R.layout.item_lv_education) {
                @Override
                public void convert(ViewHolder helper, final ArticlesBean item) {
                    helper.setText(R.id.tvItemTitle, item.getName());
                    helper.setText(R.id.tvItemTime, item.getCreate_time());
                    helper.setOnClickListener(R.id.llItemNews, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Bundle bundle = new Bundle();
                            bundle.putString("title", item.getName());
                            bundle.putString("website", GlobalSet.APP_DETAIL_URL + item.getId());
                            CommonFuncUtil.goNextActivityWithArgs(getActivity(), WebviewActivity.class,
                                    bundle, false);
                        }
                    });
                }
            };
            mLvEducation.setAdapter(mAdapter);
        }
    }
}

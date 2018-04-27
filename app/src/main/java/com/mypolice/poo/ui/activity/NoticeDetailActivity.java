package com.mypolice.poo.ui.activity;

import android.os.Bundle;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.mypolice.poo.R;
import com.mypolice.poo.widget.TitleBarView;

/**
 * @Title: NoticeDetailActivity.java
 * @Package com.mypolice.poo.ui.activity
 * @Description: 关于我们页面
 * @author wangjl
 * @crdate 2018-4-27
 * @update
 * @version v1.0.1(2)[六安]
 */
@ContentView(R.layout.activity_notice_detail)
public class NoticeDetailActivity extends BaseActivityPoo {

    /** TitleBarView 顶部标题栏 */
    @ViewInject(R.id.titleAboutUs)
    private TitleBarView mTitleAboutUs;

    @ViewInject(R.id.tv_title)
    private TextView mTvTitle;
    @ViewInject(R.id.tv_datetime)
    private TextView mTvTime;
    @ViewInject(R.id.tv_content)
    private TextView mTvContent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewUtils.inject(this);

        Bundle bundle = getIntent().getExtras();
        String title = bundle.getString("title");
        String time = bundle.getString("time");
        String content = bundle.getString("content");

        mTvTitle.setText(title);
        mTvTime.setText(time);
        mTvContent.setText(content);
    }
}

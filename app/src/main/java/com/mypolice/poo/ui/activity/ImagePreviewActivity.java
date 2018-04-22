package com.mypolice.poo.ui.activity;


import android.os.Bundle;
import android.widget.ImageView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.mypolice.poo.R;

/**
 * @Title: ImagePreviewActivity.java
 * @Package com.mypolice.poo.ui.activity
 * @Description: 图片预览页面
 * @author wangjl
 * @crdate 2017-8-28
 * @update
 * @version v2.0.0(2)
 */
@ContentView(R.layout.activity_image_preview)
public class ImagePreviewActivity extends BaseActivityPoo {

    @ViewInject(R.id.ivPreview)
    private ImageView mIvPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewUtils.inject(this);

        String bmpPath = getIntent().getExtras().getString("bmpPath");
        mApplication.mBtimapUtils.display(mIvPreview, bmpPath);

    }

    @Override
    public void initView() {
        super.initView();
    }
}

package com.mypolice.poo.ui.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.mypolice.poo.R;
import com.mypolice.poo.application.ApiCode;
import com.mypolice.poo.application.GlobalSet;
import com.mypolice.poo.bean.PunishBean;
import com.mypolice.poo.util.CommonFuncUtil;
import com.mypolice.poo.widget.TitleBarView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;

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

    /** 加载进度条 */
    private SweetAlertDialog pDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewUtils.inject(this);

        Bundle bundle = getIntent().getExtras();
        int msg_id = bundle.getInt("msg_id");
        String title = bundle.getString("title");
        String time = bundle.getString("time");
        String content = bundle.getString("content");
        int is_read = bundle.getInt("is_read");

        mTvTitle.setText(title);
        mTvTime.setText(time);
        mTvContent.setText(content);

        initView();

        if (is_read == 0) { // 未读
            pDialog.show();
            getMessage(msg_id);
        }
    }

    @Override
    public void initView() {
        super.initView();

        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("正在加载...");
        pDialog.setCancelable(false);
    }

    /**
     * 获取消息详情
     */
    private void getMessage(int msg_id) {
        String url = GlobalSet.APP_SERVER_URL + "app.message/getMessage";
        OkHttpUtils.get().url(url)
                .addHeader(GlobalSet.APP_TOKEN_KEY, mApplication.getToken())
                .addParams("id", msg_id + "")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        pDialog.dismiss();
                        CommonFuncUtil.getToast(NoticeDetailActivity.this, e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        pDialog.dismiss();
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            if (jsonResponse.getInt("code") == ApiCode.CODE_SUCCESS) {  // 已读
                                Log.i("NOTICE_MSG", "MSG is read.");

                            } else if (jsonResponse.getInt("code") == ApiCode.CODE_TOKEN_EXPIRED) {
                                // token 失效，踢出当前用户，退到登录页面
                                CommonFuncUtil.isTokenExpired(NoticeDetailActivity.this);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

    }
}

package com.mypolice.poo.ui.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mypolice.poo.R;
import com.mypolice.poo.util.CommonFuncUtil;
import com.mypolice.poo.util.FileUtils;
import com.mypolice.poo.widget.TitleBarView;

/**
 * @Title: SignaturepadActivity.java
 * @Package com.mypolice.poo.ui.activity
 * @Description: 手写板签到页面
 * @author wangjl
 * @crdate 2017-9-21
 * @update
 * @version v2.0.7(9)
 */
@ContentView(R.layout.activity_signaturepad)
public class SignaturepadActivity extends BaseActivityPoo {

    public static final int RESULT_CODE_SIGNATURE = 0x302;

    /** TitleBarView 顶部标题栏 */
    @ViewInject(R.id.titleSignaturepad)
    private TitleBarView mTitleSignaturePad;

    @ViewInject(R.id.signature_pad)
    private SignaturePad mSignaturePad;
    @ViewInject(R.id.clear_button)
    private Button mBtnClear;
    @ViewInject(R.id.save_button)
    private Button mBtnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewUtils.inject(this);

        initView();

    }

    @Override
    public void initView() {
        super.initView();
        mTitleSignaturePad.setText("签名");
        mSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {

            @Override
            public void onStartSigning() {
                //Event triggered when the pad is touched
            }

            @Override
            public void onSigned() {
                //Event triggered when the pad is signed
                mBtnSave.setEnabled(true);
                mBtnClear.setEnabled(true);
            }

            @Override
            public void onClear() {
                //Event triggered when the pad is cleared
                mBtnSave.setEnabled(false);
                mBtnClear.setEnabled(false);
            }
        });
    }

    /**
     * 清除
     * @param v
     */
    @OnClick(R.id.clear_button)
    public void onBtnClearClick(View v) {
        mSignaturePad.clear();
    }

    /**
     * 保存
     * @param v
     */
    @OnClick(R.id.save_button)
    public void onBtnSaveClick(View v) {
        Bitmap signatureBitmap = mSignaturePad.getSignatureBitmap();
        FileUtils.saveBitmap(signatureBitmap, "test_sign");
        CommonFuncUtil.getToast(SignaturepadActivity.this, "保存成功");

        setResult(RESULT_CODE_SIGNATURE);
        this.finish();
    }
}

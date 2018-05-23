package com.mypolice.poo.service;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.igexin.sdk.GTIntentService;
import com.igexin.sdk.PushConsts;
import com.igexin.sdk.PushManager;
import com.igexin.sdk.message.FeedbackCmdMessage;
import com.igexin.sdk.message.GTCmdMessage;
import com.igexin.sdk.message.GTNotificationMessage;
import com.igexin.sdk.message.GTTransmitMessage;
import com.igexin.sdk.message.SetTagCmdMessage;
import com.mypolice.poo.application.PooApplication;
import com.mypolice.poo.bean.PushMessage;
import com.mypolice.poo.ui.activity.MainActivity;
import com.mypolice.poo.util.ACache;

/**
 * 继承 GTIntentService 接收来自个推的消息, 所有消息在线程中回调, 如果注册了该服务, 则务必要在 AndroidManifest中声明, 否则无法接受消息<br>
 * onReceiveMessageData 处理透传消息<br>
 * onReceiveClientId 接收 cid <br>
 * onReceiveOnlineState cid 离线上线通知 <br>
 * onReceiveCommandResult 各种事件处理回执 <br>
 */
public class IntentService extends GTIntentService {

    PooApplication mApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = (PooApplication) getApplication();
    }

    public IntentService() {

    }

    @Override
    public void onReceiveServicePid(Context context, int pid) {
    }

    @Override
    public void onReceiveMessageData(Context context, GTTransmitMessage msg) {
        String appid = msg.getAppid();
        String taskid = msg.getTaskId();
        String messageid = msg.getMessageId();
        byte[] payload = msg.getPayload();
        String pkg = msg.getPkgName();
        String cid = msg.getClientId();

        if (payload == null) {
            Log.e(TAG, "receiver payload = null");
        } else {
            String data = new String(payload);
            Log.e(TAG, "GTTransmitMessage -> " + "GTTransmitMessage = " + data + msg.getPayload().toString());

            //处理点击通知栏消息
            PushMessage pushMessage = JSON.parseObject(data, PushMessage.class);
            int code = pushMessage.getCODE();
            switch (code) {
                //1:请假列表页
                case 2006:
                    startActivities(1, pushMessage.getID());
                    break;
            }
        }
    }

    @Override
    public void onReceiveClientId(Context context, String clientid) {
        Log.e(TAG, "onReceiveClientId -> " + "clientid = " + clientid);
        String client_id = ACache.get(this).getAsString("client_id");
        client_id = clientid;
        ACache.get(this).put("client_id",client_id);
//        EventBus.getDefault().post(new MessageEvent("checkAcc"));
//        mApplication.setClientId(clientid);
    }

    @Override
    public void onReceiveOnlineState(Context context, boolean online) {
    }

    @Override
    public void onReceiveCommandResult(Context context, GTCmdMessage cmdMessage) {
    }

    @Override
    public void onNotificationMessageArrived(Context context, GTNotificationMessage msg) {
        Log.e(TAG, "onReceiveClientId -> " + "GTNotificationMessage = " + msg.getContent());
    }

    @Override
    public void onNotificationMessageClicked(Context context, GTNotificationMessage msg) {
        Log.e(TAG, "onReceiveClientId -> " + "GTNotificationMessage = " + msg.getContent());
    }

    private void startActivities(int index, int id) {
        //1:请假列表页
        Intent intent = new Intent(this, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("id", id);
        bundle.putInt("class", index);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtras(bundle);

        startActivity(intent);
    }
}
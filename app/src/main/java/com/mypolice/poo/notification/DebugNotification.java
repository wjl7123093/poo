package com.mypolice.poo.notification;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import com.umeng.message.MessageSharedPrefs;
import com.umeng.message.util.HttpRequest;

import org.json.JSONObject;

import java.security.MessageDigest;

/**
 * Created by wupan on 16/10/24.
 *
 */

public class DebugNotification {

    // 使用这两个 【透传发送失败。。】
    private static final String UMENG_APPKEY = "59c9fb77310c934bd400010d";
    private static final String UMENG_MASTER_SECRET = "3awmebvyjpxtyirfma8nax2vks0vme2k";


    /**
     * 发送透传消息
     *
     * @param mContext
     */
    public static void transmission(final Context mContext, final Handler handler) {
        try {
            final AndroidUnicast unicast = new AndroidUnicast("59c9fb77310c934bd400010d", "3awmebvyjpxtyirfma8nax2vks0vme2k");
            unicast.setDeviceToken(MessageSharedPrefs.getInstance(mContext).getDeviceToken());
            unicast.setTicker("Android unicast ticker");
            unicast.setTitle("Title");
            unicast.setText("Demo透传测试");
            unicast.setPlaySound(true);
            unicast.goAppAfterOpen();
            unicast.setDisplayType(AndroidNotification.DisplayType.NOTIFICATION);
            unicast.setProductionMode();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        send(unicast, mContext, handler);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (Exception e) {

        }
    }

    public static void send(UmengNotification msg, final Context mContext, Handler handler) throws Exception {
        String timestamp = Integer.toString((int) (System.currentTimeMillis() / 1000));
        msg.setPredefinedKeyValue("timestamp", timestamp);

        String url = "http://msg.umeng.com/api/send";
        String postBody = msg.getPostBody();

        String p_sign = "POST" + url + postBody + msg.getAppMasterSecret();
        String sign = md5(p_sign);
        url = url + "?sign=" + sign;

        String response = HttpRequest.post(url).acceptJson()
                .send(postBody).body("UTF-8");
        JSONObject responseJson = new JSONObject(response);
        String ret = responseJson.getString("ret");

        if (!ret.equalsIgnoreCase("SUCCESS")) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, "透传发送失败", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, "透传发送成功", Toast.LENGTH_LONG).show();
                }
            });
        }

    }

    public static String md5(String string) {
        byte[] hash = null;

        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);

        for (byte b : hash) {
            int i = (b & 0xFF);
            if (i < 0x10) hex.append('0');
            hex.append(Integer.toHexString(i));
        }

        return hex.toString();
    }
}

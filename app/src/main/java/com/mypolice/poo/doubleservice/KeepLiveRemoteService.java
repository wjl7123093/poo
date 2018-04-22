package com.mypolice.poo.doubleservice;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.widget.Toast;

import com.mypolice.poo.IServiceConnector;

public class KeepLiveRemoteService extends Service {
    private MyBinder binder;
    private ServiceConnection conn;

    @Override
    public void onCreate() {
        super.onCreate();
        // System.out.println("远程进程开启");
        init();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Toast.makeText(getApplicationContext(), "远程进程启动", Toast.LENGTH_LONG).show();
        Intent intents = new Intent();
        intents.setClass(this, KeepLiveLocalService.class);
        bindService(intents, conn, Context.BIND_IMPORTANT);
        return START_STICKY;
    }

    private void init() {
        if (conn == null) {
            conn = new MyConnection();
        }
        binder = new MyBinder();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    static class MyBinder extends IServiceConnector.Stub {


        @Override
        public String getName() throws RemoteException {
            return "远程连接";
        }
    }

    class MyConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            System.out.println("获取远程连接");
        }

        @Override
        public void onServiceDisconnected(ComponentName nme) {
//            Toast.makeText(KeepLiveRemoteService.this, "本地连接被干掉了", Toast.LENGTH_SHORT).show();
            KeepLiveRemoteService.this.startService(new Intent(KeepLiveRemoteService.this,
                    KeepLiveLocalService.class));
            KeepLiveRemoteService.this.bindService(new Intent(KeepLiveRemoteService.this,
                    KeepLiveLocalService.class), conn, Context.BIND_IMPORTANT);
        }
    }

}

package com.domonion.vkphotos;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class ListLoader extends Service {
    MyBinder binder = new MyBinder();
    MyAsyncTask.Listener call;
    //explanations same as in the Photoloader

    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public IBinder onBind(Intent intent) {
        return binder;
    }

    void someTask() {
        MyAsyncTask task = new MyAsyncTask();
        task.setListener(call);
        task.execute();
    }

    class MyBinder extends Binder {
        ListLoader service;

        MyBinder() {
            service = ListLoader.this;
        }

        void setCallback(MyAsyncTask.Listener back) {
            service.call = (back);
        }

        void start() {
            someTask();
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        call = null;
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

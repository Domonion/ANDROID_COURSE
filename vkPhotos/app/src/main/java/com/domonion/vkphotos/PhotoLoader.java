package com.domonion.vkphotos;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.util.LruCache;

import java.io.File;

import static com.domonion.vkphotos.Constants.LOG_TAG;

public class PhotoLoader extends Service {
    File dir = null;
    public Listener call;//it isnt weakref cuz in unbind call=null, and unbind called every time fragment destroys
    //if it was weakref, then GC will take it on the first run and causes re, cuz all links to this listener are weakrefs(another in AnotherAsyncTask)
    AnotherAsyncTask at;
    ImageData item;

    interface Listener {
        void onSuccess(Bitmap img);
    }

    int cacheSize = 1024 * 1024 * 16;

    LruCache<Integer, Bitmap> cache = new LruCache<>(cacheSize);

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG, "onCreate");
        dir = getDir("internalStorage", MODE_PRIVATE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "intent got");
        return super.onStartCommand(intent, flags, startId);
    }

    void start() {
        Bitmap img = cache.get(item.id);
        if (img == null) {
            final String path = item.URL;
            Log.d(LOG_TAG, path.substring(0, 1));
            Log.d(LOG_TAG, "Is call null: " + Boolean.toString(call == null));
            at.execute(item, dir, cache, call);
        } else {
            call.onSuccess(img);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new PhotoLoaderBinder(this);
    }

    class PhotoLoaderBinder extends Binder {
        PhotoLoader service;

        PhotoLoaderBinder(Service servie) {
            this.service = (PhotoLoader) servie;
        }

        void setCallback(Listener back) {
            service.at = new AnotherAsyncTask();
            service.at.setListener(back);
            service.call = (back);
        }

        void setItem(ImageData item) {
            service.item = item;
        }

        void start() {
            service.start();
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

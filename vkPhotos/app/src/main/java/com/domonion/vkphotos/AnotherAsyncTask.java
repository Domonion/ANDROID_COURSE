package com.domonion.vkphotos;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.util.LruCache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;

import static com.domonion.vkphotos.Constants.LOG_TAG;

public class AnotherAsyncTask extends AsyncTask<Object, Void, Bitmap> {
    private WeakReference<PhotoLoader.Listener> pr;

    void setListener(PhotoLoader.Listener ao) {
        Log.d(LOG_TAG, "AZAZAZA: " + Boolean.toString(ao == null));
        pr = new WeakReference<>(ao);
        Log.d(LOG_TAG, "OLOLOLO: " + Boolean.toString(pr.get() == null));
    }

    @Override
    protected Bitmap doInBackground(Object... objs) {
        Log.d(LOG_TAG, Integer.toString(objs.length));
        ImageData data = (ImageData) objs[0];
        File dir = (File) objs[1];
        LruCache<Integer, Bitmap> cache = (LruCache<Integer, Bitmap>) objs[2];
        File f = new File(dir, Integer.toString(data.id) + ".jpg");
        Bitmap img2 = null;
        try {
            if (dir == null || !f.exists()) {
                URLConnection url;
                url = (new URL(data.URL)).openConnection();
                url.connect();
                img2 = BitmapFactory.decodeStream(url.getInputStream());
                FileOutputStream stream = new FileOutputStream(f);
                img2.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            } else {
                img2 = BitmapFactory.decodeStream(new FileInputStream(f));
            }
        } catch (IOException e) {
            Log.d(LOG_TAG, "error: IOEx: " + e.getMessage());
            e.printStackTrace();
        }
        cache.put(data.id, img2);
        return img2;
    }

    @Override
    protected void onPostExecute(Bitmap a) {
        super.onPostExecute(a);
        pr.get().onSuccess(a);
    }
}

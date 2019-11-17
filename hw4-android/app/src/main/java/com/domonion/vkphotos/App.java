package com.domonion.vkphotos;

import android.app.Application;
import android.util.Log;

import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import static com.domonion.vkphotos.Constants.LOG_TAG;

public class App extends Application {
    public static DBHelper helper;
    private static NetworkInterface vkApi;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG, "onCreate MyApp");
        helper = DBHelper.getInstance(this);
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://api.vk.com").addConverterFactory(JacksonConverterFactory.create()).build();
        vkApi = retrofit.create(NetworkInterface.class);
    }

    public static NetworkInterface getApi() {
        return vkApi;
    }

    public static DBHelper getDB() {
        return helper;
    }
}

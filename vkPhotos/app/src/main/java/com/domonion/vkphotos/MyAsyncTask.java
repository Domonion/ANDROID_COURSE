package com.domonion.vkphotos;

import android.os.AsyncTask;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import static java.lang.Math.min;


public class MyAsyncTask extends AsyncTask<Void, Void, ImageData[]> {
    private WeakReference<Listener> listener;

    interface Listener {
        void onSuccess(ImageData[] data);
    }

    void setListener(Listener listener) {
        this.listener = new WeakReference<>(listener);
    }

    @Override
    protected ImageData[] doInBackground(Void... voids) {
        ObjectMapper mapper = new ObjectMapper();
        int countN = Constants.LIST_N;
        ImageData[] data = new ImageData[countN];
        try {
            URL vkURL = new URL(
                    "https://api.vk.com/method/photos.search?q=мем&count=100&v=5.92&access_token=169a4327169a4327169a43272716f26e1b1169a169a43274adf06531ceabf9ce993e0d7"
            );
            URLConnection connection = vkURL.openConnection();
            connection.connect();
            JsonNode root = mapper.readTree(connection.getInputStream());
            JsonNode items = root.path("response").path("items");
            for (int i = 0; i < countN; i++) {
                JsonNode images = items.path(i).path("sizes");
                JsonNode best = items.path(i).path("sizes").path(0);
                for(JsonNode id : images)
                    if (id.path("width").asInt() * id.path("height").asInt() >
                            best.path("width").asInt() * best.path("height").asInt())
                        best = id;
                data[i] = new ImageData(i,
                        best.path("url").asText(),
                        items.path(i).path("text").asText().substring(0, min(40, items.path(i).path("text").asText().length())));
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    protected void onPostExecute(ImageData[] data) {
        listener.get().onSuccess(data);
    }
}
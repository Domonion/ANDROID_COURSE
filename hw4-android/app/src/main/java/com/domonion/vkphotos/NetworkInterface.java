package com.domonion.vkphotos;

import com.fasterxml.jackson.databind.JsonNode;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/*
 https://api.vk.com/method/photos.search?q=" + query + "&count=100&v=5.92&access_token=169a4327169a4327169a43272716f26e1b1169a169a43274adf06531ceabf9ce993e0d7
 */
public interface NetworkInterface {
    @GET("/method/photos.search")
    Call<JsonNode> getData(@Query("q") String q, @Query("count") int count, @Query("v") String v, @Query("access_token") String access_token);
}

package com.khanhleis11.appnghenhac_nhom3.api;

import com.khanhleis11.appnghenhac_nhom3.models.SingerResponse;
import com.khanhleis11.appnghenhac_nhom3.models.SongResponse;
import com.khanhleis11.appnghenhac_nhom3.models.TopicResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiClient {
    @GET("songs")
    Call<SongResponse> getSongs();  // API call to get the songs

    @GET("singers")
    Call<SingerResponse> getSingers();  // New API call to get the singers

    @GET("topics")
    Call<TopicResponse> getTopics(); // API call to get the topics
}

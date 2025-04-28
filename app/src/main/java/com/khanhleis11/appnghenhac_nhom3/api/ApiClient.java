package com.khanhleis11.appnghenhac_nhom3.api;

import com.khanhleis11.appnghenhac_nhom3.models.Song;
import com.khanhleis11.appnghenhac_nhom3.models.SongResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiClient {
    @GET("songs")
    Call<SongResponse> getSongs();  // Changed to return SongResponse
}

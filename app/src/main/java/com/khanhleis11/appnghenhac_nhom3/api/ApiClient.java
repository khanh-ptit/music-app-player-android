package com.khanhleis11.appnghenhac_nhom3.api;

import com.khanhleis11.appnghenhac_nhom3.models.SingerDetailResponse;
import com.khanhleis11.appnghenhac_nhom3.models.SingerResponse;
import com.khanhleis11.appnghenhac_nhom3.models.SongResponse;
import com.khanhleis11.appnghenhac_nhom3.models.TopicDetailResponse;
import com.khanhleis11.appnghenhac_nhom3.models.TopicResponse;
import com.khanhleis11.appnghenhac_nhom3.models.RankingResponse;  // Import thêm RankingResponse

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiClient {

    @GET("songs")
    Call<SongResponse> getSongs();  // API call to get the songs

    @GET("singers")
    Call<SingerResponse> getSingers();  // New API call to get the singers

    @GET("topics")
    Call<TopicResponse> getTopics(); // API call to get the topics

    @GET("songs/ranking")  // Add new API endpoint to get ranking songs
    Call<RankingResponse> getRanking();  // Updated to return RankingResponse instead of SongResponse

    @GET("songs/search/{slug}")
    Call<SongResponse> searchSongs(@Path("slug") String slug);

    @GET("songs/detail/{slug}")
    Call<SongResponse> getSongDetails(@Path("slug") String slug);

    @GET("songs/next/{currentSongId}")
    Call<SongResponse> getNextSong(@Path("currentSongId") String currentSongId);

    @GET("songs/prev/{currentSongId}")
    Call<SongResponse> getPrevSong(@Path("currentSongId") String currentSongId);

    @GET("/topics/{topicId}")
    Call<TopicDetailResponse> getTopicDetails(@Path("topicId") String topicId);

    @GET("/singers/{singerId}")
    Call<SingerDetailResponse> getSingerDetails(@Path("singerId") String singerId);
}

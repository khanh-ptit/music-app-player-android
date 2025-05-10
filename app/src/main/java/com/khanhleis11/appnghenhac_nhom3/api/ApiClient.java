package com.khanhleis11.appnghenhac_nhom3.api;

import com.khanhleis11.appnghenhac_nhom3.models.LoginRequest;
import com.khanhleis11.appnghenhac_nhom3.models.LoginResponse;
import com.khanhleis11.appnghenhac_nhom3.models.RegisterRequest;
import com.khanhleis11.appnghenhac_nhom3.models.RegisterResponse;
import com.khanhleis11.appnghenhac_nhom3.models.SingerDetailResponse;
import com.khanhleis11.appnghenhac_nhom3.models.SingerResponse;
import com.khanhleis11.appnghenhac_nhom3.models.SongResponse;
import com.khanhleis11.appnghenhac_nhom3.models.TopicDetailResponse;
import com.khanhleis11.appnghenhac_nhom3.models.TopicResponse;
import com.khanhleis11.appnghenhac_nhom3.models.RankingResponse;  // Import thêm RankingResponse
import com.khanhleis11.appnghenhac_nhom3.models.UserProfileResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
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

    @PATCH("songs/update-listen/{songId}")
    Call<SongResponse> updateListen(@Path("songId") String songId);

    @POST("/user/login")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);

    @POST("/user/register")  // Add the registerUser API
    Call<RegisterResponse> registerUser(@Body RegisterRequest registerRequest);

    @GET("/user/profile")
    Call<UserProfileResponse> getUserProfile(@Header("Authorization") String token);

    @GET("/user/favorite-songs")
    Call<UserProfileResponse> getFavoriteSongs(@Header("Authorization") String token);

    @PATCH("songs/like/{id}")
    Call<SongResponse> likeSong(@Path("id") String songId, @Header("Authorization") String token);

    @PATCH("songs/favorite/{id}")
    Call<SongResponse> favoriteSong(@Path("id") String songId, @Header("Authorization") String token);

    @GET("/songs/{id}/is-like")
    Call<SongResponse> checkIfLiked(@Path("id") String songId, @Header("Authorization") String token);

    @GET("/songs/{id}/is-favorite")
    Call<SongResponse> checkIfFavorite(@Path("id") String songId, @Header("Authorization") String token);
}

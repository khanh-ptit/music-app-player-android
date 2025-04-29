package com.khanhleis11.appnghenhac_nhom3;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.khanhleis11.appnghenhac_nhom3.adapters.SongAdapter;
import com.khanhleis11.appnghenhac_nhom3.adapters.SingerAdapter;
import com.khanhleis11.appnghenhac_nhom3.adapters.TopicAdapter;  // Import thêm TopicAdapter
import com.khanhleis11.appnghenhac_nhom3.models.Song;
import com.khanhleis11.appnghenhac_nhom3.models.SongResponse;
import com.khanhleis11.appnghenhac_nhom3.models.Singer;
import com.khanhleis11.appnghenhac_nhom3.models.SingerResponse;
import com.khanhleis11.appnghenhac_nhom3.models.Topic;  // Import thêm Topic model
import com.khanhleis11.appnghenhac_nhom3.models.TopicResponse;  // Import thêm TopicResponse model
import com.khanhleis11.appnghenhac_nhom3.api.ApiClient;
import com.khanhleis11.appnghenhac_nhom3.api.RetrofitInstance;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView songListRecycler;
    private SongAdapter songAdapter;
    private RecyclerView singerListRecycler;
    private SingerAdapter singerAdapter;
    private RecyclerView topicListRecycler;  // Declare RecyclerView for topics
    private TopicAdapter topicAdapter;  // Declare TopicAdapter

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup RecyclerView for songs
        songListRecycler = findViewById(R.id.song_list_recycler);
        songListRecycler.setLayoutManager(new LinearLayoutManager(this));

        // Setup RecyclerView for singers
        singerListRecycler = findViewById(R.id.singerlist_recycler);
        singerListRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Setup RecyclerView for topics
        topicListRecycler = findViewById(R.id.topic_recycler);  // Initialize topicRecycler
        topicListRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));  // Set layout manager for topics

        // Call API to get the song list
        ApiClient apiClient = RetrofitInstance.getRetrofitInstance().create(ApiClient.class);

        // Get Songs
        Call<SongResponse> songCall = apiClient.getSongs();
        songCall.enqueue(new Callback<SongResponse>() {
            @Override
            public void onResponse(Call<SongResponse> call, Response<SongResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Song> songs = response.body().getSongs();
                    songAdapter = new SongAdapter(songs);
                    songListRecycler.setAdapter(songAdapter);
                } else {
                    Toast.makeText(MainActivity.this, "Failed to load songs", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SongResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Get Singers
        Call<SingerResponse> singerCall = apiClient.getSingers();
        singerCall.enqueue(new Callback<SingerResponse>() {
            @Override
            public void onResponse(Call<SingerResponse> call, Response<SingerResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Singer> singers = response.body().getSingers();
                    singerAdapter = new SingerAdapter(singers);
                    singerListRecycler.setAdapter(singerAdapter);
                } else {
                    Toast.makeText(MainActivity.this, "Failed to load singers", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SingerResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Get Topics
        Call<TopicResponse> topicCall = apiClient.getTopics();  // Call API for topics
        topicCall.enqueue(new Callback<TopicResponse>() {
            @Override
            public void onResponse(Call<TopicResponse> call, Response<TopicResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Topic> topics = response.body().getTopics();
                    topicAdapter = new TopicAdapter(topics);  // Initialize adapter for topics
                    topicListRecycler.setAdapter(topicAdapter);  // Set adapter for RecyclerView
                } else {
                    Toast.makeText(MainActivity.this, "Failed to load topics", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TopicResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

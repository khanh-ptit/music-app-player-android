package com.khanhleis11.appnghenhac_nhom3;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.khanhleis11.appnghenhac_nhom3.adapters.SongAdapter;
import com.khanhleis11.appnghenhac_nhom3.api.RetrofitInstance;
import com.khanhleis11.appnghenhac_nhom3.models.Song;
import com.khanhleis11.appnghenhac_nhom3.models.TopicDetailResponse;
import com.khanhleis11.appnghenhac_nhom3.api.ApiClient;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TopicDetailActivity extends AppCompatActivity {

    private RecyclerView songListRecycler;
    private SongAdapter songAdapter;
    private TextView topicTitle, topicDescription;
    private ImageView topicImage;
    private LinearLayout topicHeader;
    private ProgressBar loadingSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_detail);

        // Initialize views
        topicTitle = findViewById(R.id.topic_title);
        topicDescription = findViewById(R.id.topic_description);
        topicImage = findViewById(R.id.topic_image);
        songListRecycler = findViewById(R.id.song_list_recycler);
        loadingSpinner = findViewById(R.id.loading_spinner);
        topicHeader = findViewById(R.id.topic_header); // Add this line to reference topicHeader

        // Set up RecyclerView
        songListRecycler.setLayoutManager(new LinearLayoutManager(this));

        // Get Topic ID from Intent
        String topicId = getIntent().getStringExtra("topic_id");

        // Show progress bar while loading data
        loadingSpinner.setVisibility(View.VISIBLE);
        songListRecycler.setVisibility(View.GONE);
        topicHeader.setVisibility(View.GONE); // Hide the header as well

        // Load topic details and songs
        loadTopicDetails(topicId);
    }

    private void loadTopicDetails(String topicId) {
        ApiClient apiClient = RetrofitInstance.getRetrofitInstance().create(ApiClient.class);

        // Call API to get topic details by ID
        Call<TopicDetailResponse> call = apiClient.getTopicDetails(topicId);
        call.enqueue(new Callback<TopicDetailResponse>() {
            @Override
            public void onResponse(Call<TopicDetailResponse> call, Response<TopicDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    TopicDetailResponse topicDetailResponse = response.body();

                    // Set topic details
                    topicTitle.setText(topicDetailResponse.getTopicInfo().getTitle());
                    topicDescription.setText(topicDetailResponse.getTopicInfo().getDescription());

                    // Load topic image using Picasso
                    Picasso.get().load(topicDetailResponse.getTopicInfo().getAvatar()).into(topicImage);

                    // Set up song list RecyclerView
                    List<Song> songList = topicDetailResponse.getSongs();
                    songAdapter = new SongAdapter(songList);

                    // Set the click listener
                    songAdapter.setOnItemClickListener(song -> {
                        Intent intent = new Intent(TopicDetailActivity.this, SongPlayActivity.class);
                        intent.putExtra("song_title", song.getTitle());
                        intent.putExtra("song_avatar", song.getAvatar());
                        intent.putExtra("song_audio", song.getAudio());
                        intent.putExtra("song_lyrics", song.getLyrics());
                        intent.putExtra("song_singer", song.getSingerName());
                        intent.putExtra("song_id", song.get_id());
                        startActivity(intent);
                    });

                    songListRecycler.setAdapter(songAdapter);

                    // Hide progress bar, show header and song list
                    loadingSpinner.setVisibility(View.GONE);
                    songListRecycler.setVisibility(View.VISIBLE);
                    topicHeader.setVisibility(View.VISIBLE); // Show the header after loading
                } else {
                    Toast.makeText(TopicDetailActivity.this, "Failed to load topic details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TopicDetailResponse> call, Throwable t) {
                Toast.makeText(TopicDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                loadingSpinner.setVisibility(View.GONE);
                songListRecycler.setVisibility(View.VISIBLE);
                topicHeader.setVisibility(View.VISIBLE); // Ensure the header is visible even on failure
            }
        });
    }
}

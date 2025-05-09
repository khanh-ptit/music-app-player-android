package com.khanhleis11.appnghenhac_nhom3;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.khanhleis11.appnghenhac_nhom3.models.UserProfileResponse;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
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

        // Bottom Navigation: Home, Favorite Songs, User Profile
        TextView navHome = findViewById(R.id.nav_home);
        TextView navFavoriteSong = findViewById(R.id.nav_favorite_song);
        TextView navUser = findViewById(R.id.nav_user);

        navHome.setOnClickListener(v -> {
            startActivity(new Intent(TopicDetailActivity.this, MainActivity.class));
        });

        navFavoriteSong.setOnClickListener(v -> {
            if (isLoggedIn()) {
                fetchFavoriteSongs();
            } else {
                navigateToLogin();
                Toast.makeText(TopicDetailActivity.this, "Vui lòng đăng nhập để xem bài hát yêu thích!", Toast.LENGTH_SHORT).show();
            }
        });

        navUser.setOnClickListener(v -> {
            // check xem đã đăng nhập chưa bằng cách kiểm tra giá trị trả về của isLoggedIn
            Log.d("MainActivity", "isLoggedIn: " + isLoggedIn());

            if (!isLoggedIn()) {
                navigateToLogin();
                Toast.makeText(TopicDetailActivity.this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            } else {
                // Navigate to user profile screen
                startActivity(new Intent(TopicDetailActivity.this, UserProfileActivity.class));

            }
        });
    }

    private boolean isLoggedIn() {
        String token = getSharedPreferences("user_prefs", MODE_PRIVATE).getString("auth_token", null);
        Log.d("MainActivity", "Token retrieved: " + token);
        return token != null;
    }

    private void navigateToLogin() {
        Intent intent = new Intent(TopicDetailActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    private void fetchFavoriteSongs() {
        String token = getSharedPreferences("user_prefs", MODE_PRIVATE).getString("auth_token", null);

        if (token != null) {
            ApiClient apiClient = RetrofitInstance.getRetrofitInstance().create(ApiClient.class);
            Call<UserProfileResponse> call = apiClient.getFavoriteSongs(token);

            call.enqueue(new Callback<UserProfileResponse>() {
                @Override
                public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                    if (response.isSuccessful()) {
                        // Convert List<Song> to ArrayList<Song>
                        ArrayList<Song> favoriteSongs = new ArrayList<>(response.body().getFavoriteSongs());

                        // Pass the ArrayList to the Intent
                        Intent intent = new Intent(TopicDetailActivity.this, FavoriteSongsActivity.class);
                        intent.putExtra("favorite_songs", favoriteSongs); // Using putExtra to pass ArrayList
                        startActivity(intent);
                    } else {
                        Toast.makeText(TopicDetailActivity.this, "Failed to load favorite songs", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                    Toast.makeText(TopicDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
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

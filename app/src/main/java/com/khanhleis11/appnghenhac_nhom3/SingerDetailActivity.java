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
import com.khanhleis11.appnghenhac_nhom3.api.ApiClient;
import com.khanhleis11.appnghenhac_nhom3.api.RetrofitInstance;
import com.khanhleis11.appnghenhac_nhom3.models.SingerDetailResponse;
import com.khanhleis11.appnghenhac_nhom3.models.Song;
import com.khanhleis11.appnghenhac_nhom3.models.UserProfileResponse;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SingerDetailActivity extends AppCompatActivity {

    private RecyclerView songListRecycler;
    private SongAdapter songAdapter;
    private TextView singerName, singerRealName, singerBirthYear, singerHometown, songListTitle;
    private ImageView singerImage;
    private LinearLayout singerHeader, songListHeader;
    private ProgressBar loadingSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singer_detail);

        // Initialize views
        singerName = findViewById(R.id.singer_name);
        singerRealName = findViewById(R.id.singer_real_name);
        singerBirthYear = findViewById(R.id.singer_birth_year);
        singerHometown = findViewById(R.id.singer_hometown);
        singerImage = findViewById(R.id.singer_image);
        songListRecycler = findViewById(R.id.song_list_recycler);
        loadingSpinner = findViewById(R.id.loading_spinner);
        singerHeader = findViewById(R.id.singer_header);
        songListTitle = findViewById(R.id.song_list_title);
        songListHeader = findViewById(R.id.song_list_header);

        // Set up RecyclerView
        songListRecycler.setLayoutManager(new LinearLayoutManager(this));

        // Get Singer ID from Intent
        String singerId = getIntent().getStringExtra("singer_id");

        // Show progress bar while loading data
        loadingSpinner.setVisibility(View.VISIBLE);
        songListRecycler.setVisibility(View.GONE);
        singerHeader.setVisibility(View.GONE);
        songListHeader.setVisibility(View.GONE);

        // Load singer details and songs
        loadSingerDetails(singerId);

        // Bottom Navigation: Home, Favorite Songs, User Profile
        TextView navHome = findViewById(R.id.nav_home);
        TextView navFavoriteSong = findViewById(R.id.nav_favorite_song);
        TextView navUser = findViewById(R.id.nav_user);

        navHome.setOnClickListener(v -> {
            startActivity(new Intent(SingerDetailActivity.this, MainActivity.class));
        });

        navFavoriteSong.setOnClickListener(v -> {
            if (isLoggedIn()) {
                fetchFavoriteSongs();
            } else {
                navigateToLogin();
                Toast.makeText(SingerDetailActivity.this, "Vui lòng đăng nhập để xem bài hát yêu thích!", Toast.LENGTH_SHORT).show();
            }
        });

        navUser.setOnClickListener(v -> {
            // check xem đã đăng nhập chưa bằng cách kiểm tra giá trị trả về của isLoggedIn
            Log.d("MainActivity", "isLoggedIn: " + isLoggedIn());

            if (!isLoggedIn()) {
                navigateToLogin();
                Toast.makeText(SingerDetailActivity.this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            } else {
                // Navigate to user profile screen
                startActivity(new Intent(SingerDetailActivity.this, UserProfileActivity.class));

            }
        });
    }

    private boolean isLoggedIn() {
        String token = getSharedPreferences("user_prefs", MODE_PRIVATE).getString("auth_token", null);
        Log.d("MainActivity", "Token retrieved: " + token);
        return token != null;
    }

    private void navigateToLogin() {
        Intent intent = new Intent(SingerDetailActivity.this, LoginActivity.class);
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
                        Intent intent = new Intent(SingerDetailActivity.this, FavoriteSongsActivity.class);
                        intent.putExtra("favorite_songs", favoriteSongs); // Using putExtra to pass ArrayList
                        startActivity(intent);
                    } else {
                        Toast.makeText(SingerDetailActivity.this, "Failed to load favorite songs", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                    Toast.makeText(SingerDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void loadSingerDetails(String singerId) {
        ApiClient apiClient = RetrofitInstance.getRetrofitInstance().create(ApiClient.class);

        // Call API to get singer details by ID
        Call<SingerDetailResponse> call = apiClient.getSingerDetails(singerId);
        call.enqueue(new Callback<SingerDetailResponse>() {
            @Override
            public void onResponse(Call<SingerDetailResponse> call, Response<SingerDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    SingerDetailResponse singerDetailResponse = response.body();

                    // Set singer details
                    singerName.setText(singerDetailResponse.getSingerInfo().getFullName());
                    singerRealName.setText("Tên thật: " + singerDetailResponse.getSingerInfo().getRealName());
                    singerBirthYear.setText("Năm sinh: " + singerDetailResponse.getSingerInfo().getBirthYear());
                    singerHometown.setText("Quê quán: " + singerDetailResponse.getSingerInfo().getHometown());

                    String singerNameText = singerDetailResponse.getSingerInfo().getFullName();
                    songListTitle.setText("Danh sách bài hát - " + singerNameText);


                    // Load singer image using Picasso
                    Picasso.get().load(singerDetailResponse.getSingerInfo().getAvatar()).into(singerImage);

                    // Set up song list RecyclerView
                    List<Song> songList = singerDetailResponse.getSongs();
                    songAdapter = new SongAdapter(songList);
                    songListRecycler.setAdapter(songAdapter);

                    songAdapter.setOnItemClickListener(song -> {
                        // Send data to SongPlayActivity
                        Intent intent = new Intent(SingerDetailActivity.this, SongPlayActivity.class);
                        intent.putExtra("song_title", song.getTitle());
                        intent.putExtra("song_avatar", song.getAvatar());
                        intent.putExtra("song_audio", song.getAudio());
                        intent.putExtra("song_lyrics", song.getLyrics());
                        intent.putExtra("song_singer", song.getSingerName());
                        intent.putExtra("song_id", song.get_id());
                        startActivity(intent);
                    });

                    // Hide progress bar and show song list
                    loadingSpinner.setVisibility(View.GONE);
                    songListRecycler.setVisibility(View.VISIBLE);
                    singerHeader.setVisibility(View.VISIBLE);
                    songListHeader.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(SingerDetailActivity.this, "Failed to load singer details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SingerDetailResponse> call, Throwable t) {
                Toast.makeText(SingerDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                loadingSpinner.setVisibility(View.GONE);
                songListRecycler.setVisibility(View.VISIBLE);
                singerHeader.setVisibility(View.VISIBLE);
            }
        });
    }
}

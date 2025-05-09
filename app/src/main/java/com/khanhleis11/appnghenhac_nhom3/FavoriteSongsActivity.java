package com.khanhleis11.appnghenhac_nhom3;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.khanhleis11.appnghenhac_nhom3.adapters.SongAdapter;
import com.khanhleis11.appnghenhac_nhom3.api.ApiClient;
import com.khanhleis11.appnghenhac_nhom3.api.RetrofitInstance;
import com.khanhleis11.appnghenhac_nhom3.models.Song;
import com.khanhleis11.appnghenhac_nhom3.models.UserProfileResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoriteSongsActivity extends AppCompatActivity {

    private RecyclerView favoriteSongsRecyclerView;
    private SongAdapter songAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_songs);

        favoriteSongsRecyclerView = findViewById(R.id.favorite_songs_recycler_view);

        // Set RecyclerView Layout Manager
        favoriteSongsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get the favorite songs passed from MainActivity
        List<Song> favoriteSongs = (List<Song>) getIntent().getSerializableExtra("favorite_songs");

        if (favoriteSongs != null) {
            songAdapter = new SongAdapter(favoriteSongs);
            favoriteSongsRecyclerView.setAdapter(songAdapter);
        } else {
            Toast.makeText(this, "No favorite songs found", Toast.LENGTH_SHORT).show();
        }

        // Bottom Navigation: Home, Favorite Songs, User Profile
        TextView navHome = findViewById(R.id.nav_home);
        TextView navFavoriteSong = findViewById(R.id.nav_favorite_song);
        TextView navUser = findViewById(R.id.nav_user);

        navHome.setOnClickListener(v -> {
            if (!isLoggedIn()) {
                navigateToLogin();
            } else {
                // Navigate to home screen
                startActivity(new Intent(FavoriteSongsActivity.this, MainActivity.class));
            }
        });

        navUser.setOnClickListener(v -> {
            // check xem đã đăng nhập chưa bằng cách kiểm tra giá trị trả về của isLoggedIn
            Log.d("MainActivity", "isLoggedIn: " + isLoggedIn());

            if (!isLoggedIn()) {
                navigateToLogin();
                Toast.makeText(FavoriteSongsActivity.this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            } else {
                // Navigate to user profile screen
                startActivity(new Intent(FavoriteSongsActivity.this, UserProfileActivity.class));

            }
        });
    }

    private boolean isLoggedIn() {
        String token = getSharedPreferences("user_prefs", MODE_PRIVATE).getString("auth_token", null);
        Log.d("MainActivity", "Token retrieved: " + token);
        return token != null;
    }

    private void navigateToLogin() {
        Intent intent = new Intent(FavoriteSongsActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}

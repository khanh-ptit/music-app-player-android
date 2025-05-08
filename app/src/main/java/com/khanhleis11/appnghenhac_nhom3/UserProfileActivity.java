package com.khanhleis11.appnghenhac_nhom3;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.khanhleis11.appnghenhac_nhom3.api.ApiClient;
import com.khanhleis11.appnghenhac_nhom3.api.RetrofitInstance;
import com.khanhleis11.appnghenhac_nhom3.models.Song;
import com.khanhleis11.appnghenhac_nhom3.models.UserProfileResponse;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserProfileActivity extends AppCompatActivity {

    private TextView userName, userEmail, userPhone;
    private ImageView userAvatar;
    private Button logoutButton;
    private ProgressBar loadingSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Initialize views
        userName = findViewById(R.id.user_name);
        userEmail = findViewById(R.id.user_email);
        userPhone = findViewById(R.id.user_phone);
        logoutButton = findViewById(R.id.logout_button);
        userAvatar = findViewById(R.id.user_avatar);
        loadingSpinner = findViewById(R.id.loading_spinner);

        // Initially hide the user data views
        userName.setVisibility(View.GONE);
        userEmail.setVisibility(View.GONE);
        userPhone.setVisibility(View.GONE);
        logoutButton.setVisibility(View.GONE);
        userAvatar.setVisibility(View.GONE);

        // Show the loading spinner
        loadingSpinner.setVisibility(View.VISIBLE);

        // Get the token from SharedPreferences
        String token = getSharedPreferences("user_prefs", MODE_PRIVATE).getString("auth_token", null);

        if (token != null) {
            fetchUserProfile(token);
        } else {
            Toast.makeText(this, "Token not found, please log in again", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        // Set the logout button click listener
        logoutButton.setOnClickListener(v -> {
            // Clear token and redirect to login screen
            getSharedPreferences("user_prefs", MODE_PRIVATE).edit().remove("auth_token").apply();
            startActivity(new Intent(UserProfileActivity.this, LoginActivity.class));
            finish();
        });

        // Bottom Navigation: Home, Favorite Songs, User Profile
        TextView navHome = findViewById(R.id.nav_home);
        TextView navFavoriteSong = findViewById(R.id.nav_favorite_song);
        TextView navUser = findViewById(R.id.nav_user);

        navHome.setOnClickListener(v -> {
            if (!isLoggedIn()) {
                navigateToLogin();
            } else {
                // Navigate to home screen
                startActivity(new Intent(UserProfileActivity.this, MainActivity.class));
            }
        });

        navFavoriteSong.setOnClickListener(v -> {
            if (isLoggedIn()) {
                fetchFavoriteSongs();
            } else {
                navigateToLogin();
                Toast.makeText(UserProfileActivity.this, "Vui lòng đăng nhập để xem bài hát yêu thích!", Toast.LENGTH_SHORT).show();
            }
        });


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
                        Intent intent = new Intent(UserProfileActivity.this, FavoriteSongsActivity.class);
                        intent.putExtra("favorite_songs", favoriteSongs); // Using putExtra to pass ArrayList
                        startActivity(intent);
                    } else {
                        Toast.makeText(UserProfileActivity.this, "Failed to load favorite songs", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                    Toast.makeText(UserProfileActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private boolean isLoggedIn() {
        String token = getSharedPreferences("user_prefs", MODE_PRIVATE).getString("auth_token", null);
        Log.d("MainActivity", "Token retrieved: " + token);
        return token != null;
    }

    private void navigateToLogin() {
        Intent intent = new Intent(UserProfileActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    private void fetchUserProfile(String token) {
        ApiClient apiClient = RetrofitInstance.getRetrofitInstance().create(ApiClient.class);
        Call<UserProfileResponse> call = apiClient.getUserProfile(token);

        call.enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                Log.d("UserProfileActivity", "Response Code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    UserProfileResponse userProfile = response.body();

                    // Display user data
                    userName.setText("Họ và tên: " + userProfile.getName());
                    userEmail.setText("Email: " + userProfile.getEmail());
                    userPhone.setText("Số điện thoại: " + userProfile.getPhone());

                    // Get the avatar URL and replace http with https
                    String avatarUrl = userProfile.getAvatar();
                    if (avatarUrl != null) {
                        // Replace http:// with https://
                        avatarUrl = avatarUrl.replace("http://", "https://");

                        // Load the avatar using Picasso
                        Picasso.get()
                                .load(avatarUrl) // Avatar URL with https
                                .placeholder(R.drawable.ic_user) // Placeholder image
                                .resize(200, 200) // Resize the image if needed
                                .into(userAvatar);
                    } else {
                        // If avatar URL is null, load placeholder image
                        userAvatar.setImageResource(R.drawable.ic_user);
                    }

                    // Hide the loading spinner and show the user data views
                    loadingSpinner.setVisibility(View.GONE);
                    userAvatar.setVisibility(View.VISIBLE);
                    userName.setVisibility(View.VISIBLE);
                    userEmail.setVisibility(View.VISIBLE);
                    userPhone.setVisibility(View.VISIBLE);
                    logoutButton.setVisibility(View.VISIBLE);

                } else {
                    Toast.makeText(UserProfileActivity.this, "Failed to load user profile", Toast.LENGTH_SHORT).show();
                    loadingSpinner.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                Toast.makeText(UserProfileActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                loadingSpinner.setVisibility(View.GONE);
            }
        });
    }
}

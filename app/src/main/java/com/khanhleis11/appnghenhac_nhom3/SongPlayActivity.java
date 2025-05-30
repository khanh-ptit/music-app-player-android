package com.khanhleis11.appnghenhac_nhom3;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.media.audiofx.Visualizer;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.khanhleis11.appnghenhac_nhom3.models.Song;
import com.khanhleis11.appnghenhac_nhom3.models.SongResponse;
import com.squareup.picasso.Picasso;
import com.chibde.visualizer.BarVisualizer;
import com.khanhleis11.appnghenhac_nhom3.api.ApiClient;
import com.khanhleis11.appnghenhac_nhom3.api.RetrofitInstance;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SongPlayActivity extends AppCompatActivity {

    private TextView songTitle, songCurrentTime, songDuration, songSingerName, songHearCount, songLikeCount, btnLike, btnFavorite;
    private ImageView songArt;
    private SeekBar songSeekBar;
    private Button btnPlayPause, btnNext, btnPrev, btnRandom, btnRepeat;
    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private Runnable updateSeekBarRunnable;
    private BarVisualizer visualizer;  // Declare the BarVisualizer
    private ActivityResultLauncher<String> requestPermissionLauncher;

    private List<Song> songList;  // List of songs
    private int currentSongIndex = 0;  // Current song index

    // Declare the ObjectAnimator for rotation
    private ObjectAnimator rotateAnimator;
    private String songId;
    private boolean isLiked = false;  // Biến kiểm tra xem bài hát đã được like chưa
    private boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_play);

        // Initialize views
        songTitle = findViewById(R.id.song_title);
        songArt = findViewById(R.id.song_art);
        songCurrentTime = findViewById(R.id.song_current_time);
        songDuration = findViewById(R.id.song_duration);
        songSeekBar = findViewById(R.id.song_seekbar);
        btnPlayPause = findViewById(R.id.btn_play_pause);
        btnNext = findViewById(R.id.btn_next);
        btnPrev = findViewById(R.id.btn_prev);
        btnRandom = findViewById(R.id.btn_random);
        btnRepeat = findViewById(R.id.btn_repeat);
        songSingerName = findViewById(R.id.song_singerName);
        visualizer = findViewById(R.id.visualizer);
        songHearCount = findViewById(R.id.song_hear_count);
        songLikeCount = findViewById(R.id.song_like_count);
        btnLike = findViewById(R.id.btn_like);
        btnFavorite = findViewById(R.id.btn_favorite);

        // Get song data from intent
        String songTitleText = getIntent().getStringExtra("song_title");
        Log.d("SongPlayActivity", "Song Title: " + songTitleText);
        String songArtUrl = getIntent().getStringExtra("song_avatar");
        String songAudioUrl = getIntent().getStringExtra("song_audio");
        String songSinger = getIntent().getStringExtra("song_singer");
        String songListen = getIntent().getStringExtra("song_listen");
        String songLike = getIntent().getStringExtra("song_like");
        songId = getIntent().getStringExtra("song_id");

        if (songId != null) {
            updateListenCount(songId);
        }

        // Set song title and art
        songTitle.setText(songTitleText);
        songSingerName.setText("Ca sĩ: " + songSinger);
        songHearCount.setText(songListen + " Lượt nghe");
        songLikeCount.setText(songLike + " Thích");

        checkIfLiked(songId);
        checkIfFavorite(songId);

        // Ensure the URL starts with "https"
        if (songArtUrl != null && songArtUrl.startsWith("http://")) {
            songArtUrl = songArtUrl.replace("http://", "https://");
        }

        Picasso.get().load(songArtUrl).into(songArt);

        // Initialize MediaPlayer and start playing asynchronously
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(mp -> {
            // Start playing when the MediaPlayer is ready
            mediaPlayer.start();
            songDuration.setText(formatTime(mediaPlayer.getDuration()));  // Set total song duration
            songSeekBar.setMax(mediaPlayer.getDuration());  // Set SeekBar max value
            handler.post(updateSeekBarRunnable);  // Start updating the SeekBar and current time
            // Set up BarVisualizer after MediaPlayer is prepared
            requestAudioPermission();
            startRotateAnimation();  // Start rotation animation
        });

        try {
            mediaPlayer.setDataSource(songAudioUrl);
            mediaPlayer.prepareAsync();  // Asynchronous preparation to avoid blocking UI thread
        } catch (IOException e) {
            Toast.makeText(this, "Error loading song", Toast.LENGTH_SHORT).show();
        }

        // Setup SeekBar and update time continuously
        updateSeekBarRunnable = new Runnable() {
            @Override
            public void run() {
                int currentPosition = mediaPlayer.getCurrentPosition();
                songSeekBar.setProgress(currentPosition);
                songCurrentTime.setText(formatTime(currentPosition));  // Update current time
                handler.postDelayed(this, 1000);  // Update every second
            }
        };

        btnLike.setOnClickListener(v -> {
            String token = getSharedPreferences("user_prefs", MODE_PRIVATE).getString("auth_token", null);
            if (token != null) {
                likeOrUnlikeSong();
            } else {
                Toast.makeText(SongPlayActivity.this, "Vui lòng đăng nhập để thích bài hát!", Toast.LENGTH_SHORT).show();
            }
        });

        btnFavorite.setOnClickListener(v -> {
            String token = getSharedPreferences("user_prefs", MODE_PRIVATE).getString("auth_token", null);
            if (token != null) {
                favoriteSong(songId);
            } else {
                Toast.makeText(SongPlayActivity.this, "Vui lòng đăng nhập để trải nghiệm tính năng này!", Toast.LENGTH_SHORT).show();
            }
        });

        // Play/Pause button functionality
        btnPlayPause.setOnClickListener(v -> {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                btnPlayPause.setText("\uf04b"); // Play icon
                stopRotateAnimation();  // Stop rotation when paused
            } else {
                mediaPlayer.start();
                btnPlayPause.setText("\uf04c"); // Pause icon
                handler.post(updateSeekBarRunnable); // Start updating seek bar
                startRotateAnimation();  // Start rotation animation when playing
            }
        });

        // SeekBar listener to update song position
        songSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        // Next button functionality
        btnNext.setOnClickListener(v -> {
            // Lấy ID của bài hát hiện tại (currentSongId)
            String currentSongId = songId;  // Đây là biến songId bạn đã lấy từ dữ liệu bài hát hiện tại

            // Kiểm tra ID của bài hát hiện tại
            Log.d("SongPlayActivity", "Current Song ID: " + currentSongId);  // In giá trị của currentSongId ra log

            if (currentSongId == null || currentSongId.isEmpty()) {
                Toast.makeText(SongPlayActivity.this, "ID bài hát không hợp lệ", Toast.LENGTH_SHORT).show();
                return;  // Dừng lại nếu ID không hợp lệ
            }

            // Gửi yêu cầu API để lấy bài hát tiếp theo
            ApiClient apiClient = RetrofitInstance.getRetrofitInstance().create(ApiClient.class);
            Call<SongResponse> nextSongCall = apiClient.getNextSong(currentSongId);

            nextSongCall.enqueue(new Callback<SongResponse>() {
                @Override
                public void onResponse(Call<SongResponse> call, Response<SongResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        // Lấy bài hát tiếp theo từ phản hồi (data trả về trong phần "song")
                        Song nextSong = response.body().getSong();  // Lấy bài hát tiếp theo
                        if (nextSong != null) {
                            // Nếu có bài hát tiếp theo, cập nhật giao diện và phát bài hát
                            playNextSong(nextSong);
                        } else {
                            Toast.makeText(SongPlayActivity.this, "Không thể tải bài hát tiếp theo", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(SongPlayActivity.this, "Không thể tải bài hát tiếp theo", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<SongResponse> call, Throwable t) {
                    Toast.makeText(SongPlayActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Next button functionality
        btnPrev.setOnClickListener(v -> {
            // Lấy ID của bài hát hiện tại (currentSongId)
            String currentSongId = songId;  // Đây là biến songId bạn đã lấy từ dữ liệu bài hát hiện tại

            // Kiểm tra ID của bài hát hiện tại
            Log.d("SongPlayActivity", "Current Song ID: " + currentSongId);  // In giá trị của currentSongId ra log

            if (currentSongId == null || currentSongId.isEmpty()) {
                Toast.makeText(SongPlayActivity.this, "ID bài hát không hợp lệ", Toast.LENGTH_SHORT).show();
                return;  // Dừng lại nếu ID không hợp lệ
            }

            // Gửi yêu cầu API để lấy bài hát tiếp theo
            ApiClient apiClient = RetrofitInstance.getRetrofitInstance().create(ApiClient.class);
            Call<SongResponse> nextSongCall = apiClient.getPrevSong(currentSongId);

            nextSongCall.enqueue(new Callback<SongResponse>() {
                @Override
                public void onResponse(Call<SongResponse> call, Response<SongResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        // Lấy bài hát tiếp theo từ phản hồi (data trả về trong phần "song")
                        Song nextSong = response.body().getSong();  // Lấy bài hát tiếp theo
                        if (nextSong != null) {
                            // Nếu có bài hát tiếp theo, cập nhật giao diện và phát bài hát
                            playNextSong(nextSong);
                        } else {
                            Toast.makeText(SongPlayActivity.this, "Không thể tải bài hát tiếp theo", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(SongPlayActivity.this, "Không thể tải bài hát tiếp theo", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<SongResponse> call, Throwable t) {
                    Toast.makeText(SongPlayActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });


        // Set up permission request launcher
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        // Permission is granted. Continue the action or workflow in your app.
                        setVisualizer();
                    } else {
                        // Explain to the user that the feature is unavailable because the
                        // features requires a permission that the user has denied.
                        Toast.makeText(this, "Audio permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    // Phương thức playNextSong để phát bài hát tiếp theo
    private void playNextSong(Song nextSong) {
        // Cập nhật giao diện với bài hát tiếp theo
        songTitle.setText(nextSong.getTitle());
        songSingerName.setText("Ca sĩ: " + nextSong.getSingerName());
        String avatarUrl = nextSong.getAvatar();
        if (avatarUrl != null && avatarUrl.startsWith("http://")) {
            avatarUrl = avatarUrl.replace("http://", "https://");
        }
        Picasso.get().load(avatarUrl).into(songArt);

        songId = nextSong.get_id();

        // Reset MediaPlayer để phát bài hát tiếp theo
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(nextSong.getAudio());  // Thiết lập nguồn audio của bài hát tiếp theo
            mediaPlayer.prepareAsync();  // Chuẩn bị bài hát và bắt đầu phát
        } catch (IOException e) {
            Toast.makeText(this, "Error loading song", Toast.LENGTH_SHORT).show();
        }

        // Dừng và bắt đầu lại animation xoay
        startRotateAnimation();
    }


    // Start rotation animation on song art
    private void startRotateAnimation() {
        // Kiểm tra nếu animator chưa được khởi động lại
        if (rotateAnimator == null || !rotateAnimator.isRunning()) {
            rotateAnimator = ObjectAnimator.ofFloat(songArt, "rotation", 0f, 360f);
            rotateAnimator.setDuration(8000);  // Duration của một vòng quay
            rotateAnimator.setRepeatCount(ObjectAnimator.INFINITE);  // Quay liên tục
            rotateAnimator.setRepeatMode(ObjectAnimator.RESTART);  // Quay lại sau mỗi vòng
            rotateAnimator.start();  // Bắt đầu animation
        }
    }


    // Stop rotation animation when song is paused
    private void stopRotateAnimation() {
        if (rotateAnimator != null && rotateAnimator.isRunning()) {
            rotateAnimator.pause();
        }
    }


    // Format milliseconds to "mm:ss" format
    private String formatTime(int milliseconds) {
        int minutes = (milliseconds / 1000) / 60;
        int seconds = (milliseconds / 1000) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    // Set up BarVisualizer
    private void setVisualizer() {
        if (visualizer != null && mediaPlayer != null) {
            try {
                visualizer.release(); // Release old visualizer session if exists
            } catch (Exception e) {
                e.printStackTrace();
            }

            int audioSessionId = mediaPlayer.getAudioSessionId();
            if (audioSessionId != -1) {
                try {
                    visualizer.setPlayer(audioSessionId);
                    int color = getResources().getColor(R.color.colorPrimary);  // Set color for visualizer
                    visualizer.setColor(color);  // Set the color of the visualizer bars
                } catch (RuntimeException e) {
                    Log.e("SongPlayActivity", "Error creating Visualizer: " + e.getMessage());
                }
            }
        }
    }

    private void requestAudioPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            setVisualizer();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO);
        }
    }

    private String getCurrentSongId() {
        if (songList != null && !songList.isEmpty()) {
            Song currentSong = songList.get(currentSongIndex);
            return currentSong.get_id();  // Trả về ID của bài hát hiện tại
        }
        return null;  // Trả về null nếu không có bài hát
    }

    private void updateListenCount(String songId) {
        ApiClient apiClient = RetrofitInstance.getRetrofitInstance().create(ApiClient.class);
        Call<SongResponse> call = apiClient.updateListen(songId);

        call.enqueue(new Callback<SongResponse>() {
            @Override
            public void onResponse(Call<SongResponse> call, Response<SongResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Lấy song object từ response
                    Song updatedSong = response.body().getSong();

                    // Cập nhật lại số lượt nghe trên UI
                    if (updatedSong != null) {
                        songHearCount.setText(String.valueOf(updatedSong.getListen()));
                    }
                } else {
                    Toast.makeText(SongPlayActivity.this, "Lỗi cập nhật lượt nghe", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SongResponse> call, Throwable t) {
                Log.e("SongPlayActivity", "Error updating listen count: " + t.getMessage());
                Toast.makeText(SongPlayActivity.this, "Failed to update listen count", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            stopRotateAnimation();  // Stop rotation when activity is paused
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        handler.removeCallbacks(updateSeekBarRunnable); // Clean up the handler
        if (visualizer != null) {
            visualizer.release();
        }
        if (rotateAnimator != null) {
            rotateAnimator.cancel();  // Ensure rotation stops when activity is destroyed
        }
    }


    // Like/unlike song based on current state
    private void likeOrUnlikeSong() {
        String token = getSharedPreferences("user_prefs", MODE_PRIVATE).getString("auth_token", null);
        if (token != null) {
            ApiClient apiClient = RetrofitInstance.getRetrofitInstance().create(ApiClient.class);
            Call<SongResponse> call;

            // Nếu chưa like thì gửi API để like bài hát
            call = apiClient.likeSong(songId, token);  // Send token directly

            call.enqueue(new Callback<SongResponse>() {
                @Override
                public void onResponse(Call<SongResponse> call, Response<SongResponse> response) {
                    Log.d("SongPlayActivity", "Response code: " + response.code());
                    if (response.isSuccessful()) {
                        String message = response.body().getMessage();
                        if ("Đã thích bài hát".equals(message)) {
                            // Nếu message trả về là "Đã thích bài hát", bài hát đã được thích
                            isLiked = true;
                            Toast.makeText(SongPlayActivity.this, "Bài hát đã được like!", Toast.LENGTH_SHORT).show();
                        } else if ("Đã bỏ thích bài hát!".equals(message)) {
                            // Nếu message trả về là "Đã bỏ thích bài hát", bài hát đã bị bỏ thích
                            isLiked = false;
                            Toast.makeText(SongPlayActivity.this, "Bài hát đã bị bỏ thích!", Toast.LENGTH_SHORT).show();
                        }

                        updateLikeButtonUI();  // Update the UI of the like button
                        songLikeCount.setText(String.valueOf(response.body().getLikesCount()) + " Thích");
                    } else {
                        Toast.makeText(SongPlayActivity.this, "Error liking the song", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<SongResponse> call, Throwable t) {
                    Toast.makeText(SongPlayActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    // Phương thức thêm bài hát vào yêu thích
    private void favoriteSong(String songId) {
        String token = getSharedPreferences("user_prefs", MODE_PRIVATE).getString("auth_token", null);
        if (token != null) {
            ApiClient apiClient = RetrofitInstance.getRetrofitInstance().create(ApiClient.class);
            Call<SongResponse> call = apiClient.favoriteSong(songId, token);

            call.enqueue(new Callback<SongResponse>() {
                @Override
                public void onResponse(Call<SongResponse> call, Response<SongResponse> response) {
                    if (response.isSuccessful()) {
                        SongResponse songResponse = response.body();
                        if (songResponse != null) {
                            // Handle like/unlike status change
                            String message = songResponse.getMessage();
                            Toast.makeText(SongPlayActivity.this, message, Toast.LENGTH_SHORT).show();

                            // Update the like button color based on message
                            if (message.equals("Đã yêu thích bài hát!")) {
                                isFavorite = true;
                                btnFavorite.setTextColor(ContextCompat.getColor(SongPlayActivity.this, R.color.red));
                            } else {
                                isFavorite = false;
                                btnFavorite.setTextColor(ContextCompat.getColor(SongPlayActivity.this, R.color.gray));
                            }

                            updateFavoriteButtonUI();  // Update the UI of the like button

                        }
                    } else {
                        Log.e("FavoriteSongError", "Error Code: " + response.code() + " Message favorite: " + response.message());
                        Toast.makeText(SongPlayActivity.this, "Error toggling favorite song", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<SongResponse> call, Throwable t) {
                    Toast.makeText(SongPlayActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    // Method to check if the song is liked
    private void checkIfLiked(String songId) {
        String token = getSharedPreferences("user_prefs", MODE_PRIVATE).getString("auth_token", null);
        if (token != null) {
            ApiClient apiClient = RetrofitInstance.getRetrofitInstance().create(ApiClient.class);
            Call<SongResponse> call = apiClient.checkIfLiked(songId, token);

            call.enqueue(new Callback<SongResponse>() {
                @Override
                public void onResponse(Call<SongResponse> call, Response<SongResponse> response) {
                    if (response.isSuccessful()) {
                        Song song = response.body().getSong();
                        boolean isLiked = response.body().isLiked();  // Use boolean value for isLiked

                        if (isLiked) {
                            isLiked = true;
                            btnLike.setTextColor(ContextCompat.getColor(SongPlayActivity.this, R.color.blue));  // Set to blue if liked
                        } else {
                            isLiked = false;
                            btnLike.setTextColor(ContextCompat.getColor(SongPlayActivity.this, R.color.gray));  // Set to gray if not liked
                        }
                    } else {
                        Toast.makeText(SongPlayActivity.this, "Error checking song like status", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<SongResponse> call, Throwable t) {
                    Toast.makeText(SongPlayActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void checkIfFavorite(String songId) {
        String token = getSharedPreferences("user_prefs", MODE_PRIVATE).getString("auth_token", null);
        if (token != null) {
            ApiClient apiClient = RetrofitInstance.getRetrofitInstance().create(ApiClient.class);
            Call<SongResponse> call = apiClient.checkIfFavorite(songId, token);

            call.enqueue(new Callback<SongResponse>() {
                @Override
                public void onResponse(Call<SongResponse> call, Response<SongResponse> response) {
                    Log.d("SongPlayActivity", "Response code is favorite: " + response.code());
                    if (response.isSuccessful()) {
                        Song song = response.body().getSong();
                        boolean isFavorite = response.body().isFavorite();  // Use boolean value for isLiked
                        Log.d("SongPlayActivity", "isFavorite: " + isFavorite);
                        if (isFavorite) {
                            isFavorite = true;
                            btnFavorite.setTextColor(ContextCompat.getColor(SongPlayActivity.this, R.color.red));  // Set to blue if liked
                        } else {
                            isFavorite = false;
                            btnFavorite.setTextColor(ContextCompat.getColor(SongPlayActivity.this, R.color.gray));  // Set to gray if not liked
                        }
                    } else {
                        Toast.makeText(SongPlayActivity.this, "Error checking song like status", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<SongResponse> call, Throwable t) {
                    Toast.makeText(SongPlayActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // Update the UI of the like button
    private void updateLikeButtonUI() {
        if (isLiked) {
            btnLike.setTextColor(ContextCompat.getColor(SongPlayActivity.this, R.color.blue));  // Set color to blue when liked
        } else {
            Log.d("SongPlayActivity", "isLiked is false");
            btnLike.setTextColor(ContextCompat.getColor(SongPlayActivity.this, R.color.gray));  // Set color to gray when not liked
        }
    }

    private void updateFavoriteButtonUI() {
        if (isFavorite) {
            btnFavorite.setTextColor(ContextCompat.getColor(SongPlayActivity.this, R.color.red));  // Set color to blue when liked
        } else {
            Log.d("SongPlayActivity", "isLiked is false");
            btnFavorite.setTextColor(ContextCompat.getColor(SongPlayActivity.this, R.color.gray));  // Set color to gray when not liked
        }
    }

}

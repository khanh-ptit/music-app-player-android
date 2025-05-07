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

    private TextView songTitle, songCurrentTime, songDuration, songSingerName, songHearCount, songLikeCount;
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
        songSingerName = findViewById(R.id.song_singerName); // Add this line
        visualizer = findViewById(R.id.visualizer);  // Initialize BarVisualizer
        songHearCount = findViewById(R.id.song_hear_count);
        songLikeCount = findViewById(R.id.song_like_count);

        // Get song data from intent
        String songTitleText = getIntent().getStringExtra("song_title");
        Log.d("SongPlayActivity", "Song Title: " + songTitleText);
        String songArtUrl = getIntent().getStringExtra("song_avatar");
        String songAudioUrl = getIntent().getStringExtra("song_audio");
        String songSinger = getIntent().getStringExtra("song_singer");
        String songListen = getIntent().getStringExtra("song_listen");
        String songLike = getIntent().getStringExtra("song_like");
        songId = getIntent().getStringExtra("song_id");

        // Set song title and art
        songTitle.setText(songTitleText);
        songSingerName.setText("Ca sĩ: " + songSinger);
        songHearCount.setText(songListen + " Lượt nghe");
        songLikeCount.setText(songLike + " Thích");

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
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {
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
}

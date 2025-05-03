package com.khanhleis11.appnghenhac_nhom3;

import android.Manifest;
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

import com.squareup.picasso.Picasso;
import com.chibde.visualizer.BarVisualizer;

import java.io.IOException;

public class SongPlayActivity extends AppCompatActivity {

    private TextView songTitle, songCurrentTime, songDuration, songSingerName;
    private ImageView songArt;
    private SeekBar songSeekBar;
    private Button btnPlayPause, btnNext, btnPrev, btnRandom, btnRepeat;
    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private Runnable updateSeekBarRunnable;
    private BarVisualizer visualizer;  // Declare the BarVisualizer
    private ActivityResultLauncher<String> requestPermissionLauncher;

    // Mini Player Views
    private TextView miniPlayerSongName;
    private ImageView miniPlayerSongArt;
    private Button miniPlayerPlayPauseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_play);

        // Initialize views
        songTitle = findViewById(R.id.song_title);
        songArt = findViewById(R.id.song_art_main);
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

        // Initialize mini player views
        miniPlayerSongName = findViewById(R.id.song_name);  // song_name from mini player
        miniPlayerSongArt = findViewById(R.id.song_art_mini);  // song_art_mini from mini player
        miniPlayerPlayPauseButton = findViewById(R.id.play_pause_button);  // play_pause_button from mini player

        // Get song data from intent
        String songTitleText = getIntent().getStringExtra("song_title");
        String songArtUrl = getIntent().getStringExtra("song_avatar");
        String songAudioUrl = getIntent().getStringExtra("song_audio");
        String songSinger = getIntent().getStringExtra("song_singer");

        // Check if the views are initialized properly
        if (miniPlayerSongArt == null) {
            Log.e("SongPlayActivity", "miniPlayerSongArt is null");
        }
        if (miniPlayerPlayPauseButton == null) {
            Log.e("SongPlayActivity", "miniPlayerPlayPauseButton is null");
        }
        if (miniPlayerSongName == null) {
            Log.e("SongPlayActivity", "miniPlayerSongName is null");
        }

        // Set song title and art
        songTitle.setText(songTitleText);
        songSingerName.setText("Ca sĩ: " + songSinger);
        if (miniPlayerSongName != null) {
            miniPlayerSongName.setText(songTitleText);  // Set song title for mini player
        }

        // Ensure the URL starts with "https"
        if (songArtUrl != null && songArtUrl.startsWith("http://")) {
            songArtUrl = songArtUrl.replace("http://", "https://");
        }

        // Picasso - Load image into ImageViews
        if (songArtUrl != null) {
            Picasso.get().load(songArtUrl).into(songArt);  // Set song art
            Picasso.get().load(songArtUrl).into(miniPlayerSongArt);  // Set song art for mini player
        }

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
            showMiniPlayer();
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
            } else {
                mediaPlayer.start();
                btnPlayPause.setText("\uf04c"); // Pause icon
                handler.post(updateSeekBarRunnable); // Start updating seek bar
            }
        });

        // Mini player Play/Pause functionality
        miniPlayerPlayPauseButton.setOnClickListener(v -> {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                miniPlayerPlayPauseButton.setText("\uf04b"); // Play icon
            } else {
                mediaPlayer.start();
                miniPlayerPlayPauseButton.setText("\uf04c"); // Pause icon
                handler.post(updateSeekBarRunnable); // Start updating seek bar
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

        // Handle next, previous, random, and repeat button actions (Not implemented here)
        btnNext.setOnClickListener(v -> Toast.makeText(SongPlayActivity.this, "Next button clicked", Toast.LENGTH_SHORT).show());
        btnPrev.setOnClickListener(v -> Toast.makeText(SongPlayActivity.this, "Previous button clicked", Toast.LENGTH_SHORT).show());
        btnRandom.setOnClickListener(v -> Toast.makeText(SongPlayActivity.this, "Random button clicked", Toast.LENGTH_SHORT).show());
        btnRepeat.setOnClickListener(v -> Toast.makeText(SongPlayActivity.this, "Repeat button clicked", Toast.LENGTH_SHORT).show());

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

    private void showMiniPlayer() {
        // Ensure that mini player is shown when song starts playing
        findViewById(R.id.mini_player).setVisibility(View.VISIBLE);
    }

    private void hideMiniPlayer() {
        // Hide mini player when there's no song playing
        findViewById(R.id.mini_player).setVisibility(View.GONE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
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
    }
}

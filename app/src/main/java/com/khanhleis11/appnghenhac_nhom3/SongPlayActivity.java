package com.khanhleis11.appnghenhac_nhom3;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import java.io.IOException;

public class SongPlayActivity extends AppCompatActivity {

    private TextView songTitle, songCurrentTime, songDuration, songSingerName;
    private ImageView songArt;
    private SeekBar songSeekBar;
    private Button btnPlayPause, btnNext, btnPrev, btnRandom, btnRepeat;
    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private Runnable updateSeekBarRunnable;

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

        // Get song data from intent
        String songTitleText = getIntent().getStringExtra("song_title");
        String songArtUrl = getIntent().getStringExtra("song_avatar");
        String songAudioUrl = getIntent().getStringExtra("song_audio");
        String songSinger = getIntent().getStringExtra("song_singer");

        // Set song title and art
        songTitle.setText(songTitleText);
        songSingerName.setText("Ca sĩ: " + songSinger);

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

        // SeekBar listener to update song position
        songSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}
        });

        // Handle next, previous, random, and repeat button actions (Not implemented here)
        btnNext.setOnClickListener(v -> Toast.makeText(SongPlayActivity.this, "Next button clicked", Toast.LENGTH_SHORT).show());
        btnPrev.setOnClickListener(v -> Toast.makeText(SongPlayActivity.this, "Previous button clicked", Toast.LENGTH_SHORT).show());
        btnRandom.setOnClickListener(v -> Toast.makeText(SongPlayActivity.this, "Random button clicked", Toast.LENGTH_SHORT).show());
        btnRepeat.setOnClickListener(v -> Toast.makeText(SongPlayActivity.this, "Repeat button clicked", Toast.LENGTH_SHORT).show());
    }

    // Format milliseconds to "mm:ss" format
    private String formatTime(int milliseconds) {
        int minutes = (milliseconds / 1000) / 60;
        int seconds = (milliseconds / 1000) % 60;
        return String.format("%02d:%02d", minutes, seconds);
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
    }
}

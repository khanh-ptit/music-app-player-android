package com.khanhleis11.appnghenhac_nhom3;

import android.media.MediaPlayer;

import com.khanhleis11.appnghenhac_nhom3.models.Song;

import java.io.IOException;

public class MediaPlayerSingleton {

    private static MediaPlayerSingleton instance;
    private MediaPlayer mediaPlayer;
    private Song currentSong;

    // Constructor private để đảm bảo chỉ có một instance
    private MediaPlayerSingleton() {
        mediaPlayer = new MediaPlayer();
    }

    // Phương thức để lấy instance duy nhất
    public static MediaPlayerSingleton getInstance() {
        if (instance == null) {
            instance = new MediaPlayerSingleton();
        }
        return instance;
    }

    // Phương thức để đặt bài hát hiện tại
    public void setCurrentSong(Song song) {
        this.currentSong = song;
        try {
            mediaPlayer.setDataSource(song.getAudio());
            mediaPlayer.prepareAsync(); // Chuẩn bị bất đồng bộ để tránh block UI thread
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Phương thức kiểm tra bài hát có đang phát không
    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    // Phương thức lấy thông tin bài hát hiện tại
    public Song getCurrentSong() {
        return currentSong;
    }

    // Phương thức để phát nhạc
    public void play() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    // Phương thức để tạm dừng nhạc
    public void pause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    // Phương thức để giải phóng tài nguyên MediaPlayer
    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}

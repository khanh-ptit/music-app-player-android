package com.khanhleis11.appnghenhac_nhom3.models;

import java.util.List;

public class SongResponse {
    private List<Song> songs;
    private String message; // Thêm trường message để lưu trữ thông báo lỗi
    private Song song;
    private String likesCount;
    private boolean isLiked, isFavorite;  // Sửa thành boolean thay vì String

    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }

    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(String likesCount) {
        this.likesCount = likesCount;
    }

    // Sửa lại getter và setter cho isLiked để sử dụng boolean
    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}

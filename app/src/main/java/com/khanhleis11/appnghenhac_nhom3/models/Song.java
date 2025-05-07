package com.khanhleis11.appnghenhac_nhom3.models;

import java.util.List;

public class Song {
    private String _id;
    private String title;
    private String avatar;
    private String audio;
    private String singerId;
    private String singerName;
    private int position;
    private int listen;
    private String lyrics;
    private List<String> like;

    // Constructor
    public Song(String title, String avatar, String audio, String singerId, String singerName, int position, int listen, String lyrics, List<String> like) {
        this.title = title;
        this.avatar = avatar;
        this.audio = audio;
        this.singerId = singerId;
        this.singerName = singerName;
        this.position = position;
        this.listen = listen;
        this.lyrics = lyrics;
        this.like = like;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public String getSingerId() {
        return singerId;
    }

    public void setSingerId(String singerId) {
        this.singerId = singerId;
    }

    public String getSingerName() {
        return singerName;
    }

    public void setSingerName(String singerName) {
        this.singerName = singerName;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getListen() {
        return listen;
    }

    public void setListen(int listen) {
        this.listen = listen;
    }

    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    public List<String> getLike() {
        return like; // Getter cho like
    }

    public void setLike(List<String> like) {
        this.like = like; // Setter cho like
    }

    // Phương thức để lấy số lượt like
    public int getLikeCount() {
        return like != null ? like.size() : 0; // Trả về số lượng like
    }
}

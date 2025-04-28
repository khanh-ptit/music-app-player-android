package com.khanhleis11.appnghenhac_nhom3.models;

public class Song {
    private String title;
    private String avatar;
    private String audio;
    private String singer;

    public Song(String title, String avatar, String audio, String singer) {
        this.title = title;
        this.avatar = avatar;
        this.audio = audio;
        this.singer = singer;
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

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }
}

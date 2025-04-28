package com.khanhleis11.appnghenhac_nhom3.models;

public class Song {
    private String title;
    private String avatar;
    private String audio;
    private String singerId;
    private String singerName; // Added singerName field

    // Constructor
    public Song(String title, String avatar, String audio, String singerId, String singerName) {
        this.title = title;
        this.avatar = avatar;
        this.audio = audio;
        this.singerId = singerId;
        this.singerName = singerName;
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
        return singerName;  // Getter for singerName
    }

    public void setSingerName(String singerName) {
        this.singerName = singerName;  // Setter for singerName
    }
}

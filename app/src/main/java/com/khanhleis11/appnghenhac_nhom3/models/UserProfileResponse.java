package com.khanhleis11.appnghenhac_nhom3.models;

import java.util.List;

public class UserProfileResponse {
    private String name;
    private String email;
    private String phone;
    private String avatar;
    private List<Song> favoriteSongs;

    public List<Song> getFavoriteSongs() {
        return favoriteSongs;
    }

    public void setFavoriteSongs(List<Song> favoriteSongs) {
        this.favoriteSongs = favoriteSongs;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}

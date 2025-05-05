package com.khanhleis11.appnghenhac_nhom3.models;

import java.util.List;

public class SongResponse {
    private List<Song> songs;
    private Song song;

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
}

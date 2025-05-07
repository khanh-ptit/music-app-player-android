package com.khanhleis11.appnghenhac_nhom3.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class TopicDetailResponse {

    private Topic topicInfo;  // Topic info

    private List<Song> songs;  // List of songs in the topic

    // Getter and Setter for topicInfo
    public Topic getTopicInfo() {
        return topicInfo;
    }

    public void setTopicInfo(Topic topicInfo) {
        this.topicInfo = topicInfo;
    }

    // Getter and Setter for songs
    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }
}

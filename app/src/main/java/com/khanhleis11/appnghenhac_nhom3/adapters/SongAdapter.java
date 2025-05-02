package com.khanhleis11.appnghenhac_nhom3.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.khanhleis11.appnghenhac_nhom3.R;
import com.khanhleis11.appnghenhac_nhom3.models.Song;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {

    private List<Song> songList;
    private List<Song> allSongs;  // Store all songs for filtering

    public SongAdapter(List<Song> songList) {
        this.songList = songList;
        this.allSongs = new ArrayList<>(songList);  // Keep a copy of all songs for later filtering
    }

    // Method to get the full list of songs (useful for filtering)
    public List<Song> getSongs() {
        return allSongs;
    }

    // Method to update the song list with filtered data
    public void updateList(List<Song> filteredSongs) {
        this.songList = filteredSongs;
        notifyDataSetChanged();  // Notify the adapter that the data has changed
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song song = songList.get(position);
        holder.songName.setText(song.getTitle());
        holder.songArtist.setText(song.getSingerName());

        // Check if the URL is HTTP and replace it with HTTPS
        String avatarUrl = song.getAvatar();
        if (avatarUrl != null && avatarUrl.startsWith("http://")) {
            avatarUrl = avatarUrl.replace("http://", "https://");
        }

        // Use Picasso to load the image using HTTPS URL
        Picasso.get().load(avatarUrl).into(holder.songArt);
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public static class SongViewHolder extends RecyclerView.ViewHolder {

        ImageView songArt;
        TextView songName, songArtist;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            songArt = itemView.findViewById(R.id.song_art);
            songName = itemView.findViewById(R.id.song_name);
            songArtist = itemView.findViewById(R.id.song_artist);
        }
    }
}

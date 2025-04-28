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

import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {

    private List<Song> songList;

    public SongAdapter(List<Song> songList) {
        this.songList = songList;
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
        holder.songArtist.setText(song.getSinger());

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

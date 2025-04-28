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

import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {

    private List<Song> playlistItems;

    public PlaylistAdapter(List<Song> playlistItems) {
        this.playlistItems = playlistItems;
    }

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_item, parent, false); // Ensure it's pointing to the correct layout
        return new PlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
        Song song = playlistItems.get(position);
        holder.playlistName.setText(song.getTitle());
//        holder.playlistArt.setImageResource(song.getImageResource());  // Ensure the image resource is correct
    }

    @Override
    public int getItemCount() {
        return playlistItems.size();
    }

    public static class PlaylistViewHolder extends RecyclerView.ViewHolder {

        ImageView playlistArt;
        TextView playlistName;

        public PlaylistViewHolder(@NonNull View itemView) {
            super(itemView);
            playlistArt = itemView.findViewById(R.id.playlist_art);
            playlistName = itemView.findViewById(R.id.playlist_name);
        }
    }
}

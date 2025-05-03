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

public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.RankingViewHolder> {

    private List<Song> songList;

    public RankingAdapter(List<Song> songList) {
        this.songList = songList;
    }

    @NonNull
    @Override
    public RankingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ranking, parent, false);
        return new RankingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RankingViewHolder holder, int position) {
        Song song = songList.get(position);

        // Set ranking position (order from 1, 2, 3, ...)
        holder.rankingPosition.setText(String.valueOf(position + 1));

        // Set song name and artist name
        holder.songName.setText(song.getTitle());
        holder.songArtist.setText(song.getSingerName());

        // Set listen count
        holder.listenCount.setText("Lượt nghe: " + song.getListen());

        // Check if the URL is HTTP and replace it with HTTPS
        String avatarUrl = song.getAvatar();
        if (avatarUrl != null && avatarUrl.startsWith("http://")) {
            avatarUrl = avatarUrl.replace("http://", "https://");
        }

        // Use Picasso to load the image using HTTPS URL
        Picasso.get().load(avatarUrl).resize(200, 200).into(holder.songArt);
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public static class RankingViewHolder extends RecyclerView.ViewHolder {

        TextView rankingPosition;
        ImageView songArt;
        TextView songName, songArtist, listenCount;

        public RankingViewHolder(@NonNull View itemView) {
            super(itemView);
            rankingPosition = itemView.findViewById(R.id.ranking_position);
            songArt = itemView.findViewById(R.id.song_art);
            songName = itemView.findViewById(R.id.song_name);
            songArtist = itemView.findViewById(R.id.song_artist);
            listenCount = itemView.findViewById(R.id.song_listen_count);  // Added listen count
        }
    }
}

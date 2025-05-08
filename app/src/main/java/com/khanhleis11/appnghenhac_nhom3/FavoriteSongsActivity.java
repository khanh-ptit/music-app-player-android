package com.khanhleis11.appnghenhac_nhom3;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.khanhleis11.appnghenhac_nhom3.adapters.SongAdapter;
import com.khanhleis11.appnghenhac_nhom3.models.Song;

import java.util.List;

public class FavoriteSongsActivity extends AppCompatActivity {

    private RecyclerView favoriteSongsRecyclerView;
    private SongAdapter songAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_songs);

        favoriteSongsRecyclerView = findViewById(R.id.favorite_songs_recycler_view);

        // Set RecyclerView Layout Manager
        favoriteSongsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get the favorite songs passed from MainActivity
        List<Song> favoriteSongs = (List<Song>) getIntent().getSerializableExtra("favorite_songs");

        if (favoriteSongs != null) {
            songAdapter = new SongAdapter(favoriteSongs);
            favoriteSongsRecyclerView.setAdapter(songAdapter);
        } else {
            Toast.makeText(this, "No favorite songs found", Toast.LENGTH_SHORT).show();
        }
    }
}

package com.khanhleis11.appnghenhac_nhom3;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.EditText;
import android.text.TextWatcher;
import android.text.Editable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.khanhleis11.appnghenhac_nhom3.adapters.SongAdapter;
import com.khanhleis11.appnghenhac_nhom3.adapters.SingerAdapter;
import com.khanhleis11.appnghenhac_nhom3.adapters.TopicAdapter;
import com.khanhleis11.appnghenhac_nhom3.adapters.RankingAdapter;
import com.khanhleis11.appnghenhac_nhom3.models.Song;
import com.khanhleis11.appnghenhac_nhom3.models.SongResponse;
import com.khanhleis11.appnghenhac_nhom3.models.Singer;
import com.khanhleis11.appnghenhac_nhom3.models.SingerResponse;
import com.khanhleis11.appnghenhac_nhom3.models.Topic;
import com.khanhleis11.appnghenhac_nhom3.models.TopicResponse;
import com.khanhleis11.appnghenhac_nhom3.models.RankingResponse;
import com.khanhleis11.appnghenhac_nhom3.api.ApiClient;
import com.khanhleis11.appnghenhac_nhom3.api.RetrofitInstance;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView songListRecycler;
    private SongAdapter songAdapter;
    private RecyclerView singerListRecycler;
    private SingerAdapter singerAdapter;
    private RecyclerView topicListRecycler;
    private TopicAdapter topicAdapter;
    private RecyclerView rankingListRecycler;
    private RankingAdapter rankingAdapter;
    private EditText searchEditText;
    private ProgressBar loadingSpinner;

    private List<Song> allSongs = new ArrayList<>();
    private Song currentSong;  // Store the current song
    private LinearLayout miniPlayer;
    private TextView songName, songArtist, playPauseButton;
    private ImageView songArt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        loadingSpinner = findViewById(R.id.loading_spinner);
        searchEditText = findViewById(R.id.search_edit_text);

        // Mini player views
        miniPlayer = findViewById(R.id.mini_player);
        songArt = findViewById(R.id.song_art);  // Ensure songArt is ImageView
        songName = findViewById(R.id.song_name);  // Ensure songName is TextView

        // Hiển thị ProgressBar khi bắt đầu fetch
        loadingSpinner.setVisibility(View.VISIBLE);
        findViewById(R.id.scroll_view).setVisibility(View.GONE);

        // Setup RecyclerViews
        songListRecycler = findViewById(R.id.song_list_recycler);
        songListRecycler.setLayoutManager(new LinearLayoutManager(this));

        singerListRecycler = findViewById(R.id.singerlist_recycler);
        singerListRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        topicListRecycler = findViewById(R.id.topic_recycler);
        topicListRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        rankingListRecycler = findViewById(R.id.ranking_recycler);
        rankingListRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        ApiClient apiClient = RetrofitInstance.getRetrofitInstance().create(ApiClient.class);

        // Call API for Songs
        Call<SongResponse> songCall = apiClient.getSongs();
        songCall.enqueue(new Callback<SongResponse>() {
            @Override
            public void onResponse(Call<SongResponse> call, Response<SongResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Song> songs = response.body().getSongs();
                    allSongs = new ArrayList<>(songs);
                    songAdapter = new SongAdapter(songs);
                    songListRecycler.setAdapter(songAdapter);

                    songAdapter.setOnItemClickListener(song -> {
                        currentSong = song;  // Set current song
                        Intent intent = new Intent(MainActivity.this, SongPlayActivity.class);
                        intent.putExtra("song_title", song.getTitle());
                        intent.putExtra("song_avatar", song.getAvatar());
                        intent.putExtra("song_audio", song.getAudio());
                        intent.putExtra("song_lyrics", song.getLyrics());
                        intent.putExtra("song_singer", song.getSingerName());
                        intent.putExtra("song_id", song.get_id());
                        startActivity(intent);
                    });

                } else {
                    Toast.makeText(MainActivity.this, "Failed to load songs", Toast.LENGTH_SHORT).show();
                }

                // Khi load xong bất kể thành công hay thất bại
                loadingSpinner.setVisibility(View.GONE);
                findViewById(R.id.scroll_view).setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<SongResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                loadingSpinner.setVisibility(View.GONE);
                findViewById(R.id.scroll_view).setVisibility(View.VISIBLE);
            }
        });

        // Call API for Singers
        Call<SingerResponse> singerCall = apiClient.getSingers();
        singerCall.enqueue(new Callback<SingerResponse>() {
            @Override
            public void onResponse(Call<SingerResponse> call, Response<SingerResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Singer> singers = response.body().getSingers();
                    singerAdapter = new SingerAdapter(singers);
                    singerListRecycler.setAdapter(singerAdapter);
                } else {
                    Toast.makeText(MainActivity.this, "Failed to load singers", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SingerResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Call API for Topics
        Call<TopicResponse> topicCall = apiClient.getTopics();
        topicCall.enqueue(new Callback<TopicResponse>() {
            @Override
            public void onResponse(Call<TopicResponse> call, Response<TopicResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Topic> topics = response.body().getTopics();
                    topicAdapter = new TopicAdapter(topics, MainActivity.this);
                    topicListRecycler.setAdapter(topicAdapter);
                } else {
                    Toast.makeText(MainActivity.this, "Failed to load topics", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TopicResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Call API for Ranking Songs
        Call<RankingResponse> rankingCall = apiClient.getRanking();
        rankingCall.enqueue(new Callback<RankingResponse>() {
            @Override
            public void onResponse(Call<RankingResponse> call, Response<RankingResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Song> rankingSongs = response.body().getSongs();
                    rankingAdapter = new RankingAdapter(rankingSongs);
                    rankingListRecycler.setAdapter(rankingAdapter);
                } else {
                    Toast.makeText(MainActivity.this, "Failed to load ranking songs", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RankingResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Search logic
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterSongs(s.toString());
            }
        });

        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                filterSongs(searchEditText.getText().toString().trim());
                return true;
            }
            return false;
        });
    }

    private void filterSongs(String query) {
        if (query.isEmpty()) {
            songAdapter = new SongAdapter(allSongs);
            songListRecycler.setAdapter(songAdapter);
        } else {
            ApiClient apiClient = RetrofitInstance.getRetrofitInstance().create(ApiClient.class);
            Call<SongResponse> songCall = apiClient.searchSongs(query);
            songCall.enqueue(new Callback<SongResponse>() {
                @Override
                public void onResponse(Call<SongResponse> call, Response<SongResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Song> songs = response.body().getSongs();
                        if (songs != null && !songs.isEmpty()) {
                            songAdapter = new SongAdapter(songs);
                            songListRecycler.setAdapter(songAdapter);
                        } else {
                            Toast.makeText(MainActivity.this, "Không tìm thấy bài hát", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Lỗi khi tải dữ liệu tìm kiếm", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<SongResponse> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}

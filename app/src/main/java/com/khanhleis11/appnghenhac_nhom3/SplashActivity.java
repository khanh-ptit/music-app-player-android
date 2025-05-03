package com.khanhleis11.appnghenhac_nhom3;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.khanhleis11.appnghenhac_nhom3.api.ApiClient;
import com.khanhleis11.appnghenhac_nhom3.models.RankingResponse;
import com.khanhleis11.appnghenhac_nhom3.models.SingerResponse;
import com.khanhleis11.appnghenhac_nhom3.models.SongResponse;
import com.khanhleis11.appnghenhac_nhom3.models.TopicResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_TIME_OUT = 1000; // Thời gian chờ 3 giây

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Tạo Retrofit instance
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://music-app-be-mu.vercel.app/") // Đảm bảo thay URL này với URL của backend
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Khởi tạo ApiClient từ Retrofit instance
        ApiClient apiClient = retrofit.create(ApiClient.class);

        // Gọi API để tải dữ liệu
        apiClient.getSongs().enqueue(new Callback<SongResponse>() {
            @Override
            public void onResponse(Call<SongResponse> call, Response<SongResponse> response) {
                if (response.isSuccessful()) {
                    // Dữ liệu bài hát đã được tải thành công
                    apiClient.getSingers().enqueue(new Callback<SingerResponse>() {
                        @Override
                        public void onResponse(Call<SingerResponse> call, Response<SingerResponse> response) {
                            if (response.isSuccessful()) {
                                // Dữ liệu ca sĩ đã được tải thành công
                                apiClient.getTopics().enqueue(new Callback<TopicResponse>() {
                                    @Override
                                    public void onResponse(Call<TopicResponse> call, Response<TopicResponse> response) {
                                        if (response.isSuccessful()) {
                                            // Dữ liệu chủ đề đã được tải thành công
                                            apiClient.getRanking().enqueue(new Callback<RankingResponse>() {
                                                @Override
                                                public void onResponse(Call<RankingResponse> call, Response<RankingResponse> response) {
                                                    if (response.isSuccessful()) {
                                                        // Dữ liệu xếp hạng đã được tải thành công
                                                        // Sau khi tất cả dữ liệu được tải, chuyển hướng đến MainActivity
                                                        new Handler().postDelayed(() -> {
                                                            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                                                            startActivity(intent);
                                                            finish(); // Kết thúc SplashActivity
                                                        }, SPLASH_TIME_OUT);
                                                    } else {
                                                        handleError();
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<RankingResponse> call, Throwable t) {
                                                    handleError();
                                                }
                                            });
                                        } else {
                                            handleError();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<TopicResponse> call, Throwable t) {
                                        handleError();
                                    }
                                });
                            } else {
                                handleError();
                            }
                        }

                        @Override
                        public void onFailure(Call<SingerResponse> call, Throwable t) {
                            handleError();
                        }
                    });
                } else {
                    handleError();
                }
            }

            @Override
            public void onFailure(Call<SongResponse> call, Throwable t) {
                handleError();
            }
        });
    }

    private void handleError() {
        Toast.makeText(SplashActivity.this, "Error loading data", Toast.LENGTH_SHORT).show();
        finish(); // Kết thúc SplashActivity nếu có lỗi
    }
}

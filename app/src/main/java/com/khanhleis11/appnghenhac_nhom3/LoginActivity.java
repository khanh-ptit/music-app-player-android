package com.khanhleis11.appnghenhac_nhom3;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.khanhleis11.appnghenhac_nhom3.api.ApiClient;
import com.khanhleis11.appnghenhac_nhom3.api.RetrofitInstance;
import com.khanhleis11.appnghenhac_nhom3.models.LoginRequest;
import com.khanhleis11.appnghenhac_nhom3.models.LoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private TextView forgetPasswordLink, registerLink;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Khởi tạo các view
        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        loginButton = findViewById(R.id.login_button);
        forgetPasswordLink = findViewById(R.id.forget_password_link);
        registerLink = findViewById(R.id.register_link);

        // Set sự kiện cho nút đăng nhập
        loginButton.setOnClickListener(view -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            } else {
                loginUser(email, password);
            }
        });

        registerLink.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent); // Chuyển đến màn hình đăng ký
        });

        forgetPasswordLink.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent); // Chuyển đến màn hình quên mật khẩu
        });


        // Bottom Navigation: Home, Favorite Songs, User Profile
        TextView navHome = findViewById(R.id.nav_home);
        TextView navFavoriteSong = findViewById(R.id.nav_favorite_song);

        navHome.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        });

        navFavoriteSong.setOnClickListener(v -> {
            Toast.makeText(LoginActivity.this, "Vui lòng đăng nhập để xem bài hát yêu thích!", Toast.LENGTH_SHORT).show();
        });

    }

    private void loginUser(String email, String password) {
        // Tạo đối tượng LoginRequest để gửi đi API
        LoginRequest loginRequest = new LoginRequest(email, password);

        // Gửi yêu cầu đăng nhập qua API
        ApiClient apiClient = RetrofitInstance.getRetrofitInstance().create(ApiClient.class);
        Call<LoginResponse> call = apiClient.loginUser(loginRequest);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                // Log the whole response body
                Log.d("LoginActivity", "Response Code: " + response.code());  // Log status code
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        String token = response.body().getToken();
                        Log.d("LoginActivity", "Token received: " + token);

                        // Lưu token vào SharedPreferences hoặc bộ nhớ tạm (để duy trì trạng thái đăng nhập)
                        getSharedPreferences("user_prefs", MODE_PRIVATE).edit()
                                .putString("auth_token", token)
                                .apply();

                        // Chuyển sang màn hình chính sau khi đăng nhập
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Nếu response có status code 200 nhưng body null, log chi tiết lỗi
                        Log.e("LoginActivity", "Response body is null.");
                        Toast.makeText(LoginActivity.this, "Đăng nhập thất bại. Vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    int errorCode = response.code();
                    if (errorCode == 404) {
                        Toast.makeText(LoginActivity.this, "Email không hợp lệ!", Toast.LENGTH_SHORT).show();
                    } else if (errorCode == 401) {
                        Toast.makeText(LoginActivity.this, "Mật khẩu không chính xác!", Toast.LENGTH_SHORT).show();
                    } else {
                        // Các lỗi khác
                        Toast.makeText(LoginActivity.this, "Đăng nhập thất bại. Vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                    }

                    Log.e("LoginActivity", "Error Response: " + response.errorBody());
                }
            }


            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

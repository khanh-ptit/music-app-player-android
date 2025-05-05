package com.khanhleis11.appnghenhac_nhom3;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.khanhleis11.appnghenhac_nhom3.api.ApiClient;
import com.khanhleis11.appnghenhac_nhom3.api.RetrofitInstance;
import com.khanhleis11.appnghenhac_nhom3.models.ResetPasswordRequest;
import com.khanhleis11.appnghenhac_nhom3.models.ResetPasswordResponse;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText newPasswordEditText, confirmPasswordEditText;
    private Button resetButton;
    private String email, token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        newPasswordEditText = findViewById(R.id.new_password_edit_text);
        confirmPasswordEditText = findViewById(R.id.confirm_password_edit_text);
        resetButton = findViewById(R.id.reset_password_button);

        email = getIntent().getStringExtra("email");
        token = getSharedPreferences("user_prefs", MODE_PRIVATE).getString("reset_token", null);

        resetButton.setOnClickListener(view -> {
            String newPassword = newPasswordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();

            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            } else if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(this, "Mật khẩu không trùng khớp", Toast.LENGTH_SHORT).show();
            } else {
                resetPassword(email, newPassword, token);
            }
        });
    }

    private void resetPassword(String email, String newPassword, String token) {
        // Tạo đối tượng request với password và confirmPassword giống nhau
        ResetPasswordRequest request = new ResetPasswordRequest(newPassword, newPassword);

        // Gọi API
        ApiClient apiClient = RetrofitInstance.getRetrofitInstance().create(ApiClient.class);
        Call<ResetPasswordResponse> call = apiClient.resetPassword(request, token);

        call.enqueue(new Callback<ResetPasswordResponse>() {
            @Override
            public void onResponse(Call<ResetPasswordResponse> call, Response<ResetPasswordResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResetPasswordResponse res = response.body();

                    // Kiểm tra code trả về từ BE
                    if (res.getCode() == 200) {
                        Toast.makeText(ResetPasswordActivity.this, res.getMessage(), Toast.LENGTH_LONG).show();

                        // Xóa token tạm trong SharedPreferences
                        getSharedPreferences("user_prefs", MODE_PRIVATE).edit()
                                .remove("reset_token")
                                .apply();

                        // Quay lại màn hình Login
                        Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        // Nếu code khác 200 (dù là HTTP 200) → vẫn hiển thị lỗi
                        Toast.makeText(ResetPasswordActivity.this, res.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Khi response không thành công hoặc không có body
                    Toast.makeText(ResetPasswordActivity.this, "Đổi mật khẩu thất bại! Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                    Log.e("ResetPassword", "Response error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResetPasswordResponse> call, Throwable t) {
                // Khi gặp lỗi kết nối mạng hoặc bất kỳ lỗi nào khi gửi request
                Toast.makeText(ResetPasswordActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("ResetPassword", "API call failed: ", t);
            }
        });
    }

}

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
import com.khanhleis11.appnghenhac_nhom3.models.OtpVerifyRequest;
import com.khanhleis11.appnghenhac_nhom3.models.OtpVerifyResponse;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtpVerificationActivity extends AppCompatActivity {

    private EditText otpEditText;
    private Button verifyOtpButton;
    private String email; // email truyền từ ForgotPasswordActivity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);

        otpEditText = findViewById(R.id.otp_edit_text);
        verifyOtpButton = findViewById(R.id.verify_otp_button);

        email = getIntent().getStringExtra("email");

        verifyOtpButton.setOnClickListener(view -> {
            String otp = otpEditText.getText().toString().trim();
            if (otp.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập mã OTP", Toast.LENGTH_SHORT).show();
            } else {
                verifyOtp(email, otp);
            }
        });
    }

    private void verifyOtp(String email, String otp) {
        ApiClient apiClient = RetrofitInstance.getRetrofitInstance().create(ApiClient.class);
        OtpVerifyRequest request = new OtpVerifyRequest(email, otp);

        Call<OtpVerifyResponse> call = apiClient.verifyOtp(request);
        call.enqueue(new Callback<OtpVerifyResponse>() {
            @Override
            public void onResponse(Call<OtpVerifyResponse> call, Response<OtpVerifyResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String message = response.body().getMessage();
                    String token = response.body().getToken();

                    Toast.makeText(OtpVerificationActivity.this, message, Toast.LENGTH_LONG).show();

                    getSharedPreferences("user_prefs", MODE_PRIVATE).edit()
                            .putString("reset_token", token)
                            .apply();

                    Intent intent = new Intent(OtpVerificationActivity.this, ResetPasswordActivity.class);
                    intent.putExtra("email", email);
                    startActivity(intent);
                    finish();
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        JSONObject jsonObject = new JSONObject(errorBody);
                        String message = jsonObject.getString("message");
                        Toast.makeText(OtpVerificationActivity.this, message, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(OtpVerificationActivity.this, "OTP không hợp lệ!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<OtpVerifyResponse> call, Throwable t) {
                Toast.makeText(OtpVerificationActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}

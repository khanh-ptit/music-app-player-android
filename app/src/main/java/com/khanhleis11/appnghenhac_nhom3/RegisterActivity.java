package com.khanhleis11.appnghenhac_nhom3;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.khanhleis11.appnghenhac_nhom3.api.ApiClient;
import com.khanhleis11.appnghenhac_nhom3.api.RetrofitInstance;
import com.khanhleis11.appnghenhac_nhom3.models.RegisterRequest;
import com.khanhleis11.appnghenhac_nhom3.models.RegisterResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText fullNameEditText, emailEditText, passwordEditText, confirmPasswordEditText, addressEditText, phoneEditText;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize views
        fullNameEditText = findViewById(R.id.full_name_edit_text);
        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        confirmPasswordEditText = findViewById(R.id.confirm_password_edit_text);
        addressEditText = findViewById(R.id.address_edit_text);
        phoneEditText = findViewById(R.id.phone_edit_text);
        registerButton = findViewById(R.id.register_button);

        // Set event for Register button
        registerButton.setOnClickListener(view -> {
            String fullName = fullNameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();
            String address = addressEditText.getText().toString().trim();
            String phone = phoneEditText.getText().toString().trim();

            if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || address.isEmpty() || phone.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            } else if (!password.equals(confirmPassword)) {
                Toast.makeText(RegisterActivity.this, "Mật khẩu không khớp!", Toast.LENGTH_SHORT).show();
            } else {
                registerUser(fullName, email, password, address, phone);
            }
        });
    }

    private void registerUser(String fullName, String email, String password, String address, String phone) {
        RegisterRequest registerRequest = new RegisterRequest(fullName, email, password, address, phone);

        ApiClient apiClient = RetrofitInstance.getRetrofitInstance().create(ApiClient.class);
        Call<RegisterResponse> call = apiClient.registerUser(registerRequest);

        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                Log.d("RegisterActivity", "Response Code: " + response.code());

                if (response.isSuccessful()) {
                    // Success case
                    Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                    finish(); // Close the Register Activity and return to LoginActivity
                } else {
                    // Failure case, check the response code
                    int errorCode = response.code();
                    String errorMessage = "Đăng ký thất bại. Vui lòng thử lại!";

                    // Handle specific error codes
                    if (errorCode == 400) {
                        errorMessage = "Dữ liệu không hợp lệ. Vui lòng kiểm tra lại các thông tin.";
                    } else if (errorCode == 404) {
                        errorMessage = "Email đã tồn tại. Vui lòng sử dụng email khác.";
                    } else if (errorCode == 500) {
                        errorMessage = "Đã xảy ra lỗi từ hệ thống. Vui lòng thử lại sau.";
                    }

                    // Display error message based on error code
                    Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                // Error in network or response failure
                Toast.makeText(RegisterActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}

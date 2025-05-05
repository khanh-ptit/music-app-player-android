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

            // Validate before sending
            if (validateInput(fullName, email, password, confirmPassword, address, phone)) {
                registerUser(fullName, email, password, address, phone);
            }
        });
    }

    private boolean validateInput(String fullName, String email, String password, String confirmPassword, String address, String phone) {
        if (fullName.isEmpty()) {
            fullNameEditText.setError("Vui lòng nhập họ tên");
            return false;
        }

        if (email.isEmpty()) {
            emailEditText.setError("Vui lòng nhập email");
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Email không hợp lệ");
            return false;
        }

        if (password.isEmpty()) {
            passwordEditText.setError("Vui lòng nhập mật khẩu");
            return false;
        }

        if (password.length() < 6) {
            passwordEditText.setError("Mật khẩu phải có ít nhất 6 ký tự");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Mật khẩu xác nhận không khớp");
            return false;
        }

        if (address.isEmpty()) {
            addressEditText.setError("Vui lòng nhập địa chỉ");
            return false;
        }

        if (phone.isEmpty()) {
            phoneEditText.setError("Vui lòng nhập số điện thoại");
            return false;
        }

        if (!phone.matches("^\\d{10,11}$")) {
            phoneEditText.setError("Số điện thoại không hợp lệ (10-11 số)");
            return false;
        }

        return true;
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
                    Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                    finish(); // Close the Register Activity and return to LoginActivity
                } else {
                    int errorCode = response.code();
                    String errorMessage = "Đăng ký thất bại. Vui lòng thử lại!";

                    if (errorCode == 400) {
                        errorMessage = "Dữ liệu không hợp lệ. Vui lòng kiểm tra lại các thông tin.";
                    } else if (errorCode == 404) {
                        errorMessage = "Email đã tồn tại. Vui lòng sử dụng email khác.";
                    } else if (errorCode == 500) {
                        errorMessage = "Đã xảy ra lỗi từ hệ thống. Vui lòng thử lại sau.";
                    }

                    Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

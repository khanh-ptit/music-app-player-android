package com.khanhleis11.appnghenhac_nhom3.models;

public class ResetPasswordRequest {
    private String password;
    private String confirmPassword;

    public ResetPasswordRequest(String password, String confirmPassword) {
        this.password = password;
        this.confirmPassword = confirmPassword;
    }

    public String getPassword() {
        return password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }
}

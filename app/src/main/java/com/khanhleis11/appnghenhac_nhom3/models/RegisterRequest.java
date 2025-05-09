package com.khanhleis11.appnghenhac_nhom3.models;

public class RegisterRequest {
    private String fullName;
    private String email;
    private String password;
    private String address;
    private String phone;

    public RegisterRequest(String fullName, String email, String password, String address, String phone) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.address = address;
        this.phone = phone;
    }

    // Getters and Setters

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}

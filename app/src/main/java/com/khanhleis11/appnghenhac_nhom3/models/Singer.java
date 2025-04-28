package com.khanhleis11.appnghenhac_nhom3.models;

public class Singer {

    private String _id;
    private String fullName;
    private String avatar;
    private String description;
    private String status;
    private int birthYear;
    private String hometown;
    private String nationality;
    private String realName;

    // Constructor
    public Singer(String _id, String fullName, String avatar, String description, String status, int birthYear, String hometown, String nationality, String realName) {
        this._id = _id;
        this.fullName = fullName;
        this.avatar = avatar;
        this.description = description;
        this.status = status;
        this.birthYear = birthYear;
        this.hometown = hometown;
        this.nationality = nationality;
        this.realName = realName;
    }

    // Getters and Setters
    public String getId() {
        return _id;
    }

    public void setId(String _id) {
        this._id = _id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(int birthYear) {
        this.birthYear = birthYear;
    }

    public String getHometown() {
        return hometown;
    }

    public void setHometown(String hometown) {
        this.hometown = hometown;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }
}

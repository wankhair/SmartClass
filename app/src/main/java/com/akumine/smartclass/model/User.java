package com.akumine.smartclass.model;

public class User {

    public static final String DB_USER = "User";
    public static final String DB_COLUMN_ID = "id";
    public static final String DB_COLUMN_USERNAME = "username";
    public static final String DB_COLUMN_EMAIL = "email";
    public static final String DB_COLUMN_ROLE = "role";
    public static final String DB_COLUMN_IMAGE = "image";
    public static final String DB_COLUMN_PHONE = "phone";
    public static final String DB_COLUMN_DEVICE_TOKEN = "deviceToken";

    private String id;
    private String username;
    private String email;
    private String role;
    private String image;
    private String phone;

    public User() {
    }

    public User(String id, String username, String email, String role, String image, String phone) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.image = image;
        this.phone = phone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}

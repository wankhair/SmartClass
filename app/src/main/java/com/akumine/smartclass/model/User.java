package com.akumine.smartclass.model;

public class User {

    public static final String TABLE_USER = "User";
    public static final String ID = "id";
    public static final String USERNAME = "username";
    public static final String EMAIL = "email";
    public static final String ROLE = "role";
    public static final String IMAGE = "image";
    public static final String PHONE = "phone";
    public static final String DEVICE_TOKEN = "deviceToken";

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

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public String getImage() {
        return image;
    }

    public String getPhone() {
        return phone;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}

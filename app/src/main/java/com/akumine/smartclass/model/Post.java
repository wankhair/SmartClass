package com.akumine.smartclass.model;

import java.util.Comparator;

public class Post {

    public static final String TABLE_POST = "Post";
    public static final String ID = "id";
    public static final String TEXT = "text";
    public static final String IMAGE_URL = "imageUrl";
    public static final String DATE = "date";
    public static final String TIME = "time";
    public static final String CLASS_ID = "classId";
    public static final String USER_ID = "userId";
    public static final String CREATED = "created";

    public static final Comparator<Post> BY_CREATED_DATE = new Comparator<Post>() {
        @Override
        public int compare(Post o1, Post o2) {
            return o2.getCreated().compareTo(o1.getCreated());
        }
    };

    private String id;
    private String text;
    private String imageUrl;
    private String date;
    private String time;
    private String classId;
    private String userId;
    private String created;

    public Post() {
    }

    public Post(String id, String text, String imageUrl, String date, String time, String classId, String userId, String created) {
        this.id = id;
        this.text = text;
        this.imageUrl = imageUrl;
        this.date = date;
        this.time = time;
        this.classId = classId;
        this.userId = userId;
        this.created = created;
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getClassId() {
        return classId;
    }

    public String getUserId() {
        return userId;
    }

    public String getCreated() {
        return created;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setCreated(String created) {
        this.created = created;
    }
}

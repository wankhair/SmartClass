package com.akumine.smartclass.model;

import java.util.Comparator;

public class Classes {

    public static final String DB_CLASS = "Class";
    public static final String DB_COLUMN_ID = "id";
    public static final String DB_COLUMN_NAME = "className";
    public static final String DB_COLUMN_DESC = "classDescription";
    public static final String DB_COLUMN_LECTURER_ID = "lecturerId";
    public static final String DB_COLUMN_CREATED = "created";
    public static final String DB_COLUMN_MODIFY = "modify";
    public static final String DB_COLUMN_CURRENT_USER = "currentUser";
    public static final String DB_COLUMN_MAX_USER = "maxUser";

    public static final Comparator<Classes> BY_CREATED_DATE = new Comparator<Classes>() {
        @Override
        public int compare(Classes o1, Classes o2) {
            return o2.getCreated().compareTo(o1.getCreated());
        }
    };

    private String id;
    private String className;
    private String classDescription;
    private String lecturerId;
    private String created;
    private String modify;
    private String currentUser;
    private String maxUser;

    public Classes() {
    }

    public Classes(String id, String className, String classDescription, String lecturerId, String created, String modify, String currentUser, String maxUser) {
        this.id = id;
        this.className = className;
        this.classDescription = classDescription;
        this.lecturerId = lecturerId;
        this.created = created;
        this.modify = modify;
        this.currentUser = currentUser;
        this.maxUser = maxUser;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassDescription() {
        return classDescription;
    }

    public void setClassDescription(String classDescription) {
        this.classDescription = classDescription;
    }

    public String getLecturerId() {
        return lecturerId;
    }

    public void setLecturerId(String lecturerId) {
        this.lecturerId = lecturerId;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getModify() {
        return modify;
    }

    public void setModify(String modify) {
        this.modify = modify;
    }

    public String getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
    }

    public String getMaxUser() {
        return maxUser;
    }

    public void setMaxUser(String maxUser) {
        this.maxUser = maxUser;
    }
}

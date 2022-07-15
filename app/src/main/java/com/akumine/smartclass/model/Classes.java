package com.akumine.smartclass.model;

import java.util.Comparator;

public class Classes {

    public static final String TABLE_CLASS = "Class";
    public static final String ID = "id";
    public static final String CLASS_NAME = "className";
    public static final String CLASS_DESC = "classDescription";
    public static final String LECTURER_ID = "lecturerId";
    public static final String CREATED = "created";
    public static final String MODIFY = "modify";
    public static final String CURRENT_USER = "currentUser";
    public static final String MAX_USER = "maxUser";

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

    public String getClassName() {
        return className;
    }

    public String getClassDescription() {
        return classDescription;
    }

    public String getLecturerId() {
        return lecturerId;
    }

    public String getCreated() {
        return created;
    }

    public String getModify() {
        return modify;
    }

    public String getCurrentUser() {
        return currentUser;
    }

    public String getMaxUser() {
        return maxUser;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setClassDescription(String classDescription) {
        this.classDescription = classDescription;
    }

    public void setLecturerId(String lecturerId) {
        this.lecturerId = lecturerId;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public void setModify(String modify) {
        this.modify = modify;
    }

    public void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
    }

    public void setMaxUser(String maxUser) {
        this.maxUser = maxUser;
    }
}

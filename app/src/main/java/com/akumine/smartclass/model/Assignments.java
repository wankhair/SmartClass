package com.akumine.smartclass.model;

import java.util.Comparator;

public class Assignments {

    public static final String DB_ASSIGNMENT = "Assignment";
    public static final String DB_COLUMN_ID = "id";
    public static final String DB_COLUMN_NAME = "assignmentName";
    public static final String DB_COLUMN_DESC = "assignmentDescription";
    public static final String DB_COLUMN_DOC_URL = "documentUrl";
    public static final String DB_COLUMN_DOC_NAME = "documentName";
    public static final String DB_COLUMN_DATE = "date";
    public static final String DB_COLUMN_TIME = "time";
    public static final String DB_COLUMN_CREATED = "created";
    public static final String DB_COLUMN_MODIFY = "modify";
    public static final String DB_COLUMN_CLASS_ID = "classId";

    public static final Comparator<Assignments> BY_CREATED_DATE = new Comparator<Assignments>() {
        @Override
        public int compare(Assignments o1, Assignments o2) {
            return o2.getCreated().compareTo(o1.getCreated());
        }
    };

    private String id;
    private String assignmentName;
    private String assignmentDescription;
    private String documentUrl;
    private String documentName;
    private String date;
    private String time;
    private String created;
    private String modify;
    private String classId;

    public Assignments() {
    }

    public Assignments(String id, String assignmentName, String assignmentDescription, String documentUrl, String documentName, String date, String time, String created, String modify, String classId) {
        this.id = id;
        this.assignmentName = assignmentName;
        this.assignmentDescription = assignmentDescription;
        this.documentUrl = documentUrl;
        this.documentName = documentName;
        this.date = date;
        this.time = time;
        this.created = created;
        this.modify = modify;
        this.classId = classId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAssignmentName() {
        return assignmentName;
    }

    public void setAssignmentName(String assignmentName) {
        this.assignmentName = assignmentName;
    }

    public String getAssignmentDescription() {
        return assignmentDescription;
    }

    public void setAssignmentDescription(String assignmentDescription) {
        this.assignmentDescription = assignmentDescription;
    }

    public String getDocumentUrl() {
        return documentUrl;
    }

    public void setDocumentUrl(String documentUrl) {
        this.documentUrl = documentUrl;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }
}

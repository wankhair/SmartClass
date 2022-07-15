package com.akumine.smartclass.model;

import java.util.Comparator;

public class Assignments {

    public static final String TABLE_ASSIGNMENT = "Assignment";
    public static final String ID = "id";
    public static final String ASSIGN_NAME = "assignmentName";
    public static final String ASSIGN_DESC = "assignmentDescription";
    public static final String DOCUMENT_URL = "documentUrl";
    public static final String DOCUMENT_NAME = "documentName";
    public static final String DATE = "date";
    public static final String TIME = "time";
    public static final String CREATED = "created";
    public static final String MODIFY = "modify";
    public static final String CLASS_ID = "classId";

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

    public String getAssignmentName() {
        return assignmentName;
    }

    public String getAssignmentDescription() {
        return assignmentDescription;
    }

    public String getDocumentUrl() {
        return documentUrl;
    }

    public String getDocumentName() {
        return documentName;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getCreated() {
        return created;
    }

    public String getModify() {
        return modify;
    }

    public String getClassId() {
        return classId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setAssignmentName(String assignmentName) {
        this.assignmentName = assignmentName;
    }

    public void setAssignmentDescription(String assignmentDescription) {
        this.assignmentDescription = assignmentDescription;
    }

    public void setDocumentUrl(String documentUrl) {
        this.documentUrl = documentUrl;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public void setModify(String modify) {
        this.modify = modify;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }
}

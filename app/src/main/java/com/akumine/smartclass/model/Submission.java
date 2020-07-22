package com.akumine.smartclass.model;

public class Submission {

    public static final String DB_SUBMIT = "Submit";
    public static final String DB_COLUMN_MEMBER_ID = "memberId";
    public static final String DB_COLUMN_ASSIGN_ID = "assignId";
    public static final String DB_COLUMN_STATUS = "status";

    private String memberId;
    private String assignId;
    private String status;

    public Submission() {
    }

    public Submission(String memberId, String assignId, String status) {
        this.memberId = memberId;
        this.assignId = assignId;
        this.status = status;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getAssignId() {
        return assignId;
    }

    public void setAssignId(String assignId) {
        this.assignId = assignId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

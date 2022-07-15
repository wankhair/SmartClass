package com.akumine.smartclass.model;

public class Submission {

    public static final String TABLE_SUBMIT = "Submit";
    public static final String MEMBER_ID = "memberId";
    public static final String ASSIGN_ID = "assignId";
    public static final String STATUS = "status";

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

    public String getAssignId() {
        return assignId;
    }

    public String getStatus() {
        return status;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public void setAssignId(String assignId) {
        this.assignId = assignId;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

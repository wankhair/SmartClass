package com.akumine.smartclass.model;

import java.util.Comparator;

public class ClassMember {

    public static final String TABLE_MEMBER = "ClassMember";
    public static final String MEMBER_ID = "memberId";
    public static final String MEMBER_NAME = "memberName";
    public static final String CLASS_ID = "classId";
    public static final String JOIN_DATE = "joinDate";

    public static final Comparator<ClassMember> BY_USERNAME = new Comparator<ClassMember>() {
        @Override
        public int compare(ClassMember o1, ClassMember o2) {
            return o1.getMemberName().compareTo(o2.getMemberName());
        }
    };
    public static final Comparator<ClassMember> BY_JOIN_DATE = new Comparator<ClassMember>() {
        @Override
        public int compare(ClassMember o1, ClassMember o2) {
            return o2.getJoinDate().compareTo(o1.getJoinDate());
        }
    };

    private String memberId;
    private String memberName;
    private String classId;
    private String joinDate;

    public ClassMember() {
    }

    public ClassMember(String memberId, String memberName, String classId, String joinDate) {
        this.memberId = memberId;
        this.memberName = memberName;
        this.classId = classId;
        this.joinDate = joinDate;
    }

    public String getMemberId() {
        return memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public String getClassId() {
        return classId;
    }

    public String getJoinDate() {
        return joinDate;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public void setJoinDate(String joinDate) {
        this.joinDate = joinDate;
    }
}

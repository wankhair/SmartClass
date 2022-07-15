package com.akumine.smartclass.util;

import com.akumine.smartclass.model.Assignments;
import com.akumine.smartclass.model.ClassMember;
import com.akumine.smartclass.model.Classes;
import com.akumine.smartclass.model.Notification;
import com.akumine.smartclass.model.Post;
import com.akumine.smartclass.model.Submission;
import com.akumine.smartclass.model.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DatabaseUtil {

    public DatabaseUtil() {
        throw new AssertionError("Do not initialize constructor");
    }

    // region DatabaseReference without Child Path

    public static DatabaseReference tableUser() {
        return tableUserWithOneChild(null);
    }

    public static DatabaseReference tableClass() {
        return tableClassWithOneChild(null);
    }

    public static DatabaseReference tableAssignment() {
        return tableAssignmentWithOneChild(null);
    }

    public static DatabaseReference tableMember() {
        return tableMemberWithOneChild(null);
    }

    public static DatabaseReference tablePost() {
        return tablePostWithOneChild(null);
    }

    public static DatabaseReference tableSubmission() {
        return tableSubmissionWithOneChild(null);
    }

    public static DatabaseReference tableNotification() {
        return tableNotificationWithOneChild(null);
    }

    // endregion

    // region DatabaseReference with 1 Child Path

    public static DatabaseReference tableUserWithOneChild(String child) {
        return tableUserWithTwoChild(child, null);
    }

    public static DatabaseReference tableClassWithOneChild(String child) {
        return tableClassWithTwoChild(child, null);
    }

    public static DatabaseReference tableAssignmentWithOneChild(String child) {
        return tableAssignmentWithTwoChild(child, null);
    }

    public static DatabaseReference tableMemberWithOneChild(String child) {
        return tableMemberWithTwoChild(child, null);
    }

    public static DatabaseReference tablePostWithOneChild(String child) {
        return tablePostWithTwoChild(child, null);
    }

    public static DatabaseReference tableSubmissionWithOneChild(String child) {
        return tableSubmissionWithTwoChild(child, null);
    }

    public static DatabaseReference tableNotificationWithOneChild(String child) {
        return tableNotificationWithTwoChild(child, null);
    }

    // endregion

    // region DatabaseReference with 2 Child Path

    public static DatabaseReference tableUserWithTwoChild(String child1, String child2) {
        return constructDBReference(User.TABLE_USER, child1, child2);
    }

    public static DatabaseReference tableClassWithTwoChild(String child1, String child2) {
        return constructDBReference(Classes.TABLE_CLASS, child1, child2);
    }

    public static DatabaseReference tableAssignmentWithTwoChild(String child1, String child2) {
        return constructDBReference(Assignments.TABLE_ASSIGNMENT, child1, child2);
    }

    public static DatabaseReference tableMemberWithTwoChild(String child1, String child2) {
        return constructDBReference(ClassMember.TABLE_MEMBER, child1, child2);
    }

    public static DatabaseReference tablePostWithTwoChild(String child1, String child2) {
        return constructDBReference(Post.TABLE_POST, child1, child2);
    }

    public static DatabaseReference tableSubmissionWithTwoChild(String child1, String child2) {
        return constructDBReference(Submission.TABLE_SUBMIT, child1, child2);
    }

    public static DatabaseReference tableNotificationWithTwoChild(String child1, String child2) {
        return constructDBReference(Notification.TABLE_NOTIFICATION, child1, child2);
    }

    // endregion

    private static DatabaseReference constructDBReference(String tableName, String child1, String child2) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(tableName);
        if (child1 != null) {
            if (child2 != null) {
                return ref.child(child1).child(child2);
            } else {
                return ref.child(child1);
            }
        } else {
            return ref;
        }
    }
}

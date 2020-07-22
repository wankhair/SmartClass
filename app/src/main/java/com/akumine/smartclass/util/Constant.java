package com.akumine.smartclass.util;

public class Constant {

    //role
    public static final String ROLE_LECTURER = "Lecturer";
    public static final String ROLE_STUDENT = "Student";

    //preference
    public static final String PREFERENCE_ROLE = "userRole";

    //intent extra
    public static final String EXTRA_USER_ID = "extra.user_id";
    public static final String EXTRA_EMAIL = "extra.email";
    public static final String EXTRA_CLASS_ID = "extra.class_id";
    public static final String EXTRA_ASSIGN_ID = "extra.assign_id";
    public static final String EXTRA_MEMBER_ID = "extra.member_id";
    public static final String EXTRA_POST_ID = "extra.post_id";

    //bundle args
    public static final String ARGS_USER_ID = "args.user_id";
    public static final String ARGS_CLASS_ID = "args.class_id";
    public static final String ARGS_ASSIGN_ID = "args.assign_id";
    public static final String ARGS_MEMBER_ID = "args.member_id";

    //request code
    public static final int REQUEST_CODE_MULTIPLE_PERMISSIONS = 1010;
    public static final int REQUEST_CODE_PICK_IMAGE = 1011;
    public static final int REQUEST_CODE_SELECT_FILE = 1012;

    //validation message
    public static final String ERROR_EMAIL_EMPTY = "Email is required";
    public static final String ERROR_EMAIL_INVALID = "Please enter a valid email";
    public static final String ERROR_PASSWORD_EMPTY = "Password is required";
    public static final String ERROR_PASSWORD_TOO_WEAK = "Password too weak";
    public static final String ERROR_USERNAME_EMPTY = "Username is required";
    public static final String ERROR_PHONE_NUM_EMPTY = "Phone No. is required";
    public static final String ERROR_PHONE_NUM_INVALID = "Please enter a valid number";
    public static final String ERROR_CLASS_NAME_EMPTY = "Class Name is empty";
    public static final String ERROR_CLASS_DESC_EMPTY = "Class Description is empty";

    //firebase storage reference
    public static final String STORAGE_USER_IMAGES = "User Images";
    public static final String STORAGE_POST_IMAGES = "Post Images";
    public static final String STORAGE_UPLOAD_FILES = "Uploads";

}

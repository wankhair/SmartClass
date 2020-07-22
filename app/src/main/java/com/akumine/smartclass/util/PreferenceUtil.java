package com.akumine.smartclass.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.akumine.smartclass.model.User;

public class PreferenceUtil {

    public PreferenceUtil() {
        throw new IllegalStateException("Do not initialize constructor");
    }

    public static void setRole(Context context, String role) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constant.PREFERENCE_ROLE, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(User.DB_COLUMN_ROLE, role);
        editor.apply();
    }

    public static String getRole(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constant.PREFERENCE_ROLE, 0);
        return sharedPreferences.getString(User.DB_COLUMN_ROLE, null);
    }
}

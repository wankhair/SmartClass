package com.akumine.smartclass.util;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

public class KeyboardUtil {

    public KeyboardUtil() {
        throw new AssertionError("Do not initialize constructor");
    }

    public static void hideSoftKeyboard(Activity activity) {
        if (activity == null)
            return;

        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (activity.getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }
}

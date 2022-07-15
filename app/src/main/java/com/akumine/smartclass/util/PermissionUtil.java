package com.akumine.smartclass.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class PermissionUtil {

    public PermissionUtil() {
        throw new AssertionError("Do not initialize constructor");
    }

    public static final int REQUEST_CODE_WRITE_STORAGE = 10001;
    public static final int REQUEST_CODE_READ_STORAGE = 10002;
    public static final int REQUEST_CODE_CAMERA = 10003;

    public static final String PERMISSION_WRITE_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    public static final String PERMISSION_READ_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
    public static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;

    public static void requestPermissionsOnRuntime(Activity activity) {
        int camera = ContextCompat.checkSelfPermission(activity,
                Manifest.permission.CAMERA);
        int write_storage = ContextCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int read_storage = ContextCompat.checkSelfPermission(activity,
                Manifest.permission.READ_EXTERNAL_STORAGE);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (camera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (write_storage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (read_storage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(activity,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                    Constant.REQUEST_CODE_MULTIPLE_PERMISSIONS);
        }
    }

    public static boolean isWritePermissionToExternalStorageGranted(Activity activity) {
        return checkAndRequestPermission(activity, PERMISSION_WRITE_STORAGE, REQUEST_CODE_WRITE_STORAGE);
    }

    public static boolean isReadPermissionFromExternalStorageGranted(Activity activity) {
        return checkAndRequestPermission(activity, PERMISSION_READ_STORAGE, REQUEST_CODE_READ_STORAGE);
    }

    public static boolean isCameraPermissionGranted(Activity activity) {
        return checkAndRequestPermission(activity, PERMISSION_CAMERA, REQUEST_CODE_CAMERA);
    }

    private static boolean checkAndRequestPermission(Activity activity, String permission, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
                return false;
            }
        } else {
            //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }

    /**
     * Check if the user granted write permissions to the external storage.
     *
     * @param context The Context to check the permissions for.
     * @return True if granted the permissions. False otherwise.
     */
    public static boolean hasWritePermissionToExternalStorage(Context context) {
        return ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Check if the user granted read permissions from the external storage.
     *
     * @param context The Context to check the permissions for.
     * @return True if granted the permissions. False otherwise.
     */
    public static boolean hasReadPermissionToExternalStorage(Context context) {
        return ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Check if the user granted camera permissions.
     *
     * @param context The Context to check the permissions for.
     * @return True if granted the permissions. False otherwise.
     */
    public static boolean hasCameraPermission(Context context) {
        return ContextCompat.checkSelfPermission(context,
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }
}

package com.t4app.t4everandroid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;

public class CameraPermissionManager {
    private static final String CAMERA_PERMISSION = Manifest.permission.CAMERA;

    public interface PermissionCallback {
        void onPermissionGranted();
        void onPermissionDenied();
        void onPermissionPermanentlyDenied();
    }

    public static boolean hasCameraPermission(Context context) {
        return ContextCompat.checkSelfPermission(context, CAMERA_PERMISSION)
                == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestCameraPermission(Activity activity,
                                               ActivityResultLauncher<String> permissionLauncher,
                                               PermissionCallback callback) {
        if (hasCameraPermission(activity)) {
            callback.onPermissionGranted();
            return;
        }

        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, CAMERA_PERMISSION)) {
            showPermissionRationale(activity, permissionLauncher, callback);
        } else {
            permissionLauncher.launch(CAMERA_PERMISSION);
        }
    }

    public static void requestCameraPermission(Fragment fragment,
                                               ActivityResultLauncher<String> permissionLauncher,
                                               PermissionCallback callback) {
        if (hasCameraPermission(fragment.requireContext())) {
            callback.onPermissionGranted();
            return;
        }

        if (fragment.shouldShowRequestPermissionRationale(CAMERA_PERMISSION)) {
            showPermissionRationale(fragment.requireContext(), permissionLauncher, callback);
        } else {
            permissionLauncher.launch(CAMERA_PERMISSION);
        }
    }

    public static void handlePermissionResult(boolean isGranted,
                                              Context context,
                                              PermissionCallback callback) {
        if (isGranted) {
            callback.onPermissionGranted();
        } else {
            if (context instanceof Activity) {
                Activity activity = (Activity) context;
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, CAMERA_PERMISSION)) {
                    callback.onPermissionDenied();
                } else {
                    callback.onPermissionPermanentlyDenied();
                }
            } else {
                callback.onPermissionDenied();
            }
        }
    }

    private static void showPermissionRationale(Context context,
                                                ActivityResultLauncher<String> permissionLauncher,
                                                PermissionCallback callback) {
        new AlertDialog.Builder(context)
                .setTitle(R.string.camera_permission_required)
                .setMessage(R.string.msg_camera_permission)
                .setPositiveButton(R.string.allow, (dialog, which) ->
                        permissionLauncher.launch(CAMERA_PERMISSION))
                .setNegativeButton(context.getString(R.string.camera), (dialog, which) ->
                        callback.onPermissionDenied())
                .setCancelable(false)
                .show();
    }

    public static void showPermanentlyDeniedDialog(Context context) {
        new AlertDialog.Builder(context)
                .setTitle(R.string.permission_required)
                .setMessage(context.getString(R.string.you_have_permanently_denied_permission) +
                        context.getString(R.string.please_enable_camera_permission_in_the_app_settings))
                .setPositiveButton(R.string.open_settings, (dialog, which) ->
                        openAppSettings(context))
                .setNegativeButton(context.getString(R.string.camera), null)
                .setCancelable(false)
                .show();
    }

    private static void openAppSettings(Context context) {
        try {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", context.getPackageName(), null);
            intent.setData(uri);
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, R.string.error_opening_settings, Toast.LENGTH_SHORT).show();
        }
    }
}

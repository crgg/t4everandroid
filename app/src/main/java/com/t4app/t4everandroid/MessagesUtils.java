package com.t4app.t4everandroid;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.t4app.t4everandroid.Login.T4EverLoginActivity;
import com.t4app.t4everandroid.main.T4EverMainActivity;

import java.io.Serializable;
import java.util.Objects;

public class MessagesUtils {
    private static boolean isDialogShowing = false;
    private static AlertDialog currentDialog;

    public static boolean isIsDialogShowing() {
        return isDialogShowing;
    }

    public static AlertDialog getCurrentDialog() {
        return currentDialog;
    }

    public static void setIsDialogShowing(boolean isDialogShowing) {
        MessagesUtils.isDialogShowing = isDialogShowing;
    }

    public static void setCurrentDialog(AlertDialog currentDialog) {
        MessagesUtils.currentDialog = currentDialog;
    }

    public static void showErrorDialog(Context context, String errorMessage) {
        if (errorMessage == null){
            return;
        }
        if (errorMessage.toUpperCase().contains("CREDENTIALS NOT FOUND") && context instanceof Activity){
            Activity activity = (Activity) context;
            SessionManager sessionManager = SessionManager.getInstance();
            showCredentialsError(sessionManager, activity);
            return;
        }
        if (isDialogShowing){
            return;
        }
        isDialogShowing = true;

        if (currentDialog != null && currentDialog.isShowing()) {
            currentDialog.dismiss();
        }

        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            Activity activity = (Activity) context;
            LayoutInflater inflater = activity.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.normal_error_layout, null);
            builder.setView(dialogView)
                    .setCancelable(false);

            currentDialog = builder.create();
            Objects.requireNonNull(currentDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            TextView tvMessage = dialogView.findViewById(R.id.tvMessage);
            Button ok_btn = dialogView.findViewById(R.id.ok_btn);

            tvMessage.setText(errorMessage);

            ok_btn.setOnClickListener(new SafeClickListener() {
                @Override
                public void onSafeClick(View v) {
                    currentDialog.dismiss();
                    isDialogShowing = false;
                }
            });

            currentDialog.show();
        } catch (Exception e) {
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
            isDialogShowing = false;
        }
    }

    public static void showSuccessDialog(Context context, String successMessage) {
        if (successMessage.toUpperCase().contains("CREDENTIALS NOT FOUND") && context instanceof Activity){
            Activity activity = (Activity) context;
            SessionManager sessionManager = SessionManager.getInstance();
            showCredentialsError(sessionManager, activity);
            return;
        }
        if (isDialogShowing){
            return;
        }
        isDialogShowing = true;

        if (currentDialog != null && currentDialog.isShowing()) {
            currentDialog.dismiss();
        }

        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            Activity activity = (Activity) context;
            LayoutInflater inflater = activity.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.success_message_layout, null);
            builder.setView(dialogView)
                    .setCancelable(false);

            currentDialog = builder.create();
            Objects.requireNonNull(currentDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            TextView tvMessage = dialogView.findViewById(R.id.tvMessage);
            Button ok_btn = dialogView.findViewById(R.id.ok_btn);

            tvMessage.setText(successMessage);

            ok_btn.setOnClickListener(new SafeClickListener() {
                @Override
                public void onSafeClick(View v) {
                    currentDialog.dismiss();
                    isDialogShowing = false;
                }
            });

            currentDialog.show();
        } catch (Exception e) {
            Toast.makeText(context, successMessage, Toast.LENGTH_LONG).show();
            isDialogShowing = false;
        }
    }


    public static void showSuccessDialogListener(Context context, String successMessage, ListenersUtils.ConfirmationCallback listener) {
        if (successMessage.toUpperCase().contains("CREDENTIALS NOT FOUND") && context instanceof Activity){
            Activity activity = (Activity) context;
            SessionManager sessionManager = SessionManager.getInstance();
            showCredentialsError(sessionManager, activity);
            return;
        }
        if (isDialogShowing){
            return;
        }
        isDialogShowing = true;

        if (currentDialog != null && currentDialog.isShowing()) {
            currentDialog.dismiss();
        }

        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            Activity activity = (Activity) context;
            LayoutInflater inflater = activity.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.success_message_layout, null);
            builder.setView(dialogView)
                    .setCancelable(false);

            currentDialog = builder.create();
            Objects.requireNonNull(currentDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            TextView tvMessage = dialogView.findViewById(R.id.tvMessage);
            Button ok_btn = dialogView.findViewById(R.id.ok_btn);

            tvMessage.setText(successMessage);

            ok_btn.setOnClickListener(new SafeClickListener() {
                @Override
                public void onSafeClick(View v) {
                    currentDialog.dismiss();
                    isDialogShowing = false;
                    listener.onResult(true);
                }
            });

            currentDialog.show();
        } catch (Exception e) {
            Toast.makeText(context, successMessage, Toast.LENGTH_LONG).show();
            isDialogShowing = false;
        }
    }

    public static void showMessageAndFinish(Context context, String errorMessage) {
        if (isDialogShowing){
            return;
        }
        isDialogShowing = true;

        if (currentDialog != null && currentDialog.isShowing()) {
            currentDialog.dismiss();
        }

        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            Activity activity = (Activity) context;
            LayoutInflater inflater = activity.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.success_message_layout, null);
            builder.setView(dialogView)
                    .setCancelable(false);

            currentDialog = builder.create();
            Objects.requireNonNull(currentDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            TextView tvMessage = dialogView.findViewById(R.id.tvMessage);
            Button ok_btn = dialogView.findViewById(R.id.ok_btn);

            tvMessage.setText(errorMessage);

            ok_btn.setOnClickListener(new SafeClickListener() {
                @Override
                public void onSafeClick(View v) {
                    currentDialog.dismiss();
                    isDialogShowing = false;
                    activity.finish();
                }
            });

            currentDialog.show();
        } catch (Exception e) {
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
            isDialogShowing = false;
        }
    }


    public static void showMessageErrorAndFinish(Context context, String errorMessage) {
        if (isDialogShowing){
            return;
        }
        isDialogShowing = true;

        if (currentDialog != null && currentDialog.isShowing()) {
            currentDialog.dismiss();
        }

        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            Activity activity = (Activity) context;
            LayoutInflater inflater = activity.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.normal_error_layout, null);
            builder.setView(dialogView)
                    .setCancelable(false);

            currentDialog = builder.create();
            Objects.requireNonNull(currentDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            TextView tvMessage = dialogView.findViewById(R.id.tvMessage);
            Button ok_btn = dialogView.findViewById(R.id.ok_btn);

            tvMessage.setText(errorMessage);

            ok_btn.setOnClickListener(new SafeClickListener() {
                @Override
                public void onSafeClick(View v) {
                    currentDialog.dismiss();
                    isDialogShowing = false;
                    activity.finish();
                }
            });

            currentDialog.show();
        } catch (Exception e) {
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
            isDialogShowing = false;
        }
    }

    public static void showMessageConfirmation(Context context, String errorMessage, ListenersUtils.ConfirmationCallback callback) {
        if (isDialogShowing) {
            callback.onResult(false);
            return;
        }

        isDialogShowing = true;

        if (currentDialog != null && currentDialog.isShowing()) {
            currentDialog.dismiss();
        }

        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            Activity activity = (Activity) context;
            LayoutInflater inflater = activity.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.confirmation_message_layout, null);
            builder.setView(dialogView)
                    .setCancelable(false);

            currentDialog = builder.create();
            Objects.requireNonNull(currentDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            TextView tvMessage = dialogView.findViewById(R.id.tvMessage);
            Button okBtn = dialogView.findViewById(R.id.ok_btn);
            Button cancelBtn = dialogView.findViewById(R.id.cancel_btn);

            tvMessage.setText(errorMessage);

            okBtn.setOnClickListener(new SafeClickListener() {
                @Override
                public void onSafeClick(View v) {
                    currentDialog.dismiss();
                    isDialogShowing = false;
                    callback.onResult(true);
                }
            });

            cancelBtn.setOnClickListener(new SafeClickListener() {
                @Override
                public void onSafeClick(View v) {
                    currentDialog.dismiss();
                    isDialogShowing = false;
                    callback.onResult(false);
                }
            });

            currentDialog.show();

        } catch (Exception e) {
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
            isDialogShowing = false;
            callback.onResult(false);
        }
    }

    public static void showMessageFinishAndReturnBool(Context context,
                                                      String message) {
        if (isDialogShowing){
            return;
        }
        isDialogShowing = true;

        if (currentDialog != null && currentDialog.isShowing()) {
            currentDialog.dismiss();
        }

        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            Activity activity = (Activity) context;
            LayoutInflater inflater = activity.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.success_message_layout, null);
            builder.setView(dialogView)
                    .setCancelable(false);

            currentDialog = builder.create();
            Objects.requireNonNull(currentDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            TextView tvMessage = dialogView.findViewById(R.id.tvMessage);
            Button ok_btn = dialogView.findViewById(R.id.ok_btn);

            tvMessage.setText(message);

            ok_btn.setOnClickListener(new SafeClickListener() {
                @Override
                public void onSafeClick(View v) {
                    currentDialog.dismiss();
                    isDialogShowing = false;
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("update", true);
                    activity.setResult(RESULT_OK, resultIntent);
                    activity.finish();
                }
            });

            currentDialog.show();
        } catch (Exception e) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            currentDialog.dismiss();
            isDialogShowing = false;
            Intent resultIntent = new Intent();
            resultIntent.putExtra("update", true);
            Activity activity = (Activity) context;
            activity.setResult(RESULT_OK, resultIntent);
            activity.finish();
            isDialogShowing = false;
        }
    }

    public static void showMessageFinishAndReturn(Context context,
                                                  String errorMessage,
                                                  String type,
                                                  Serializable data) {
        if (isDialogShowing){
            return;
        }
        isDialogShowing = true;

        if (currentDialog != null && currentDialog.isShowing()) {
            currentDialog.dismiss();
        }

        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            Activity activity = (Activity) context;
            LayoutInflater inflater = activity.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.success_message_layout, null);
            builder.setView(dialogView)
                    .setCancelable(false);

            currentDialog = builder.create();
            Objects.requireNonNull(currentDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            TextView tvMessage = dialogView.findViewById(R.id.tvMessage);
            Button ok_btn = dialogView.findViewById(R.id.ok_btn);

            tvMessage.setText(errorMessage);

            ok_btn.setOnClickListener(new SafeClickListener() {
                @Override
                public void onSafeClick(View v) {
                    currentDialog.dismiss();
                    isDialogShowing = false;
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("type", type);
                    resultIntent.putExtra("data", data);
                    activity.setResult(RESULT_OK, resultIntent);
                    activity.finish();
                }
            });

            currentDialog.show();
        } catch (Exception e) {
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
            isDialogShowing = false;
        }
    }

    public static void showCredentialsError(SessionManager sessionManager, Activity activity) {
        if (isDialogShowing){
            return;
        }
        isDialogShowing = true;
        if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
            if (activity != null) {
                Toast.makeText(activity.getApplicationContext(), activity.getString(R.string.authentication_is_expired), Toast.LENGTH_SHORT).show();
            }
            sessionManager.clearSession();

            Intent intent = new Intent(activity, T4EverLoginActivity.class);
            Activity activityMain = T4EverMainActivity.getInstance();
            activityMain.startActivity(intent);
            activityMain.finish();
            isDialogShowing = false;
            return;
        }

        activity.runOnUiThread(() -> {

            if (currentDialog != null && currentDialog.isShowing()) {
                currentDialog.dismiss();
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            LayoutInflater inflater = activity.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.normal_error_layout, null);
            builder.setView(dialogView)
                    .setCancelable(false);

            currentDialog = builder.create();
            Objects.requireNonNull(currentDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            TextView tvMessage = dialogView.findViewById(R.id.tvMessage);
            Button ok_btn = dialogView.findViewById(R.id.ok_btn);

            tvMessage.setText(R.string.authentication_is_expired);

            ok_btn.setOnClickListener(new SafeClickListener() {
                @Override
                public void onSafeClick(View v) {
                    currentDialog.dismiss();
                    sessionManager.clearSession();
                    isDialogShowing = false;

                    Intent intent = new Intent(activity, T4EverLoginActivity.class);
                    activity.startActivity(intent);
                    activity.finish();
                }
            });

            currentDialog.show();
        });
    }
}

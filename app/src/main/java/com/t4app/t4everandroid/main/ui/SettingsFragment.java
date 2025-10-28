package com.t4app.t4everandroid.main.ui;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.JsonObject;
import com.t4app.t4everandroid.AppController;
import com.t4app.t4everandroid.ErrorUtils;
import com.t4app.t4everandroid.ListenersUtils;
import com.t4app.t4everandroid.Login.models.User;
import com.t4app.t4everandroid.MessagesUtils;
import com.t4app.t4everandroid.R;
import com.t4app.t4everandroid.SafeClickListener;
import com.t4app.t4everandroid.SessionManager;
import com.t4app.t4everandroid.databinding.FragmentSettingsBinding;
import com.t4app.t4everandroid.main.GlobalDataCache;
import com.t4app.t4everandroid.main.Models.ResponseUpdateProfile;
import com.t4app.t4everandroid.network.ApiServices;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;

    public SettingsFragment() {

    }

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SessionManager sessionManager = SessionManager.getInstance();
        if (sessionManager.getEmailVerifiedAt() == null){
            binding.itemEmailVerification.messageVerified.setText(R.string.txt_email_not_verified);
            binding.itemEmailVerification.messageVerified.setTextColor(ContextCompat.
                    getColorStateList(requireContext(), R.color.brown));

            binding.itemEmailVerification.mainContainer.setBackgroundTintList(ContextCompat.
                    getColorStateList(requireContext(),R.color.email_not_verified_bg));
            binding.itemEmailVerification.resendEmailBtn.setBackgroundTintList(ContextCompat.
                    getColorStateList(requireContext(),R.color.alert_email_not_verified));

            binding.itemEmailVerification.emailValue.setText(sessionManager.getUserEmail());
            binding.itemEmailVerification.emailValue.setTextColor(ContextCompat.
                    getColorStateList(requireContext(), R.color.brown));

            binding.itemEmailVerification.txtEmailVerified.setText(R.string.resend_email);
            binding.itemEmailVerification.changeEmailBtn.setVisibility(View.VISIBLE);

            binding.itemEmailVerification.ivEmailVerified.setImageResource(R.drawable.ic_email_not_verified);

            binding.itemEmailVerification.resendEmailBtn.setOnClickListener(new SafeClickListener() {
                @Override
                public void onSafeClick(View v) {
                    resendEmail();
                }
            });

            binding.itemEmailVerification.changeEmailBtn.setOnClickListener(new SafeClickListener() {
                @Override
                public void onSafeClick(View v) {
                    showChangeCharges(sessionManager.getUserEmail(), newEmail -> {
                        Map<String, Object> data = new HashMap<>();
                        data.put("email", newEmail);
                        updateProfile(data, user -> {
                            sessionManager.setUserEmail(user.getEmail());
                            binding.itemEmailVerification.emailValue.setText(user.getEmail());
                        });
                    });
                }
            });

        }else{
            binding.itemEmailVerification.messageVerified.setText(R.string.txt_email_verified);
            binding.itemEmailVerification.messageVerified.setTextColor(ContextCompat.
                    getColorStateList(requireContext(), R.color.email_verified_txt));

            binding.itemEmailVerification.mainContainer.setBackgroundTintList(ContextCompat.
                    getColorStateList(requireContext(),R.color.email_verified_bg));
            binding.itemEmailVerification.resendEmailBtn.setBackgroundTintList(ContextCompat.
                    getColorStateList(requireContext(),R.color.alert_email_verified));

            binding.itemEmailVerification.emailValue.setText(sessionManager.getUserEmail());
            binding.itemEmailVerification.emailValue.setTextColor(ContextCompat.
                    getColorStateList(requireContext(), R.color.email_verified_txt));

            binding.itemEmailVerification.txtEmailVerified.setText(R.string.email_verified);
            binding.itemEmailVerification.changeEmailBtn.setVisibility(View.GONE);
            binding.itemEmailVerification.ivEmailVerified.setImageResource(R.drawable.ic_email_verified);
        }
    }

    private void resendEmail(){
        ApiServices apiServices = AppController.getApiServices();
        Call<JsonObject> call = apiServices.resendEmail();
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject body = response.body();
                    if (body != null) {
                        if (body.get("status").getAsBoolean()) {
                            if (body.has("msg")) {
                                String message = body.get("msg").getAsString();
                                MessagesUtils.showSuccessDialog(requireActivity(), message);
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                MessagesUtils.showErrorDialog(requireActivity(), ErrorUtils.parseError(throwable));
            }
        });
    }

    private void updateProfile(Map<String,Object> data, ListenersUtils.OnUserUpdateListener listener){
        ApiServices apiServices = AppController.getApiServices();
        Call<ResponseUpdateProfile> call = apiServices.updateProfile(data);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ResponseUpdateProfile> call, @NonNull Response<ResponseUpdateProfile> response) {
                if (response.isSuccessful()){
                    ResponseUpdateProfile body = response.body();
                    if (body != null){
                        if (body.isStatus()){
                            if (body.getData() != null){
                                listener.onResult(body.getData());
                                MessagesUtils.showSuccessDialog(requireActivity(), body.getMessage());
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseUpdateProfile> call, @NonNull Throwable throwable) {
                MessagesUtils.showErrorDialog(requireActivity(), ErrorUtils.parseError(throwable));
            }
        });
    }

    private void showChangeCharges(String email, ListenersUtils.OnEmailChangeListener listener) {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View dialogView = inflater.inflate(R.layout.edit_user_email, null);

        EditText editText = dialogView.findViewById(R.id.editFieldInput);
        editText.setText(email);

        Button saveButton = dialogView.findViewById(R.id.ok_btn);
        Button cancelBtn = dialogView.findViewById(R.id.cancelBtn);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create();

        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.setOnShowListener(d -> {
            cancelBtn.setOnClickListener(new SafeClickListener() {
                @Override
                public void onSafeClick(View v) {
                    dialog.dismiss();
                }
            });

            saveButton.setOnClickListener(new SafeClickListener() {
                @Override
                public void onSafeClick(View v) {
                    String newEmail = editText.getText().toString().trim();
                    if (newEmail.isEmpty()) {
                        editText.setError(getString(R.string.email_is_required));
                    } else {
                        listener.onEmailChanged(newEmail);
                        dialog.dismiss();
                    }
                }
            });
        });

        dialog.show();
    }
}
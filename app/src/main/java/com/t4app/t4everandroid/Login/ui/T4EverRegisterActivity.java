package com.t4app.t4everandroid.Login.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.JsonObject;
import com.t4app.t4everandroid.AppController;
import com.t4app.t4everandroid.AppUtils;
import com.t4app.t4everandroid.ErrorUtils;
import com.t4app.t4everandroid.MessagesUtils;
import com.t4app.t4everandroid.R;
import com.t4app.t4everandroid.SafeClickListener;
import com.t4app.t4everandroid.databinding.ActivityT4EverRegisterBinding;
import com.t4app.t4everandroid.network.ApiServices;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class T4EverRegisterActivity extends AppCompatActivity {
    private static final String TAG = "REGISTER_ACT";

    private ActivityT4EverRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_t4_ever_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding = ActivityT4EverRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.signIn.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                Intent intent = new Intent(T4EverRegisterActivity.this, T4EverLoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        binding.nameValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                binding.nameLayout.setError(null);
            }
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
        });

        binding.emailValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                binding.emailLayout.setError(null);
            }
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
        });

        binding.passwordValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                binding.passwordLayout.setError(null);
            }
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
        });

        binding.rePasswordValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                binding.rePasswordLayout.setError(null);
            }
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
        });


        binding.createAccountBtn.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                if (binding.acceptTermsAndPolicy.isChecked()){
                    if (validateInputs()){
                        String name = binding.nameValue.getText().toString().trim();
                        String email = binding.emailValue.getText().toString().trim();
                        String password = binding.passwordValue.getText().toString().trim();
                        String rePassword = binding.rePasswordValue.getText().toString().trim();

                        Map<String, Object> data = new HashMap<>();
                        data.put("name", name);
                        data.put("email", email);
                        data.put("password", password);
                        data.put("password_confirmation", rePassword);

                        register(data);
                    }
                }else{
                    MessagesUtils.showErrorDialog(T4EverRegisterActivity.this,
                            getString(R.string.you_must_accept_the_terms_and_conditions_before_continuing));
                }
            }
        });
    }

    private void register(Map<String, Object> data){
        ApiServices apiServices = AppController.getApiServices();
        Call<JsonObject> call = apiServices.register(data);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()){
                    JsonObject body = response.body();
                    if (body != null){
                        if (body.has("status")){
                            if (body.get("status").getAsBoolean()){
                                if (body.has("msg")){
                                    String msg = body.get("msg").getAsString();
                                    if (msg != null){
                                        MessagesUtils.showSuccessDialogListener(T4EverRegisterActivity.this,
                                            msg, confirmed -> {
                                                Intent intent = new Intent(T4EverRegisterActivity.this,
                                                        T4EverLoginActivity.class);
                                                startActivity(intent);
                                                finish();
                                            });
                                    }
                                }
                            }else{
                                Log.d(TAG, "ENTRY EN ELSE RESPONSE");
                                if (body.has("data")){
                                    MessagesUtils.showErrorDialog(T4EverRegisterActivity.this,
                                            ErrorUtils.extractMsgErrors(body.get("data").getAsJsonObject().get("errors").getAsJsonObject(),
                                                    T4EverRegisterActivity.this));
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                Log.e(TAG, "ENTRY IN FAILURE: " + throwable.getMessage());
                String error = ErrorUtils.parseError(throwable);
                try {
                    if (error.contains("email has already been")){
                        binding.emailLayout.setError(error);
                        binding.emailValue.requestFocus();
                    }else{
                        MessagesUtils.showErrorDialog(T4EverRegisterActivity.this, error);
                    }
                }catch (Exception e){
                    MessagesUtils.showErrorDialog(T4EverRegisterActivity.this, throwable.getMessage());
                }
            }
        });
    }

    private boolean validateInputs() {
        boolean isValid = true;

        binding.nameLayout.setError(null);
        binding.emailLayout.setError(null);
        binding.passwordLayout.setError(null);
        binding.rePasswordLayout.setError(null);

        String name = binding.nameValue.getText().toString().trim();
        String email = binding.emailValue.getText().toString().trim();
        String password = binding.passwordValue.getText().toString().trim();
        String rePassword = binding.rePasswordValue.getText().toString().trim();

        if (name.isEmpty()) {
            binding.nameLayout.setError(getString(R.string.name_is_required));
            binding.nameValue.requestFocus();
            isValid = false;
        }

        if (email.isEmpty()) {
            binding.emailLayout.setError(getString(R.string.email_is_required));
            if (isValid) binding.emailValue.requestFocus();
            isValid = false;
        } else if (!AppUtils.isValidEmail(email)) {
            binding.emailLayout.setError(getString(R.string.the_email_must_be_a_valid_email_address));
            if (isValid) binding.emailValue.requestFocus();
            isValid = false;
        }


        if (password.isEmpty()) {
            binding.passwordLayout.setError(getString(R.string.password_is_required));
            if (isValid) binding.passwordValue.requestFocus();
            isValid = false;
        } else if (password.length() < 8) {
            binding.passwordLayout.setError(getString(R.string.password_must_be_at_least_8_characters));
            if (isValid) binding.passwordValue.requestFocus();
            isValid = false;
        }

        if (rePassword.isEmpty()) {
            binding.rePasswordLayout.setError(getString(R.string.confirm_password_is_required));
            if (isValid) binding.rePasswordValue.requestFocus();
            isValid = false;
        } else if (!rePassword.equals(password)) {
            binding.rePasswordLayout.setError(getString(R.string.passwords_do_not_match));
            if (isValid) binding.rePasswordValue.requestFocus();
            isValid = false;
        }

        return isValid;
    }

}
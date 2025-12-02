package com.t4app.t4everandroid.Login.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;
import androidx.credentials.exceptions.GetCredentialProviderConfigurationException;
import androidx.credentials.exceptions.NoCredentialException;

import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.t4app.t4everandroid.AppController;
import com.t4app.t4everandroid.AppUtils;
import com.t4app.t4everandroid.BaseActivity;
import com.t4app.t4everandroid.ErrorUtils;
import com.t4app.t4everandroid.Login.models.Token;
import com.t4app.t4everandroid.Login.models.User;
import com.t4app.t4everandroid.MessagesUtils;
import com.t4app.t4everandroid.R;
import com.t4app.t4everandroid.SafeClickListener;
import com.t4app.t4everandroid.SessionManager;
import com.t4app.t4everandroid.databinding.ActivityT4EverLoginBinding;
import com.t4app.t4everandroid.main.T4EverMainActivity;
import com.t4app.t4everandroid.network.ApiConfig;
import com.t4app.t4everandroid.network.ApiServices;
import com.t4app.t4everandroid.network.responses.LoginResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class T4EverLoginActivity extends BaseActivity {
    private static final String TAG = "LOGIN_ACT";
    private ActivityT4EverLoginBinding binding;

    private CredentialManager credentialManager;
    private final Executor executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_t4_ever_login);

        binding = ActivityT4EverLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.signUp.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                Intent intent = new Intent(T4EverLoginActivity.this, T4EverRegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        credentialManager = CredentialManager.create(this);

        binding.signIn.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                if (validateData()){
                    String email = binding.emailValue.getText().toString().trim();
                    String password = binding.passwordValue.getText().toString().trim();

                    Map<String, Object> data = new HashMap<>();

                    data.put("email", email);
                    data.put("password", password);

                    login(data);
                }
            }
        });

        binding.signInWithGoogle.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                signInWithGoogle(true);
            }
        });
    }

    private void signInWithGoogle(boolean firstAttempt){
        Log.d(TAG, "signInWithGoogle: ");
        GetSignInWithGoogleOption googleOption =
                new GetSignInWithGoogleOption.Builder(ApiConfig.CLIENT_ID).build();

        GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(ApiConfig.CLIENT_ID)
                .setAutoSelectEnabled(false)
                .build();

        GetCredentialRequest request;
        if (firstAttempt){
            request = new GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build();
        }else{
            request = new GetCredentialRequest.Builder()
                    .addCredentialOption(googleOption)
                    .build();
        }

        try {
            credentialManager.getCredentialAsync(
                    this,
                    request,
                    null,
                    ContextCompat.getMainExecutor(this),
                    new CredentialManagerCallback<>() {
                        @Override
                        public void onResult(GetCredentialResponse response) {
                            handleSignIn(response);
                        }

                        @Override
                        public void onError(@NonNull GetCredentialException e) {
                            Log.e(TAG, "onError: " + e.getMessage(), e);
                            if(e instanceof NoCredentialException){
                                if (firstAttempt){
                                    new Handler(Looper.getMainLooper()).postDelayed(
                                            () -> signInWithGoogle(false), 300
                                    );
                                }else{
                                    runOnUiThread(() -> {
                                        MessagesUtils.showErrorDialog(T4EverLoginActivity.this,
                                                getString(R.string.your_device_does_not_support_quick_start_with_google_please_use_manual_login));
                                    });
                                }
                            }else if (e instanceof GetCredentialProviderConfigurationException){
                                Log.e(TAG, "PROVIDER ERROR NO CONFIG: " + e.getMessage(), e);
                            }else{
                                runOnUiThread(() -> {
                                    MessagesUtils.showErrorDialog(T4EverLoginActivity.this,
                                            getString(R.string.error_starting_session));
                                });
                                Log.e(TAG, "ERROR START SESSION" + e.getMessage(), e);
                            }
                        }
                    });
        }catch (Exception e){
            Log.e(TAG, "run: ", e);
        }
    }


    private void handleSignIn(GetCredentialResponse result) {
        Credential credential = result.getCredential();

        if (credential.getType().equals(GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL)) {
            GoogleIdTokenCredential googleIdCredential = GoogleIdTokenCredential.createFrom(credential.getData());

            String id = googleIdCredential.getId();
            String name = googleIdCredential.getDisplayName();
            String email = googleIdCredential.getId();
            String picture = googleIdCredential.getProfilePictureUri() != null
                    ? googleIdCredential.getProfilePictureUri().toString()
                    : "";

            Log.d(TAG, "handleSignIn: " + id + name + email + picture);
            String token = googleIdCredential.getIdToken();
            if (!token.isEmpty()){
                Map<String, Object> data = new HashMap<>();
                data.put("code", token);
                loginWithGoogle(data);
            }
        }
    }

    private void loginWithGoogle(Map<String,Object> data){
        ApiServices apiServices = AppController.getApiServices();
        Call<LoginResponse> call = apiServices.loginWithGoogle(data);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    LoginResponse body = response.body();
                    if (body != null) {
                        if (body.isStatus()) {
                            Intent intent = new Intent(T4EverLoginActivity.this, T4EverMainActivity.class);
                            SessionManager sessionManager = SessionManager.getInstance();
                            Token token = body.getData();
                            User user = token.getUser();
                            sessionManager.saveUserDetails(
                                    user.getId(),
                                    user.getName(),
                                    user.getEmail(),
                                    user.getAvatarUrl(),
                                    token.getToken(),
                                    user.getEmailVerifiedAt(),
                                    true,
                                    binding.rememberMe.isChecked());
                            startActivity(intent);
                            finish();
                        } else {
                            if (body.getError() != null) {
                                MessagesUtils.showErrorDialog(T4EverLoginActivity.this,
                                        ErrorUtils.parseErrorApi(body.getError()));
                            } else {
                                if (body.getMsg() != null) {
                                    MessagesUtils.showErrorDialog(T4EverLoginActivity.this, body.getMsg());
                                } else {
                                    MessagesUtils.showErrorDialog(T4EverLoginActivity.this,
                                            getString(R.string.unknown_error_while_attempting_to_log_in));
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable throwable) {
                MessagesUtils.showErrorDialog(T4EverLoginActivity.this, ErrorUtils.parseError(throwable));
            }
        });
    }

    private void login(Map<String, Object> data){
        ApiServices apiServices = AppController.getApiServices();
        Call<LoginResponse> call = apiServices.login(data);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()){
                    LoginResponse body = response.body();
                    if (body != null){
                        if (body.isStatus()){
                            Intent intent = new Intent(T4EverLoginActivity.this, T4EverMainActivity.class);
                            SessionManager sessionManager = SessionManager.getInstance();
                            Token token = body.getData();
                            User user = token.getUser();
                            sessionManager.saveUserDetails(
                                    user.getId(),
                                    user.getName(),
                                    user.getEmail(),
                                    user.getAvatarUrl(),
                                    token.getToken(),
                                    user.getEmailVerifiedAt(),
                                    true,
                                    binding.rememberMe.isChecked());
                            startActivity(intent);
                            finish();
                        }else{
                            if (body.getError() != null){
                                MessagesUtils.showErrorDialog(T4EverLoginActivity.this,
                                        ErrorUtils.parseErrorApi(body.getError()));
                            }else{
                                if (body.getMsg() != null){
                                    MessagesUtils.showErrorDialog(T4EverLoginActivity.this, body.getMsg());
                                }else{
                                    MessagesUtils.showErrorDialog(T4EverLoginActivity.this,
                                            getString(R.string.unknown_error_while_attempting_to_log_in));
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable throwable) {
                MessagesUtils.showErrorDialog(T4EverLoginActivity.this, ErrorUtils.parseError(throwable));
            }
        });
    }

    private boolean validateData(){
        boolean isValid = true;

        binding.emailLayout.setError(null);
        binding.passwordLayout.setError(null);

        String email = binding.emailValue.getText().toString().trim();
        String password = binding.passwordValue.getText().toString().trim();

        if (email.isEmpty()){
            binding.emailLayout.setError(getString(R.string.email_is_required));
            isValid = false;
        }else if (!AppUtils.isValidEmail(email)){
            binding.emailLayout.setError(getString(R.string.the_email_must_be_a_valid_email_address));
            binding.emailValue.requestFocus();
            isValid = false;
        }

        if (password.isEmpty()){
            binding.passwordLayout.setError(getString(R.string.password_is_required));
            if (isValid)binding.passwordValue.requestFocus();
            isValid = false;
        }

        return isValid;
    }



}
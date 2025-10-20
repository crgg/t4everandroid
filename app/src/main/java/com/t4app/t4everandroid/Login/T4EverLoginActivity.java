package com.t4app.t4everandroid.Login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.t4app.t4everandroid.AppController;
import com.t4app.t4everandroid.AppUtils;
import com.t4app.t4everandroid.ErrorUtils;
import com.t4app.t4everandroid.Login.models.LoginResponse;
import com.t4app.t4everandroid.Login.models.Token;
import com.t4app.t4everandroid.Login.models.User;
import com.t4app.t4everandroid.MessagesUtils;
import com.t4app.t4everandroid.R;
import com.t4app.t4everandroid.SafeClickListener;
import com.t4app.t4everandroid.SessionManager;
import com.t4app.t4everandroid.main.T4EverMainActivity;
import com.t4app.t4everandroid.databinding.ActivityT4EverLoginBinding;
import com.t4app.t4everandroid.network.ApiServices;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class T4EverLoginActivity extends AppCompatActivity {

    private ActivityT4EverLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_t4_ever_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

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
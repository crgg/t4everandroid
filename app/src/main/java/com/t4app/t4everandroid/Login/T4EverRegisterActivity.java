package com.t4app.t4everandroid.Login;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.t4app.t4everandroid.R;
import com.t4app.t4everandroid.databinding.ActivityT4EverLoginBinding;
import com.t4app.t4everandroid.databinding.ActivityT4EverRegisterBinding;

public class T4EverRegisterActivity extends AppCompatActivity {

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

        binding.signIn.setOnClickListener(view -> {
            Intent intent = new Intent(T4EverRegisterActivity.this, T4EverLoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
package com.t4app.t4everandroid.Login.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.t4app.t4everandroid.Login.adapter.OnboardingAdapter;
import com.t4app.t4everandroid.Login.models.OnboardingItem;
import com.t4app.t4everandroid.R;
import com.t4app.t4everandroid.databinding.ActivityT4EverOnboardingBinding;

import java.util.ArrayList;
import java.util.List;

public class T4EverOnboardingActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private Button btnNext;
    private TabLayout tabLayout;
    private OnboardingAdapter adapter;

    private ActivityT4EverOnboardingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_t4_ever_onboarding);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding = ActivityT4EverOnboardingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (hasSeenOnboarding()) {
            goToLogin();
            return;
        }

        viewPager = findViewById(R.id.viewPager);
        btnNext = findViewById(R.id.btnNext);
        tabLayout = findViewById(R.id.tabLayout);

        List<OnboardingItem> items = new ArrayList<>();
        items.add(new OnboardingItem(R.drawable.logo_t4_person, getString(R.string.welcome_to_t4ever),
                getString(R.string.first_welcome_text)));
        items.add(new OnboardingItem(R.drawable.ic_group, getString(R.string.life_questions),
                getString(R.string.second_welcome_text)));
        items.add(new OnboardingItem(R.drawable.ic_camera, getString(R.string.messages),
                getString(R.string.third_welcome_text)));
        items.add(new OnboardingItem(R.drawable.ic_chat_dashboard, getString(R.string.your_legacy_begins_now),
                getString(R.string.final_welcome_text)));

        OnboardingAdapter adapter = new OnboardingAdapter(items);
        viewPager.setAdapter(adapter);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {}).attach();

        btnNext.setOnClickListener(v -> {
            if (viewPager.getCurrentItem() + 1 < adapter.getItemCount()) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                if (viewPager.getCurrentItem() + 1 == adapter.getItemCount()){
                    btnNext.setText(R.string.finish);
                }
            } else {
                setSeenOnboarding();
                goToLogin();
            }
        });
    }

    private boolean hasSeenOnboarding() {
        return getSharedPreferences("prefs", MODE_PRIVATE).getBoolean("seen_onboarding", false);
    }

    private void setSeenOnboarding() {
        getSharedPreferences("prefs", MODE_PRIVATE)
                .edit()
                .putBoolean("seen_onboarding", true)
                .apply();
    }

    private void goToLogin() {
        startActivity(new Intent(this, T4EverLoginActivity.class));
        finish();
    }
}
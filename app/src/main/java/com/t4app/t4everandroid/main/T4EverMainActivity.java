package com.t4app.t4everandroid.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.t4app.t4everandroid.R;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;
import com.t4app.t4everandroid.Login.T4EverLoginActivity;
import com.t4app.t4everandroid.R;
import com.t4app.t4everandroid.SafeClickListener;
import com.t4app.t4everandroid.SessionManager;
import com.t4app.t4everandroid.databinding.ActivityT4EverLoginBinding;
import com.t4app.t4everandroid.databinding.ActivityT4EverMainBinding;
import com.t4app.t4everandroid.main.ui.ChatFragment;
import com.t4app.t4everandroid.main.ui.ConversationsFragment;
import com.t4app.t4everandroid.main.ui.HomeFragment;
import com.t4app.t4everandroid.main.ui.LegacyProfilesFragment;
import com.t4app.t4everandroid.main.ui.MediaFragment;
import com.t4app.t4everandroid.main.ui.MessagesFragment;
import com.t4app.t4everandroid.main.ui.QuestionsFragment;
import com.t4app.t4everandroid.main.ui.SettingsFragment;

public class T4EverMainActivity extends AppCompatActivity {

    public static T4EverMainActivity instance;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;

    private ActivityT4EverMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_t4_ever_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        instance = T4EverMainActivity.this;

        binding = ActivityT4EverMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SessionManager sessionManager = SessionManager.getInstance();

        if (!sessionManager.getIsLogged()){
            Intent intent = new Intent(T4EverMainActivity.this, T4EverLoginActivity.class);
            startActivity(intent);
            finish();
        }

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        binding.toolbar.btnMenu.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        binding.toolbar.userName.setText(sessionManager.getName());

        View navHeader = navigationView.getHeaderView(0);
        TextView name = navHeader.findViewById(R.id.name);
        TextView email = navHeader.findViewById(R.id.email);

        name.setText(sessionManager.getName());
        email.setText(sessionManager.getUserEmail());

        showFragment(new HomeFragment());

        navigationView.setNavigationItemSelectedListener(item -> {
            int id  = item.getItemId();

            if (id == R.id.nav_home) {
                showFragment(new HomeFragment());
            }else  if (id == R.id.nav_legacy_profiles) {
                showFragment(new LegacyProfilesFragment());
            }else  if (id == R.id.nav_questions) {
                showFragment(new QuestionsFragment());
            }else  if (id == R.id.nav_conversations) {
                showFragment(new ConversationsFragment());
            }else  if (id == R.id.nav_messages) {
                showFragment(new MessagesFragment());
            }else  if (id == R.id.nav_chat) {
                showFragment(new ChatFragment());
            }else  if (id == R.id.nav_media) {
                showFragment(new MediaFragment());
            }else  if (id == R.id.nav_settings) {
                showFragment(new SettingsFragment());
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    public static T4EverMainActivity getInstance() {
        return instance;
    }

    private void showFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }



}
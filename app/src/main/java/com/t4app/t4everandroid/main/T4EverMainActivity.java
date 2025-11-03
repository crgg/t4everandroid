package com.t4app.t4everandroid.main;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.JsonObject;
import com.t4app.t4everandroid.AppController;
import com.t4app.t4everandroid.ErrorUtils;
import com.t4app.t4everandroid.ListenersUtils;
import com.t4app.t4everandroid.Login.ui.T4EverLoginActivity;
import com.t4app.t4everandroid.MessagesUtils;
import com.t4app.t4everandroid.R;
import com.t4app.t4everandroid.SafeClickListener;
import com.t4app.t4everandroid.SessionManager;
import com.t4app.t4everandroid.databinding.ActivityT4EverMainBinding;
import com.t4app.t4everandroid.network.responses.ResponseGetUserInfo;
import com.t4app.t4everandroid.main.repository.UserRepository;
import com.t4app.t4everandroid.main.ui.ChatFragment;
import com.t4app.t4everandroid.main.ui.NotificationsFragment;
import com.t4app.t4everandroid.main.ui.media.MediaFragment;
import com.t4app.t4everandroid.main.ui.HomeFragment;
import com.t4app.t4everandroid.main.ui.legacyProfile.LegacyProfilesFragment;
import com.t4app.t4everandroid.main.ui.EmailFragment;
import com.t4app.t4everandroid.main.ui.questions.QuestionsFragment;
import com.t4app.t4everandroid.main.ui.SettingsFragment;
import com.t4app.t4everandroid.main.ui.UpdateProfileFragment;
import com.t4app.t4everandroid.network.ApiServices;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class T4EverMainActivity extends AppCompatActivity {

    private static final String TAG  = "MAIN_ACT";
    public static T4EverMainActivity instance;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private ActivityT4EverMainBinding binding;

    private UserRepository userRepository;

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
            return;
        }else if (!sessionManager.getRememberMe() && !sessionManager.getIsLogged()){
            Intent intent = new Intent(T4EverMainActivity.this, T4EverLoginActivity.class);
            startActivity(intent);
            finish();
            return;
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
        ImageView icon = navHeader.findViewById(R.id.icon_user);

        name.setText(sessionManager.getName());
        email.setText(sessionManager.getUserEmail());
        if(sessionManager.getAvatarUrl() != null && !sessionManager.getAvatarUrl().isEmpty()){
            String path = sessionManager.getAvatarUrl();
            if (!sessionManager.getAvatarUrl().contains("https://go2storage.s3.us-east-2.amazonaws.com")){
                path = "https://go2storage.s3.us-east-2.amazonaws.com/" + sessionManager.getAvatarUrl();
            }
            icon.setImageResource(0);
            icon.setImageDrawable(null);
            icon.setBackground(null);
            Glide.with(this)
                    .load(path)
                    .placeholder(new ColorDrawable(Color.TRANSPARENT))
                    .error(new ColorDrawable(Color.TRANSPARENT))
                    .transform(new CircleCrop())
                    .into(icon);
            Log.d(TAG, "onCreate:   " + path);
//            icon.setBackground(null);
        }
        userRepository = new UserRepository();
        getUserInfo();

        showFragment(new HomeFragment());

        MenuItem settingsItem = navigationView.getMenu().findItem(R.id.nav_settings);

        if (sessionManager.getEmailVerifiedAt() == null) {
            settingsItem.setTitle("Settings !");
        } else {
            settingsItem.setTitle("Settings");
        }


        navigationView.setNavigationItemSelectedListener(item -> {
            int id  = item.getItemId();

            if (id == R.id.nav_home) {
                showFragment(new HomeFragment());
            }else  if (id == R.id.nav_legacy_profiles) {
                showFragment(new LegacyProfilesFragment());
            }else  if (id == R.id.nav_questions) {
                showFragment(new QuestionsFragment());
            }else  if (id == R.id.nav_chat) {
                showFragment(new ChatFragment());
            }else  if (id == R.id.nav_media) {
                showFragment(new MediaFragment());
            }else  if (id == R.id.nav_notifications) {
                showFragment(new NotificationsFragment());
            }else  if (id == R.id.nav_email) {
                showFragment(new EmailFragment());
            }else  if (id == R.id.nav_settings) {
                showFragment(new SettingsFragment());
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        binding.toolbar.userContainer.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                PopupMenu popup = new PopupMenu(T4EverMainActivity.this, v);
                popup.getMenuInflater().inflate(R.menu.menu_user_actions, popup.getMenu());

                try {
                    Field mFieldPopup = popup.getClass().getDeclaredField("mPopup");
                    mFieldPopup.setAccessible(true);
                    Object menuPopupHelper = mFieldPopup.get(popup);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
//
//                    Method getPopup = classPopupHelper.getMethod("getPopup");
//                    Object popupWindow = getPopup.invoke(menuPopupHelper);
//                    if (popupWindow instanceof android.widget.PopupWindow) {
//                        ((android.widget.PopupWindow) popupWindow).setBackgroundDrawable(new ColorDrawable(Color.WHITE));
//                    }
                } catch (Exception e) {
                    Log.e(TAG, "onSafeClick: ", e);
                }

                MenuItem settingsItem = popup.getMenu().findItem(R.id.action_sign_out);
                SpannableString s = new SpannableString(settingsItem.getTitle());
                s.setSpan(new ForegroundColorSpan(Color.RED), 0, s.length(), 0);
                settingsItem.setTitle(s);

                MenuItem updateItem = popup.getMenu().findItem(R.id.action_update_profile);
                SpannableString sI = new SpannableString(settingsItem.getTitle());
                sI.setSpan(new ForegroundColorSpan(Color.BLACK), 0, sI.length(), 0);
                updateItem.setTitle(sI);

                popup.setOnMenuItemClickListener(item -> {
                    int id = item.getItemId();
                    if(id == R.id.action_update_profile){
                        showFragment(new UpdateProfileFragment());
                        return true;
                    }else if (id == R.id.action_sign_out){
                        logout(confirmed -> {
                            if (confirmed){
                                Log.d(TAG, "ON CLEAR: ");
                                sessionManager.clearSession();
                                Intent intent = new Intent(T4EverMainActivity.this, T4EverLoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                        return true;
                    }
                    return false;
                });
                popup.show();
            }
        });
    }

    private void logout(ListenersUtils.ConfirmationCallback callback){
        ApiServices apiServices = AppController.getApiServices();
        Call<JsonObject> call = apiServices.logout();
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()){
                    JsonObject body = response.body();
                    if (body != null){
                        if (body.get("status").getAsBoolean()){
                            GlobalDataCache.clearData();
                            callback.onResult(true);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                MessagesUtils.showErrorDialog(T4EverMainActivity.this, ErrorUtils.parseError(throwable));
            }
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

    private void getUserInfo(){
        ApiServices apiServices = AppController.getApiServices();
        Call<ResponseGetUserInfo> call = apiServices.getUserInfo();
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseGetUserInfo> call, Response<ResponseGetUserInfo> response) {
                if (response.isSuccessful()) {
                    ResponseGetUserInfo body = response.body();
                    if (body != null && body.isStatus()) {
                        Log.d(TAG, "ENTRY HERE GET INFO OK ");
                        userRepository.updateUser(body.getData().getUser());
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseGetUserInfo> call, Throwable throwable) {
                Log.d(TAG, "onFailure: GET USER " + throwable.getMessage());
            }
        });
    }

    public void selectNavItem(int itemId) {
        Menu menu = navigationView.getMenu();
        for(int i = 0; i < menu.size(); i++){
            MenuItem item = menu.getItem(i);
            item.setChecked(item.getItemId() == itemId);
        }
    }


}
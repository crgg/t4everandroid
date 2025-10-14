package com.t4app.t4everandroid.main.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.t4app.t4everandroid.AppController;
import com.t4app.t4everandroid.R;
import com.t4app.t4everandroid.SafeClickListener;
import com.t4app.t4everandroid.SessionManager;
import com.t4app.t4everandroid.databinding.FragmentHomeBinding;
import com.t4app.t4everandroid.network.ApiServices;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private static final String TAG = "HOME_FRAG";

    private FragmentHomeBinding binding;
    private SessionManager sessionManager;
    public HomeFragment() {}

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = SessionManager.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        binding.welcomeItem.welcomeMessage.setText(getString(R.string.welcome, sessionManager.getName()));
        //TODO:OPTIMIZE THIS CREATE GLOBAL CACHE OR SOMETHING TO SAVE STATE AND ADD IN DIFF FRAGMENTS
        getAssistants();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.legacyProfilesItem.getRoot().setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                showFragment(new LegacyProfilesFragment());
            }
        });

        binding.welcomeItem.createLegacyProfile.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                showFragment(new LegacyProfilesFragment());
            }
        });

        binding.conversationsItem.getRoot().setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                showFragment(new ConversationsFragment());
            }
        });

        binding.scheduledMessagesItem.getRoot().setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                showFragment(new MessagesFragment());
            }
        });

        binding.questionAnsweredItem.getRoot().setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                showFragment(new QuestionsFragment());
            }
        });
    }

    private void getAssistants(){
        ApiServices apiServices = AppController.getApiServices();
        Call<JsonObject> call = apiServices.getAssistants();
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject body = response.body();
                    if (body != null) {
                        if (body.has("status")) {
                            if (body.get("status").getAsBoolean()) {
                                if (body.get("data").getAsJsonArray() != null) {
                                    JsonArray jsonElements = body.get("data").getAsJsonArray();
                                    if (jsonElements.isEmpty()) {
                                        binding.conversationsItem.countConversations.setText("0");
                                        binding.legacyProfilesItem.countLegacyProfiles.setText("0");
                                        binding.questionAnsweredItem.countQuestionsAnswered.setText("0");
                                        binding.scheduledMessagesItem.countMessages.setText("0");
                                    }
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                Log.e(TAG, "onFailure Get Assistants ", throwable);
            }
        });
    }

    private void showFragment(Fragment fragment) {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}
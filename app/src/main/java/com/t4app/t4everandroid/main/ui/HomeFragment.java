package com.t4app.t4everandroid.main.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.t4app.t4everandroid.AppController;
import com.t4app.t4everandroid.R;
import com.t4app.t4everandroid.SafeClickListener;
import com.t4app.t4everandroid.SessionManager;
import com.t4app.t4everandroid.databinding.FragmentHomeBinding;
import com.t4app.t4everandroid.main.GlobalDataCache;
import com.t4app.t4everandroid.main.Models.ResponseGetAssistants;
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
        if (GlobalDataCache.legacyProfiles == null){
            getAssistants();
        }else{
            binding.legacyProfilesItem.countLegacyProfiles.setText(String.valueOf(GlobalDataCache.legacyProfiles.size()));
            binding.questionAnsweredItem.countQuestionsAnswered.setText("0");
            binding.scheduledMessagesItem.countMessages.setText("0");
            binding.conversationsItem.countConversations.setText("0");
        }

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

        binding.itemQuickActions.itemAnswerQuestions.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                showFragment(new QuestionsFragment());
            }
        });

        binding.itemQuickActions.itemRecordConversation.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                showFragment(new ConversationsFragment());
            }
        });

        binding.itemQuickActions.itemCreateMessage.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                showFragment(new MessagesFragment());
            }
        });

    }

    private void getAssistants(){
        ApiServices apiServices = AppController.getApiServices();
        Call<ResponseGetAssistants> call = apiServices.getAssistants();
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseGetAssistants> call, Response<ResponseGetAssistants> response) {
                if (response.isSuccessful()) {
                    ResponseGetAssistants body = response.body();
                    if (body != null) {
                        if (body.isStatus()) {
                            if (body.getData() != null) {
                                GlobalDataCache.legacyProfiles = body.getData();
                                if (body.getData().isEmpty()) {
                                    binding.conversationsItem.countConversations.setText("0");
                                    binding.legacyProfilesItem.countLegacyProfiles.setText("0");
                                    binding.questionAnsweredItem.countQuestionsAnswered.setText("0");
                                    binding.scheduledMessagesItem.countMessages.setText("0");
                                }else{
                                    binding.legacyProfilesItem.countLegacyProfiles.setText(String.valueOf(body.getData().size()));
                                    binding.questionAnsweredItem.countQuestionsAnswered.setText("0");
                                    binding.scheduledMessagesItem.countMessages.setText("0");
                                    binding.conversationsItem.countConversations.setText("0");
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseGetAssistants> call, Throwable throwable) {
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
package com.t4app.t4everandroid.main.ui;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.t4app.t4everandroid.AppController;
import com.t4app.t4everandroid.DateUtils;
import com.t4app.t4everandroid.ListenersUtils;
import com.t4app.t4everandroid.R;
import com.t4app.t4everandroid.SafeClickListener;
import com.t4app.t4everandroid.SessionManager;
import com.t4app.t4everandroid.databinding.FragmentHomeBinding;
import com.t4app.t4everandroid.main.GlobalDataCache;
import com.t4app.t4everandroid.main.Models.LegacyProfile;
import com.t4app.t4everandroid.main.Models.Session;
import com.t4app.t4everandroid.main.T4EverMainActivity;
import com.t4app.t4everandroid.main.ui.chat.ChatFragment;
import com.t4app.t4everandroid.network.responses.ResponseGetAssistants;
import com.t4app.t4everandroid.main.ui.legacyProfile.LegacyProfilesFragment;
import com.t4app.t4everandroid.main.ui.media.MediaFragment;
import com.t4app.t4everandroid.main.ui.questions.QuestionsFragment;
import com.t4app.t4everandroid.network.ApiServices;
import com.t4app.t4everandroid.network.responses.ResponseStartEndSession;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private static final String TAG = "HOME_FRAG";
    private static final String ARG_URI = "arg_uri";

    private Uri uriImport = null;

    private FragmentHomeBinding binding;
    private SessionManager sessionManager;
    public HomeFragment() {}

    public static HomeFragment newInstance(Uri uri) {
        HomeFragment fragment = new HomeFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_URI,uri.toString());
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null){
            String uriStr = args.getString(ARG_URI);
            uriImport = Uri.parse(uriStr);

        }

        sessionManager = SessionManager.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        binding.welcomeItem.welcomeMessage.setText(getString(R.string.welcome, sessionManager.getName()));
        if (GlobalDataCache.legacyProfiles == null){
            getAssistants();
        }else{
            binding.legacyProfilesItem.countLegacyProfiles.setText(String.valueOf(GlobalDataCache.legacyProfiles.size()));
            binding.questionAnsweredItem.countQuestionsAnswered.setText("0");
            binding.scheduledMessagesItem.countMessages.setText("0");
            binding.mediaItem.countMedia.setText(String.valueOf(GlobalDataCache.mediaList.size()));
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
                ((T4EverMainActivity)requireActivity()).selectNavItem(R.id.nav_legacy_profiles);
            }
        });

        binding.welcomeItem.createLegacyProfile.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                showFragment(new LegacyProfilesFragment());
                ((T4EverMainActivity)requireActivity()).selectNavItem(R.id.nav_legacy_profiles);
            }
        });

        binding.mediaItem.getRoot().setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                showFragment(new MediaFragment());
                ((T4EverMainActivity)requireActivity()).selectNavItem(R.id.nav_media);
            }
        });

        binding.scheduledMessagesItem.getRoot().setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                showFragment(new ChatFragment());
                ((T4EverMainActivity)requireActivity()).selectNavItem(R.id.nav_chat);
            }
        });

        binding.questionAnsweredItem.getRoot().setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                showFragment(new QuestionsFragment());
                ((T4EverMainActivity)requireActivity()).selectNavItem(R.id.nav_questions);
            }
        });

        binding.itemQuickActions.itemAnswerQuestions.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                showFragment(new QuestionsFragment());
                ((T4EverMainActivity)requireActivity()).selectNavItem(R.id.nav_questions);
            }
        });

        binding.itemQuickActions.itemRecordConversation.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                showFragment(new MediaFragment());
                ((T4EverMainActivity)requireActivity()).selectNavItem(R.id.nav_media);
            }
        });

        binding.itemQuickActions.itemCreateMessage.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                showFragment(new ChatFragment());
                ((T4EverMainActivity)requireActivity()).selectNavItem(R.id.nav_chat);
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
                                    binding.mediaItem.countMedia.setText("0");
                                    binding.legacyProfilesItem.countLegacyProfiles.setText("0");
                                    binding.questionAnsweredItem.countQuestionsAnswered.setText("0");
                                    binding.scheduledMessagesItem.countMessages.setText("0");
                                }else{
                                    if (GlobalDataCache.legacyProfiles != null && !GlobalDataCache.legacyProfiles.isEmpty()) {

                                        LegacyProfile keepProfile = null;
                                        Session keepSession = null;

                                        for (LegacyProfile legacyProfile : GlobalDataCache.legacyProfiles) {
                                            Session s = legacyProfile.getOpenSession();
                                            if (s == null) continue;

                                            if (keepSession == null || DateUtils.isAfter(s.getDateStart(), keepSession.getDateStart())) {
                                                keepSession = s;
                                                keepProfile = legacyProfile;
                                            }
                                        }

                                        if (keepSession != null) {
                                            GlobalDataCache.legacyProfileSelected = keepProfile;
                                            GlobalDataCache.sessionId = keepSession.getId();

                                            endSessionsExcept(keepSession.getId());
                                        }
                                    }
                                    if (uriImport != null){
                                        showFragment(ChatFragment.newInstance(uriImport));
                                        ((T4EverMainActivity)requireActivity()).selectNavItem(R.id.nav_chat);
                                    }
                                    binding.legacyProfilesItem.countLegacyProfiles.setText(String.valueOf(body.getData().size()));
                                    binding.questionAnsweredItem.countQuestionsAnswered.setText(String.valueOf(GlobalDataCache.mediaList.size()));
                                    binding.scheduledMessagesItem.countMessages.setText("0");
                                    binding.mediaItem.countMedia.setText("0");
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

    private void endSessionsExcept(String keepSessionId) {
        if (GlobalDataCache.legacyProfiles == null || GlobalDataCache.legacyProfiles.isEmpty()) return;

        for (LegacyProfile legacyProfile : GlobalDataCache.legacyProfiles) {
            Session s = legacyProfile.getOpenSession();
            if (s == null) continue;

            if (!s.getId().equals(keepSessionId)) {
                endSession(s.getId(), session -> {});
            }
        }
    }


    private void endSession(String sessionId, ListenersUtils.OnSessionStartedOrEndCallback callback){
        ApiServices apiServices = AppController.getApiServices();
        Call<ResponseStartEndSession> call = apiServices.endSession(sessionId);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseStartEndSession> call, Response<ResponseStartEndSession> response) {
                if (response.isSuccessful()) {
                    ResponseStartEndSession body = response.body();
                    if (body != null) {
                        if (body.isStatus()) {
                            if (body.getData() != null) {
                                callback.onSession(body.getData());
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseStartEndSession> call, Throwable throwable) {
                Log.e(TAG, "onFailure:END SESSION " + throwable.getMessage());
            }
        });
    }

}
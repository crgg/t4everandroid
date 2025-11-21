package com.t4app.t4everandroid.main.ui.legacyProfile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.JsonObject;
import com.t4app.t4everandroid.AppController;
import com.t4app.t4everandroid.ErrorUtils;
import com.t4app.t4everandroid.ListenersUtils;
import com.t4app.t4everandroid.MessagesUtils;
import com.t4app.t4everandroid.R;
import com.t4app.t4everandroid.SafeClickListener;
import com.t4app.t4everandroid.databinding.FragmentLegacyProfilesBinding;
import com.t4app.t4everandroid.main.GlobalDataCache;
import com.t4app.t4everandroid.main.Models.LegacyProfile;
import com.t4app.t4everandroid.main.T4EverMainActivity;
import com.t4app.t4everandroid.main.adapter.LegacyProfileAdapter;
import com.t4app.t4everandroid.main.ui.ChatFragment;
import com.t4app.t4everandroid.main.ui.questions.QuestionsFragment;
import com.t4app.t4everandroid.main.viewmodel.MainViewModel;
import com.t4app.t4everandroid.network.ApiServices;
import com.t4app.t4everandroid.network.responses.ResponseStartEndSession;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LegacyProfilesFragment extends Fragment {
    private static final String TAG = "LEGACY_FRAG";

    private FragmentLegacyProfilesBinding binding;
    private ActivityResultLauncher<Intent> launcher;

    private LegacyProfileAdapter adapter;

    public LegacyProfilesFragment() {}

    public static LegacyProfilesFragment newInstance() {
        return new LegacyProfilesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLegacyProfilesBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        if (GlobalDataCache.legacyProfiles != null && !GlobalDataCache.legacyProfiles.isEmpty()){
            binding.itemNoProfilesCreated.getRoot().setVisibility(View.GONE);
        }

        launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        boolean update = result.getData().getBooleanExtra("update", false);
                        String type = result.getData().getStringExtra("type");
                        if (update){
                            binding.itemTotalProfiles.countTotalProfiles.setText(String.valueOf(GlobalDataCache.legacyProfiles.size()));
                            binding.itemActiveProfiles.countActiveProfiles.setText("0");
                            binding.itemCompletedProfiles.countCompletedProfiles.setText("0");
                            adapter.setProfileList(GlobalDataCache.legacyProfiles);
                            adapter.notifyDataSetChanged();
                        }else if (type != null && type.equalsIgnoreCase("update_list")){
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
        );

        binding.createLegacyProfileBtn.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                Intent intent = new Intent(requireActivity(), CreateLegacyProfileActivity.class);
                launcher.launch(intent);
            }
        });

        binding.itemNoProfilesCreated.createFirstProfile.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                Intent intent = new Intent(requireActivity(), CreateLegacyProfileActivity.class);
                launcher.launch(intent);
            }
        });

        adapter = new LegacyProfileAdapter(GlobalDataCache.legacyProfiles, requireActivity(), new ListenersUtils.OnProfileActionListener() {
            @Override
            public void onSelect(LegacyProfile profile) {
                if (GlobalDataCache.legacyProfileSelected == null){
                    Map<String, Object> data = new HashMap<>();
                    data.put("assistant_id", profile.getId());
                    startSession(data, session1 -> {
                        GlobalDataCache.legacyProfiles.set(
                                GlobalDataCache.legacyProfiles.indexOf(profile), session1.getAssistant()
                        );
                        GlobalDataCache.legacyProfileSelected = session1.getAssistant();
                        GlobalDataCache.sessionId = session1.getId();
                    });
                }else if (GlobalDataCache.legacyProfileSelected.getId().equalsIgnoreCase(profile.getId())){
                    Log.d(TAG, "IS SAME: ");
                }else if (GlobalDataCache.legacyProfileSelected.getOpenSession() != null){
                    endSession(GlobalDataCache.legacyProfileSelected.getOpenSession().getId(),
                            session -> {
                                GlobalDataCache.legacyProfiles.set(
                                        GlobalDataCache.legacyProfiles.indexOf(GlobalDataCache.legacyProfileSelected),
                                        session.getAssistant());

                                Map<String, Object> data = new HashMap<>();
                                data.put("assistant_id", profile.getId());
                                startSession(data, session1 -> {
                                    GlobalDataCache.legacyProfiles.set(
                                            GlobalDataCache.legacyProfiles.indexOf(profile), session1.getAssistant()
                                    );
                                    GlobalDataCache.legacyProfileSelected = session1.getAssistant();
                                    GlobalDataCache.sessionId = session1.getId();
                                });
                            });

                }else{
                    Map<String, Object> data = new HashMap<>();
                    data.put("assistant_id", profile.getId());
                    startSession(data, session1 -> {
                        GlobalDataCache.legacyProfiles.set(
                                GlobalDataCache.legacyProfiles.indexOf(profile), session1.getAssistant()
                        );
                        GlobalDataCache.legacyProfileSelected = session1.getAssistant();
                        GlobalDataCache.sessionId = session1.getId();
                    });
                }

            }

            @Override
            public void onChat(LegacyProfile profile) {
                if (GlobalDataCache.legacyProfileSelected == null){
                    Map<String, Object> data = new HashMap<>();
                    data.put("assistant_id", profile.getId());
                    startSession(data, session1 -> {
                        GlobalDataCache.legacyProfiles.set(
                                GlobalDataCache.legacyProfiles.indexOf(profile), session1.getAssistant()
                        );
                        GlobalDataCache.legacyProfileSelected = session1.getAssistant();
                        GlobalDataCache.sessionId = session1.getId();
                        showFragment(new ChatFragment());
                        ((T4EverMainActivity)requireActivity()).selectNavItem(R.id.nav_chat);
                    });
                } else if (GlobalDataCache.legacyProfileSelected.getId().equalsIgnoreCase(profile.getId())){
                    GlobalDataCache.legacyProfileSelected = profile;
                    if (profile.getOpenSession() != null){
                        GlobalDataCache.sessionId = profile.getOpenSession().getId();
                    }
                    showFragment(new ChatFragment());
                    ((T4EverMainActivity)requireActivity()).selectNavItem(R.id.nav_chat);
                }else if (GlobalDataCache.legacyProfileSelected.getOpenSession() != null){
                    endSession(GlobalDataCache.legacyProfileSelected.getOpenSession().getId(),
                            session -> {
                                GlobalDataCache.legacyProfiles.set(
                                        GlobalDataCache.legacyProfiles.indexOf(GlobalDataCache.legacyProfileSelected),
                                        session.getAssistant());

                                Map<String, Object> data = new HashMap<>();
                                data.put("assistant_id", profile.getId());
                                startSession(data, session1 -> {
                                    GlobalDataCache.legacyProfiles.set(
                                            GlobalDataCache.legacyProfiles.indexOf(profile), session1.getAssistant()
                                    );
                                    GlobalDataCache.legacyProfileSelected = session1.getAssistant();
                                    GlobalDataCache.sessionId = session1.getId();
                                    showFragment(new ChatFragment());
                                    ((T4EverMainActivity)requireActivity()).selectNavItem(R.id.nav_chat);
                                });
                            });

                }else{
                    Map<String, Object> data = new HashMap<>();
                    data.put("assistant_id", profile.getId());
                    startSession(data, session1 -> {
                        GlobalDataCache.legacyProfiles.set(
                                GlobalDataCache.legacyProfiles.indexOf(profile), session1.getAssistant()
                        );
                        GlobalDataCache.legacyProfileSelected = session1.getAssistant();
                        GlobalDataCache.sessionId = session1.getId();
                        showFragment(new ChatFragment());
                        ((T4EverMainActivity)requireActivity()).selectNavItem(R.id.nav_chat);
                    });
                }

            }

            @Override
            public void onQuestion(LegacyProfile profile) {
                if (GlobalDataCache.legacyProfileSelected == null){
                    Map<String, Object> data = new HashMap<>();
                    data.put("assistant_id", profile.getId());
                    startSession(data, session -> {
                        GlobalDataCache.legacyProfiles.set(
                                GlobalDataCache.legacyProfiles.indexOf(profile), session.getAssistant()
                        );
                        GlobalDataCache.legacyProfileSelected = session.getAssistant();
                        GlobalDataCache.sessionId = session.getId();
                        showFragment(new QuestionsFragment());
                        ((T4EverMainActivity)requireActivity()).selectNavItem(R.id.nav_questions);
                    });
                }else if (GlobalDataCache.legacyProfileSelected.getId().equalsIgnoreCase(profile.getId())){
                    GlobalDataCache.legacyProfileSelected = profile;
                    if (profile.getOpenSession() != null){
                        GlobalDataCache.sessionId = profile.getOpenSession().getId();
                    }
                    showFragment(new ChatFragment());
                    ((T4EverMainActivity)requireActivity()).selectNavItem(R.id.nav_chat);
                }else if (GlobalDataCache.legacyProfileSelected.getOpenSession() != null){
                    endSession(GlobalDataCache.legacyProfileSelected.getOpenSession().getId(),
                            session -> {
                                GlobalDataCache.legacyProfiles.set(
                                        GlobalDataCache.legacyProfiles.indexOf(GlobalDataCache.legacyProfileSelected),
                                        session.getAssistant());

                                Map<String, Object> data = new HashMap<>();
                                data.put("assistant_id", profile.getId());
                                startSession(data, session1 -> {
                                    GlobalDataCache.legacyProfiles.set(
                                            GlobalDataCache.legacyProfiles.indexOf(profile), session1.getAssistant()
                                    );
                                    GlobalDataCache.legacyProfileSelected = session1.getAssistant();
                                    GlobalDataCache.sessionId = session1.getId();
                                    showFragment(new QuestionsFragment());
                                    ((T4EverMainActivity)requireActivity()).selectNavItem(R.id.nav_questions);
                                });
                            });

                }else{
                    Map<String, Object> data = new HashMap<>();
                    data.put("assistant_id", profile.getId());
                    startSession(data, session1 -> {
                        GlobalDataCache.legacyProfiles.set(
                                GlobalDataCache.legacyProfiles.indexOf(profile), session1.getAssistant()
                        );
                        GlobalDataCache.legacyProfileSelected = session1.getAssistant();
                        GlobalDataCache.sessionId = session1.getId();
                        showFragment(new QuestionsFragment());
                        ((T4EverMainActivity)requireActivity()).selectNavItem(R.id.nav_questions);
                    });
                }

            }

            @Override
            public void onEdit(LegacyProfile profile, int pos) {
                    Intent intent = new Intent(requireActivity(), CreateLegacyProfileActivity.class);
                    intent.putExtra("is_update", true);
                    intent.putExtra("legacy_profile", profile);
                    launcher.launch(intent);

            }

            @Override
            public void onDelete(LegacyProfile profile, int pos) {
                MessagesUtils.showMessageConfirmation(requireActivity(),
                        getString(R.string.msg_delete_profile),
                        confirmed -> {
                            if (confirmed){
                                deleteProfile(profile, pos);
                            }
                        });
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (GlobalDataCache.legacyProfiles != null){
            binding.itemTotalProfiles.countTotalProfiles.setText(String.valueOf(GlobalDataCache.legacyProfiles.size()));
            binding.itemActiveProfiles.countActiveProfiles.setText("0");
            binding.itemCompletedProfiles.countCompletedProfiles.setText("0");
        }

        binding.rvProfiles.setLayoutManager(new LinearLayoutManager(requireActivity()));
        binding.rvProfiles.setAdapter(adapter);

    }

    private void startSession(Map<String, Object> data, ListenersUtils.OnSessionStartedOrEndCallback callback){
        ApiServices apiServices = AppController.getApiServices();
        Call<ResponseStartEndSession> call = apiServices.startSession(data);
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
                Log.e(TAG, "onFailure: START SESSION " + throwable.getMessage());
            }
        });
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

    private void deleteProfile(LegacyProfile legacyProfile, int pos){
        ApiServices apiServices = AppController.getApiServices();
        Call<JsonObject> call = apiServices.deleteAssistant(legacyProfile.getId());
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject body = response.body();
                    if (body != null) {
                        if (body.has("status")) {
                            if (body.get("status").getAsBoolean()) {
                                adapter.removeItem(pos);
                                GlobalDataCache.legacyProfiles.remove(legacyProfile);
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable throwable) {
                MessagesUtils.showErrorDialog(requireActivity(), ErrorUtils.parseError(throwable));
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
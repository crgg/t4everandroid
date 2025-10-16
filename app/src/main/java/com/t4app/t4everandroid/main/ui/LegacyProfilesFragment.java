package com.t4app.t4everandroid.main.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.t4app.t4everandroid.main.adapter.LegacyProfileAdapter;
import com.t4app.t4everandroid.network.ApiServices;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LegacyProfilesFragment extends Fragment {

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

        adapter = new LegacyProfileAdapter(GlobalDataCache.legacyProfiles, requireActivity(), new ListenersUtils.OnProfileActionListener() {
            @Override
            public void onSelect(LegacyProfile profile) {
                GlobalDataCache.legacyProfileSelected = profile;
            }

            @Override
            public void onChat(LegacyProfile profile) {

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
}
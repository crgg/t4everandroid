package com.t4app.t4everandroid.main.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.t4app.t4everandroid.AppController;
import com.t4app.t4everandroid.ErrorUtils;
import com.t4app.t4everandroid.ListenersUtils;
import com.t4app.t4everandroid.MessagesUtils;
import com.t4app.t4everandroid.R;
import com.t4app.t4everandroid.SafeClickListener;
import com.t4app.t4everandroid.databinding.FragmentChatBinding;
import com.t4app.t4everandroid.main.GlobalDataCache;
import com.t4app.t4everandroid.main.Models.Interactions;
import com.t4app.t4everandroid.main.adapter.ChatAdapter;
import com.t4app.t4everandroid.main.ui.legacyProfile.LegacyProfilesFragment;
import com.t4app.t4everandroid.network.ApiServices;
import com.t4app.t4everandroid.network.responses.ResponseCreateInteraction;
import com.t4app.t4everandroid.network.responses.ResponseGetInteractions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatFragment extends Fragment {

    private ChatAdapter adapter;
    private FragmentChatBinding binding;

    public ChatFragment() {
    }

    public static ChatFragment newInstance(String param1, String param2) {
        return new ChatFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        if (GlobalDataCache.legacyProfileSelected != null){
            binding.itemChatHelper.cardChatHelper.setVisibility(View.VISIBLE);
            binding.itemChat.getRoot().setVisibility(View.VISIBLE);
            binding.titleContainer.setVisibility(View.VISIBLE);
            binding.itemSelectLegacy.getRoot().setVisibility(View.GONE);

            binding.chatSubtitle.setText(getString(R.string.chat_with_the_digital_version_of,
                    GlobalDataCache.legacyProfileSelected.getName()));

            binding.chatTitle.setText(getString(R.string.chat_with,
                    GlobalDataCache.legacyProfileSelected.getName()));

        }else {
            binding.itemChatHelper.cardChatHelper.setVisibility(View.GONE);
            binding.itemChat.getRoot().setVisibility(View.GONE);
            binding.titleContainer.setVisibility(View.GONE);
            binding.itemSelectLegacy.getRoot().setVisibility(View.VISIBLE);

            binding.itemSelectLegacy.selectLegacyDescription.setText(R.string.you_need_to_select_a_legacy_profile_chat);
            binding.itemSelectLegacy.btnAddFirst.setOnClickListener(new SafeClickListener() {
                @Override
                public void onSafeClick(View v) {
                    showFragment(new LegacyProfilesFragment());
                }
            });

            binding.itemSelectLegacy.buttonActionsProfile.setOnClickListener(new SafeClickListener() {
                @Override
                public void onSafeClick(View v) {
                    showFragment(new LegacyProfilesFragment());
                }
            });
        }

        List<Interactions> interactions = new ArrayList<>();
        adapter = new ChatAdapter(requireContext(), interactions);
        binding.itemChat.chatRv.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.itemChat.chatRv.setAdapter(adapter);

        if (GlobalDataCache.legacyProfileSelected != null && GlobalDataCache.sessionId != null){
            getInteractions();
        }
        return view;
    }

    private void getInteractions(){
        ApiServices apiServices = AppController.getApiServices();
        Call<ResponseGetInteractions> call = apiServices.getInteractions(GlobalDataCache.sessionId);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseGetInteractions> call, Response<ResponseGetInteractions> response) {
                if (response.isSuccessful()){
                    ResponseGetInteractions body = response.body();
                    if (body != null){
                        if (body.isStatus()){
                            if (body.getData() != null){
                                adapter.updateMessages(body.getData());
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseGetInteractions> call, Throwable throwable) {
                MessagesUtils.showErrorDialog(requireActivity(), ErrorUtils.parseError(throwable));
            }
        });
    }


    private void sendInteraction(Map<String, Object> data, ListenersUtils.ConfirmationCallback callback){
        ApiServices apiServices = AppController.getApiServices();
        Call<ResponseCreateInteraction> call = apiServices.sendInteraction(data);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseCreateInteraction> call, Response<ResponseCreateInteraction> response) {
                if (response.isSuccessful()) {
                    ResponseCreateInteraction body = response.body();
                    if (body != null) {
                        if (body.isStatus()) {
                            if (body.getData() != null) {
                                adapter.addMessage(body.getData());
                                callback.onResult(true);
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseCreateInteraction> call, Throwable throwable) {
                MessagesUtils.showErrorDialog(requireActivity(), ErrorUtils.parseError(throwable));
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (GlobalDataCache.sessionId != null){
            binding.itemChat.sendInteractionBtn.setOnClickListener(new SafeClickListener() {
                @Override
                public void onSafeClick(View v) {
                    String msg = binding.itemChat.textInteraction.getText().toString().trim();
                    if (!msg.isEmpty()){
                        Map<String, Object> data = new HashMap<>();
                        data.put("continue", false);
                        data.put("session_id", GlobalDataCache.sessionId);
                        data.put("text_from_user", msg);
                        sendInteraction(data, confirmed -> {
                            binding.itemChat.textInteraction.setText("");
                        });
                    }
                }
            });
        }

    }

    private void showFragment(Fragment fragment) {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}
package com.t4app.t4everandroid.main.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BulletSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.t4app.t4everandroid.R;
import com.t4app.t4everandroid.SafeClickListener;
import com.t4app.t4everandroid.databinding.FragmentChatBinding;
import com.t4app.t4everandroid.main.GlobalDataCache;
import com.t4app.t4everandroid.main.Models.Message;
import com.t4app.t4everandroid.main.adapter.ChatAdapter;
import com.t4app.t4everandroid.main.ui.legacyProfile.LegacyProfilesFragment;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

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

        List<Message> messages = new ArrayList<>();
        messages.add(new Message("1", Message.TYPE_TEXT, "Hola 👋", true, "10:00", true));
        messages.add(new Message("2", Message.TYPE_TEXT, "¿Cómo estás?", false, "10:01", false));
        ChatAdapter adapter = new ChatAdapter(requireContext(), messages);
        binding.itemChat.chatRv.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.itemChat.chatRv.setAdapter(adapter);

        //TODO: LOGIC TO SEND AND RECEIVE MESSAGES O INTERACTIONS

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    private void showFragment(Fragment fragment) {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}
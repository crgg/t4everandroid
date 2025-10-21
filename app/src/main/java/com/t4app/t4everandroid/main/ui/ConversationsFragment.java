package com.t4app.t4everandroid.main.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.t4app.t4everandroid.ListenersUtils;
import com.t4app.t4everandroid.R;
import com.t4app.t4everandroid.SafeClickListener;
import com.t4app.t4everandroid.databinding.FragmentConversationsBinding;
import com.t4app.t4everandroid.databinding.FragmentQuestionsBinding;
import com.t4app.t4everandroid.main.GlobalDataCache;
import com.t4app.t4everandroid.main.Models.ConversationTest;
import com.t4app.t4everandroid.main.adapter.ConversationAdapter;

import java.util.ArrayList;
import java.util.List;

public class ConversationsFragment extends Fragment {
    private FragmentConversationsBinding binding;

    private List<ConversationTest> conversationTestList;
    private ConversationAdapter adapter;

    public ConversationsFragment() {}

    public static ConversationsFragment newInstance() {
        return new ConversationsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentConversationsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        if (GlobalDataCache.legacyProfiles == null || GlobalDataCache.legacyProfiles.isEmpty()){
            binding.itemSelectLegacy.selectLegacyDescription.setText(getString(R.string.you_need_to_select_a_legacy_profile));

            binding.itemSelectLegacy.btnAddFirst.setVisibility(View.GONE);
            binding.itemSelectLegacy.buttonActionsProfile.setVisibility(View.VISIBLE);

        } else if (GlobalDataCache.legacyProfileSelected == null){
            binding.itemSelectLegacy.selectLegacyDescription.setText(R.string.you_need_to_select_a_legacy_profile_conversation);
            binding.createNewConversationBtn.setEnabled(false);
            binding.createNewConversationBtn.setAlpha(0.5f);


            binding.itemSelectLegacy.btnAddFirst.setVisibility(View.GONE);
            binding.itemSelectLegacy.buttonActionsProfile.setVisibility(View.VISIBLE);
        }else {
            binding.itemSelectLegacy.selectLegacyDescription.setText(
                    getString(R.string.profile_selected_conversation,
                            GlobalDataCache.legacyProfileSelected.getName()));
            binding.createNewConversationBtn.setEnabled(true);
            binding.createNewConversationBtn.setAlpha(1f);

            binding.itemSelectLegacy.btnAddFirst.setVisibility(View.VISIBLE);
            binding.itemSelectLegacy.btnAddFirst.setText(R.string.record_first_conversation);
            binding.itemSelectLegacy.buttonActionsProfile.setVisibility(View.GONE);
        }

        binding.itemSelectLegacy.buttonActionsProfile.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                showFragment(new LegacyProfilesFragment());
            }
        });
        conversationTestList = new ArrayList<>();
        adapter = new ConversationAdapter(requireContext(), conversationTestList,
                (conversationTest, pos) -> {

        });
        binding.conversationRv.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.conversationRv.setAdapter(adapter);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        CreateConversationBottomSheet bottomSheet = new CreateConversationBottomSheet();
        bottomSheet.setListener(conversationTest -> {
            if (binding.conversationRv.getVisibility() == View.GONE){
                binding.conversationRv.setVisibility(View.VISIBLE);
                binding.itemSelectLegacy.getRoot().setVisibility(View.GONE);
            }
            adapter.addItem(conversationTest);
//            conversationTestList.add(conversationTest);

        });
        binding.createNewConversationBtn.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                bottomSheet.show(getChildFragmentManager(), "create_conversation");
            }
        });

        binding.itemSelectLegacy.btnAddFirst.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                bottomSheet.show(getChildFragmentManager(), "create_conversation");
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
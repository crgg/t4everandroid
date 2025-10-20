package com.t4app.t4everandroid.main.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.t4app.t4everandroid.R;
import com.t4app.t4everandroid.SafeClickListener;
import com.t4app.t4everandroid.databinding.FragmentConversationsBinding;
import com.t4app.t4everandroid.databinding.FragmentQuestionsBinding;
import com.t4app.t4everandroid.main.GlobalDataCache;

public class ConversationsFragment extends Fragment {
    private FragmentConversationsBinding binding;

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

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        CreateConversationBottomSheet bottomSheet = new CreateConversationBottomSheet();
        binding.createNewConversationBtn.setOnClickListener(new SafeClickListener() {
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
package com.t4app.t4everandroid.main.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.t4app.t4everandroid.R;
import com.t4app.t4everandroid.databinding.FragmentHomeBinding;
import com.t4app.t4everandroid.databinding.FragmentQuestionsBinding;

public class QuestionsFragment extends Fragment {

    private FragmentQuestionsBinding binding;

    public QuestionsFragment() {}

    public static QuestionsFragment newInstance() {
        return new QuestionsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentQuestionsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        binding.itemSelectLegacy.selectLegacyDescription.setText(getString(R.string.you_need_to_select_a_legacy_profile));
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
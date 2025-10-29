package com.t4app.t4everandroid.main.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.chip.Chip;
import com.t4app.t4everandroid.databinding.FragmentEmailBinding;
import com.t4app.t4everandroid.main.Models.EmailTest;
import com.t4app.t4everandroid.main.adapter.EmailAdapter;

import java.util.ArrayList;
import java.util.List;

public class EmailFragment extends Fragment {

    private FragmentEmailBinding binding;

    public EmailFragment() {
    }

    public static EmailFragment newInstance() {
        return new EmailFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEmailBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        binding.searchEmail.categoriesAuto.setVisibility(View.INVISIBLE);
        binding.emailQuantityText.setText("5 emails total");

        List<EmailTest> testEmails = new ArrayList<>();
        testEmails.add(new EmailTest("Alice Johnson", "Meeting Reminder", "Don't forget the meeting at 10am", "24 Oct", false));
        testEmails.add(new EmailTest("Bob Smith", "Invoice Attached", "Please see the attached invoice", "23 Oct", true));
        testEmails.add(new EmailTest("Carol White", "Happy Birthday!", "Wishing you a wonderful day!", "22 Oct", false));
        testEmails.add(new EmailTest("David Brown", "Project Update", "The project is 80% complete", "21 Oct", true));
        testEmails.add(new EmailTest("Eve Black", "Lunch?", "Are you free for lunch tomorrow?", "20 Oct", false));


        EmailAdapter adapter = new EmailAdapter(testEmails, requireContext(), (item, position) -> {

        });
        binding.emailRv.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.emailRv.setAdapter(adapter);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.itemAddEmail.inboxBtn.post(() -> binding.itemAddEmail.inboxBtn.performClick());

        Chip chipPersonal = binding.itemAddEmail.chipPersonal;
        Chip chipCareer = binding.itemAddEmail.chipCareer;
        Chip chipFamily = binding.itemAddEmail.chipFamily;
        Chip chipMemories = binding.itemAddEmail.chipMemories;
        Chip chipMusic = binding.itemAddEmail.chipMusic;
        Chip chipPhotos = binding.itemAddEmail.chipPhotos;
        Chip chipTravel = binding.itemAddEmail.chipTravel;
        Chip chipRecipes = binding.itemAddEmail.chipRecipes;

        chipPersonal.setOnCheckedChangeListener((buttonView, isChecked) -> {
            chipCloseBtnVisibility(chipPersonal);
        });

        chipCareer.setOnCheckedChangeListener((buttonView, isChecked) -> {
            chipCloseBtnVisibility(chipCareer);
        });

        chipFamily.setOnCheckedChangeListener((buttonView, isChecked) -> {
            chipCloseBtnVisibility(chipFamily);
        });

        chipMemories.setOnCheckedChangeListener((buttonView, isChecked) -> {
            chipCloseBtnVisibility(chipMemories);
        });

        chipMusic.setOnCheckedChangeListener((buttonView, isChecked) -> {
            chipCloseBtnVisibility(chipMusic);
        });

        chipPhotos.setOnCheckedChangeListener((buttonView, isChecked) -> {
            chipCloseBtnVisibility(chipPhotos);
        });

        chipTravel.setOnCheckedChangeListener((buttonView, isChecked) -> {
            chipCloseBtnVisibility(chipTravel);
        });

        chipRecipes.setOnCheckedChangeListener((buttonView, isChecked) -> {
            chipCloseBtnVisibility(chipRecipes);
        });








    }

    private void chipCloseBtnVisibility(Chip chip){
        chip.setCloseIconVisible(chip.isChecked());
    }
}
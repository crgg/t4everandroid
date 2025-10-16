package com.t4app.t4everandroid.main.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.t4app.t4everandroid.R;
import com.t4app.t4everandroid.SafeClickListener;
import com.t4app.t4everandroid.databinding.FragmentQuestionsBinding;
import com.t4app.t4everandroid.main.GlobalDataCache;
import com.t4app.t4everandroid.main.Models.ListItem;
import com.t4app.t4everandroid.main.Models.QuestionTest;
import com.t4app.t4everandroid.main.adapter.CategoriesAdapter;
import com.t4app.t4everandroid.main.adapter.QuestionGroupedAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

        if (GlobalDataCache.legacyProfiles == null || GlobalDataCache.legacyProfiles.isEmpty()){
            binding.itemSelectLegacy.selectLegacyDescription.setText(getString(R.string.you_need_to_select_a_legacy_profile));

            binding.itemSelectLegacy.btnAddFirst.setVisibility(View.GONE);
            binding.itemSelectLegacy.buttonActionsProfile.setVisibility(View.VISIBLE);

        } else if (GlobalDataCache.legacyProfileSelected == null){
            binding.itemSelectLegacy.selectLegacyDescription.setText(R.string.you_need_to_select_a_legacy_to_start);
            binding.addNewQuestionBtn.setEnabled(false);
            binding.addNewQuestionBtn.setAlpha(0.5f);


            binding.itemSelectLegacy.btnAddFirst.setVisibility(View.GONE);
            binding.itemSelectLegacy.buttonActionsProfile.setVisibility(View.VISIBLE);
        }else {
            binding.itemSelectLegacy.selectLegacyDescription.setText(
                    getString(R.string.start_adding_questions_to_capture_the_essence_of,
                    GlobalDataCache.legacyProfileSelected.getName()));
            binding.addNewQuestionBtn.setEnabled(true);
            binding.addNewQuestionBtn.setAlpha(1f);

            binding.itemSelectLegacy.btnAddFirst.setVisibility(View.VISIBLE);
            binding.itemSelectLegacy.btnAddFirst.setText(R.string.add_first_question);
            binding.itemSelectLegacy.buttonActionsProfile.setVisibility(View.GONE);
        }

        binding.itemSearch.categoriesAuto.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                showCategories();
            }
        });

        binding.itemSelectLegacy.buttonActionsProfile.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                showFragment(new LegacyProfilesFragment());
            }
        });


        return view;
    }


    public void showCategories() {
        BottomSheetDialog categoriesBottomSheet = new BottomSheetDialog(requireContext());
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_categories, null);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewCategories);

        List<String> items = Arrays.asList(
                getString(R.string.all_categories),
                getString(R.string.personal),
                getString(R.string.family),
                getString(R.string.career),
                getString(R.string.hobbies),
                getString(R.string.beliefs),
                getString(R.string.memories),
                getString(R.string.wisdom),
                getString(R.string.humor),
                getString(R.string.values),
                getString(R.string.experiences)
        );

        CategoriesAdapter adapter = new CategoriesAdapter(items);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter.setOnItemClickListener(category -> {
            binding.itemSearch.categoriesAuto.setText(category);
            categoriesBottomSheet.dismiss();
        });

        categoriesBottomSheet.setContentView(view);
        categoriesBottomSheet.show();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<QuestionTest> questions = GlobalDataCache.questions;
        List<ListItem> grouped = groupByCategory(questions, null);
        QuestionGroupedAdapter adapter = new QuestionGroupedAdapter(requireContext(), grouped);
        binding.questionsRv.setAdapter(adapter);
        binding.questionsRv.setLayoutManager(new LinearLayoutManager(requireContext()));

        CreateQuestionBottomSheet bottomSheet = new CreateQuestionBottomSheet();
        bottomSheet.setListener(question -> {
            if (questions.isEmpty()){
                binding.itemSelectLegacy.getRoot().setVisibility(View.GONE);
                binding.questionsRv.setVisibility(View.VISIBLE);
            }
            questions.add(question);
            adapter.addQuestionToCategory(question);

        });

        binding.itemSelectLegacy.btnAddFirst.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                bottomSheet.show(requireActivity().getSupportFragmentManager(), "CreateQuestionBottomSheet");
            }
        });
        binding.addNewQuestionBtn.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                bottomSheet.show(requireActivity().getSupportFragmentManager(), "CreateQuestionBottomSheet");
            }
        });

        if (!questions.isEmpty()){
            binding.itemSelectLegacy.getRoot().setVisibility(View.GONE);
            binding.questionsRv.setVisibility(View.VISIBLE);
        }else{
            binding.itemSelectLegacy.getRoot().setVisibility(View.VISIBLE);
            binding.questionsRv.setVisibility(View.GONE);
        }

        binding.itemSearch.categoriesAuto.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                if (editable != null){
                    if (!editable.toString().isEmpty()){
                        adapter.updateList(groupByCategory(questions, editable.toString()));
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
        });

    }

    private List<ListItem> groupByCategory(List<QuestionTest> list, @Nullable String filterCategory) {
        Map<String, List<QuestionTest>> grouped = new LinkedHashMap<>();

        for (QuestionTest q : list) {
            String cat = q.getCategory();
            if (filterCategory != null && !filterCategory.equals(cat)) continue;
            if (!grouped.containsKey(cat)) grouped.put(cat, new ArrayList<>());
            grouped.get(cat).add(q);
        }

        List<ListItem> result = new ArrayList<>();
        for (Map.Entry<String, List<QuestionTest>> entry : grouped.entrySet()) {
            result.add(new ListItem(ListItem.TYPE_HEADER, entry.getKey(), null));
            for (QuestionTest q : entry.getValue()) {
                result.add(new ListItem(ListItem.TYPE_ITEM, entry.getKey(), q));
            }
        }
        return result;
    }

    private void showFragment(Fragment fragment) {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }


}
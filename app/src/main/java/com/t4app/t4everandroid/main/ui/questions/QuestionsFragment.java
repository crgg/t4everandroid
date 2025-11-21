package com.t4app.t4everandroid.main.ui.questions;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.t4app.t4everandroid.AppController;
import com.t4app.t4everandroid.ListenersUtils;
import com.t4app.t4everandroid.MessagesUtils;
import com.t4app.t4everandroid.R;
import com.t4app.t4everandroid.SafeClickListener;
import com.t4app.t4everandroid.databinding.FragmentQuestionsBinding;
import com.t4app.t4everandroid.main.GlobalDataCache;
import com.t4app.t4everandroid.main.Models.LegacyProfile;
import com.t4app.t4everandroid.main.Models.ListItem;
import com.t4app.t4everandroid.main.Models.Question;
import com.t4app.t4everandroid.main.Models.Session;
import com.t4app.t4everandroid.main.T4EverMainActivity;
import com.t4app.t4everandroid.main.ui.legacyProfile.LegacyItemsAdapter;
import com.t4app.t4everandroid.network.responses.ResponseGetAssistantQuestions;
import com.t4app.t4everandroid.main.adapter.CategoriesAdapter;
import com.t4app.t4everandroid.main.adapter.QuestionGroupedAdapter;
import com.t4app.t4everandroid.main.ui.legacyProfile.LegacyProfilesFragment;
import com.t4app.t4everandroid.network.ApiServices;
import com.t4app.t4everandroid.network.responses.ResponseStartEndSession;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuestionsFragment extends Fragment {
    private static final String TAG = "QUESTION_FRAG";

    private FragmentQuestionsBinding binding;

    private QuestionGroupedAdapter adapter;
    private CreateQuestionBottomSheet bottomSheet;

    private List<Question> questions;

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

        checkStatus();

        bottomSheet = new CreateQuestionBottomSheet();

        binding.itemSearch.categoriesAuto.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                showCategories();
            }
        });

        binding.changeGlobalProfile.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                showProfilesBottomSheet();
            }
        });

        binding.itemSelectLegacy.buttonActionsProfile.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                showFragment(new LegacyProfilesFragment());
                ((T4EverMainActivity)requireActivity()).selectNavItem(R.id.nav_legacy_profiles);
            }
        });

        questions = GlobalDataCache.questions;

        calculateTotalQuestions(questions);
        List<ListItem> grouped = groupByCategory(questions, null);

        Log.d(TAG, "onCreateView: SET ADAPTER");
        adapter = new QuestionGroupedAdapter(requireContext(), grouped, new ListenersUtils.OnQuestionActionsListener() {
            @Override
            public void onDelete(Question questionTest, int pos) {
                MessagesUtils.showMessageConfirmation(requireActivity(), getString(R.string.msg_delete), confirmed -> {
                    if (confirmed){
                        adapter.removeItem(pos);
                        questions.remove(questionTest);
                        calculateTotalQuestions(questions);
                    }
                });
            }

            @Override
            public void onEdit(Question questionTest, int pos) {
                Log.d(TAG, "onEdit: " + questionTest.getQuestion());
                bottomSheet.setEdit(true);
                bottomSheet.setQuestionEdit(questionTest);
                bottomSheet.setUpdatePos(pos);
                FragmentManager fm = requireActivity().getSupportFragmentManager();
                Fragment prev = fm.findFragmentByTag("CreateQuestionBottomSheet");
                if (prev != null) {
                    fm.beginTransaction().remove(prev).commit();
                }
                bottomSheet.show(requireActivity().getSupportFragmentManager(), "CreateQuestionBottomSheet");
            }
        });
        binding.questionsRv.setAdapter(adapter);
        binding.questionsRv.setLayoutManager(new LinearLayoutManager(requireContext()));

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.itemSearch.searchValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {

            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                String value = s.toString();
                if (!value.isEmpty()) {
                    List<Question> searchResourceList = new ArrayList<>();
                    for (Question object : questions) {
                        String nameDevice = object.getQuestion();
                        if (nameDevice.toLowerCase().contains(value.toLowerCase())) {
                            searchResourceList.add(object);
                        }
                    }
                    List<Question> dataSearch = new ArrayList<>(searchResourceList);

//                    checkFoundData(dataSearch, noFoundDataTv, getString(R.string.customer));
                    adapter.updateList(groupByCategory(dataSearch, null));
                } else {
                    adapter.updateList(groupByCategory(questions, null));
                }
            }
        });

        bottomSheet.setListener(new CreateQuestionBottomSheet.CreateQuestionListener() {
            @Override
            public void onQuestionCreated(Question question) {
                if (questions.isEmpty()){
                    binding.itemSelectLegacy.getRoot().setVisibility(View.GONE);
                    binding.questionsRv.setVisibility(View.VISIBLE);
                }
                questions.add(question);
                adapter.addQuestionToCategory(question);
                calculateTotalQuestions(questions);
            }

            @Override
            public void onQuestionUpdated(Question question, int pos) {
                questions.set(questions.indexOf(question), question);
                calculateTotalQuestions(questions);
                adapter.updateItem(pos, question);
            }
        });

        if (GlobalDataCache.legacyProfileSelected != null){
            getQuestions();
        }

        binding.itemSelectLegacy.btnAddFirst.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                bottomSheet.setEdit(false);
                FragmentManager fm = requireActivity().getSupportFragmentManager();
                Fragment prev = fm.findFragmentByTag("CreateQuestionBottomSheet");
                if (prev != null) {
                    fm.beginTransaction().remove(prev).commit();
                }
                bottomSheet.show(requireActivity().getSupportFragmentManager(), "CreateQuestionBottomSheet");
            }
        });
        binding.addNewQuestionBtn.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                bottomSheet.setEdit(false);
                FragmentManager fm = requireActivity().getSupportFragmentManager();
                Fragment prev = fm.findFragmentByTag("CreateQuestionBottomSheet");
                if (prev != null) {
                    fm.beginTransaction().remove(prev).commit();
                }
                bottomSheet.show(requireActivity().getSupportFragmentManager(), "CreateQuestionBottomSheet");
            }
        });

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

    private void checkStatus(){
        if (GlobalDataCache.legacyProfiles == null || GlobalDataCache.legacyProfiles.isEmpty()){
            binding.itemSelectLegacy.selectLegacyDescription.setText(getString(R.string.you_need_to_select_a_legacy_profile));
            binding.addNewQuestionBtn.setEnabled(false);
            binding.addNewQuestionBtn.setAlpha(0.5f);

            binding.itemSelectLegacy.btnAddFirst.setVisibility(View.GONE);
            binding.itemSelectLegacy.buttonActionsProfile.setVisibility(View.VISIBLE);

        } else if (GlobalDataCache.legacyProfileSelected == null){
            binding.itemSelectLegacy.selectLegacyDescription.setText(R.string.you_need_to_select_a_legacy_to_start);
            binding.addNewQuestionBtn.setEnabled(false);
            binding.addNewQuestionBtn.setAlpha(0.5f);


            binding.itemSelectLegacy.btnAddFirst.setVisibility(View.GONE);
            binding.itemSelectLegacy.buttonActionsProfile.setVisibility(View.VISIBLE);
        }else {
            binding.changeGlobalProfile.setText(GlobalDataCache.legacyProfileSelected.getName());
            binding.itemSelectLegacy.selectLegacyDescription.setText(
                    getString(R.string.start_adding_questions_to_capture_the_essence_of,
                            GlobalDataCache.legacyProfileSelected.getName()));
            binding.addNewQuestionBtn.setEnabled(true);
            binding.addNewQuestionBtn.setAlpha(1f);

            binding.itemSelectLegacy.btnAddFirst.setVisibility(View.VISIBLE);
            binding.itemSelectLegacy.btnAddFirst.setText(R.string.add_first_question);
            binding.itemSelectLegacy.buttonActionsProfile.setVisibility(View.GONE);
        }
    }


    private void getQuestions(){
        ApiServices apiServices = AppController.getApiServices();
        Call<ResponseGetAssistantQuestions> call = apiServices.getQuestionsAssistant(
                GlobalDataCache.legacyProfileSelected.getId());
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseGetAssistantQuestions> call, Response<ResponseGetAssistantQuestions> response) {
                if (response.isSuccessful()){
                    ResponseGetAssistantQuestions body = response.body();
                    if (body != null){
                        if (body.isStatus()){
                            GlobalDataCache.questions.addAll(body.getQuestions());
//                            questions.addAll(body.getQuestions());
                            if (adapter != null){
                                Log.d(TAG, "UPDATE LIST " + body.getQuestions().size());
                                adapter.updateList(groupByCategory(body.getQuestions(), null));
                                calculateTotalQuestions(body.getQuestions());
                            }
                            checkList();
                            checkStatus();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseGetAssistantQuestions> call, Throwable throwable) {
                Log.d(TAG, "onFailure get messages: " + throwable.getMessage());
            }
        });
    }

    private void checkList() {
        if (!questions.isEmpty()){
            binding.itemSelectLegacy.getRoot().setVisibility(View.GONE);
            binding.questionsRv.setVisibility(View.VISIBLE);
        }else{
            binding.itemSelectLegacy.getRoot().setVisibility(View.VISIBLE);
            binding.questionsRv.setVisibility(View.GONE);
        }
    }

    public void showCategories() {
        BottomSheetDialog categoriesBottomSheet = new BottomSheetDialog(requireContext());
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_categories, null);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewCategories);

        List<String> items = Arrays.asList(
                getString(R.string.agreeableness),
                getString(R.string.conscientiousness),
                getString(R.string.extraversion),
                getString(R.string.neuroticism),
                getString(R.string.openness)
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

    private void showProfilesBottomSheet() {
        BottomSheetDialog profilesBottomSheet = new BottomSheetDialog(requireContext());
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_categories, null);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewCategories);

        List<LegacyProfile> profiles = GlobalDataCache.legacyProfiles;
        if (profiles == null) {
            profiles = new ArrayList<>();
        }

        LegacyItemsAdapter adapter = new LegacyItemsAdapter(profiles);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter.setOnItemClickListener(profile -> {
            processChange(profile, profilesBottomSheet);
        });

        profilesBottomSheet.setContentView(view);
        profilesBottomSheet.show();
    }

    private List<ListItem> groupByCategory(List<Question> list, @Nullable String filterCategory) {
        Map<String, List<Question>> grouped = new LinkedHashMap<>();

        Map<String, String> emojiMap = new HashMap<>();
        emojiMap.put("agreeableness", getString(R.string.agreeableness));
        emojiMap.put("conscientiousness", getString(R.string.conscientiousness));
        emojiMap.put("extraversion", getString(R.string.extraversion));
        emojiMap.put("neuroticism", getString(R.string.neuroticism));
        emojiMap.put("openness", getString(R.string.openness));

        if (filterCategory != null) {
            filterCategory = removeEmojis(filterCategory).trim();
        }

        for (Question question : list) {
            String cat = question.getDimension();
            if (filterCategory != null && !filterCategory.equalsIgnoreCase(cat)) continue;

            if (!grouped.containsKey(cat)) {
                grouped.put(cat, new ArrayList<>());
            }
            grouped.get(cat).add(question);
        }

        List<ListItem> result = new ArrayList<>();
        for (Map.Entry<String, List<Question>> entry : grouped.entrySet()) {
            String normalCat = entry.getKey();
            String displayCat = emojiMap.getOrDefault(normalCat, normalCat);
            result.add(new ListItem(ListItem.TYPE_HEADER, displayCat, null));

            for (Question q : entry.getValue()) {
                result.add(new ListItem(ListItem.TYPE_ITEM, displayCat, q));
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

    public String removeEmojis(String input) {
        if (input == null) return null;
        return input.replaceAll("[^\\p{L}\\p{N}\\p{P}\\p{Z}]", "");
    }

    private void calculateTotalQuestions(List<Question> questions){
        if (questions.isEmpty()){
            binding.totalAnswered.setText("0");
            binding.totalCategories.setText("0");
            binding.totalQuestions.setText("0");
            binding.totalPending.setText("0");
        }else{
            binding.totalQuestions.setText(String.valueOf(questions.size()));
            int answered = 0;
            int pending = 0;

            List<String> categoriesFounded = new ArrayList<>();
            for (Question questionTest : questions){
                if (!categoriesFounded.contains(questionTest.getDimension())){
                    categoriesFounded.add(questionTest.getDimension());
                }
                if (!questionTest.getQuestion().isEmpty() && questionTest.getAnsweredAt() != null){
                    answered++;
                }else if (questionTest.getAnsweredAt() == null){
                    pending++;
                }
            }
            binding.totalAnswered.setText(String.valueOf(answered));
            binding.totalCategories.setText(String.valueOf(categoriesFounded.size()));
            binding.totalPending.setText(String.valueOf(pending));
        }
    }

    private void processChange(LegacyProfile profile, BottomSheetDialog profilesBottomSheet){
        if (GlobalDataCache.legacyProfileSelected == null){
            Map<String, Object> data = new HashMap<>();
            data.put("assistant_id", profile.getId());
            startSession(data, session -> {
                GlobalDataCache.legacyProfiles.set(
                        GlobalDataCache.legacyProfiles.indexOf(profile), session.getAssistant()
                );
                GlobalDataCache.legacyProfileSelected = session.getAssistant();
                GlobalDataCache.sessionId = session.getId();
                binding.changeGlobalProfile.setText(profile.getName(), false);
                getQuestions();
                profilesBottomSheet.dismiss();
            });
        }else if (GlobalDataCache.legacyProfileSelected.getId().equalsIgnoreCase(profile.getId())){
            Log.d(TAG, "IS SAME: ");
            binding.changeGlobalProfile.setText(profile.getName(), false);
            profilesBottomSheet.dismiss();
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
                            binding.changeGlobalProfile.setText(profile.getName(), false);
                            getQuestions();
                            profilesBottomSheet.dismiss();
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
                binding.changeGlobalProfile.setText(profile.getName(), false);
                getQuestions();
                profilesBottomSheet.dismiss();
            });
        }
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


}
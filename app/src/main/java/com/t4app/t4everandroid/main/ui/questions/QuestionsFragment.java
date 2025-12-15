package com.t4app.t4everandroid.main.ui.questions;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.t4app.t4everandroid.AppController;
import com.t4app.t4everandroid.ErrorUtils;
import com.t4app.t4everandroid.ListenersUtils;
import com.t4app.t4everandroid.MessagesUtils;
import com.t4app.t4everandroid.R;
import com.t4app.t4everandroid.SafeClickListener;
import com.t4app.t4everandroid.databinding.FragmentQuestionsBinding;
import com.t4app.t4everandroid.main.GlobalDataCache;
import com.t4app.t4everandroid.main.Models.LegacyProfile;
import com.t4app.t4everandroid.main.Models.ListItem;
import com.t4app.t4everandroid.main.Models.Question;
import com.t4app.t4everandroid.main.T4EverMainActivity;
import com.t4app.t4everandroid.main.adapter.CategoriesAdapter;
import com.t4app.t4everandroid.main.adapter.QuestionGroupedAdapter;
import com.t4app.t4everandroid.main.ui.legacyProfile.LegacyItemsAdapter;
import com.t4app.t4everandroid.main.ui.legacyProfile.LegacyProfilesFragment;
import com.t4app.t4everandroid.main.ui.questions.models.Answer;
import com.t4app.t4everandroid.network.ApiServices;
import com.t4app.t4everandroid.network.RetrofitClient;
import com.t4app.t4everandroid.network.responses.CreateAnswerResponse;
import com.t4app.t4everandroid.network.responses.GetAnswerResponse;
import com.t4app.t4everandroid.network.responses.ResponseGetAssistantQuestions;
import com.t4app.t4everandroid.network.responses.ResponseStartEndSession;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuestionsFragment extends Fragment implements ListenersUtils.CreateQuestionListener {
    private static final String TAG = "QUESTION_FRAG";

    private FragmentQuestionsBinding binding;

    private QuestionGroupedAdapter adapter;

    private List<Question> questions;

    private Map<String, String> emojiMap;

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

        if (isAdded()){
            checkStatus();
        }

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

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        calculateTotalQuestions(questions);
        List<ListItem> grouped = groupByCategory(questions, null);

        Log.d(TAG, "onCreateView: SET ADAPTER");
        adapter = new QuestionGroupedAdapter(requireContext(), grouped, new ListenersUtils.OnQuestionActionsListener() {
            @Override
            public void onDelete(Question question, int pos) {
                MessagesUtils.showMessageConfirmation(requireActivity(), getString(R.string.msg_delete),
                        confirmed -> {
                    if (confirmed){
                        deleteAnswer(question.getAnswer().getId(), () -> {
                            question.setAnswer(null);
                            questions.set(questions.indexOf(question), question);
                            calculateTotalQuestions(questions);
                            adapter.updateItem(pos, question);
                        });

                    }
                });
            }

            @Override
            public void onEdit(Question question, int pos) {
                Log.d(TAG, "onEdit: " + question.getQuestion());
                FragmentManager fm = requireActivity().getSupportFragmentManager();
                Fragment prev = fm.findFragmentByTag("CreateQuestionBottomSheet");
                if (prev != null) {
                    fm.beginTransaction().remove(prev).commit();
                }
                EditQuestionsBottomSheet bottomSheet = EditQuestionsBottomSheet.newInstance(question, pos);
                bottomSheet.show(getChildFragmentManager(), "EditQuestionBottomSheet");
            }
        });
        binding.questionsRv.setAdapter(adapter);
        binding.questionsRv.setLayoutManager(new LinearLayoutManager(requireContext()));

        if (GlobalDataCache.legacyProfileSelected != null){
            getQuestions();
        }

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

            binding.itemSelectLegacy.buttonActionsProfile.setVisibility(View.VISIBLE);

        } else if (GlobalDataCache.legacyProfileSelected == null){
            binding.itemSelectLegacy.selectLegacyDescription.setText(R.string.you_need_to_select_a_legacy_to_start);

            binding.itemSelectLegacy.buttonActionsProfile.setVisibility(View.VISIBLE);
        }else {
            binding.changeGlobalProfile.setText(GlobalDataCache.legacyProfileSelected.getName());
            binding.itemSelectLegacy.selectLegacyDescription.setText(
                    getString(R.string.start_adding_questions_to_capture_the_essence_of,
                            GlobalDataCache.legacyProfileSelected.getName()));

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
                            Map<Integer, Question> questionMap = new HashMap<>();
                            for (Question question : questions) {
                                questionMap.put(question.getQuestionId(), question);
                            }
                            getAnswers(questionMap);
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

    private void getAnswers(Map<Integer, Question> questionMap){
        ApiServices apiServices = RetrofitClient.getChatBotRetrofitClient().create(ApiServices.class);
        Call<GetAnswerResponse> call = apiServices.getAnswers(GlobalDataCache.legacyProfileSelected.getId());
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<GetAnswerResponse> call, Response<GetAnswerResponse> response) {
                if (response.isSuccessful()){
                    GetAnswerResponse body = response.body();
                    if (body != null){
                        if (body.getData() != null){
                            for (Answer answer : body.getData()) {
                                Question question = questionMap.get(answer.getQuestionExternalId());
                                if (question != null) {
                                    question.setAnswer(answer);
                                }
                            }

                            if (adapter != null){
                                adapter.updateList(groupByCategory(GlobalDataCache.questions, null));
                                calculateTotalQuestions(GlobalDataCache.questions);
                            }
                            checkList();
                            if (isAdded()){
                                checkStatus();
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<GetAnswerResponse> call, Throwable throwable) {
                Log.e(TAG, "onFailure: get ANSWER" + throwable.getMessage());
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

        categoriesBottomSheet.setContentView(view);

        categoriesBottomSheet.setOnShowListener(dialogInterface -> {

            BottomSheetDialog dialog = (BottomSheetDialog) dialogInterface;

            FrameLayout bottomSheet = dialog.findViewById(
                    com.google.android.material.R.id.design_bottom_sheet
            );
            if (bottomSheet == null) return;

            bottomSheet.setBackgroundColor(Color.TRANSPARENT);

            BottomSheetBehavior<FrameLayout> behavior =
                    BottomSheetBehavior.from(bottomSheet);

            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            behavior.setSkipCollapsed(true);
            behavior.setPeekHeight(0);
            behavior.setDraggable(true);
        });
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewCategories);
        view.findViewById(R.id.cancel_btn).setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                categoriesBottomSheet.dismiss();
            }
        });

        List<String> items = Arrays.asList(
                getString(R.string.agreeableness),
                getString(R.string.conscientiousness),
                getString(R.string.extraversion),
                getString(R.string.neuroticism),
                getString(R.string.openness)
        );

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                recyclerView.getContext(), LinearLayoutManager.VERTICAL);
        dividerItemDecoration.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(requireActivity(),
                R.drawable.recycler_divider)));
        recyclerView.addItemDecoration(dividerItemDecoration);

        CategoriesAdapter adapter = new CategoriesAdapter(items);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter.setOnItemClickListener(category -> {
            binding.itemSearch.categoriesAuto.setText(category);
            categoriesBottomSheet.dismiss();
        });

        categoriesBottomSheet.show();
    }

    private void showProfilesBottomSheet() {
        BottomSheetDialog profilesBottomSheet = new BottomSheetDialog(requireContext());
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_profiles, null);
        profilesBottomSheet.setContentView(view);

        profilesBottomSheet.setOnShowListener(dialogInterface -> {

            BottomSheetDialog dialog = (BottomSheetDialog) dialogInterface;

            FrameLayout bottomSheet = dialog.findViewById(
                    com.google.android.material.R.id.design_bottom_sheet
            );
            if (bottomSheet == null) return;

            bottomSheet.setBackgroundColor(Color.TRANSPARENT);

            BottomSheetBehavior<FrameLayout> behavior =
                    BottomSheetBehavior.from(bottomSheet);

            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            behavior.setSkipCollapsed(true);
            behavior.setPeekHeight(0);
            behavior.setDraggable(true);
        });

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewProfiles);
        view.findViewById(R.id.cancel_btn).setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                profilesBottomSheet.dismiss();
            }
        });

        List<LegacyProfile> profiles = GlobalDataCache.legacyProfiles;
        if (profiles == null) {
            profiles = new ArrayList<>();
        }

        LegacyItemsAdapter adapter = new LegacyItemsAdapter(profiles);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                recyclerView.getContext(), LinearLayoutManager.VERTICAL);
        dividerItemDecoration.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(requireActivity(),
                R.drawable.recycler_divider)));

        recyclerView.addItemDecoration(dividerItemDecoration);

        adapter.setOnItemClickListener(profile -> {
            processChange(profile, profilesBottomSheet);
        });

        profilesBottomSheet.show();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        emojiMap = new HashMap<>();
        emojiMap.put("agreeableness", getString(R.string.agreeableness));
        emojiMap.put("conscientiousness", getString(R.string.conscientiousness));
        emojiMap.put("extraversion", getString(R.string.extraversion));
        emojiMap.put("neuroticism", getString(R.string.neuroticism));
        emojiMap.put("openness", getString(R.string.openness));
    }

    private List<ListItem> groupByCategory(List<Question> list, @Nullable String filterCategory) {
        Map<String, List<Question>> grouped = new LinkedHashMap<>();

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
            for (Question question : questions){
                if (!categoriesFounded.contains(question.getDimension())){
                    categoriesFounded.add(question.getDimension());
                }
                if (!question.getQuestion().isEmpty() && (question.getAnswer() != null)){
                    answered++;
                }else if (question.getAnswer() == null){
                    pending++;
                }
            }
            binding.totalAnswered.setText(String.valueOf(answered));
            binding.totalCategories.setText(String.valueOf(categoriesFounded.size()));
            binding.totalPending.setText(String.valueOf(pending));
        }
    }

    private void deleteAnswer(String answerId, ListenersUtils.OnActionSuccessListener listener){
        ApiServices apiServices = RetrofitClient.getChatBotRetrofitClient().create(ApiServices.class);
        Call<GetAnswerResponse> call = apiServices.deleteAnswer(answerId);
        call.enqueue(new Callback<GetAnswerResponse>() {
            @Override
            public void onResponse(Call<GetAnswerResponse> call, Response<GetAnswerResponse> response) {
                if (response.isSuccessful()){
                    GetAnswerResponse body = response.body();
                    if (body != null){
                        if (body.getMessage() != null && body.getMessage().contains("Answer deleted")){
                            listener.onSuccess();
                        }else if (body.getError() != null){
                            MessagesUtils.showErrorDialog(requireActivity(), body.getError());
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<GetAnswerResponse> call, Throwable throwable) {
                Log.e(TAG, "onFailure: DELETE ANSWER" + throwable.getMessage());
                MessagesUtils.showErrorDialog(requireActivity(), ErrorUtils.parseError(throwable));
            }
        });
    }

    public void createAnswer(Map<String, Object> data, Question question, int pos){
        ApiServices apiServices = RetrofitClient.getChatBotRetrofitClient().create(ApiServices.class);
        Call<CreateAnswerResponse> call = apiServices.createAnswer(data);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<CreateAnswerResponse> call, Response<CreateAnswerResponse> response) {
                if (response.isSuccessful()) {
                    CreateAnswerResponse body = response.body();
                    if (body != null) {
                        if (body.getMessage() != null && body.getData() != null) {
                            question.setAnswer(body.getData());
                            questions.set(questions.indexOf(question), question);
                            calculateTotalQuestions(questions);
                            adapter.updateItem(pos, question);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<CreateAnswerResponse> call, Throwable throwable) {
                Log.e(TAG, "onFailure: create Answer" + throwable.getMessage());
                MessagesUtils.showErrorDialog(requireActivity(), ErrorUtils.parseError(throwable));
            }
        });
    }

    public void updateAnswer(Map<String, Object> data, Question question, int pos){
        ApiServices apiServices = RetrofitClient.getChatBotRetrofitClient().create(ApiServices.class);
        Call<CreateAnswerResponse> call = apiServices.updateAnswer(question.getAnswer().getId(), data);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<CreateAnswerResponse> call, Response<CreateAnswerResponse> response) {
                if (response.isSuccessful()) {
                    CreateAnswerResponse body = response.body();
                    if (body != null) {
                        if (body.getMessage() != null && body.getData() != null) {
                            question.setAnswer(body.getData());
                            questions.set(questions.indexOf(question), question);
                            calculateTotalQuestions(questions);
                            adapter.updateItem(pos, question);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<CreateAnswerResponse> call, Throwable throwable) {
                Log.e(TAG, "onFailure: create Answer" + throwable.getMessage());
                MessagesUtils.showErrorDialog(requireActivity(), ErrorUtils.parseError(throwable));
            }
        });
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

    @Override
    public void onQuestionUpdated(Question question, String answer,int pos) {
        if (question.getAnswer() != null){
            Map<String, Object> data = new HashMap<>();
            data.put("answer", answer);
            data.put("question", question.getQuestion());
            data.put("questionExternalId", question.getQuestionId());
            updateAnswer(data, question, pos);
        }else{
            Map<String, Object> data = new HashMap<>();
            data.put("answer", answer);
            data.put("legacyProfileId", GlobalDataCache.legacyProfileSelected.getId());
            data.put("question", question.getQuestion());
            data.put("questionExternalId", question.getQuestionId());
            createAnswer(data, question, pos);
        }

    }
}
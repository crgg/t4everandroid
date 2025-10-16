package com.t4app.t4everandroid.main.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.t4app.t4everandroid.R;
import com.t4app.t4everandroid.SafeClickListener;
import com.t4app.t4everandroid.main.Models.QuestionTest;
import com.t4app.t4everandroid.main.adapter.CategoriesAdapter;

import java.util.Arrays;
import java.util.List;

public class CreateQuestionBottomSheet extends BottomSheetDialogFragment {

    public interface CreateQuestionListener {
        void onQuestionCreated(QuestionTest question);
    }

    private AppCompatImageButton closeBtn;
    private AppCompatAutoCompleteTextView categories;
    private MaterialButton saveQuestion;
    private MaterialButton cancelBtn;
    private TextInputLayout questionLayout;
    private TextInputEditText questionValue;
    private TextInputLayout answerLayout;
    private TextInputEditText answerValue;

    private CreateQuestionListener listener;

    public void setListener(CreateQuestionListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.bottom_sheet_create_question, container, false);

        closeBtn = view.findViewById(R.id.btn_close);
        categories = view.findViewById(R.id.auto_category);
        saveQuestion = view.findViewById(R.id.save_question);
        cancelBtn = view.findViewById(R.id.cancel_btn);
        questionLayout = view.findViewById(R.id.question_layout);
        questionValue = view.findViewById(R.id.question_value);
        answerLayout = view.findViewById(R.id.answer_layout);
        answerValue = view.findViewById(R.id.answer_value);
        cancelBtn.setOnClickListener(v -> dismiss());


        categories.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
               showCategories();
            }
        });
        saveQuestion.setOnClickListener(v -> {
            String question = questionValue.getText().toString().trim();
            String answer = answerValue.getText().toString().trim();
            if (!question.isEmpty() && !answer.isEmpty() &&listener != null) {
                QuestionTest questionTest = new QuestionTest();
                questionTest.setQuestion(question);
                questionTest.setAnswer(answer);
                questionTest.setCategory(categories.getText().toString());
                listener.onQuestionCreated(questionTest);

                questionValue.setText("");
                answerValue.setText("");
                dismiss();
            } else {
                questionValue.setError(getString(R.string.you_must_write_a_question));
            }
        });

        return view;
    }

    public void showCategories() {
        BottomSheetDialog categoriesBottomSheet = new BottomSheetDialog(requireContext());
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_categories, null);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewCategories);

        List<String> items = Arrays.asList(
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
            categories.setText(category);
            categoriesBottomSheet.dismiss();
        });

        categoriesBottomSheet.setContentView(view);
        categoriesBottomSheet.show();
    }
}

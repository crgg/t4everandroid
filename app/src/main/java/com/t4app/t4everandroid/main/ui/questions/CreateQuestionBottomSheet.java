package com.t4app.t4everandroid.main.ui.questions;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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
import com.t4app.t4everandroid.main.Models.Question;
import com.t4app.t4everandroid.main.adapter.CategoriesAdapter;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CreateQuestionBottomSheet extends BottomSheetDialogFragment {

    private static final String TAG = "CREATE_QUESTION";

    public interface CreateQuestionListener {
        void onQuestionCreated(Question question);
        void onQuestionUpdated(Question question, int pos);
    }

    private AppCompatImageButton closeBtn;
    private AppCompatAutoCompleteTextView categories;
    private MaterialButton saveQuestion;
    private MaterialButton cancelBtn;
    private TextInputLayout questionLayout;
    private TextInputEditText questionValue;
    private TextInputLayout answerLayout;
    private TextInputEditText answerValue;

    private boolean isEdit;
    private Question questionEdit;
    private int updatePos;

    private CreateQuestionListener listener;

    public void setListener(CreateQuestionListener listener) {
        this.listener = listener;
    }

    public void setEdit(boolean edit) {
        this.isEdit = edit;
    }

    public void setUpdatePos(int updatePos) {
        this.updatePos = updatePos;
    }

    public void setQuestionEdit(Question questionEdit) {
        this.questionEdit = questionEdit;
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
        closeBtn.setOnClickListener(v -> dismiss());

        if (isEdit){
            Log.d(TAG, "onCreateView: " + isEdit + " QUESTION: " + questionEdit.getQuestion());
            view.post(() -> {
                questionValue.setText(questionEdit.getQuestion());
//                answerValue.setText(questionEdit.getAnswer());
                categories.setText(questionEdit.getDimension());
            });
        }

        categories.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
               showCategories();
            }
        });
        saveQuestion.setOnClickListener(v -> {
            String question = questionValue.getText().toString().trim();
            String answer = answerValue.getText().toString().trim();
            if (!question.isEmpty() && !answer.isEmpty() && listener != null) {
                if (isEdit){
                    questionEdit.setDimension(removeEmojis(categories.getText().toString()));
                    questionEdit.setQuestion(question);
                    questionEdit.setAnsweredAt(getCurrentDateTime());

                    listener.onQuestionUpdated(questionEdit, updatePos);
                }else{
                    Question questionTest = new Question();
                    questionTest.setQuestion(question);
                    questionTest.setAnsweredAt(getCurrentDateTime());
                    questionTest.setDimension(removeEmojis(categories.getText().toString()));
                    listener.onQuestionCreated(questionTest);
                }

                questionValue.setText("");
                answerValue.setText("");
                dismiss();
            } else {
                questionValue.setError(getString(R.string.you_must_write_a_question));
            }
        });

        return view;
    }

    public String getCurrentDateTime() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime now = null;
            now = LocalDateTime.now();
            return now.format(formatter);
        }else{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date now = new Date();
            return sdf.format(now);
        }

    }

    public String removeEmojis(String input) {
        if (input == null) return null;
        return input.replaceAll("[^\\p{L}\\p{N}\\p{P}\\p{Z}]", "");
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
            categories.setText(category);
            categoriesBottomSheet.dismiss();
        });

        categoriesBottomSheet.setContentView(view);
        categoriesBottomSheet.show();
    }
}

package com.t4app.t4everandroid.main.ui.questions;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.t4app.t4everandroid.ListenersUtils;
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

public class EditQuestionsBottomSheet extends BottomSheetDialogFragment {

    private static final String TAG = "CREATE_QUESTION";
    private static final String ARG_QUESTION = "arg_question";
    private static final String ARG_POS = "arg_pos";

    private TextView questionValueText;

    private TextInputLayout answerLayout;
    private TextInputEditText answerValue;

    private AppCompatAutoCompleteTextView categories;

    private MaterialButton saveQuestion;
    private MaterialButton cancelBtn;
    private AppCompatImageButton closeBtn;

    private Question questionEdit;
    private int updatePos;

    private ListenersUtils.CreateQuestionListener listener;

    public static EditQuestionsBottomSheet newInstance(Question question, int position) {
        EditQuestionsBottomSheet sheet = new EditQuestionsBottomSheet();

        Bundle args = new Bundle();
        args.putSerializable(ARG_QUESTION, question);
        args.putInt(ARG_POS, position);
        sheet.setArguments(args);

        return sheet;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        Fragment parent = getParentFragment();
        if (parent instanceof ListenersUtils.CreateQuestionListener) {
            listener = (ListenersUtils.CreateQuestionListener) parent;
        } else if (context instanceof ListenersUtils.CreateQuestionListener) {
            listener = (ListenersUtils.CreateQuestionListener) context;
        } else {
            throw new IllegalStateException(
                    "Parent Fragment o Activity deben implementar CreateQuestionListener"
            );
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            questionEdit = (Question) args.getSerializable(ARG_QUESTION);
            updatePos = args.getInt(ARG_POS);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.bottom_sheet_create_question, container, false);

        closeBtn = view.findViewById(R.id.btn_close);
        questionValueText = view.findViewById(R.id.question_value_text);
        categories = view.findViewById(R.id.auto_category);
        saveQuestion = view.findViewById(R.id.save_question);
        cancelBtn = view.findViewById(R.id.cancel_btn);
        answerLayout = view.findViewById(R.id.answer_layout);
        answerValue = view.findViewById(R.id.answer_value);
        cancelBtn.setOnClickListener(v -> dismiss());
        closeBtn.setOnClickListener(v -> dismiss());

        if (questionEdit.getAnswer() != null){
            answerValue.setText(questionEdit.getAnswer().getAnswer());
        }

        view.post(() -> {
            questionValueText.setText(questionEdit.getQuestion());
            if (getString(R.string.extraversion).toLowerCase().contains(questionEdit.getDimension().toLowerCase())) {
                categories.setText(R.string.extraversion);
            } else if (getString(R.string.conscientiousness).toLowerCase().contains(questionEdit.getDimension().toLowerCase())) {
                categories.setText(R.string.conscientiousness);
            } else if (getString(R.string.agreeableness).toLowerCase().contains(questionEdit.getDimension().toLowerCase())) {
                categories.setText(R.string.agreeableness);
            } else if (getString(R.string.neuroticism).toLowerCase().contains(questionEdit.getDimension().toLowerCase())) {
                categories.setText(R.string.neuroticism);
            } else if (getString(R.string.openness).toLowerCase().contains(questionEdit.getDimension().toLowerCase())) {
                categories.setText(R.string.openness);
            } else {
                categories.setText(questionEdit.getDimension());
            }


        });

        saveQuestion.setOnClickListener(v -> {
            String answer = answerValue.getText().toString().trim();
            if (!answer.isEmpty() && listener != null) {
                questionEdit.setDimension(removeEmojis(categories.getText().toString()));

                listener.onQuestionUpdated(questionEdit, answer, updatePos);

                answerValue.setText("");
                dismiss();
            } else {
                answerValue.setError(getString(R.string.you_must_write_a_question));
            }
        });

        return view;
    }


    public String removeEmojis(String input) {
        if (input == null) return null;
        return input.replaceAll("[^\\p{L}\\p{N}\\p{P}\\p{Z}]", "");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewTreeObserver.OnGlobalLayoutListener listener2 =
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        view.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                        BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
                        if (dialog == null) return;

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
                    }
                };
        view.getViewTreeObserver().addOnGlobalLayoutListener(listener2);
    }
}

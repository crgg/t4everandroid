package com.t4app.t4everandroid.main.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.t4app.t4everandroid.R;

public class AddEmailBottomSheet extends BottomSheetDialogFragment {

    private TextInputEditText toValue, subjectValue, messageValue;
    private AppCompatImageButton btnClose;
    private MaterialButton cancelBtn, forwardBtn;

    public interface OnEmailSendListener {
        void onSend(String to, String subject, String message);
    }

    private OnEmailSendListener listener;

    public AddEmailBottomSheet(OnEmailSendListener listener) {
        this.listener = listener;
    }

    public AddEmailBottomSheet() {}

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_email_bottom_sheet, container, false);

        btnClose = view.findViewById(R.id.btn_close);
        toValue = view.findViewById(R.id.to_value);
        subjectValue = view.findViewById(R.id.subject_value);
        messageValue = view.findViewById(R.id.message_value);
        cancelBtn = view.findViewById(R.id.cancel_btn);
        forwardBtn = view.findViewById(R.id.forward_btn);

        btnClose.setOnClickListener(v -> dismiss());
        cancelBtn.setOnClickListener(v -> dismiss());

        forwardBtn.setOnClickListener(v -> {
            String to = toValue.getText() != null ? toValue.getText().toString().trim() : "";
            String subject = subjectValue.getText() != null ? subjectValue.getText().toString().trim() : "";
            String message = messageValue.getText() != null ? messageValue.getText().toString().trim() : "";

            if (to.isEmpty()) {
                toValue.setError(getString(R.string.required));
                return;
            }

            if (subject.isEmpty()) subject = "(No subject)";
            if (message.isEmpty()) {
                messageValue.setError(getString(R.string.required));
                return;
            }

            if (listener != null) {
                toValue.setText("");
                subjectValue.setText("");
                messageValue.setText("");
                listener.onSend(to, subject, message);
            }

            dismiss();
        });

        return view;
    }
}


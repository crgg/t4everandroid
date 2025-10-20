package com.t4app.t4everandroid;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ImageSelectorBottomSheet extends BottomSheetDialogFragment {

    public interface Listener {
        void onCameraSelected();
        void onGallerySelected();
    }

    private Listener listener;

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.image_selector_layout, container, false);

        LinearLayout cameraOption = view.findViewById(R.id.camera_option);
        LinearLayout galleryOption = view.findViewById(R.id.gallery_option);
        Button cancelBtn = view.findViewById(R.id.cancel_btn);

        cameraOption.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                if (listener != null) listener.onCameraSelected();
                dismiss();
            }
        });

        galleryOption.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                if (listener != null) listener.onGallerySelected();
                dismiss();
            }
        });

        if (cancelBtn != null) {
            cancelBtn.setOnClickListener(new SafeClickListener() {
                @Override
                public void onSafeClick(View v) {
                    dismiss();
                }
            });
        }

        return view;
    }

}


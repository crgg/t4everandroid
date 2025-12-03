package com.t4app.t4everandroid;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ImageSelectorBottomSheet extends BottomSheetDialogFragment {
    private ListenersUtils.OnOptionImageListener listener;

    public static ImageSelectorBottomSheet newInstance() {
        return new ImageSelectorBottomSheet();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        Fragment parent = getParentFragment();
        if (parent instanceof ListenersUtils.OnOptionImageListener) {
            listener = (ListenersUtils.OnOptionImageListener) parent;
        } else if (context instanceof ListenersUtils.OnOptionImageListener) {
            listener = (ListenersUtils.OnOptionImageListener) context;
        } else {
            throw new IllegalStateException(
                    "Parent Fragment o Activity deben implementar OnOptionImageListener"
            );
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
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


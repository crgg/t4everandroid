package com.t4app.t4everandroid.main.ui.media;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.t4app.t4everandroid.R;
import com.t4app.t4everandroid.SafeClickListener;

public class UploadMediaBottomSheet extends BottomSheetDialogFragment {

    private static final String TAG = "UPDATE_MEDIA";
    private String type;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_add_media_layout, container, false);

        LinearLayout optionText = view.findViewById(R.id.option_text);
        LinearLayout optionAudio = view.findViewById(R.id.option_audio);
        LinearLayout optionImage = view.findViewById(R.id.option_image);
        LinearLayout optionVideo = view.findViewById(R.id.option_video);

        ImageView textIcon = view.findViewById(R.id.icon_text);
        ImageView audioIcon = view.findViewById(R.id.icon_audio);
        ImageView videoIcon = view.findViewById(R.id.icon_video);
        ImageView imageIcon = view.findViewById(R.id.icon_image);

        TextView text = view.findViewById(R.id.text);
        TextView audio = view.findViewById(R.id.audio);
        TextView video = view.findViewById(R.id.video);
        TextView image = view.findViewById(R.id.image);

        MaterialButton cancelBtn = view.findViewById(R.id.cancel_btn);
        AppCompatImageButton closeBtn = view.findViewById(R.id.btn_close);

        optionText.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                type = "text";

                text.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
                audio.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_color));
                video.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_color));
                image.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_color));

                textIcon.setImageTintList(
                        ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
                );
                imageIcon.setImageTintList(
                        ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.second_login_color))
                );
                audioIcon.setImageTintList(
                        ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.second_login_color))
                );
                videoIcon.setImageTintList(
                        ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.second_login_color))
                );

                optionText.setBackgroundTintList(
                        ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.second_login_color))
                );
                optionAudio.setBackgroundTintList(
                        null
                );
                optionVideo.setBackgroundTintList(
                        null
                );
                optionImage.setBackgroundTintList(
                        null
                );

            }
        });

        optionAudio.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                type = "audio";

                text.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_color));
                audio.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
                video.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_color));
                image.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_color));

                textIcon.setImageTintList(
                        ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.second_login_color))
                );
                audioIcon.setImageTintList(
                        ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
                );
                videoIcon.setImageTintList(
                        ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.second_login_color))
                );
                imageIcon.setImageTintList(
                        ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.second_login_color))
                );

                optionText.setBackgroundTintList(
                        null
                );
                optionAudio.setBackgroundTintList(
                        ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.second_login_color))
                );
                optionVideo.setBackgroundTintList(
                        null
                );
                optionImage.setBackgroundTintList(
                        null
                );
            }
        });

        optionVideo.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                type = "video";

                text.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_color));
                audio.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_color));
                image.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_color));
                video.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));

                textIcon.setImageTintList(
                        ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.second_login_color))
                );
                audioIcon.setImageTintList(
                        ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.second_login_color))
                );
                imageIcon.setImageTintList(
                        ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.second_login_color))
                );
                videoIcon.setImageTintList(
                        ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
                );

                optionText.setBackgroundTintList(
                        null
                );
                optionAudio.setBackgroundTintList(
                        null
                );
                optionImage.setBackgroundTintList(
                        null
                );
                optionVideo.setBackgroundTintList(
                        ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.second_login_color))
                );
            }
        });

        optionImage.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                type = "video";

                text.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_color));
                audio.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_color));
                image.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
                video.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_color));

                textIcon.setImageTintList(
                        ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.second_login_color))
                );
                audioIcon.setImageTintList(
                        ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.second_login_color))
                );
                imageIcon.setImageTintList(
                        ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
                );
                videoIcon.setImageTintList(
                        ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.second_login_color))
                );

                optionText.setBackgroundTintList(
                        null
                );
                optionAudio.setBackgroundTintList(
                        null
                );
                optionImage.setBackgroundTintList(
                        ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.second_login_color))
                );
                optionVideo.setBackgroundTintList(
                       null
                );
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

        if (closeBtn != null) {
            closeBtn.setOnClickListener(new SafeClickListener() {
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
    }

}

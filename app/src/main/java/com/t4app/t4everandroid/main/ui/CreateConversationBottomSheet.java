package com.t4app.t4everandroid.main.ui;


import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.t4app.t4everandroid.R;
import com.t4app.t4everandroid.SafeClickListener;

import java.io.IOException;

public class CreateConversationBottomSheet extends BottomSheetDialogFragment {

    private MaterialButton recordAudioBtn;
    private MaterialButton recordVideoBtn;

    private ActivityResultLauncher<Intent> launcher;
    private AudioPlayerView audioPlayerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_conversation_layout, container, false);

        LinearLayout optionText = view.findViewById(R.id.option_text);
        LinearLayout optionAudio = view.findViewById(R.id.option_audio);
        LinearLayout optionVideo = view.findViewById(R.id.option_video);

        LinearLayout containerText = view.findViewById(R.id.container_text);
        recordAudioBtn = view.findViewById(R.id.start_record_audio);
        recordVideoBtn = view.findViewById(R.id.start_record_video);

        audioPlayerView = view.findViewById(R.id.audioPlayer);

        ImageView textIcon = view.findViewById(R.id.icon_text);
        ImageView audioIcon = view.findViewById(R.id.icon_audio);
        ImageView videoIcon = view.findViewById(R.id.icon_video);

        TextView text = view.findViewById(R.id.text);
        TextView audio = view.findViewById(R.id.audio);
        TextView video = view.findViewById(R.id.video);

        MaterialButton cancelBtn = view.findViewById(R.id.cancel_btn);
        AppCompatImageButton closeBtn = view.findViewById(R.id.btn_close);

        optionText.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                containerText.setVisibility(View.VISIBLE);
                recordAudioBtn.setVisibility(View.GONE);
                recordVideoBtn.setVisibility(View.GONE);

                text.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
                audio.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_color));
                video.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_color));

                textIcon.setImageTintList(
                        ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
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
            }
        });

        optionAudio.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                containerText.setVisibility(View.GONE);
                recordAudioBtn.setVisibility(View.VISIBLE);
                recordVideoBtn.setVisibility(View.GONE);

                text.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_color));
                audio.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
                video.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_color));

                textIcon.setImageTintList(
                        ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.second_login_color))
                );
                audioIcon.setImageTintList(
                        ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
                );
                videoIcon.setImageTintList(
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
            }
        });

        optionVideo.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                containerText.setVisibility(View.GONE);
                recordAudioBtn.setVisibility(View.GONE);
                recordVideoBtn.setVisibility(View.VISIBLE);

                text.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_color));
                audio.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_color));
                video.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));

                textIcon.setImageTintList(
                        ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.second_login_color))
                );
                audioIcon.setImageTintList(
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
                optionVideo.setBackgroundTintList(
                        ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.second_login_color))
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
        launcher =
                registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        String uriString = result.getData().getStringExtra("audio_uri");
                        Uri uri = Uri.parse(uriString);
                        setupAudioPlayer(uri);
//                        MediaPlayer player = MediaPlayer.create(this, audioUri);
//                        player.start();
                    }
                });

        recordAudioBtn.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                Intent intent = new Intent(requireActivity(), RecordAudioActivity.class);
                launcher.launch(intent);
            }
        });

        recordVideoBtn.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {

            }
        });
    }

    public void setupAudioPlayer(Uri audioUri) {
        if (audioPlayerView != null) {
            audioPlayerView.setVisibility(View.VISIBLE);
            audioPlayerView.setAudioUri(audioUri);
        }
    }

    public void hideAudioPlayer() {
        if (audioPlayerView != null) {
            audioPlayerView.setVisibility(View.GONE);
            audioPlayerView.stopPlayback();
        }
    }

}

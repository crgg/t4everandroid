package com.t4app.t4everandroid.main.ui;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.t4app.t4everandroid.ListenersUtils;
import com.t4app.t4everandroid.R;
import com.t4app.t4everandroid.SafeClickListener;
import com.t4app.t4everandroid.main.Models.ConversationTest;

import java.io.File;

public class CreateConversationBottomSheet extends BottomSheetDialogFragment {
    private static final String TAG = "CREATE_CONVERSATION";
    private MaterialButton recordAudioBtn;
    private MaterialButton recordVideoBtn;
    private MaterialButton saveConversation;

    private ActivityResultLauncher<Intent> launcher;
    private AudioPlayerView audioPlayerView;

    private Uri videoUri;
    private Uri audioUri;
    private ActivityResultLauncher<Intent> videoLauncher;

    private MaterialButton videoView;
    private String type;
    private String textConversationValue;

    private TextInputEditText conversationText;
    private TextInputLayout conversationTextLayout;

    private ListenersUtils.OnConversationAddedListener listener;

    public void setListener(ListenersUtils.OnConversationAddedListener listener) {
        this.listener = listener;
    }

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
        conversationText = view.findViewById(R.id.conversation_value);
        conversationTextLayout = view.findViewById(R.id.conversation_layout);
        recordAudioBtn = view.findViewById(R.id.start_record_audio);
        recordVideoBtn = view.findViewById(R.id.start_record_video);

        audioPlayerView = view.findViewById(R.id.audioPlayer);
        videoView = view.findViewById(R.id.video_player);

        ImageView textIcon = view.findViewById(R.id.icon_text);
        ImageView audioIcon = view.findViewById(R.id.icon_audio);
        ImageView videoIcon = view.findViewById(R.id.icon_video);

        TextView text = view.findViewById(R.id.text);
        TextView audio = view.findViewById(R.id.audio);
        TextView video = view.findViewById(R.id.video);

        MaterialButton cancelBtn = view.findViewById(R.id.cancel_btn);
        saveConversation = view.findViewById(R.id.save_conversation);
        AppCompatImageButton closeBtn = view.findViewById(R.id.btn_close);

        optionText.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                type = "text";
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
                videoView.setVisibility(View.GONE);
            }
        });

        optionAudio.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                type = "audio";
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
                videoView.setVisibility(View.GONE);
            }
        });

        optionVideo.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                type = "video";
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
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        String uriString = result.getData().getStringExtra("audio_uri");
                        audioUri = Uri.parse(uriString);
                        setupAudioPlayer(audioUri);
                    }
                });

        videoLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            videoUri = data.getData();
                        }

                        if (videoUri != null) {
                            videoView.setVisibility(View.VISIBLE);

                        }else{
                            Log.d(TAG, "VIDEO URI IS NULL");
                        }
                    }
                }
        );

        videoView.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                showVideoPopup(videoUri);
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
                startVideoCapture();
            }
        });

        saveConversation.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                ConversationTest conversationTest = new ConversationTest();
                conversationTest.setType(type);
                if (type.equalsIgnoreCase("audio")){
                    conversationTest.setUri(audioUri);
                } else if (type.equalsIgnoreCase("video")) {
                    conversationTest.setUri(videoUri);
                }else {
                    textConversationValue = conversationText.getText().toString().trim();
                    if (textConversationValue.isEmpty()){
                        conversationTextLayout.setError(getString(R.string.the_text_cannot_be_empty));
                        conversationTextLayout.setErrorIconDrawable(null);
                        conversationText.requestFocus();
                        return;
                    }else{
                        conversationTest.setText(textConversationValue);
                    }

                }
                conversationText.setText("");
                listener.onAddConversation(conversationTest);
                dismiss();
            }
        });
    }

    private void startVideoCapture() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        File videoFile = new File(
                requireActivity().getExternalFilesDir(Environment.DIRECTORY_MOVIES),
                "video_" + System.currentTimeMillis() + ".mp4"
        );

        videoUri = FileProvider.getUriForFile(requireContext(), requireActivity().getPackageName()
                + ".provider", videoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 60);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);

        videoLauncher.launch(intent);
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

    private void showVideoPopup(Uri videoUri) {
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_video_player, null);

        VideoView videoView = view.findViewById(R.id.videoView);
        ImageButton btnPlayPause = view.findViewById(R.id.btnPlayPause);
        ImageButton btnForward = view.findViewById(R.id.btnForward);
        ImageButton btnRewind = view.findViewById(R.id.btnRewind);

        videoView.setVideoURI(videoUri);

        videoView.setOnPreparedListener(mp -> {
            mp.setLooping(false);
            videoView.start();
            btnPlayPause.setImageResource(R.drawable.ic_pause_48);
        });

        btnPlayPause.setOnClickListener(v -> {
            if (videoView.isPlaying()) {
                videoView.pause();
                btnPlayPause.setImageResource(R.drawable.ic_play_48);
            } else {
                videoView.start();
                btnPlayPause.setImageResource(R.drawable.ic_pause_48);
            }
        });

        btnForward.setOnClickListener(v -> {
            int pos = videoView.getCurrentPosition() + 5000;
            videoView.seekTo(pos);
        });

        btnRewind.setOnClickListener(v -> {
            int pos = videoView.getCurrentPosition() - 5000;
            videoView.seekTo(Math.max(pos, 0));
        });

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(view)
                .setCancelable(true)
                .create();

        dialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (audioPlayerView != null) {
            audioPlayerView.cleanupMediaPlayer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (audioPlayerView != null && audioPlayerView.isPlaying()) {
            audioPlayerView.stopPlayback();
        }
    }
}

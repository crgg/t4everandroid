package com.t4app.t4everandroid.main.ui.media;

import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.t4app.t4everandroid.R;
import com.t4app.t4everandroid.SafeClickListener;
import com.t4app.t4everandroid.main.Models.Media;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ViewerDocumentBottomSheet extends BottomSheetDialogFragment {
    private static final String TAG = "VIEWER_MEDIA";

    private ExoPlayer player;

    private TextView textContent;
    private TextView fileName;
    private View itemVideoPlayer;
    private SeekBar seekBarVideo;
    private AudioPlayerView audioPlayerView;
    private ImageView contentImage;

    private PlayerView videoView;
    private ImageButton btnPlayPause;
    private ImageButton btnForward;
    private ImageButton btnRewind;

    private Handler handler;

    private Media media;

    public void setMedia(Media media) {
        this.media = media;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.viewer_doc_bottom_sheet, container, false);

        textContent = view.findViewById(R.id.text_content);
        fileName = view.findViewById(R.id.file_name);
        itemVideoPlayer = view.findViewById(R.id.item_video_player);
        audioPlayerView = view.findViewById(R.id.audioPlayer);
        contentImage = view.findViewById(R.id.content_image);

        videoView = itemVideoPlayer.findViewById(R.id.videoView);
        btnPlayPause = itemVideoPlayer.findViewById(R.id.btnPlayPause);
        btnForward = itemVideoPlayer.findViewById(R.id.btnForward);
        btnRewind = itemVideoPlayer.findViewById(R.id.btnRewind);
        seekBarVideo = itemVideoPlayer.findViewById(R.id.seekBarVideo);

        view.findViewById(R.id.btn_close).setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                dismiss();
            }
        });

        view.findViewById(R.id.btn_close_image).setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                dismiss();
            }
        });

        textContent.setMovementMethod(new ScrollingMovementMethod());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        String name = extractReadableName(media.getStorageUrl());
        fileName.setText(name);
        Log.d(TAG, "FILE NAME: " + name);
        switch (media.getType()){
            case "text":
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(media.getStorageUrl())
                        .build();
                textContent.setVisibility(View.VISIBLE);

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        Log.e(TAG, "onFailure LOAD TEXT ERROR ", e);
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if (response.isSuccessful()){
                            String content = response.body().string();
                            requireActivity().runOnUiThread(() -> {
                                textContent.setText(content);
                            });
                        }else{
                            requireActivity().runOnUiThread(() -> {
                                textContent.setText("Error load content");
                            });
                        }
                    }
                });

                return;
            case "image":
                contentImage.setVisibility(View.VISIBLE);
                Glide.with(requireActivity())
                        .load(media.getStorageUrl())
                        .into(contentImage);
                return;
            case "audio":
                audioPlayerView.setVisibility(View.VISIBLE);
                setupAudioPlayer(media.getStorageUrl());

                return;
            case "video":
                itemVideoPlayer.setVisibility(View.VISIBLE);
                player = new ExoPlayer.Builder(requireContext()).build();
                videoView.setPlayer(player);

                String videoUrl = media.getStorageUrl();
                MediaItem mediaItem = MediaItem.fromUri(videoUrl);
                player.setMediaItem(mediaItem);

                player.prepare();
                player.play();

        }
    }

    public void setupAudioPlayer(String audioUrl) {
        if (audioPlayerView != null) {
            audioPlayerView.setVisibility(View.VISIBLE);
            audioPlayerView.setAudioUrl(audioUrl);
        }
    }

    public void hideAudioPlayer() {
        if (audioPlayerView != null) {
            audioPlayerView.setVisibility(View.GONE);
            audioPlayerView.stopPlayback();
        }
    }

    public String extractReadableName(String fileName) {
        if (fileName == null || fileName.isEmpty()) return "";

        int lastSlash = fileName.lastIndexOf('/');
        if (lastSlash != -1) {
            fileName = fileName.substring(lastSlash + 1);
        }

        return fileName.replaceFirst("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}-", "");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (audioPlayerView != null) {
            audioPlayerView.cleanupMediaPlayer();
        }
        if (handler != null){
            handler.removeCallbacksAndMessages(null);
        }

        if (videoView != null){
            videoView.setPlayer(null);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (player != null) {
            player.pause();
            player.release();
            player = null;
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        if (audioPlayerView != null && audioPlayerView.isPlaying()) {
            audioPlayerView.stopPlayback();
        }

        if (player != null) {
            player.pause();
        }
    }

}

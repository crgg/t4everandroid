package com.t4app.t4everandroid.main.ui.chat;

import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
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
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.t4app.t4everandroid.R;
import com.t4app.t4everandroid.SafeClickListener;
import com.t4app.t4everandroid.main.ui.chat.models.InlineData;
import com.t4app.t4everandroid.main.ui.media.AudioPlayerView;

import java.util.Locale;

public class ViewerInlineDataBottomSheet extends BottomSheetDialogFragment {
    private static final String TAG = "VIEWER_INLINE";

    private static final String ARG_INLINE = "arg_inline";
    private static final String ARG_TYPE_DOC = "arg_type_doc";
    private static final String ARG_FROM_URI = "arg_from_uri";
    private static final String ARG_URI = "arg_uri";

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

    private InlineData inlineData;

    private String typeDoc;   // "text" | "image" | "audio" | "video"


    public static ViewerInlineDataBottomSheet newInstance(InlineData inlineData) {
        ViewerInlineDataBottomSheet bottomSheet = new ViewerInlineDataBottomSheet();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_INLINE, inlineData);
        bottomSheet.setArguments(bundle);
        return bottomSheet;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            inlineData = (InlineData) args.getSerializable(ARG_INLINE);
            typeDoc = args.getString(ARG_TYPE_DOC);
        }
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
            @Override public void onSafeClick(View v) { dismiss(); }
        });

        view.findViewById(R.id.btn_close_image).setOnClickListener(new SafeClickListener() {
            @Override public void onSafeClick(View v) { dismiss(); }
        });

        textContent.setMovementMethod(new ScrollingMovementMethod());

        textContent.setVisibility(View.GONE);
        contentImage.setVisibility(View.GONE);
        audioPlayerView.setVisibility(View.GONE);
        itemVideoPlayer.setVisibility(View.GONE);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
                if (dialog == null) return;

                FrameLayout bottomSheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
                if (bottomSheet == null) return;

                bottomSheet.setBackgroundColor(Color.TRANSPARENT);
                BottomSheetBehavior<FrameLayout> behavior = BottomSheetBehavior.from(bottomSheet);

                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                behavior.setSkipCollapsed(true);
                behavior.setPeekHeight(0);
                behavior.setDraggable(true);
            }
        });

        String url = inlineData != null ? inlineData.getFileUrl() : null;
        String name = extractReadableName(url);

        fileName.setText(name);

        String type;

        type = inlineTypeFromMime(inlineData != null ? inlineData.getMimeType() : null);


        switch (type) {
            case "image":
                contentImage.setVisibility(View.VISIBLE);
                String urlStr = inlineData != null ? inlineData.getFileUrl() : null;
                Glide.with(requireActivity()).load(urlStr).into(contentImage);
                return;
            case "audio":
                audioPlayerView.setVisibility(View.VISIBLE);
                String audioUrl = inlineData != null ? inlineData.getFileUrl() : null;
                setupAudioPlayer(audioUrl);
                return;

            case "video":
                itemVideoPlayer.setVisibility(View.VISIBLE);

                player = new ExoPlayer.Builder(requireContext()).build();
                videoView.setPlayer(player);

                MediaItem mediaItem = MediaItem.fromUri(inlineData.getFileUrl());

                player.setMediaItem(mediaItem);
                player.prepare();
                player.play();

        }
    }

    private String inlineTypeFromMime(String mime) {
        if (mime == null) return "text";
        mime = mime.toLowerCase(Locale.US);

        if (mime.startsWith("image/")) return "image";
        if (mime.startsWith("audio/")) return "audio";
        if (mime.startsWith("video/")) return "video";
        if (mime.startsWith("text/") || mime.contains("json") || mime.contains("xml")) return "text";
        return "text";
    }

    public void setupAudioPlayer(String audioUrl) {
        if (audioPlayerView != null) {
            audioPlayerView.setVisibility(View.VISIBLE);
            audioPlayerView.setAudioUrl(audioUrl);
        }
    }


    public String extractReadableName(String fileName) {
        if (fileName == null || fileName.isEmpty()) return "";

        int lastSlash = fileName.lastIndexOf('/');
        if (lastSlash != -1) fileName = fileName.substring(lastSlash + 1);

        return fileName.replaceFirst("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}-", "");
    }

    @Override
    public void onPause() {
        super.onPause();
        if (audioPlayerView != null && audioPlayerView.isPlaying()) audioPlayerView.stopPlayback();
        if (player != null) player.pause();
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
    public void onDestroyView() {
        super.onDestroyView();
        if (audioPlayerView != null) audioPlayerView.cleanupMediaPlayer();
        if (handler != null) handler.removeCallbacksAndMessages(null);
        if (videoView != null) videoView.setPlayer(null);
    }
}


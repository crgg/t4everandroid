package com.t4app.t4everandroid.main.ui.media;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.t4app.t4everandroid.AppController;
import com.t4app.t4everandroid.CameraPermissionManager;
import com.t4app.t4everandroid.ErrorUtils;
import com.t4app.t4everandroid.ListenersUtils;
import com.t4app.t4everandroid.MessagesUtils;
import com.t4app.t4everandroid.R;
import com.t4app.t4everandroid.SafeClickListener;
import com.t4app.t4everandroid.main.GlobalDataCache;
import com.t4app.t4everandroid.network.ApiServices;
import com.t4app.t4everandroid.network.responses.ResponseCreateMedia;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateMediaBottomSheet extends BottomSheetDialogFragment {
    private static final String TAG = "CREATE_CONVERSATION";

    private String type;
    private String textConversationValue;

    private Uri videoUri;
    private Uri audioUri;

    private ListenersUtils.OnMediaAddedListener listener;
    private CameraPermissionManager.PermissionCallback permissionCallback;

    private ActivityResultLauncher<Intent> launcher;
    private ActivityResultLauncher<Intent> videoLauncher;
    private ActivityResultLauncher<String> cameraPermissionLauncher;

    private AudioPlayerView audioPlayerView;

    private LinearLayout dataContainer;
    private LinearLayout loadContainer;
    private LinearLayout containerText;
    private LinearLayout optionText;
    private LinearLayout optionAudio;
    private LinearLayout optionVideo;

    private TextInputEditText conversationText;
    private TextInputLayout conversationTextLayout;

    private ImageView textIcon;
    private ImageView audioIcon;
    private ImageView videoIcon;

    private TextView text;
    private TextView audio;
    private TextView video;


    private MaterialButton recordAudioBtn;
    private MaterialButton recordVideoBtn;
    private MaterialButton saveConversation;
    private MaterialButton videoView;
    private MaterialButton cancelBtn;
    private AppCompatImageButton closeBtn;

    public static CreateMediaBottomSheet newInstance(){
        return new CreateMediaBottomSheet();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        Fragment parent = getParentFragment();
        if (parent instanceof ListenersUtils.OnMediaAddedListener) {
            listener = (ListenersUtils.OnMediaAddedListener) parent;
        } else if (context instanceof ListenersUtils.OnMediaAddedListener) {
            listener = (ListenersUtils.OnMediaAddedListener) context;
        } else {
            throw new IllegalStateException(
                    "Parent Fragment o Activity deben implementar OnMediaAddedListener"
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
        setupPermissionCallback();
        setupPermissionLauncher();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_media_layout, container, false);

        initViews(view);

        optionText.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                type = "text";
                applyMediaTypeState(type);
            }
        });

        optionAudio.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                type = "audio";
                applyMediaTypeState(type);
            }
        });

        optionVideo.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                type = "video";
                applyMediaTypeState(type);
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
                            long sizeBytes = getFileSizeFromUri(requireContext(), videoUri);
                            if (sizeBytes > 0) {
                                double sizeMB = sizeBytes / (1024.0 * 1024.0);
                                Log.d(TAG, "Video size: " + sizeMB + " MB");
                            }

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
                if (CameraPermissionManager.hasCameraPermission(requireContext())){
                    startVideoCapture();
                }else{
                    CameraPermissionManager.requestCameraPermission(
                            requireActivity(),
                            cameraPermissionLauncher,
                            permissionCallback);
                }
            }
        });

        saveConversation.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                loadContainer.setVisibility(View.VISIBLE);
                dataContainer.setVisibility(View.GONE);
                if (type.equalsIgnoreCase("audio")){
                    uploadMedia(audioUri);
                }else if (type.equalsIgnoreCase("video")) {
                    uploadMedia(videoUri);
                }else if (type.equalsIgnoreCase("text")){
                    textConversationValue = conversationText.getText().toString().trim();
                    if (textConversationValue.isEmpty()){
                        conversationTextLayout.setError(getString(R.string.the_text_cannot_be_empty));
                        conversationTextLayout.setErrorIconDrawable(null);
                        conversationText.requestFocus();
                        return;
                    }else{
                        String fileName = "text_" + System.currentTimeMillis() + ".txt";
                        Uri uri = createFileTxt(requireContext(), fileName, textConversationValue);
                        uploadMedia(uri);
                    }
                }
            }
        });

        if (savedInstanceState != null) {
            String audioString = savedInstanceState.getString("audio_uri_state");
            if (audioString != null) {
                audioUri = Uri.parse(audioString);
                setupAudioPlayer(audioUri);
            }

            String videoString = savedInstanceState.getString("video_uri");
            if (videoString != null) {
                Log.d(TAG, "Video URI NO ES NULL: ");
                videoUri = Uri.parse(videoString);
                videoView.setVisibility(View.VISIBLE);
            }

            type = savedInstanceState.getString("type", "text");
            applyMediaTypeState(type);
        }

    }


    private void uploadMedia(Uri uri){
        ApiServices apiServices = AppController.getApiServices();

        String mimeType = requireActivity().getContentResolver().getType(uri);
        String fileName = getFileName(requireContext(), uri);

        try {
            RequestBody requestBody = createRequestBodyFromUri(requireContext(), uri, mimeType);
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", fileName, requestBody);

            RequestBody typePart = RequestBody.create(type, MediaType.parse("text/plain"));
            RequestBody assistantId = RequestBody.create(GlobalDataCache.legacyProfileSelected.getId(),MediaType.parse("text/plain"));

            Call<ResponseCreateMedia> call = apiServices.uploadMedia(filePart, typePart, assistantId);
            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<ResponseCreateMedia> call, @NonNull Response<ResponseCreateMedia> response) {
                    if (response.isSuccessful()){
                        ResponseCreateMedia body = response.body();
                        if (body != null){
                            if (body.isStatus()){
                                if (body.getData() != null){
                                    listener.onAddConversation(body.getData());
                                    dismiss();
                                }
                            }else{
                                loadContainer.setVisibility(View.GONE);
                                dataContainer.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseCreateMedia> call, @NonNull Throwable throwable) {
                    Log.e(TAG, "onFailure: UPLOAD FILE ERROR " + throwable.getMessage());
                    MessagesUtils.showErrorDialog(requireActivity(), ErrorUtils.parseError(throwable));
                    loadContainer.setVisibility(View.GONE);
                    dataContainer.setVisibility(View.VISIBLE);
                }
            });
        }catch (Exception e){
            Log.e(TAG, "uploadMedia: Error ",e);
            loadContainer.setVisibility(View.GONE);
            dataContainer.setVisibility(View.VISIBLE);
        }
    }

    public Uri createFileTxt(Context context, String fileName, String content) {
        try {
            File folder = new File(context.getExternalFilesDir(null), "Text");
            if (!folder.exists()) folder.mkdirs();

            File file = new File(folder, fileName);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(content.getBytes());
            fos.close();

            return FileProvider.getUriForFile(
                    context,
                    context.getPackageName() + ".provider",
                    file
            );

        } catch (IOException e) {
            Log.e(TAG, "createFileTxt: ", e);
            return null;
        }
    }

    private RequestBody createRequestBodyFromUri(Context context, Uri uri, String mimeType) {
        return new RequestBody() {
            @Override
            public MediaType contentType() {
                return MediaType.parse(mimeType);
            }

            @Override
            public void writeTo(okio.BufferedSink sink) throws IOException {
                try (InputStream in = context.getContentResolver().openInputStream(uri)) {
                    if (in == null) return;
                    byte[] buffer = new byte[8192];
                    int read;
                    while ((read = in.read(buffer)) != -1) {
                        sink.write(buffer, 0, read);
                    }
                }
            }
        };
    }


    private String getFileName(Context context, Uri uri) {
        String result = null;

        if ("content".equals(uri.getScheme())) {
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex != -1) {
                        result = cursor.getString(nameIndex);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "getFileName: ", e);
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }

        if (result == null) {
            String path = uri.getPath();
            if (path != null) {
                int cut = path.lastIndexOf('/');
                if (cut != -1 && cut < path.length() - 1) {
                    result = path.substring(cut + 1);
                } else {
                    result = "archivo_desconocido";
                }
            } else {
                result = "archivo_desconocido";
            }
        }

        return result;
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
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
        intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 30L * 1024L * 1024L);

        videoLauncher.launch(intent);
    }

    private long getFileSizeFromUri(Context context, Uri uri) {
        try (ParcelFileDescriptor pfd =
                     context.getContentResolver().openFileDescriptor(uri, "r")) {
            if (pfd != null) {
                long size = pfd.getStatSize(); // bytes
                return size; // puede ser -1 en algunos casos
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
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
        SeekBar seekBar = view.findViewById(R.id.seekBarVideo);
        ImageButton btnRewind = view.findViewById(R.id.btnRewind);

        videoView.setVideoURI(videoUri);

        videoView.setOnPreparedListener(mp -> {
            mp.setLooping(false);
            seekBar.setMax(videoView.getDuration());
            videoView.start();
            btnPlayPause.setImageResource(R.drawable.ic_pause_48);

            Handler handler = new Handler();
            Runnable updateSeekBar = new Runnable() {
                @Override
                public void run() {
                    if (videoView.isPlaying()) {
                        seekBar.setProgress(videoView.getCurrentPosition());
                    }
                    handler.postDelayed(this, 500);
                }
            };
            handler.postDelayed(updateSeekBar, 0);

            videoView.setOnCompletionListener(mediaPlayer -> {
                handler.removeCallbacks(updateSeekBar);
                btnPlayPause.setImageResource(R.drawable.ic_play_48);
                seekBar.setProgress(seekBar.getMax());
            });
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

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    videoView.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(view)
                .setCancelable(true)
                .create();

        dialog.show();
    }

    private void setupPermissionCallback() {
        permissionCallback = new CameraPermissionManager.PermissionCallback() {
            @Override
            public void onPermissionGranted() {
                startVideoCapture();
            }

            @Override
            public void onPermissionDenied() {
                MessagesUtils.showErrorDialog(requireActivity(),getString(R.string.permission_to_use_camera_denied));
            }

            @Override
            public void onPermissionPermanentlyDenied() {
                CameraPermissionManager.showPermanentlyDeniedDialog(requireContext());
            }
        };
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        if (audioUri != null) {
            outState.putString("audio_uri_state", audioUri.toString());
        }

        if (videoUri != null) {
            outState.putString("video_uri", videoUri.toString());
        }

        if (type != null) {
            outState.putString("type", type);
        }
    }


    private void setupPermissionLauncher() {
        cameraPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> CameraPermissionManager.handlePermissionResult(
                        isGranted, requireContext(), permissionCallback));
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

    private void applyMediaTypeState(String type) {

        int white = ContextCompat.getColor(requireContext(), R.color.white);
        int normal = ContextCompat.getColor(requireContext(), R.color.text_color);
        int tintNormal = ContextCompat.getColor(requireContext(), R.color.second_login_color);
        int tintSelected = white;
        int bgSelected = tintNormal;

        containerText.setVisibility(View.GONE);
        recordAudioBtn.setVisibility(View.GONE);
        recordVideoBtn.setVisibility(View.GONE);
        if (videoUri == null){
            videoView.setVisibility(View.GONE);
        }

        text.setTextColor(normal);
        audio.setTextColor(normal);
        video.setTextColor(normal);

        textIcon.setImageTintList(ColorStateList.valueOf(tintNormal));
        audioIcon.setImageTintList(ColorStateList.valueOf(tintNormal));
        videoIcon.setImageTintList(ColorStateList.valueOf(tintNormal));

        optionText.setBackgroundTintList(null);
        optionAudio.setBackgroundTintList(null);
        optionVideo.setBackgroundTintList(null);

        switch (type) {

            case "text":
                containerText.setVisibility(View.VISIBLE);

                text.setTextColor(white);
                textIcon.setImageTintList(ColorStateList.valueOf(tintSelected));

                optionText.setBackgroundTintList(ColorStateList.valueOf(bgSelected));
                break;

            case "audio":
                recordAudioBtn.setVisibility(View.VISIBLE);

                audio.setTextColor(white);
                audioIcon.setImageTintList(ColorStateList.valueOf(tintSelected));

                optionAudio.setBackgroundTintList(ColorStateList.valueOf(bgSelected));
                break;

            case "video":
                recordVideoBtn.setVisibility(View.VISIBLE);
                hideAudioPlayer();

                video.setTextColor(white);
                videoIcon.setImageTintList(ColorStateList.valueOf(tintSelected));

                optionVideo.setBackgroundTintList(ColorStateList.valueOf(bgSelected));
                break;
        }
    }

    private void initViews(View view){
        optionText = view.findViewById(R.id.option_text);
        optionAudio = view.findViewById(R.id.option_audio);
        optionVideo = view.findViewById(R.id.option_video);

        containerText = view.findViewById(R.id.container_text);
        conversationText = view.findViewById(R.id.conversation_value);
        conversationTextLayout = view.findViewById(R.id.conversation_layout);
        recordAudioBtn = view.findViewById(R.id.start_record_audio);
        recordVideoBtn = view.findViewById(R.id.start_record_video);

        loadContainer = view.findViewById(R.id.load_container);
        dataContainer = view.findViewById(R.id.data_container);

        audioPlayerView = view.findViewById(R.id.audioPlayer);
        videoView = view.findViewById(R.id.video_player);

        textIcon = view.findViewById(R.id.icon_text);
        audioIcon = view.findViewById(R.id.icon_audio);
        videoIcon = view.findViewById(R.id.icon_video);

        text = view.findViewById(R.id.text);
        audio = view.findViewById(R.id.audio);
        video = view.findViewById(R.id.video);

        cancelBtn = view.findViewById(R.id.cancel_btn);
        saveConversation = view.findViewById(R.id.save_conversation);
        closeBtn = view.findViewById(R.id.btn_close);
    }

}

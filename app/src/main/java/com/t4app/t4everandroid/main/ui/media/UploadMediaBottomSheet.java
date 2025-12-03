package com.t4app.t4everandroid.main.ui.media;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.t4app.t4everandroid.AppController;
import com.t4app.t4everandroid.ErrorUtils;
import com.t4app.t4everandroid.ListenersUtils;
import com.t4app.t4everandroid.MessagesUtils;
import com.t4app.t4everandroid.R;
import com.t4app.t4everandroid.SafeClickListener;
import com.t4app.t4everandroid.main.GlobalDataCache;
import com.t4app.t4everandroid.network.ApiServices;
import com.t4app.t4everandroid.network.responses.ResponseCreateMedia;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadMediaBottomSheet extends BottomSheetDialogFragment {
    private static final String TAG = "UPDATE_MEDIA";

    private String type;
    private boolean fromUri;
    private Uri fileSelected;
    private ListenersUtils.OnMediaAddedListener listener;

    private ActivityResultLauncher<Intent> filePickerLauncher;

    private LinearLayout containerOption;
    private LinearLayout dataContainer;
    private LinearLayout loadContainer;

    private MaterialButton saveMedia;
    private MaterialButton playMedia;
    private MaterialButton cancelBtn;
    private AppCompatImageButton closeBtn;

    private TextView uploadTitle;
    private TextView uploadDescription;

    private ImageView iconUpload;

    private LinearLayout optionText;
    private LinearLayout optionAudio;
    private LinearLayout optionImage;
    private LinearLayout optionVideo;

    private ImageView textIcon;
    private ImageView audioIcon;
    private ImageView videoIcon;
    private ImageView imageIcon;

    private TextView text;
    private TextView audio;
    private TextView video;
    private TextView image;


    public static UploadMediaBottomSheet newInstance(){
        return new UploadMediaBottomSheet();
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

        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri selectedFileUri = result.getData().getData();
                        if (selectedFileUri != null) {
                            fileSelected = selectedFileUri;
                            fromUri = true;
                            Drawable startDrawable = null;
                            switch (type){
                                case "text":
                                    playMedia.setText(R.string.view_text);
                                    startDrawable = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_eye);
                                    iconUpload.setImageResource(R.drawable.ic_doc);
                                    break;
                                case "image":
                                    playMedia.setText(R.string.view_image);
                                    startDrawable = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_eye);
                                    iconUpload.setImageResource(R.drawable.ic_gallery);
                                    break;
                                case "audio":
                                    playMedia.setText(R.string.play_audio);
                                    startDrawable = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_play);
                                    iconUpload.setImageResource(R.drawable.ic_headset);
                                    break;
                                case "video":
                                    playMedia.setText(R.string.play_video);
                                    startDrawable = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_play);
                                    iconUpload.setImageResource(R.drawable.ic_video);
                                    break;
                            }
                            saveMedia.setEnabled(true);
                            saveMedia.setAlpha(1.0f);

                            uploadTitle.setText(getFileNameFromUri(selectedFileUri));
                            uploadDescription.setText(R.string.click_to_select_other_file);

                            playMedia.setCompoundDrawablesWithIntrinsicBounds(startDrawable, null, null, null);
                            playMedia.setVisibility(View.VISIBLE);
                        }
                    }
                }
        );

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_add_media_layout, container, false);

        initViews(view);

        playMedia.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                ViewerDocumentBottomSheet bottomSheet = ViewerDocumentBottomSheet.newInstance(type, fromUri, fileSelected);
                bottomSheet.show(getChildFragmentManager(), "view_media");
            }
        });

        optionText.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                type = "text";
                fileSelected = null;
                applyTypeVisualState();

            }
        });

        optionAudio.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                type = "audio";
                fileSelected = null;
                applyTypeVisualState();
            }
        });

        optionVideo.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                type = "video";
                fileSelected = null;
                applyTypeVisualState();
            }
        });

        optionImage.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                type = "image";
                fileSelected = null;
                applyTypeVisualState();
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

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        containerOption.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                switch (type){
                    case "text":
                        intent.setType("text/plain");
                        break;
                    case "image":
                        intent.setType("image/*");
                        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/jpeg", "image/png"});
                        break;
                    case "audio":
                        intent.setType("audio/*");
                        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"audio/mpeg", "audio/wav", "audio/ogg"});
                        break;
                    case "video":
                        intent.setType("video/*");
                        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"video/mp4", "video/webm", "video/ogg"});
                        break;
                }
                filePickerLauncher.launch(intent);
            }
        });

        saveMedia.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                loadContainer.setVisibility(View.VISIBLE);
                dataContainer.setVisibility(View.GONE);
                if (fileSelected != null && GlobalDataCache.legacyProfileSelected != null){
                    uploadMedia(fileSelected);
                }
            }
        });

        if (savedInstanceState != null) {
            String uriString = savedInstanceState.getString("fileSelected");
            fromUri = savedInstanceState.getBoolean("fromUri", false);
            type = savedInstanceState.getString("type");
            if (uriString != null) {
                fileSelected = Uri.parse(uriString);
            }
            if (type != null){
                applyTypeVisualState();
            }
            restoreUIState();
        }else {
            optionText.post(optionText::performClick);
        }

    }

    private void uploadMedia (Uri uri){
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

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        if (fileSelected != null) {
            outState.putString("fileSelected", fileSelected.toString());
        }

        outState.putBoolean("fromUri", fromUri);
        outState.putString("type", type);
    }


    private RequestBody createRequestBodyFromUri(Context context, Uri uri, String mimeType) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[8192];
        int nRead;
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        return RequestBody.create(buffer.toByteArray(), MediaType.parse(mimeType));
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
                    result = "f_" + System.currentTimeMillis();
                }
            } else {
                result = "f_" + System.currentTimeMillis();
            }
        }

        return result;
    }

    public String getFileNameFromUri(Uri uri) {
        String result = null;

        if (uri.getScheme().equals("content")) {
            Cursor cursor = requireActivity().getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                if (cursor != null) cursor.close();
            }
        }

        if (result == null) {
            result = uri.getPath();
            if (result != null) {
                int cut = result.lastIndexOf('/');
                if (cut != -1) {
                    result = result.substring(cut + 1);
                }
            }
        }

        return result;
    }

    private void restoreUIState() {
        if (fileSelected == null) return;

        Drawable startDrawable = null;

        switch (type){
            case "text":
                playMedia.setText(R.string.view_text);
                startDrawable = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_eye);
                iconUpload.setImageResource(R.drawable.ic_doc);
                break;

            case "image":
                playMedia.setText(R.string.view_image);
                startDrawable = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_eye);
                iconUpload.setImageResource(R.drawable.ic_gallery);
                break;

            case "audio":

                playMedia.setText(R.string.play_audio);
                startDrawable = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_play);
                iconUpload.setImageResource(R.drawable.ic_headset);
                break;

            case "video":
                playMedia.setText(R.string.play_video);
                startDrawable = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_play);
                iconUpload.setImageResource(R.drawable.ic_video);
                break;
        }

        saveMedia.setEnabled(true);
        saveMedia.setAlpha(1.0f);

        uploadTitle.setText(getFileNameFromUri(fileSelected));
        uploadDescription.setText(R.string.click_to_select_other_file);

        playMedia.setCompoundDrawablesWithIntrinsicBounds(startDrawable, null, null, null);
        playMedia.setVisibility(View.VISIBLE);
    }


    private void initViews(View view){
        optionText = view.findViewById(R.id.option_text);
        optionAudio = view.findViewById(R.id.option_audio);
        optionImage = view.findViewById(R.id.option_image);
        optionVideo = view.findViewById(R.id.option_video);
        containerOption = view.findViewById(R.id.container_upload);

        loadContainer = view.findViewById(R.id.load_container);
        dataContainer = view.findViewById(R.id.data_container);

        textIcon = view.findViewById(R.id.icon_text);
        audioIcon = view.findViewById(R.id.icon_audio);
        videoIcon = view.findViewById(R.id.icon_video);
        imageIcon = view.findViewById(R.id.icon_image);

        iconUpload = view.findViewById(R.id.icon_upload);

        text = view.findViewById(R.id.text);
        audio = view.findViewById(R.id.audio);
        video = view.findViewById(R.id.video);
        image = view.findViewById(R.id.image);

        uploadTitle = view.findViewById(R.id.upload_title);
        uploadDescription = view.findViewById(R.id.upload_description);

        cancelBtn = view.findViewById(R.id.cancel_btn);
        playMedia = view.findViewById(R.id.play_media);
        saveMedia = view.findViewById(R.id.save_media);
        closeBtn = view.findViewById(R.id.btn_close);
    }

    private void applyTypeVisualState() {

        saveMedia.setEnabled(false);
        saveMedia.setAlpha(0.5f);
        playMedia.setVisibility(View.GONE);
        iconUpload.setImageResource(R.drawable.ic_upload);

        int normalTextColor = ContextCompat.getColor(requireContext(), R.color.text_color);
        int normalTint = ContextCompat.getColor(requireContext(), R.color.second_login_color);

        text.setTextColor(normalTextColor);
        audio.setTextColor(normalTextColor);
        video.setTextColor(normalTextColor);
        image.setTextColor(normalTextColor);

        textIcon.setImageTintList(ColorStateList.valueOf(normalTint));
        audioIcon.setImageTintList(ColorStateList.valueOf(normalTint));
        videoIcon.setImageTintList(ColorStateList.valueOf(normalTint));
        imageIcon.setImageTintList(ColorStateList.valueOf(normalTint));

        optionText.setBackgroundTintList(null);
        optionAudio.setBackgroundTintList(null);
        optionVideo.setBackgroundTintList(null);
        optionImage.setBackgroundTintList(null);

        int selectedColor = ContextCompat.getColor(requireContext(), R.color.white);
        int selectedBg = ContextCompat.getColor(requireContext(), R.color.second_login_color);

        switch (type) {
            case "text":
                uploadTitle.setText(R.string.upload_a_file);
                uploadDescription.setText(R.string.click_to_select_txt_up_to_50mb);
                text.setTextColor(selectedColor);
                textIcon.setImageTintList(ColorStateList.valueOf(selectedColor));
                optionText.setBackgroundTintList(ColorStateList.valueOf(selectedBg));
                break;

            case "audio":
                uploadTitle.setText(R.string.upload_an_audio);
                uploadDescription.setText(R.string.click_to_select_mp3_wav_ogg_up_to_50mb);
                audio.setTextColor(selectedColor);
                audioIcon.setImageTintList(ColorStateList.valueOf(selectedColor));
                optionAudio.setBackgroundTintList(ColorStateList.valueOf(selectedBg));
                break;

            case "video":
                uploadTitle.setText(R.string.upload_a_video);
                uploadDescription.setText(R.string.click_to_select_mp4_webm_ogg_up_to_100mb);
                video.setTextColor(selectedColor);
                videoIcon.setImageTintList(ColorStateList.valueOf(selectedColor));
                optionVideo.setBackgroundTintList(ColorStateList.valueOf(selectedBg));
                break;

            case "image":
                uploadTitle.setText(R.string.upload_a_photo);
                uploadDescription.setText(R.string.click_to_select_png_jpg_up_to_5mb);
                image.setTextColor(selectedColor);
                imageIcon.setImageTintList(ColorStateList.valueOf(selectedColor));
                optionImage.setBackgroundTintList(ColorStateList.valueOf(selectedBg));
                break;
        }
    }


}

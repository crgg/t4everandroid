package com.t4app.t4everandroid.main.ui.media;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.t4app.t4everandroid.AppController;
import com.t4app.t4everandroid.ErrorUtils;
import com.t4app.t4everandroid.ListenersUtils;
import com.t4app.t4everandroid.MessagesUtils;
import com.t4app.t4everandroid.R;
import com.t4app.t4everandroid.SafeClickListener;
import com.t4app.t4everandroid.main.GlobalDataCache;
import com.t4app.t4everandroid.main.Models.ResponseCreateMedia;
import com.t4app.t4everandroid.network.ApiServices;

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

    private ActivityResultLauncher<Intent> filePickerLauncher;
    private LinearLayout containerOption;
    private Uri fileSelected;
    private MaterialButton saveMedia;

    private LinearLayout dataContainer;
    private LinearLayout loadContainer;

    private ListenersUtils.OnMediaAddedListener listener;

    public void setListener(ListenersUtils.OnMediaAddedListener listener) {
        this.listener = listener;
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

        LinearLayout optionText = view.findViewById(R.id.option_text);
        LinearLayout optionAudio = view.findViewById(R.id.option_audio);
        LinearLayout optionImage = view.findViewById(R.id.option_image);
        LinearLayout optionVideo = view.findViewById(R.id.option_video);
        containerOption = view.findViewById(R.id.container_upload);

        loadContainer = view.findViewById(R.id.load_container);
        dataContainer = view.findViewById(R.id.data_container);

        ImageView textIcon = view.findViewById(R.id.icon_text);
        ImageView audioIcon = view.findViewById(R.id.icon_audio);
        ImageView videoIcon = view.findViewById(R.id.icon_video);
        ImageView imageIcon = view.findViewById(R.id.icon_image);

        TextView text = view.findViewById(R.id.text);
        TextView audio = view.findViewById(R.id.audio);
        TextView video = view.findViewById(R.id.video);
        TextView image = view.findViewById(R.id.image);

        TextView uploadTitle = view.findViewById(R.id.upload_title);
        TextView uploadDescription = view.findViewById(R.id.upload_description);

        MaterialButton cancelBtn = view.findViewById(R.id.cancel_btn);
        saveMedia = view.findViewById(R.id.save_media);
        AppCompatImageButton closeBtn = view.findViewById(R.id.btn_close);

        optionText.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                type = "text";

                uploadTitle.setText(R.string.upload_a_file);
                uploadDescription.setText(R.string.click_to_select_txt_up_to_50mb);

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

                uploadTitle.setText(R.string.upload_an_audio);
                uploadDescription.setText(R.string.click_to_select_mp3_wav_ogg_up_to_50mb);

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

                uploadTitle.setText(R.string.upload_a_video);
                uploadDescription.setText(R.string.click_to_select_mp4_webm_ogg_up_to_100mb);

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
                type = "image";

                uploadTitle.setText(R.string.upload_a_photo);
                uploadDescription.setText(R.string.click_to_select_png_jpg_up_to_5mb);

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

        optionText.post(optionText::performClick);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
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
}

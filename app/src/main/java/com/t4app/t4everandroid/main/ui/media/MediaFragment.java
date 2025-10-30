package com.t4app.t4everandroid.main.ui.media;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.t4app.t4everandroid.AppController;
import com.t4app.t4everandroid.ErrorUtils;
import com.t4app.t4everandroid.ListenersUtils;
import com.t4app.t4everandroid.MessagesUtils;
import com.t4app.t4everandroid.R;
import com.t4app.t4everandroid.SafeClickListener;
import com.t4app.t4everandroid.databinding.FragmentMediaBinding;
import com.t4app.t4everandroid.main.GlobalDataCache;
import com.t4app.t4everandroid.main.Models.Media;
import com.t4app.t4everandroid.main.T4EverMainActivity;
import com.t4app.t4everandroid.network.responses.ResponseGetMedia;
import com.t4app.t4everandroid.main.adapter.MediaAdapter;
import com.t4app.t4everandroid.main.ui.legacyProfile.LegacyProfilesFragment;
import com.t4app.t4everandroid.network.ApiServices;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MediaFragment extends Fragment {
    private static final String TAG = "MEDIA_FRAG";

    private FragmentMediaBinding binding;

    private List<Media> mediaTestList;
    private MediaAdapter adapter;

    private Snackbar snackbar;

    private CreateMediaBottomSheet createMediaBottomSheet;
    private UploadMediaBottomSheet uploadMediaBottomSheet;

    public MediaFragment() {}

    public static MediaFragment newInstance() {
        return new MediaFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentMediaBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        if (GlobalDataCache.legacyProfiles == null || GlobalDataCache.legacyProfiles.isEmpty()){
            binding.itemSelectLegacy.selectLegacyDescription.setText(getString(R.string.you_need_to_select_a_legacy_profile));
            binding.createNewMediaBtn.setEnabled(false);
            binding.createNewMediaBtn.setAlpha(0.5f);
            binding.itemSelectLegacy.btnAddFirst.setVisibility(View.GONE);
            binding.itemSelectLegacy.buttonActionsProfile.setVisibility(View.VISIBLE);

        } else if (GlobalDataCache.legacyProfileSelected == null){
            binding.itemSelectLegacy.selectLegacyDescription.setText(R.string.you_need_to_select_a_legacy_profile_media);
            binding.createNewMediaBtn.setEnabled(false);
            binding.createNewMediaBtn.setAlpha(0.5f);


            binding.itemSelectLegacy.btnAddFirst.setVisibility(View.GONE);
            binding.itemSelectLegacy.buttonActionsProfile.setVisibility(View.VISIBLE);
        }else {
            binding.itemSelectLegacy.selectLegacyDescription.setText(
                    getString(R.string.profile_selected_media,
                            GlobalDataCache.legacyProfileSelected.getName()));
            binding.createNewMediaBtn.setEnabled(true);
            binding.createNewMediaBtn.setAlpha(1f);

            binding.itemSelectLegacy.btnAddFirst.setVisibility(View.VISIBLE);
            binding.itemSelectLegacy.btnAddFirst.setText(R.string.record_first_media);
            binding.itemSelectLegacy.buttonActionsProfile.setVisibility(View.GONE);
        }

        binding.itemSelectLegacy.buttonActionsProfile.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                showFragment(new LegacyProfilesFragment());
                ((T4EverMainActivity)requireActivity()).selectNavItem(R.id.nav_legacy_profiles);
            }
        });
        mediaTestList = new ArrayList<>();

        ViewerDocumentBottomSheet viewerDocumentBottomSheet = new ViewerDocumentBottomSheet();
        adapter = new MediaAdapter(requireContext(), mediaTestList, new ListenersUtils.OnMediaActionsListener() {
            @Override
            public void onDelete(Media mediaTest, int pos) {
                MessagesUtils.showMessageConfirmation(requireActivity(), getString(R.string.are_you_sure_you_won_t_be_able_to_undo_this), new ListenersUtils.ConfirmationCallback() {
                    @Override
                    public void onResult(boolean confirmed) {
                        if (confirmed){
                            deleteMedia(mediaTest, pos);
                        }
                    }
                });
            }

            @Override
            public void onDownload(Media mediaTest, int pos) {
                downloadFile(requireActivity(), mediaTest.getStorageUrl());
            }

            @Override
            public void onView(Media mediaTest, int pos) {
                viewerDocumentBottomSheet.setMedia(mediaTest);
                FragmentManager fm = requireActivity().getSupportFragmentManager();
                Fragment prev = fm.findFragmentByTag("viewer_doc");
                if (prev != null) {
                    fm.beginTransaction().remove(prev).commit();
                }
                viewerDocumentBottomSheet.show(getChildFragmentManager(), "viewer_doc");
            }
        });

        binding.mediaRv.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.mediaRv.setAdapter(adapter);
        calculateMedias(mediaTestList);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        createMediaBottomSheet = getCreateMediaBottomSheet();
        uploadMediaBottomSheet = new UploadMediaBottomSheet();
        uploadMediaBottomSheet.setListener(mediaTest -> {
            if (binding.mediaRv.getVisibility() == View.GONE){
                binding.mediaRv.setVisibility(View.VISIBLE);
                binding.itemSelectLegacy.getRoot().setVisibility(View.GONE);
            }
            adapter.addItem(mediaTest);
            calculateMedias(mediaTestList);
        });
        binding.createNewMediaBtn.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
               showCustomDialog();
            }
        });

        binding.itemSelectLegacy.btnAddFirst.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                showCustomDialog();
            }
        });
        if (GlobalDataCache.legacyProfileSelected != null){
            getMedia();
        }
    }

    private void showCustomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.popup_manager_option, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        LinearLayout uploadNewMedia = dialogView.findViewById(R.id.option_upload_media);
        LinearLayout recordNewMedia = dialogView.findViewById(R.id.option_record_media);

        AppCompatImageButton btnClose = dialogView.findViewById(R.id.btn_close);
        MaterialButton btnCancel = dialogView.findViewById(R.id.cancel_btn);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        btnClose.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                dialog.dismiss();
            }
        });


        uploadNewMedia.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                FragmentManager fm = requireActivity().getSupportFragmentManager();
                Fragment prev = fm.findFragmentByTag("add_media");
                if (prev != null) {
                    fm.beginTransaction().remove(prev).commit();
                }
                uploadMediaBottomSheet.show(getChildFragmentManager(), "add_media");
                dialog.dismiss();
            }
        });

        recordNewMedia.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                FragmentManager fm = requireActivity().getSupportFragmentManager();
                Fragment prev = fm.findFragmentByTag("create_media");
                if (prev != null) {
                    fm.beginTransaction().remove(prev).commit();
                }
                createMediaBottomSheet.show(getChildFragmentManager(), "create_media");
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @NonNull
    private CreateMediaBottomSheet getCreateMediaBottomSheet() {
        CreateMediaBottomSheet bottomSheet = new CreateMediaBottomSheet();
        bottomSheet.setListener(media -> {
            if (binding.mediaRv.getVisibility() == View.GONE){
                binding.mediaRv.setVisibility(View.VISIBLE);
                binding.itemSelectLegacy.getRoot().setVisibility(View.GONE);
            }
            adapter.addItem(media);
            calculateMedias(mediaTestList);
//            mediaTestList.add(mediaTest);

        });
        return bottomSheet;
    }

    private void calculateMedias(List<Media> mediaTestList){
        int typeText = 0;
        int typeAudio = 0;
        int typeVideo = 0;
        int typeImage = 0;
        for (Media mediaTest : mediaTestList){
            if (mediaTest.getType().equalsIgnoreCase("text")){
                typeText++;
            }else if (mediaTest.getType().equalsIgnoreCase("audio")){
                typeAudio++;
            }else if (mediaTest.getType().equalsIgnoreCase("video")){
                typeVideo++;
            }else if (mediaTest.getType().equalsIgnoreCase("image")){
                typeImage++;
            }
        }

        binding.totalMedias.setText(String.valueOf(mediaTestList.size()));
        binding.totalAudio.setText(String.valueOf(typeAudio));
        binding.totalVideo.setText(String.valueOf(typeVideo));
        binding.totalText.setText(String.valueOf(typeText));
        binding.totalImages.setText(String.valueOf(typeImage));
    }

    private void getMedia(){
        ApiServices apiServices = AppController.getApiServices();
        Call<ResponseGetMedia> call = apiServices.getMediaAssistant(GlobalDataCache.legacyProfileSelected.getId(), "All");
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ResponseGetMedia> call, @NonNull Response<ResponseGetMedia> response) {
                if (response.isSuccessful()){
                    ResponseGetMedia body = response.body();
                    if (body != null){
                        if (body.isStatus()){
                            if (body.getData() != null){
                                adapter.updateList(body.getData());
                                calculateMedias(body.getData());
                                if (!body.getData().isEmpty()){
                                    if (binding.mediaRv.getVisibility() == View.GONE){
                                        binding.mediaRv.setVisibility(View.VISIBLE);
                                        binding.itemSelectLegacy.getRoot().setVisibility(View.GONE);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseGetMedia> call, @NonNull Throwable throwable) {
                Log.e(TAG, "onFailure: GET MEDIA " + throwable.getMessage());
                MessagesUtils.showErrorDialog(requireContext(),
                        getString(R.string.error_get_media) + ErrorUtils.parseError(throwable));
            }
        });
    }

    private void showDownloadProgress(){
        snackbar = Snackbar.make(binding.linearMain,
                R.string.downloading_file,
                Snackbar.LENGTH_LONG);


        ProgressBar progressBar = new ProgressBar(requireContext());
        progressBar.setIndeterminate(true);
        progressBar.setPadding(16, 16, 16, 16);

        binding.linearMain.addView(progressBar);

        snackbar.show();
    }

    private void deleteMedia(Media media, int pos){
        ApiServices apiServices = AppController.getApiServices();
        Call<JsonObject> call = apiServices.deleteMedia(media.getId());
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject body = response.body();
                    if (body != null) {
                        if (body.has("status")) {
                            if (body.get("status").getAsBoolean()) {
                                adapter.deleteItem(pos);
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable throwable) {
                Log.e(TAG, "onFailure ERROR DELETE MEDIA ", throwable);
                MessagesUtils.showErrorDialog(requireActivity(), ErrorUtils.parseError(throwable));
            }
        });
    }


    public void downloadFile(Context context, String fileUrl) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(fileUrl)
                .build();

        showDownloadProgress();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Toast.makeText(context, R.string.error_download_file, Toast.LENGTH_SHORT).show();
                    return;
                }

                String fileName = extractFileName(fileUrl);

                InputStream inputStream = null;
                OutputStream outputStream = null;

                try {
                    inputStream = response.body().byteStream();

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        ContentValues values = new ContentValues();
                        values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
                        values.put(MediaStore.Downloads.MIME_TYPE, guessMimeType(fileName));
                        values.put(MediaStore.Downloads.IS_PENDING, 1);

                        Uri uri = context.getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
                        outputStream = context.getContentResolver().openOutputStream(uri);

                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }

                        values.clear();
                        values.put(MediaStore.Downloads.IS_PENDING, 0);
                        context.getContentResolver().update(uri, values, null, null);

                    } else {
                        File downloadsFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                        File outFile = new File(downloadsFolder, fileName);
                        outputStream = new FileOutputStream(outFile);

                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                    }

                    new android.os.Handler(context.getMainLooper()).post(() -> {
                        Toast.makeText(context, getString(R.string.downloaded_file) + fileName, Toast.LENGTH_SHORT).show();
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (inputStream != null) inputStream.close();
                    if (outputStream != null) outputStream.close();
                }
            }
        });
    }

    public String guessMimeType(String fileName) {
        String extension = "";
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            extension = fileName.substring(i + 1).toLowerCase();
        }

        switch (extension) {
            case "mp4": return "video/mp4";
            case "mp3": return "audio/mpeg";
            case "txt": return "text/plain";
            case "jpg":
            case "jpeg": return "image/jpeg";
            case "png": return "image/png";
            default: return "*/*";
        }
    }

    public String extractFileName(String url) {
        if (url == null || url.isEmpty()) return "file";
        int lastSlash = url.lastIndexOf('/');
        return lastSlash == -1 ? url : url.substring(lastSlash + 1);
    }



    private void showFragment(Fragment fragment) {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}
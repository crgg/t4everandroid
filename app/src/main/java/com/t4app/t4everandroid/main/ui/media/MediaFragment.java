package com.t4app.t4everandroid.main.ui.media;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.button.MaterialButton;
import com.t4app.t4everandroid.AppController;
import com.t4app.t4everandroid.ErrorUtils;
import com.t4app.t4everandroid.ListenersUtils;
import com.t4app.t4everandroid.MessagesUtils;
import com.t4app.t4everandroid.R;
import com.t4app.t4everandroid.SafeClickListener;
import com.t4app.t4everandroid.databinding.FragmentMediaBinding;
import com.t4app.t4everandroid.main.GlobalDataCache;
import com.t4app.t4everandroid.main.Models.Media;
import com.t4app.t4everandroid.main.Models.ResponseGetMedia;
import com.t4app.t4everandroid.main.adapter.MediaAdapter;
import com.t4app.t4everandroid.main.ui.legacyProfile.LegacyProfilesFragment;
import com.t4app.t4everandroid.network.ApiServices;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MediaFragment extends Fragment {
    private static final String TAG = "MEDIA_FRAG";

    private FragmentMediaBinding binding;

    private List<Media> mediaTestList;
    private MediaAdapter adapter;

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
            }
        });
        mediaTestList = new ArrayList<>();
        adapter = new MediaAdapter(requireContext(), mediaTestList, new ListenersUtils.OnMediaActionsListener() {
            @Override
            public void onDelete(Media mediaTest, int pos) {

            }

            @Override
            public void onDownload(Media mediaTest, int pos) {

            }

            @Override
            public void onView(Media mediaTest, int pos) {

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
                uploadMediaBottomSheet.show(getChildFragmentManager(), "add_media");
                dialog.dismiss();
            }
        });

        recordNewMedia.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
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
            public void onResponse(Call<ResponseGetMedia> call, Response<ResponseGetMedia> response) {
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
            public void onFailure(Call<ResponseGetMedia> call, Throwable throwable) {
                Log.e(TAG, "onFailure: GET MEDIA " + throwable.getMessage());
                MessagesUtils.showErrorDialog(requireContext(),
                        getString(R.string.error_get_media) + ErrorUtils.parseError(throwable));
            }
        });
    }

    private void showFragment(Fragment fragment) {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}
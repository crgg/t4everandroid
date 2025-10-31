package com.t4app.t4everandroid.main.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.JsonObject;
import com.t4app.t4everandroid.AppController;
import com.t4app.t4everandroid.ErrorUtils;
import com.t4app.t4everandroid.ListenersUtils;
import com.t4app.t4everandroid.MessagesUtils;
import com.t4app.t4everandroid.R;
import com.t4app.t4everandroid.SafeClickListener;
import com.t4app.t4everandroid.databinding.FragmentChatBinding;
import com.t4app.t4everandroid.main.GlobalDataCache;
import com.t4app.t4everandroid.main.Models.Interactions;
import com.t4app.t4everandroid.main.T4EverMainActivity;
import com.t4app.t4everandroid.main.adapter.ChatAdapter;
import com.t4app.t4everandroid.main.ui.legacyProfile.LegacyProfilesFragment;
import com.t4app.t4everandroid.main.ui.media.RecordAudioActivity;
import com.t4app.t4everandroid.network.ApiServices;
import com.t4app.t4everandroid.network.responses.ResponseCreateInteraction;
import com.t4app.t4everandroid.network.responses.ResponseGetInteractions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatFragment extends Fragment {
    private static final String TAG = "CHAT_FRAGMENT";

    private ChatAdapter adapter;
    private FragmentChatBinding binding;
    boolean isShowingText = true;

    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long elapsed = System.currentTimeMillis() - startTime;

            int seconds = (int) (elapsed / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;

            String time = String.format("%02d:%02d", minutes, seconds);
            binding.itemChat.audioTimer.setText(time);

            handler.postDelayed(this, 1000);
        }
    };

    private final ActivityResultLauncher<String> permissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted){
//                    startRecording();
                }else{
                    MessagesUtils.showErrorDialog(requireActivity(),
                            getString(R.string.microphone_permission_denied));
                }

            });


    private MediaRecorder recorder;
    private File audioFile;
    private boolean isRecording = false;

    private Handler handler = new Handler();
    private boolean isRunning = false;
    private long startTime = 0L;

    public ChatFragment() {
    }

    public static ChatFragment newInstance(String param1, String param2) {
        return new ChatFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        if (GlobalDataCache.legacyProfileSelected != null){
            binding.itemChatHelper.cardChatHelper.setVisibility(View.VISIBLE);
            binding.itemChat.getRoot().setVisibility(View.VISIBLE);
            binding.titleContainer.setVisibility(View.VISIBLE);
            binding.itemSelectLegacy.getRoot().setVisibility(View.GONE);

            binding.chatSubtitle.setText(getString(R.string.chat_with_the_digital_version_of,
                    GlobalDataCache.legacyProfileSelected.getName()));

            binding.chatTitle.setText(getString(R.string.chat_with,
                    GlobalDataCache.legacyProfileSelected.getName()));

        }else {
            binding.itemChatHelper.cardChatHelper.setVisibility(View.GONE);
            binding.itemChat.getRoot().setVisibility(View.GONE);
            binding.titleContainer.setVisibility(View.GONE);
            binding.itemSelectLegacy.getRoot().setVisibility(View.VISIBLE);

            binding.itemSelectLegacy.selectLegacyDescription.setText(R.string.you_need_to_select_a_legacy_profile_chat);
            binding.itemSelectLegacy.btnAddFirst.setOnClickListener(new SafeClickListener() {
                @Override
                public void onSafeClick(View v) {
                    showFragment(new LegacyProfilesFragment());
                    ((T4EverMainActivity)requireActivity()).selectNavItem(R.id.nav_legacy_profiles);
                }
            });

            binding.itemSelectLegacy.buttonActionsProfile.setOnClickListener(new SafeClickListener() {
                @Override
                public void onSafeClick(View v) {
                    showFragment(new LegacyProfilesFragment());
                    ((T4EverMainActivity)requireActivity()).selectNavItem(R.id.nav_legacy_profiles);
                }
            });
        }

        List<Interactions> interactions = new ArrayList<>();
        adapter = new ChatAdapter(requireContext(), interactions, new ListenersUtils.OnInteractionActionsListener() {
            @Override
            public void onDelete(Interactions interactions, int position) {
                MessagesUtils.showMessageConfirmation(requireActivity(), getString(R.string.msg_delete), confirmed -> {
                    if (confirmed){
                        deleteInteraction(interactions, position);
                    }
                });
            }

            @Override
            public void onDeleteAudio(Interactions interactions, int position) {
                MessagesUtils.showMessageConfirmation(requireActivity(), getString(R.string.msg_delete), confirmed -> {
                    if (confirmed){
                        adapter.deleteItem(position);
                    }
                });
            }
        });
        binding.itemChat.chatRv.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.itemChat.chatRv.setAdapter(adapter);

        if (GlobalDataCache.legacyProfileSelected != null && GlobalDataCache.sessionId != null){
            getInteractions();
        }
        return view;
    }

    private void getInteractions(){
        ApiServices apiServices = AppController.getApiServices();
        Call<ResponseGetInteractions> call = apiServices.getInteractions(GlobalDataCache.sessionId);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseGetInteractions> call, Response<ResponseGetInteractions> response) {
                if (response.isSuccessful()){
                    ResponseGetInteractions body = response.body();
                    if (body != null){
                        if (body.isStatus()){
                            if (body.getData() != null){
                                adapter.updateMessages(body.getData());
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseGetInteractions> call, Throwable throwable) {
                MessagesUtils.showErrorDialog(requireActivity(), ErrorUtils.parseError(throwable));
            }
        });
    }


    private void sendInteraction(Map<String, Object> data, ListenersUtils.ConfirmationCallback callback){
        ApiServices apiServices = AppController.getApiServices();
        Call<ResponseCreateInteraction> call = apiServices.sendInteraction(data);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseCreateInteraction> call, Response<ResponseCreateInteraction> response) {
                if (response.isSuccessful()) {
                    ResponseCreateInteraction body = response.body();
                    if (body != null) {
                        if (body.isStatus()) {
                            if (body.getData() != null) {
                                adapter.addMessage(body.getData());
                                callback.onResult(true);
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseCreateInteraction> call, Throwable throwable) {
                MessagesUtils.showErrorDialog(requireActivity(), ErrorUtils.parseError(throwable));
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Animation slideIn = AnimationUtils.loadAnimation(requireActivity(), R.anim.slide_in_left);
        Animation slideOut = AnimationUtils.loadAnimation(requireActivity(), R.anim.slide_in_right);


        if (GlobalDataCache.sessionId != null){
            binding.itemChat.sendInteractionBtn.setOnClickListener(new SafeClickListener() {
                @Override
                public void onSafeClick(View v) {
                    String msg = binding.itemChat.textInteraction.getText().toString().trim();
                    if (!msg.isEmpty()){
                        Map<String, Object> data = new HashMap<>();
                        data.put("continue", false);
                        data.put("session_id", GlobalDataCache.sessionId);
                        data.put("text_from_user", msg);
                        sendInteraction(data, confirmed -> {
                            binding.itemChat.textInteraction.setText("");
                        });
                    }
                }
            });
        }

        binding.itemChat.uploadFile.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {

            }
        });

//        binding.itemChat.recordAudioBtn.setOnLongClickListener(v -> {
//            Log.d(TAG, "LONG PRESSED ");
////            binding.itemChat.containerRecordAudio.setVisibility(View.VISIBLE);
////            binding.itemChat.containerTextInteraction.setVisibility(View.GONE);
//            showRecordContainer(slideOut, slideIn);
//            return true;
//        });

        binding.itemChat.recordAudioBtn.setOnTouchListener((view1, motionEvent) -> {
            switch (motionEvent.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    Log.d(TAG, "ON DOWN ACTION: ");
                    if (isRecording){
                        stopRecording();
                    }else{
                        checkPermissionAndStart(confirmed -> {
                            if (confirmed){
                                startRecording();
                                startTime = System.currentTimeMillis();
                                handler.post(timerRunnable);
                                isRunning = true;
                                binding.getRoot().requestDisallowInterceptTouchEvent(true);
                                showRecordContainer(slideOut, slideIn);
                            }
                        });
                    }
                    return true;

                case MotionEvent.ACTION_MOVE:
                    Log.d(TAG, "ON MOVE ACTION: ");
                    return true;

                case MotionEvent.ACTION_UP:
                    binding.getRoot().requestDisallowInterceptTouchEvent(false);
                    showTextContainer(slideOut, slideIn);
                    handler.removeCallbacks(timerRunnable);
                    isRunning = false;
                    Log.d(TAG, "ON UP ACTION");
                    uploadFileAudio();
                    return true;

                case MotionEvent.ACTION_CANCEL:
                    binding.getRoot().requestDisallowInterceptTouchEvent(false);
                    showTextContainer(slideOut, slideIn);
                    handler.removeCallbacks(timerRunnable);
                    isRunning = false;
                    Log.d(TAG, "ON CANCEL ACTION");
                    uploadFileAudio();
                    return true;

            }
            return false;
        });

        binding.itemChat.cancelSendAudio.setOnTouchListener((view2, motionEvent) -> {
            switch (motionEvent.getAction()){
                case MotionEvent.ACTION_DOWN:
                    Log.d(TAG, "ON DOWN ACTION: CANCEL ");
                    return true;

                case MotionEvent.ACTION_MOVE:
                    Log.d(TAG, "ON MOVE ACTION:CANCEL ");
                    return true;

                case MotionEvent.ACTION_UP:
                    Log.d(TAG, "ON UP ACTION CANCEL");
                    return true;

                case MotionEvent.ACTION_CANCEL:
                    Log.d(TAG, "ON CANCEL ACTION CANCEL");
                    return true;

            }
            return false;
        });

    }

    private void deleteInteraction(Interactions interactions, int pos){
        ApiServices apiServices = AppController.getApiServices();
        Call<JsonObject> call = apiServices.deleteInteraction(interactions.getId());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
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
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                Log.e(TAG, "onFailure ERROR DELETE MEDIA ", throwable);
                MessagesUtils.showErrorDialog(requireActivity(), ErrorUtils.parseError(throwable));
            }
        });
    }

    private void checkPermissionAndStart(ListenersUtils.ConfirmationCallback callback) {
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO);
        } else {
            callback.onResult(true);
        }
    }

    private void showRecordContainer(Animation slideOut, Animation slideIn) {
        binding.itemChat.containerTextInteraction.startAnimation(slideOut);
        binding.itemChat.containerTextInteraction.setVisibility(View.GONE);

        binding.itemChat.containerRecordAudio.setVisibility(View.VISIBLE);
        binding.itemChat.containerRecordAudio.startAnimation(slideIn);
    }

    private void showTextContainer(Animation slideOut, Animation slideIn) {
        binding.itemChat.containerRecordAudio.startAnimation(slideOut);
        binding.itemChat.containerRecordAudio.setVisibility(View.GONE);

        binding.itemChat.containerTextInteraction.setVisibility(View.VISIBLE);
        binding.itemChat.containerTextInteraction.startAnimation(slideIn);
    }

    private void startRecording() {
        try {
            File dir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_MUSIC);
            if (dir != null && !dir.exists()) dir.mkdirs();

            audioFile = new File(dir, "audio_" + System.currentTimeMillis() + ".m4a");

            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            recorder.setAudioEncodingBitRate(128000);
            recorder.setAudioSamplingRate(44100);
            recorder.setOutputFile(audioFile.getAbsolutePath());

            recorder.prepare();
            recorder.start();

            isRecording = true;

        } catch (IOException e) {
            Log.e(TAG, "ERROR START RECORDING: ", e);
        }
    }

    private void uploadFileAudio(){
        if (isRecording) stopRecording();

        if (audioFile != null && audioFile.exists()) {
            Uri uri = FileProvider.getUriForFile(
                    requireActivity(),
                    requireActivity().getPackageName() + ".provider",
                    audioFile
            );

            Interactions interaction = new Interactions();
            interaction.setId(String.valueOf(System.currentTimeMillis()));
            interaction.setSessionId(GlobalDataCache.sessionId);
            interaction.setTextFromUser(null);
            interaction.setUserAudioUrl(uri.toString());
            interaction.setAssistantTextResponse(null);
            interaction.setAssistantAudioResponse(null);
            interaction.setEmotionDetected(null);
            interaction.setTimestamp("2025-10-31T09:14:45.000000Z");
            interaction.setHasResponse(false);
            interaction.setWasCanceled(false);
            interaction.setFileUuid(true);
            interaction.setTextFromUser(null);

            adapter.addMessage(interaction);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }
    }

    private void stopRecording() {
        try {
            recorder.stop();
            recorder.release();
            recorder = null;
            isRecording = false;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showFragment(Fragment fragment) {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}
package com.t4app.t4everandroid.main.ui.chat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.t4app.t4everandroid.AppController;
import com.t4app.t4everandroid.ErrorUtils;
import com.t4app.t4everandroid.ListenersUtils;
import com.t4app.t4everandroid.MessagesUtils;
import com.t4app.t4everandroid.R;
import com.t4app.t4everandroid.SafeClickListener;
import com.t4app.t4everandroid.SessionManager;
import com.t4app.t4everandroid.databinding.FragmentChatBinding;
import com.t4app.t4everandroid.main.GlobalDataCache;
import com.t4app.t4everandroid.main.Models.CategoryItem;
import com.t4app.t4everandroid.main.Models.LegacyProfile;
import com.t4app.t4everandroid.main.T4EverMainActivity;
import com.t4app.t4everandroid.main.adapter.ChatAdapter;
import com.t4app.t4everandroid.main.adapter.OptionsFirstMsgAdapter;
import com.t4app.t4everandroid.main.adapter.SelectContactAdapter;
import com.t4app.t4everandroid.main.ui.GridSpacingItemDecoration;
import com.t4app.t4everandroid.main.ui.chat.models.CreateMessageUtils;
import com.t4app.t4everandroid.main.ui.chat.models.InlineData;
import com.t4app.t4everandroid.main.ui.chat.models.Messages;
import com.t4app.t4everandroid.main.ui.legacyProfile.LegacyProfilesFragment;
import com.t4app.t4everandroid.network.ApiServices;
import com.t4app.t4everandroid.network.RetrofitClient;
import com.t4app.t4everandroid.network.responses.ResponseCreateMessage;
import com.t4app.t4everandroid.network.responses.ResponseGetMessages;
import com.t4app.t4everandroid.network.responses.ResponseStartEndSession;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatFragment extends Fragment {
    private static final String TAG = "CHAT_FRAGMENT";

    private ChatAdapter adapter;
    private FragmentChatBinding binding;

    private float cancelThreshold = 200;
    private boolean canceled = false;

    private MediaRecorder recorder;
    private File audioFile;
    private boolean isRecording = false;

    private Gson gson = new Gson();
    private SessionManager sessionManager;

    private Handler handler = new Handler();
    private boolean isRunning = false;
    private long startTime = 0L;
    private float startX = 0f;


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

    private ActivityResultLauncher<String[]> pickMediaLauncher;
    private ActivityResultLauncher<String[]> pickMultipleMediaLauncher;

    AddFilesManager filesManager;

    public ChatFragment() {
    }

    public static ChatFragment newInstance() {
        return new ChatFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = SessionManager.getInstance();

        pickMediaLauncher = registerForActivityResult(new ActivityResultContracts.OpenDocument(),
                uri -> {
                    if (uri == null) return;

                    final int flags = (requireActivity().getIntent() != null ? requireActivity().getIntent().getFlags() : 0)
                    & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                    try {
                        requireActivity().getContentResolver().takePersistableUriPermission(uri,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }catch (Exception ign){}

                    //TODO CREATE IMAGEVIEW AND ADD IN LAYOUT PREVIEW

                });


        pickMultipleMediaLauncher = registerForActivityResult(new ActivityResultContracts.OpenMultipleDocuments(),
                uris -> {
                    if (uris == null || uris.isEmpty()) return;

                    for (Uri uri : uris) {
                        try {
                            requireActivity().getContentResolver().takePersistableUriPermission(
                                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                            );
                        } catch (Exception ign) {
                        }

                        filesManager.addImage(uri);

                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        filesManager = new AddFilesManager(getLayoutInflater(),  getChildFragmentManager(), binding.itemChat.imagesSelectedContainer);

        if (GlobalDataCache.legacyProfileSelected != null){
            binding.itemChat.getRoot().setVisibility(View.VISIBLE);
            binding.itemChat.titleContainer.setVisibility(View.VISIBLE);
            binding.itemSelectLegacy.getRoot().setVisibility(View.GONE);

            binding.itemChat.chatSubtitle.setText(getString(R.string.chat_with_the_digital_version_of,
                    GlobalDataCache.legacyProfileSelected.getName()));

            binding.itemChat.chatTitle.setText(getString(R.string.chat_with,
                    GlobalDataCache.legacyProfileSelected.getName()));

        }else {
            binding.itemChat.getRoot().setVisibility(View.GONE);
            binding.itemChat.titleContainer.setVisibility(View.GONE);
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

        List<Messages> interactions = new ArrayList<>();
        adapter = new ChatAdapter(requireContext(), interactions, getLayoutInflater(), new ListenersUtils.OnMessageActionsListener() {
            @Override
            public void onDelete(Messages interactions, int position) {
                MessagesUtils.showMessageConfirmation(requireActivity(), getString(R.string.msg_delete), confirmed -> {
                    if (confirmed){
                        deleteMessage(interactions, position);
                    }
                });
            }

            @Override
            public void onViewFile(InlineData inlineData) {
                FragmentManager fm = requireActivity().getSupportFragmentManager();
                Fragment prev = fm.findFragmentByTag("viewer_doc");
                if (prev != null) {
                    fm.beginTransaction().remove(prev).commit();
                }
                ViewerInlineDataBottomSheet.newInstance(inlineData)
                        .show(requireActivity().getSupportFragmentManager(), "viewer_doc");

            }

            @Override
            public void onDeleteAudio(Messages interactions, int position) {
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
            getMessages(GlobalDataCache.legacyProfileSelected.getId());
        }

        GridLayoutManager grid = new GridLayoutManager(getContext(), 4);
        binding.itemChat.rvCategoriesFirstMsg.setLayoutManager(grid);
        int spacing = (int) (2 * getResources().getDisplayMetrics().density);
        binding.itemChat.rvCategoriesFirstMsg.addItemDecoration(
                new GridSpacingItemDecoration(4, spacing, true));

        binding.itemChat.rvCategoriesFirstMsg.setAdapter(getOptionsAdapter());

        binding.itemChat.btnProfileData.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                if (GlobalDataCache.legacyProfileSelected != null){
                    showProfileBottomSheet(GlobalDataCache.legacyProfileSelected);
                }
            }
        });


        SelectContactAdapter contactAdapter = new SelectContactAdapter(GlobalDataCache.legacyProfiles, requireActivity(), profile -> {
            if (GlobalDataCache.legacyProfileSelected == null){
                Map<String, Object> data = new HashMap<>();
                data.put("assistant_id", profile.getId());
                startSession(data, session1 -> {
                    GlobalDataCache.legacyProfiles.set(
                            GlobalDataCache.legacyProfiles.indexOf(profile), session1.getAssistant()
                    );
                    GlobalDataCache.legacyProfileSelected = session1.getAssistant();
                    GlobalDataCache.sessionId = session1.getId();
                    getMessages(GlobalDataCache.legacyProfileSelected.getId());
                });
            } else if (GlobalDataCache.legacyProfileSelected.getId().equalsIgnoreCase(profile.getId())){
                Log.d(TAG, "IS SAME: ");
                binding.itemChat.containerChat.setVisibility(View.VISIBLE);
                binding.itemChat.containerSelectContact.setVisibility(View.GONE);
            }else if (GlobalDataCache.legacyProfileSelected.getOpenSession() != null){
                endSession(GlobalDataCache.legacyProfileSelected.getOpenSession().getId(),
                        session -> {
                            GlobalDataCache.legacyProfiles.set(
                                    GlobalDataCache.legacyProfiles.indexOf(GlobalDataCache.legacyProfileSelected),
                                    session.getAssistant());

                            Map<String, Object> data = new HashMap<>();
                            data.put("assistant_id", profile.getId());
                            startSession(data, session1 -> {
                                GlobalDataCache.legacyProfiles.set(
                                        GlobalDataCache.legacyProfiles.indexOf(profile), session1.getAssistant()
                                );
                                GlobalDataCache.legacyProfileSelected = session1.getAssistant();
                                GlobalDataCache.sessionId = session1.getId();
                                getMessages(GlobalDataCache.legacyProfileSelected.getId());
                            });
                        });

            }else{
                Map<String, Object> data = new HashMap<>();
                data.put("assistant_id", profile.getId());
                startSession(data, session1 -> {
                    GlobalDataCache.legacyProfiles.set(
                            GlobalDataCache.legacyProfiles.indexOf(profile), session1.getAssistant()
                    );
                    GlobalDataCache.legacyProfileSelected = session1.getAssistant();
                    GlobalDataCache.sessionId = session1.getId();
                    getMessages(GlobalDataCache.legacyProfileSelected.getId());
                });
            }
        });
        binding.itemChat.contactsRv.setLayoutManager(new LinearLayoutManager(requireActivity()));
        binding.itemChat.contactsRv.setAdapter(contactAdapter);

        return view;
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (GlobalDataCache.legacyProfileSelected != null){
            parseLastMessage(GlobalDataCache.legacyProfileSelected.getId());
        }

        Animation slideIn = AnimationUtils.loadAnimation(requireActivity(), R.anim.slide_in_left);
        Animation slideOut = AnimationUtils.loadAnimation(requireActivity(), R.anim.slide_in_right);

        CreateMessageUtils createMessageUtils = new CreateMessageUtils();

        if (GlobalDataCache.sessionId != null){
            binding.itemChat.sendMessageBtn.setOnClickListener(new SafeClickListener() {
                @Override
                public void onSafeClick(View v) {
                    String msg = binding.itemChat.textMessage.getText().toString().trim();
                    if (!msg.isEmpty()){
                        if (filesManager.getSelectedUris().isEmpty()){
                            Map<String, Object> data =
                                    createMessageUtils.postTextMessage(GlobalDataCache.legacyProfileSelected, msg);

                            sendMessageText(data, confirmed -> {
                                binding.itemChat.textMessage.setText("");
                                binding.itemChat.sendMessageBtn.setEnabled(true);
                                binding.itemChat.sendMessageBtn.setAlpha(1.0f);
                                updateUI(adapter.getMessages());
                                binding.itemChat.chatRv.post(() -> binding.itemChat.chatRv.
                                        smoothScrollToPosition(adapter.getItemCount() - 1));
                            });
                        }else{
                            RequestBody data = createMessageUtils.postMessageWithFile(GlobalDataCache.legacyProfileSelected,
                                    msg, filesManager.getSelectedUris(), requireActivity());

                            sendMessageMedia(data, confirmed -> {
                                binding.itemChat.textMessage.setText("");
                                binding.itemChat.sendMessageBtn.setEnabled(true);
                                binding.itemChat.sendMessageBtn.setAlpha(1.0f);
                                binding.itemChat.imagesSelectedContainer.removeAllViews();
                                filesManager.clearList();
                                binding.itemChat.chatRv.post(() -> binding.itemChat.chatRv.
                                        smoothScrollToPosition(adapter.getItemCount() - 1));
                            });
                        }
                    }
                }
            });
        }

        binding.itemChat.uploadFile.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                pickManyFiles();
            }
        });
        binding.itemChat.recordAudioBtn.setOnTouchListener((view1, motionEvent) -> {

            switch (motionEvent.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    Log.d(TAG, "ON DOWN ACTION: ");
                    startX = motionEvent.getRawX();
                    canceled = false;
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
                    float currentX = motionEvent.getRawX();
                    float diffX = currentX - startX;
                    if (diffX > 0) {
                        binding.itemChat.textSideToCancel.setTranslationX(diffX);

                        if (diffX > cancelThreshold && !canceled) {
                            Log.d(TAG, "onViewCreated: ENTRY IN CANCEL RECORD");
                            canceled = true;
                            binding.getRoot().requestDisallowInterceptTouchEvent(false);
                            showTextContainer(slideOut, slideIn);
                            handler.removeCallbacks(timerRunnable);

                            isRunning = false;
                            stopRecording();

                            binding.itemChat.textSideToCancel.animate()
                                    .translationX(0)
                                    .alpha(0f)
                                    .setDuration(150)
                                    .start();
                        }

                        Log.d(TAG,  "Swipe Right");
                    }
                    return true;

                case MotionEvent.ACTION_UP:

                    binding.getRoot().requestDisallowInterceptTouchEvent(false);
                    showTextContainer(slideOut, slideIn);
                    handler.removeCallbacks(timerRunnable);
                    isRunning = false;
                    Log.d(TAG, "ON UP ACTION");
                    if (!canceled){
                        uploadFileAudio();
                    }

                    binding.itemChat.textSideToCancel.animate()
                            .translationX(0)
                            .alpha(1f)
                            .setDuration(200)
                            .start();
                    return true;

                case MotionEvent.ACTION_CANCEL:
                    binding.getRoot().requestDisallowInterceptTouchEvent(false);
                    showTextContainer(slideOut, slideIn);
                    handler.removeCallbacks(timerRunnable);
                    isRunning = false;
                    Log.d(TAG, "ON CANCEL ACTION");
                    if (!canceled){
                        uploadFileAudio();
                    }
                    binding.itemChat.textSideToCancel.animate()
                            .translationX(0)
                            .alpha(1f)
                            .setDuration(200)
                            .start();
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

        binding.itemChat.changeContactBtn.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                binding.itemChat.containerChat.setVisibility(View.GONE);
                binding.itemChat.containerSelectContact.setVisibility(View.VISIBLE);
            }
        });

        binding.itemChat.btnCloseSelectContact.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                binding.itemChat.containerChat.setVisibility(View.VISIBLE);
                binding.itemChat.containerSelectContact.setVisibility(View.GONE);
            }
        });


    }

    private void pickManyFiles(){
        pickMultipleMediaLauncher.launch(new String[]{
                "image/*",
                "audio/*",
                "video/*"
        });
    }

    private void getMessages(String sessionId){
        ApiServices apiServices = RetrofitClient.getChatBotRetrofitClient().create(ApiServices.class);
        Call<ResponseGetMessages> call = apiServices.getMessages(sessionId);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseGetMessages> call, Response<ResponseGetMessages> response) {
                if (response.isSuccessful()){
                    ResponseGetMessages body = response.body();
                    if (body != null){
                        if (body.getData() != null){
                            Collections.reverse(body.getData());
                            adapter.updateMessages(body.getData());
                            updateUI(body.getData());
                            binding.itemChat.chatRv.post(() -> {
                                int last = adapter.getItemCount() - 1;
                                if (last >= 0) binding.itemChat.chatRv.scrollToPosition(last);
                            });
                            binding.itemChat.containerChat.setVisibility(View.VISIBLE);
                            binding.itemChat.containerSelectContact.setVisibility(View.GONE);
                            binding.itemChat.chatSubtitle.setText(getString(R.string.chat_with_the_digital_version_of,
                                    GlobalDataCache.legacyProfileSelected.getName()));

                            binding.itemChat.chatTitle.setText(getString(R.string.chat_with,
                                    GlobalDataCache.legacyProfileSelected.getName()));
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseGetMessages> call, Throwable throwable) {
                MessagesUtils.showErrorDialog(requireActivity(), ErrorUtils.parseError(throwable));
            }
        });
    }


    private void sendMessageText(Map<String, Object> data, ListenersUtils.ConfirmationCallback callback){
        binding.itemChat.sendMessageBtn.setEnabled(false);
        binding.itemChat.sendMessageBtn.setAlpha(0.5f);

        ApiServices apiServices = RetrofitClient.getChatBotRetrofitClient().create(ApiServices.class);
        Call<ResponseCreateMessage> call = apiServices.sendMessageText(data);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseCreateMessage> call, Response<ResponseCreateMessage> response) {
                if (response.isSuccessful()) {
                    ResponseCreateMessage body = response.body();
                    if (body != null) {
                        if (body.getData() != null) {
                            adapter.addMessage(body.getData());
                            callback.onResult(true);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseCreateMessage> call, Throwable throwable) {
                Log.e(TAG, "onFailure: " + throwable.getMessage());
                binding.itemChat.sendMessageBtn.setEnabled(true);
                binding.itemChat.sendMessageBtn.setAlpha(1.0f);
                MessagesUtils.showErrorDialog(requireActivity(), ErrorUtils.parseError(throwable));
            }
        });
    }

    private void sendMessageMedia(RequestBody data, ListenersUtils.ConfirmationCallback callback){
        binding.itemChat.sendMessageBtn.setEnabled(false);
        binding.itemChat.sendMessageBtn.setAlpha(0.5f);
        ApiServices apiServices = RetrofitClient.getChatBotRetrofitClient().create(ApiServices.class);
        Call<ResponseCreateMessage> call = apiServices.sendMessageWithFile(data);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseCreateMessage> call, Response<ResponseCreateMessage> response) {
                if (response.isSuccessful()) {
                    ResponseCreateMessage body = response.body();
                    if (body != null) {
                        if (body.getData() != null) {
                            adapter.addMessage(body.getData());
                            callback.onResult(true);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseCreateMessage> call, Throwable throwable) {
                Log.e(TAG, "onFailure: " + throwable.getMessage());
                binding.itemChat.sendMessageBtn.setEnabled(true);
                binding.itemChat.sendMessageBtn.setAlpha(1.0f);
                MessagesUtils.showErrorDialog(requireActivity(), ErrorUtils.parseError(throwable));
            }
        });
    }


    private void deleteMessage(Messages interactions, int pos){
        ApiServices apiServices = RetrofitClient.getChatBotRetrofitClient().create(ApiServices.class);
        Call<JsonObject> call = apiServices.deleteMessage(interactions.getId());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject body = response.body();
                    if (body != null) {
                        if (body.has("status")) {
                            if (body.get("status").getAsBoolean()) {
                                adapter.deleteItem(pos);
                                updateUI(adapter.getMessages());
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

    private void updateUI(List<Messages> messages){
        if (!messages.isEmpty()){
            binding.itemChat.chatRv.setVisibility(View.VISIBLE);
            binding.itemChat.containerFirstMsg.setVisibility(View.GONE);
        }else{
            binding.itemChat.chatRv.setVisibility(View.GONE);
            binding.itemChat.containerFirstMsg.setVisibility(View.VISIBLE);
        }
    }

    private void showRecordContainer(Animation slideOut, Animation slideIn) {
        binding.itemChat.containerTextMessage.startAnimation(slideOut);
        binding.itemChat.containerTextMessage.setVisibility(View.GONE);

        binding.itemChat.containerRecordAudio.setVisibility(View.VISIBLE);
        binding.itemChat.containerRecordAudio.startAnimation(slideIn);
    }

    private void showTextContainer(Animation slideOut, Animation slideIn) {
        binding.itemChat.containerRecordAudio.startAnimation(slideOut);
        binding.itemChat.containerRecordAudio.setVisibility(View.GONE);

        binding.itemChat.containerTextMessage.setVisibility(View.VISIBLE);
        binding.itemChat.containerTextMessage.startAnimation(slideIn);
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


            filesManager.addImage(uri);
//            Messages message = new Messages();

            //TODO: LOGIC UPLOAD AUDIO HERE
//            adapter.addMessage(message);
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

    private void startSession(Map<String, Object> data, ListenersUtils.OnSessionStartedOrEndCallback callback){
        ApiServices apiServices = AppController.getApiServices();
        Call<ResponseStartEndSession> call = apiServices.startSession(data);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseStartEndSession> call, Response<ResponseStartEndSession> response) {
                if (response.isSuccessful()) {
                    ResponseStartEndSession body = response.body();
                    if (body != null) {
                        if (body.isStatus()) {
                            if (body.getData() != null) {
                                callback.onSession(body.getData());
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseStartEndSession> call, Throwable throwable) {
                Log.e(TAG, "onFailure: START SESSION " + throwable.getMessage());
            }
        });
    }

    private void endSession(String sessionId, ListenersUtils.OnSessionStartedOrEndCallback callback){
        ApiServices apiServices = AppController.getApiServices();
        Call<ResponseStartEndSession> call = apiServices.endSession(sessionId);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseStartEndSession> call, Response<ResponseStartEndSession> response) {
                if (response.isSuccessful()) {
                    ResponseStartEndSession body = response.body();
                    if (body != null) {
                        if (body.isStatus()) {
                            if (body.getData() != null) {
                                callback.onSession(body.getData());
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseStartEndSession> call, Throwable throwable) {
                Log.e(TAG, "onFailure:END SESSION " + throwable.getMessage());
            }
        });
    }


    private OptionsFirstMsgAdapter getOptionsAdapter(){

        List<CategoryItem> items = new ArrayList<>();
        items.add(new CategoryItem(getString(R.string.memories_no_icon), R.drawable.ic_memories, R.color.card_memories_bg));
        items.add(new CategoryItem(getString(R.string.family_no_icon), R.drawable.ic_family, R.color.card_family_bg));
        items.add(new CategoryItem(getString(R.string.stories), R.drawable.ic_book, R.color.card_stories_bg));
        items.add(new CategoryItem(getString(R.string.photos), R.drawable.ic_camera, R.color.card_photos_bg));
        items.add(new CategoryItem(getString(R.string.music), R.drawable.ic_music, R.color.card_music_bg));
        items.add(new CategoryItem(getString(R.string.lessons), R.drawable.ic_star, R.color.card_lessons_bg));
        items.add(new CategoryItem(getString(R.string.chat), R.drawable.ic_sms, R.color.card_chat_bg));
        items.add(new CategoryItem(getString(R.string.relax), R.drawable.ic_relax, R.color.card_relax_bg));

        OptionsFirstMsgAdapter adapter = new OptionsFirstMsgAdapter(items, item -> {
            String title = item.getText();
            String textMsg = "";
            if (title.equals(getString(R.string.memories_no_icon))) {
                textMsg = "What is your favorite memory from childhood?";
            }
            else if (title.equals(getString(R.string.family_no_icon))) {
                textMsg = "Tell me about your family";
            }
            else if (title.equals(getString(R.string.stories))) {
                textMsg = "Tell me a story from your life";
            }
            else if (title.equals(getString(R.string.photos))) {
                textMsg = "Do you have any favorite photos you'd like to share?";
            }
            else if (title.equals(getString(R.string.music))) {
                textMsg = "What's the most important lesson you've learned?";
            }
            else if (title.equals(getString(R.string.lessons))) {
                textMsg = "What are you most proud of?";
            }
            else if (title.equals(getString(R.string.chat))) {
                textMsg = "Let's have a conversation";
            }
            else if (title.equals(getString(R.string.relax))) {
                textMsg = "Let's have a relaxed chat";
            }

            binding.itemChat.textMessage.setText(textMsg);
            binding.itemChat.textMessage.post(() -> {
                EditText edit = binding.itemChat.textMessage;
                edit.setSelection(edit.getText().length());
                edit.requestFocus();
            });
        });

        return adapter;
    }

    private void showProfileBottomSheet(LegacyProfile profile) {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_profile_info, null);

        TextView textName = view.findViewById(R.id.textName);
        TextView textRelationship = view.findViewById(R.id.textRelationshipValue);
        TextView textAge = view.findViewById(R.id.textAge);
        TextView textCountry = view.findViewById(R.id.textCountry);
        TextView textLanguage = view.findViewById(R.id.textLanguage);
        TextView textPersonality = view.findViewById(R.id.textPersonality);
        TextView textStatus = view.findViewById(R.id.textStatus);
        TextView textSessionInfo = view.findViewById(R.id.textSessionInfo);
        View viewStatusDot = view.findViewById(R.id.viewStatusDot);
        View btnClose = view.findViewById(R.id.buttonClose);

        textName.setText(profile.getName());

        textRelationship.setText(profile.getFamilyRelationship());

        if (profile.getAge() > 0) {
            textAge.setText(profile.getAge() + " years old");
        } else {
            textAge.setText("-");
        }

        textCountry.setText(
                profile.getCountry() != null && !profile.getCountry().isEmpty()
                        ? profile.getCountry()
                        : "-"
        );

        textLanguage.setText(
                profile.getLanguage() != null && !profile.getLanguage().isEmpty()
                        ? profile.getLanguage()
                        : "-"
        );

        if (profile.getBasePersonality() != null && !profile.getBasePersonality().isEmpty()) {
            String traits = android.text.TextUtils.join(", ", profile.getBasePersonality());
            textPersonality.setText(traits);
        } else {
            textPersonality.setText("-");
        }

        String state = profile.getState();
        if (state == null || state.isEmpty()) {
            state = "Inactive";
        }
        textStatus.setText(state);

        if ("Active".equalsIgnoreCase(state)) {
            viewStatusDot.setBackgroundResource(R.drawable.bg_status_dot_active);
        } else {
            viewStatusDot.setBackgroundResource(R.drawable.bg_status_dot_inactive);
        }

        if (profile.getDateCreation() != null && !profile.getDateCreation().isEmpty()) {
            textSessionInfo.setText("Active session started " + profile.getDateCreation());
        } else {
            textSessionInfo.setText("No active session information");
        }

        btnClose.setOnClickListener(v -> dialog.dismiss());

        dialog.setContentView(view);
        dialog.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (GlobalDataCache.legacyProfileSelected != null){
            String lastMsg = binding.itemChat.textMessage.getText().toString();
            String json = sessionManager.getLastMsgMap();
            Type type = new TypeToken<Map<String, String>>(){}.getType();
            Map<String, String> map = gson.fromJson(json, type);
            if (map == null){
                map = new HashMap<>();
            }
            map.put(GlobalDataCache.legacyProfileSelected.getId(), lastMsg);
            String newJson = gson.toJson(map);
            sessionManager.setLastMsgMap(newJson);
        }
    }

    private void parseLastMessage(String profileId){
        String json = sessionManager.getLastMsgMap();
        Type type = new TypeToken<Map<String, String>>(){}.getType();
        Map<String, String> map = gson.fromJson(json, type);
        if (map != null){
            if(map.containsKey(profileId)){
                if (map.get(profileId) != null){
                    if (!Objects.requireNonNull(map.get(profileId)).isEmpty()){
                        String lastMsg = map.get(profileId);
                        if (lastMsg != null){
                            if (!lastMsg.isEmpty()){
                                binding.itemChat.textMessage.post(() -> {
                                    binding.itemChat.textMessage.setText(lastMsg);
                                    binding.itemChat.textMessage.requestFocus();
                                    binding.itemChat.textMessage.setSelection(binding.itemChat.textMessage.getText().length());
                                });

                            }else{
                                binding.itemChat.textMessage.post(() -> binding.itemChat.textMessage.setText(""));
                            }

                        }
                    }
                }
            }
        }
    }

}
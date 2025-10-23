package com.t4app.t4everandroid.main.ui.media;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.t4app.t4everandroid.MessagesUtils;
import com.t4app.t4everandroid.R;
import com.t4app.t4everandroid.SafeClickListener;
import com.t4app.t4everandroid.databinding.ActivityRecordAudioBinding;

import java.io.File;
import java.io.IOException;

public class RecordAudioActivity extends AppCompatActivity {
    private static final String TAG = "";
    private ActivityRecordAudioBinding binding;

    private MediaRecorder recorder;
    private File audioFile;
    private boolean isRecording = false;

    private Handler handler = new Handler();
    private boolean isRunning = false;
    private long startTime = 0L;

    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long elapsed = System.currentTimeMillis() - startTime;

            int seconds = (int) (elapsed / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;

            String time = String.format("%02d:%02d", minutes, seconds);
            binding.timeRecord.setText(time);

            handler.postDelayed(this, 1000);
        }
    };

    private final ActivityResultLauncher<String> permissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted){
                    startRecording();
                }else{
                    MessagesUtils.showErrorDialog(RecordAudioActivity.this,
                            getString(R.string.microphone_permission_denied));
                }

            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_record_audio);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding = ActivityRecordAudioBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.startRecordingBtn.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                if (isRecording){
                    stopRecording();
                }else{
                    checkPermissionAndStart();
                }
                startTime = System.currentTimeMillis();
                handler.post(timerRunnable);
                isRunning = true;
                binding.containerStopRecord.setVisibility(View.VISIBLE);
                binding.containerRecordAudio.setVisibility(View.GONE);
            }
        });

        binding.stopRecordAudio.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                handler.removeCallbacks(timerRunnable);
                isRunning = false;

                binding.containerStopRecord.setVisibility(View.GONE);
                binding.containerRecordAudio.setVisibility(View.VISIBLE);
                finishAndReturn();
            }
        });
    }

    private void checkPermissionAndStart() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO);
        } else {
            startRecording();
        }
    }

    private void startRecording() {
        try {
            File dir = getExternalFilesDir(Environment.DIRECTORY_MUSIC);
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

    private void finishAndReturn() {
        if (isRecording) stopRecording();

        if (audioFile != null && audioFile.exists()) {
            Uri uri = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".provider",
                    audioFile
            );

            Intent resultIntent = new Intent();
            resultIntent.putExtra("audio_uri", uri.toString());
            setResult(RESULT_OK, resultIntent);
        } else {
            setResult(RESULT_CANCELED);
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }
    }
}
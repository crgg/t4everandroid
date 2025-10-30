package com.t4app.t4everandroid.main.ui.legacyProfile;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.t4app.t4everandroid.AppController;
import com.t4app.t4everandroid.CameraPermissionManager;
import com.t4app.t4everandroid.ErrorUtils;
import com.t4app.t4everandroid.ImageSelectorBottomSheet;
import com.t4app.t4everandroid.ListenersUtils;
import com.t4app.t4everandroid.MessagesUtils;
import com.t4app.t4everandroid.R;
import com.t4app.t4everandroid.SafeClickListener;
import com.t4app.t4everandroid.SelectImageUtils;
import com.t4app.t4everandroid.databinding.ActivityCreateLegacyProfileBinding;
import com.t4app.t4everandroid.main.GlobalDataCache;
import com.t4app.t4everandroid.main.Models.LegacyProfile;
import com.t4app.t4everandroid.main.Models.ProfileRequest;
import com.t4app.t4everandroid.network.responses.ResponseCreateAssistant;
import com.t4app.t4everandroid.network.ApiServices;
import com.t4app.t4everandroid.network.responses.ResponseUpdateProfile;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateLegacyProfileActivity extends AppCompatActivity {
    private static final String TAG = "CREATE_LEGACY_PROFILE_ACT";
    private ActivityCreateLegacyProfileBinding binding;

    private TextInputLayout fullNameLayout, aliasLayout, relationshipLayout, personalityLayout,
            birthdateLayout, deathdateLayout;
    private TextInputEditText fullNameValue, aliasValue, relationshipValue, personalityValue,
            birthdateValue, deathdateValue;
    private AutoCompleteTextView autoCountry, autoLanguage;

    private LegacyProfile profile;

    private ActivityResultLauncher<Uri> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private Uri photoUri;
    private Uri realPdfUri;
    private File photoFile;

    private SelectImageUtils selectImageUtils;

    private ActivityResultLauncher<String> cameraPermissionLauncher;
    private CameraPermissionManager.PermissionCallback permissionCallback;
    private ImageSelectorBottomSheet bottomSheet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_legacy_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        boolean isUpdate = getIntent().getBooleanExtra("is_update", false);

        binding = ActivityCreateLegacyProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initViews();

        selectImageUtils = new SelectImageUtils(this);

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null){
                            photoUri = imageUri;
                            MessagesUtils.showPreviewImage(CreateLegacyProfileActivity.this, photoUri, true, new ListenersUtils.OnActionPreviewImageListener() {
                                @Override
                                public void onSaveImage() {
                                    realPdfUri = imageUri;
                                    Glide.with(CreateLegacyProfileActivity.this)
                                            .load(realPdfUri)
                                            .transform(new CircleCrop())
                                            .into(binding.uploadImageBtn);
                                    binding.iconUpload.setImageTintList(ColorStateList.valueOf(
                                            ContextCompat.getColor(CreateLegacyProfileActivity.this, R.color.second_login_color)
                                    ));
                                }

                                @Override
                                public void onTakeAnother() {
                                    openGallery();
                                }
                            });
                        }

                    }
                }
        );

        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                success -> {
                    if (success && photoUri != null) {
                        MessagesUtils.showPreviewImage(CreateLegacyProfileActivity.this, photoUri, false, new ListenersUtils.OnActionPreviewImageListener() {
                            @Override
                            public void onSaveImage() {
                                realPdfUri = photoUri;
                                Glide.with(CreateLegacyProfileActivity.this)
                                        .load(realPdfUri)
                                        .transform(new CircleCrop())
                                        .into(binding.uploadImageBtn);
                                binding.iconUpload.setImageTintList(ColorStateList.valueOf(
                                        ContextCompat.getColor(CreateLegacyProfileActivity.this, R.color.second_login_color)
                                ));
                            }

                            @Override
                            public void onTakeAnother() {
                                openCamera();
                            }
                        });
                    }
                }
        );

        bottomSheet = getImageSelectorBottomSheet();

        setupPermissionCallback();
        setupPermissionLauncher();

        binding.backBtn.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                finish();
            }
        });

        if (isUpdate){
            profile = (LegacyProfile) getIntent().getSerializableExtra("legacy_profile");
            if (profile != null){
                setValues(profile);
            }

            binding.titleCreateProfile.setText(R.string.edit_legacy_profile);

            binding.createProfileBtn.setText(R.string.update_legacy_profile);
        }

        binding.birthdateValue.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                showDatePicker(binding.birthdateValue);
            }
        });

        binding.deathdateValue.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                showDatePicker(binding.deathdateValue);
            }
        });

        setupCountryAutocomplete();
        setupLanguageAutocomplete();

        binding.createProfileBtn.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                if (validateInputs()){
                    String fullName = getTextTv(fullNameValue);
                    String alias = getTextTv(aliasValue);
                    String relationship = getTextTv(relationshipValue);
                    String birthdate = formatDate(getTextTv(birthdateValue));
                    String deathdate = formatDate(getTextTv(deathdateValue));
                    List<String> personalityList = getPersonalityList(getTextTv(personalityValue));
                    String country = autoCountry.getText().toString().trim();
                    String language = autoLanguage.getText().toString().trim();

                    ProfileRequest request = new ProfileRequest(alias,
                            calculateAge(birthdate),
                            birthdate,
                            deathdate,
                            relationship,
                            personalityList,
                            binding.activeCheckBox.isChecked(),
                            language,
                            country,
                            fullName
                    );

                    if (isUpdate){
                        updateProfile(request, profile);
                    }else{
                        createUser(request);
                    }
                }
            }
        });

        binding.uploadImageBtn.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                bottomSheet.show(getSupportFragmentManager(), "ImageSelector");
            }
        });
    }

    private void createUser(ProfileRequest data){
        ApiServices apiServices = AppController.getApiServices();
        Call<ResponseCreateAssistant> call = apiServices.createAssistant(data);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseCreateAssistant> call, Response<ResponseCreateAssistant> response) {
                if (response.isSuccessful()) {
                    ResponseCreateAssistant body = response.body();
                    if (body != null) {
                        if (body.isStatus()) {
                            if (body.getData() != null) {
                                GlobalDataCache.legacyProfiles.add(body.getData());
                                if (body.getMsg() != null){
                                    MessagesUtils.showMessageFinishAndReturnBool(CreateLegacyProfileActivity.this, body.getMsg());
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseCreateAssistant> call, Throwable throwable) {
                MessagesUtils.showErrorDialog(CreateLegacyProfileActivity.this,
                        ErrorUtils.parseError(throwable));
            }
        });
    }

    private void updateProfile(ProfileRequest data, LegacyProfile legacyProfile){
        ApiServices apiServices = AppController.getApiServices();
        Call<ResponseCreateAssistant> call = apiServices.updateAssistant(legacyProfile.getId(), data);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseCreateAssistant> call, Response<ResponseCreateAssistant> response) {
                if (response.isSuccessful()) {
                    ResponseCreateAssistant body = response.body();
                    if (body != null) {
                        if (body.isStatus()) {
                            if (body.getData() != null) {
                                GlobalDataCache.legacyProfiles.set(
                                        GlobalDataCache.legacyProfiles.indexOf(legacyProfile),
                                        body.getData()
                                );
                                MessagesUtils.showMessageFinishAndReturn(CreateLegacyProfileActivity.this,
                                        body.getMsg(), "update_list");

                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseCreateAssistant> call, Throwable throwable) {
                MessagesUtils.showErrorDialog(CreateLegacyProfileActivity.this,
                        ErrorUtils.parseError(throwable));
            }
        });
    }


    private String getTextTv(@NonNull TextInputEditText editText) {
        String text = editText.getText() != null ? editText.getText().toString().trim() : "";
        return text;
    }


    private void showDatePicker(TextInputEditText et) {
        final Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (DatePicker view, int selectedYear, int selectedMonth, int selectedDay) -> {
                    String selectedDate = String.format(Locale.ENGLISH,"%02d/%02d/%04d", selectedDay, (selectedMonth + 1), selectedYear);
                    et.setText(selectedDate);
                },
                year, month, day
        );

        datePickerDialog.show();
    }

    private void setupLanguageAutocomplete() {
        List<String> languages = Arrays.asList(getString(R.string.english), getString(R.string.spanish),
                getString(R.string.french), getString(R.string.portuguese), getString(R.string.russian));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                languages
        );

        binding.autoLanguage.setAdapter(adapter);
    }

    private void setupCountryAutocomplete() {
        String[] isoCountries = Locale.getISOCountries();
        List<String> countryList = new ArrayList<>();

        for (String countryCode : isoCountries) {
            Locale locale = new Locale("", countryCode);
            String countryName = locale.getDisplayCountry();
            if (!countryName.isEmpty()) {
                countryList.add(countryName);
            }
        }
        Collections.sort(countryList);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                countryList
        );

        binding.autoCountry.setAdapter(adapter);
        binding.autoCountry.setText(getString(R.string.united_state), false);
    }

    private boolean validateInputs() {
        boolean valid = true;

        clearErrors();

        if (isEmpty(fullNameValue)) {
            setError(fullNameLayout, getString(R.string.full_name_is_required));
            valid = false;
        }

        if (isEmpty(aliasValue)) {
            setError(aliasLayout, getString(R.string.alias_is_required));
            valid = false;
        }

        if (isEmpty(relationshipValue)) {
            setError(relationshipLayout, getString(R.string.relationship_is_required));
            valid = false;
        }

        if (isEmpty(personalityValue)) {
            setError(personalityLayout, getString(R.string.personality_is_required));
            valid = false;
        }

        if (isEmpty(birthdateValue)) {
            setError(birthdateLayout, getString(R.string.birthdate_is_required));
            valid = false;
        }

        if (isEmpty(autoCountry)) {
            autoCountry.setError(getString(R.string.country_is_required));
            valid = false;
        }

        if (isEmpty(autoLanguage)) {
            autoLanguage.setError(getString(R.string.language_is_required));
            valid = false;
        }

        return valid;
    }

    private boolean isEmpty(TextView view) {
        return view.getText() == null || view.getText().toString().trim().isEmpty();
    }

    private void setError(TextInputLayout layout, String message) {
        layout.setErrorEnabled(true);
        layout.setError(message);

        try {
            layout.setErrorIconDrawable(null);
        } catch (Exception ignored) {}
    }

    private String formatDate(String dateString) {
        if (dateString.isEmpty()) return "";
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
            Date date = inputFormat.parse(dateString);
            return outputFormat.format(date);
        } catch (ParseException e) {
            Log.e(TAG, "formatDate: ", e);
            return dateString;
        }
    }

    private List<String> getPersonalityList(String personalityText) {
        if (personalityText == null || personalityText.isEmpty()) return new ArrayList<>();

        List<String> items = Arrays.asList(personalityText.split(","));
        List<String> cleanList = new ArrayList<>();
        for (String item : items) {
            String trimmed = item.trim();
            if (!trimmed.isEmpty()) {
                cleanList.add(trimmed);
            }
        }
        return cleanList;
    }

    private void initViews() {
        fullNameLayout = findViewById(R.id.full_name_layout);
        aliasLayout = findViewById(R.id.alias_layout);
        relationshipLayout = findViewById(R.id.relationship_layout);
        personalityLayout = findViewById(R.id.personality_layout);
        birthdateLayout = findViewById(R.id.birthdate_layout);
        deathdateLayout = findViewById(R.id.deathdate_layout);

        fullNameValue = findViewById(R.id.full_name_value);
        aliasValue = findViewById(R.id.alias_value);
        relationshipValue = findViewById(R.id.relationship_value);
        personalityValue = findViewById(R.id.personality_value);
        birthdateValue = findViewById(R.id.birthdate_value);
        deathdateValue = findViewById(R.id.deathdate_value);

        autoCountry = findViewById(R.id.auto_country);
        autoLanguage = findViewById(R.id.auto_language);
    }

    @NonNull
    private ImageSelectorBottomSheet getImageSelectorBottomSheet() {
        ImageSelectorBottomSheet bottomSheet = new ImageSelectorBottomSheet();
        bottomSheet.setListener(new ImageSelectorBottomSheet.Listener() {
            @Override
            public void onCameraSelected() {
                if (CameraPermissionManager.hasCameraPermission(CreateLegacyProfileActivity.this)){
                    openCamera();
                }else{
                    CameraPermissionManager.requestCameraPermission(
                            CreateLegacyProfileActivity.this,
                            cameraPermissionLauncher,
                            permissionCallback);
                }
            }

            @Override
            public void onGallerySelected() {
                openGallery();
            }
        });
        return bottomSheet;
    }

    private void clearErrors() {
        fullNameLayout.setError(null);
        aliasLayout.setError(null);

        relationshipLayout.setError(null);
        personalityLayout.setError(null);
        birthdateLayout.setError(null);
        deathdateLayout.setError(null);
        autoCountry.setError(null);
        autoLanguage.setError(null);
    }

    private void setValues(LegacyProfile profile) {
        fullNameValue.setText(profile.getName());
        aliasValue.setText(profile.getAlias());
        relationshipValue.setText(profile.getFamilyRelationship());
        personalityValue.setText(String.join(", ", profile.getBasePersonality()));
//        birthdateValue.setText(profile.get);
//        deathdateValue.setText();
        autoCountry.setText(profile.getCountry());
        autoLanguage.setText(profile.getLanguage());
    }

    public int calculateAge(String birthDateString) {
        if (birthDateString == null || birthDateString.isEmpty()) return 0;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date birthDate = sdf.parse(birthDateString);

            Calendar birthCal = Calendar.getInstance();
            birthCal.setTime(birthDate);

            Calendar today = Calendar.getInstance();

            int age = today.get(Calendar.YEAR) - birthCal.get(Calendar.YEAR);

            if (today.get(Calendar.DAY_OF_YEAR) < birthCal.get(Calendar.DAY_OF_YEAR)) {
                age--;
            }
            return age;

        } catch (ParseException e) {
            Log.e(TAG, "calculateAge: ", e);
            return 0;
        }
    }

    private void setupPermissionCallback() {
        permissionCallback = new CameraPermissionManager.PermissionCallback() {
            @Override
            public void onPermissionGranted() {
                openCamera();
            }

            @Override
            public void onPermissionDenied() {
                MessagesUtils.showErrorDialog(CreateLegacyProfileActivity.this,getString(R.string.permission_to_use_camera_denied));
            }

            @Override
            public void onPermissionPermanentlyDenied() {
                CameraPermissionManager.showPermanentlyDeniedDialog(CreateLegacyProfileActivity.this);
            }
        };
    }

    private void uploadImageUser(){
        try {
            Log.d(TAG, "uploadImageUser: ");
            File file = selectImageUtils.getFileFromUri(realPdfUri);
            RequestBody requestFile = RequestBody.create(file, MediaType.parse("image/*"));
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

            ApiServices apiServices = AppController.getApiServices();
            Call<ResponseUpdateProfile> call = apiServices.uploadImageProfile(body);
            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<ResponseUpdateProfile> call, Response<ResponseUpdateProfile> response) {
                    if (response.isSuccessful()){
                        ResponseUpdateProfile body = response.body();
                        if (body != null){
                            if (body.isStatus()){
                                if (body.getData() != null){
                                    Log.d(TAG, "AVATAR URL :  " + body.getData().getAvatarUrl());
                                }
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseUpdateProfile> call, Throwable throwable) {
                    Log.e(TAG, "Upload Image Error Throw" + throwable.getMessage());
                }
            });
        }catch (Exception e){
            Log.e(TAG, "Upload Image Error " + e);
        }

    }

    private void setupPermissionLauncher() {
        cameraPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> CameraPermissionManager.handlePermissionResult(
                        isGranted, CreateLegacyProfileActivity.this, permissionCallback));
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private void openCamera() {
        photoFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "photo_" + System.currentTimeMillis() + ".jpg");
        photoUri = FileProvider.getUriForFile(
                this,
                getPackageName() + ".provider",
                photoFile
        );
        cameraLauncher.launch(photoUri);
    }


}
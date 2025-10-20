package com.t4app.t4everandroid.main.ui;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonObject;
import com.t4app.t4everandroid.AppController;
import com.t4app.t4everandroid.CameraPermissionManager;
import com.t4app.t4everandroid.ErrorUtils;
import com.t4app.t4everandroid.ImageSelectorBottomSheet;
import com.t4app.t4everandroid.ListenersUtils;
import com.t4app.t4everandroid.MessagesUtils;
import com.t4app.t4everandroid.R;
import com.t4app.t4everandroid.SafeClickListener;
import com.t4app.t4everandroid.SelectImageUtils;
import com.t4app.t4everandroid.SessionManager;
import com.t4app.t4everandroid.databinding.FragmentUpdateProfileBinding;
import com.t4app.t4everandroid.databinding.ItemChangePasswordBinding;
import com.t4app.t4everandroid.main.Models.ResponseUpdateProfile;
import com.t4app.t4everandroid.main.repository.UserRepository;
import com.t4app.t4everandroid.network.ApiServices;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateProfileFragment extends Fragment {
    private static final String TAG = "UPDATE_PROFILE";
    private FragmentUpdateProfileBinding binding;

    private ItemChangePasswordBinding changePasswordBinding;

    private SessionManager sessionManager;

    private ActivityResultLauncher<Uri> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private Uri photoUri;
    private Uri realPdfUri;
    private File photoFile;

    private ActivityResultLauncher<String> cameraPermissionLauncher;
    private CameraPermissionManager.PermissionCallback permissionCallback;

    private ImageSelectorBottomSheet bottomSheet;

    private SelectImageUtils selectImageUtils;

    private UserRepository userRepository;

    public UpdateProfileFragment() {
    }

    public static UpdateProfileFragment newInstance() {
        return new UpdateProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        selectImageUtils = new SelectImageUtils(requireActivity());

        setupPermissionCallback();
        setupPermissionLauncher();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUpdateProfileBinding.inflate(inflater, container, false);
        sessionManager = SessionManager.getInstance();
        View view = binding.getRoot();

        binding.birthdateValue.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                showDatePicker(binding.birthdateValue);
            }
        });
        changePasswordBinding = binding.changePasswordItem;

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null){
                            photoUri = imageUri;
                            MessagesUtils.showPreviewImage(requireActivity(), photoUri, true, new ListenersUtils.OnActionPreviewImageListener() {
                                @Override
                                public void onSaveImage() {
                                    realPdfUri = imageUri;
                                    Glide.with(requireActivity())
                                            .load(realPdfUri)
                                            .transform(new CircleCrop())
                                            .into(binding.uploadImageBtn);
                                    binding.iconUpload.setImageTintList(ColorStateList.valueOf(
                                            ContextCompat.getColor(requireActivity(), R.color.second_login_color)
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
                        MessagesUtils.showPreviewImage(requireActivity(), photoUri, false, new ListenersUtils.OnActionPreviewImageListener() {
                            @Override
                            public void onSaveImage() {
                                realPdfUri = photoUri;
                                Glide.with(requireActivity())
                                        .load(realPdfUri)
                                        .transform(new CircleCrop())
                                        .into(binding.uploadImageBtn);
                                binding.iconUpload.setImageTintList(ColorStateList.valueOf(
                                        ContextCompat.getColor(requireActivity(), R.color.second_login_color)
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

        userRepository = new UserRepository();
        userRepository.getUser(sessionManager.getUserId()).observe(getViewLifecycleOwner(), user -> {
            Log.d(TAG, "GET USER: ");
            if (user != null){
                binding.fullNameValue.setText(user.getName());
                binding.autoCountry.setText(user.getCountry());
                binding.autoLanguage.setText(user.getLanguage());
                binding.aliasValue.setText(user.getAlias());
                if (user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()){
                    String path = user.getAvatarUrl();
                    if (!user.getAvatarUrl().contains("https://go2storage.s3.us-east-2.amazonaws.com")){
                        path = "https://go2storage.s3.us-east-2.amazonaws.com/" + user.getAvatarUrl();
                    }
                    Log.d(TAG, "USER HAS AVATAR " + path);
                    Glide.with(requireActivity())
                            .load(path)
                            .transform(new CircleCrop())
                            .into(binding.uploadImageBtn);
                    binding.iconUpload.setImageTintList(ColorStateList.valueOf(
                            ContextCompat.getColor(requireActivity(), R.color.second_login_color)
                    ));
                    sessionManager.setAvatarUrl(path);
                }
            }
        });


        changePasswordBinding.savePassword.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                if (validatePasswordInputs()){
                    if (!validatePasswordInputs()) return;

                    Map<String, Object> data = new HashMap<>();
                    data.put("current_password", changePasswordBinding.currentPasswordValue.getText().toString().trim());
                    data.put("password", changePasswordBinding.newPasswordValue.getText().toString().trim());
                    data.put("password_confirmation", changePasswordBinding.confirmPasswordValue.getText().toString().trim());

                    updatePassword(data);
                }
            }
        });
        changePasswordBinding.changePasswordCheck.setOnCheckedChangeListener((compoundButton, b) -> {
            if(b){
                changePasswordBinding.inputPasswordContainer.setVisibility(View.VISIBLE);
            }else{
                changePasswordBinding.inputPasswordContainer.setVisibility(View.GONE);
            }
        });

        setupCountryAutocomplete();
        setupLanguageAutocomplete();

        bottomSheet = getImageSelectorBottomSheet();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        binding.iconUpload.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                bottomSheet.show(getChildFragmentManager(), "ImageSelector");
            }
        });

        binding.updateProfileBtn.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                if (validateInputs()){
                    Map<String, Object> data = new HashMap<>();

                    String fullName = binding.fullNameValue.getText().toString().trim();
                    String alias = binding.aliasValue.getText().toString().trim();
                    String birthdate = binding.birthdateValue.getText().toString().trim();
                    String country = binding.autoCountry.getText().toString().trim();
                    String language = binding.autoLanguage.getText().toString().trim();

                    data.put("name", fullName);
                    data.put("alias", alias);
                    data.put("age", calculateAge(birthdate));
                    data.put("birthdate", birthdate);
                    data.put("country", country);
                    data.put("language", language);
                    if (realPdfUri != null){
                        uploadImageUser();
                    }
                    updateProfile(data, user -> {
                        sessionManager.saveExtraUserDetails(user.getAlias(),user.getRol(), user.getAge(), user.getCountry(),
                                user.getAvatarUrl(), user.getDateRegister(), user.getLastLogin(), user.getEmailVerifiedAt());

                        showFragment(new HomeFragment());

                    });


                }
            }
        });

        binding.cancelBtn.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                showFragment(new HomeFragment());
            }
        });
    }

    @NonNull
    private ImageSelectorBottomSheet getImageSelectorBottomSheet() {
        ImageSelectorBottomSheet bottomSheet = new ImageSelectorBottomSheet();
        bottomSheet.setListener(new ImageSelectorBottomSheet.Listener() {
            @Override
            public void onCameraSelected() {
                if (CameraPermissionManager.hasCameraPermission(requireContext())){
                    openCamera();
                }else{
                    CameraPermissionManager.requestCameraPermission(
                            requireActivity(),
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
                                    userRepository.updateImagePath(body.getData().getId(), body.getData().getAvatarUrl());
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


    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private void openCamera() {
        photoFile = new File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "photo_" + System.currentTimeMillis() + ".jpg");
        photoUri = FileProvider.getUriForFile(
                requireContext(),
                requireActivity().getPackageName() + ".provider",
                photoFile
        );
        cameraLauncher.launch(photoUri);
    }




    private boolean validatePasswordInputs() {
        boolean valid = true;

        String current = changePasswordBinding.currentPasswordValue.getText() != null ?
                changePasswordBinding.currentPasswordValue.getText().toString().trim() : "";
        String newPass = changePasswordBinding.newPasswordValue.getText() != null ?
                changePasswordBinding.newPasswordValue.getText().toString().trim() : "";
        String confirm = changePasswordBinding.confirmPasswordValue.getText() != null ?
                changePasswordBinding.confirmPasswordValue.getText().toString().trim() : "";

        if (current.isEmpty()) {
            changePasswordBinding.currentPasswordLayout.setError(getString(R.string.current_password_is_required));
            changePasswordBinding.currentPasswordValue.requestFocus();
            valid = false;
        }

        if (newPass.isEmpty()) {
            changePasswordBinding.newPasswordLayout.setError(getString(R.string.new_password_is_required));
            changePasswordBinding.newPasswordValue.requestFocus();
            valid = false;
        }

        if (confirm.isEmpty()) {
            changePasswordBinding.confirmPasswordLayout.setError(getString(R.string.confirm_password_is_required));
            changePasswordBinding.confirmPasswordValue.requestFocus();
            valid = false;
        }

        if (!newPass.isEmpty() && !confirm.isEmpty() && !newPass.equals(confirm)) {
            changePasswordBinding.confirmPasswordLayout.setError(getString(R.string.passwords_do_not_match));
            changePasswordBinding.confirmPasswordValue.requestFocus();
            valid = false;
        }

        return valid;
    }


    private void showFragment(Fragment fragment) {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
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
//            Log.e(TAG, "calculateAge: ", e);
            return 0;
        }
    }

    private void updateProfile(Map<String,Object> data, ListenersUtils.OnUserUpdateListener listener){
        ApiServices apiServices = AppController.getApiServices();
        Call<ResponseUpdateProfile> call = apiServices.updateProfile(data);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ResponseUpdateProfile> call, @NonNull Response<ResponseUpdateProfile> response) {
                if (response.isSuccessful()){
                    ResponseUpdateProfile body = response.body();
                    if (body != null){
                        if (body.isStatus()){
                            if (body.getData() != null){
                                listener.onResult(body.getData());
                                MessagesUtils.showSuccessDialog(requireActivity(), body.getMessage());
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseUpdateProfile> call, @NonNull Throwable throwable) {
                MessagesUtils.showErrorDialog(requireActivity(), ErrorUtils.parseError(throwable));
            }
        });
    }

    private void updatePassword(Map<String,Object> data){
        ApiServices apiServices = AppController.getApiServices();
        Call<JsonObject> call = apiServices.changePassword(data);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()){
                    JsonObject body = response.body();
                    if (body != null){
                        if (body.get("status").getAsBoolean()){
                            if (body.has("message")){
                                String message = body.get("message").getAsString();
                                MessagesUtils.showSuccessDialog(requireActivity(), message);
                                changePasswordBinding.currentPasswordValue.setText("");
                                changePasswordBinding.newPasswordValue.setText("");
                                changePasswordBinding.confirmPasswordValue.setText("");
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable throwable) {
                MessagesUtils.showErrorDialog(requireActivity(), ErrorUtils.parseError(throwable));
            }
        });
    }

    private boolean validateInputs() {
        boolean isValid = true;

        String fullName = binding.fullNameValue.getText().toString().trim();
        if (fullName.isEmpty()) {
            binding.fullNameLayout.setError(getString(R.string.please_enter_your_full_name));
            binding.fullNameValue.requestFocus();
            isValid =  false;
        }

        String alias = binding.aliasValue.getText().toString().trim();
        if (alias.isEmpty()) {
            binding.aliasLayout.setError(getString(R.string.please_enter_an_alias));
            binding.aliasValue.requestFocus();
            isValid =  false;
        }

        String birthdate = binding.birthdateValue.getText().toString().trim();
        if (birthdate.isEmpty()) {
            binding.birthdateLayout.setError(getString(R.string.please_enter_your_birthdate));
            binding.birthdateValue.requestFocus();
            isValid =  false;
        }

        String country = binding.autoCountry.getText().toString().trim();
        if (country.isEmpty()) {
            binding.autoCountry.setError(getString(R.string.please_select_a_country));
            binding.autoCountry.requestFocus();
            isValid =  false;
        }

        String language = binding.autoLanguage.getText().toString().trim();
        if (language.isEmpty()) {
            binding.autoLanguage.setError(getString(R.string.please_select_a_language));
            binding.autoLanguage.requestFocus();
            isValid =  false;
        }

        return isValid;
    }

    private void setupLanguageAutocomplete() {
        List<String> languages = Arrays.asList(getString(R.string.english), getString(R.string.spanish),
                getString(R.string.french), getString(R.string.portuguese), getString(R.string.russian));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireActivity(),
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
                requireActivity(),
                android.R.layout.simple_dropdown_item_1line,
                countryList
        );

        binding.autoCountry.setAdapter(adapter);
        binding.autoCountry.setText(getString(R.string.united_state), false);
    }

    private void showDatePicker(TextInputEditText et) {
        final Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                (DatePicker view, int selectedYear, int selectedMonth, int selectedDay) -> {
                    String selectedDate = String.format(Locale.ENGLISH, "%02d/%02d/%04d",
                            selectedDay, (selectedMonth + 1), selectedYear);
                    et.setText(selectedDate);
                },
                year, month, day
        );

        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
        datePickerDialog.getDatePicker().setMinDate(new GregorianCalendar(1900, 0, 1).getTimeInMillis());
        datePickerDialog.show();

    }

    private void setupPermissionCallback() {
        permissionCallback = new CameraPermissionManager.PermissionCallback() {
            @Override
            public void onPermissionGranted() {
                openCamera();
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

    private void setupPermissionLauncher() {
        cameraPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> CameraPermissionManager.handlePermissionResult(
                        isGranted, requireContext(), permissionCallback));
    }

}
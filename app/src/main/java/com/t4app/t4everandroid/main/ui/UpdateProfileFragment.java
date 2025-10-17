package com.t4app.t4everandroid.main.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.DatePicker;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonObject;
import com.t4app.t4everandroid.AppController;
import com.t4app.t4everandroid.ErrorUtils;
import com.t4app.t4everandroid.ListenersUtils;
import com.t4app.t4everandroid.Login.models.User;
import com.t4app.t4everandroid.MessagesUtils;
import com.t4app.t4everandroid.R;
import com.t4app.t4everandroid.SafeClickListener;
import com.t4app.t4everandroid.SessionManager;
import com.t4app.t4everandroid.databinding.FragmentLegacyProfilesBinding;
import com.t4app.t4everandroid.databinding.FragmentUpdateProfileBinding;
import com.t4app.t4everandroid.databinding.ItemChangePasswordBinding;
import com.t4app.t4everandroid.main.Models.ResponseUpdateProfile;
import com.t4app.t4everandroid.network.ApiServices;

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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateProfileFragment extends Fragment {

    private FragmentUpdateProfileBinding binding;

    private ItemChangePasswordBinding changePasswordBinding;

    private SessionManager sessionManager;

    public UpdateProfileFragment() {
    }

    public static UpdateProfileFragment newInstance() {
        return new UpdateProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUpdateProfileBinding.inflate(inflater, container, false);
        sessionManager = SessionManager.getInstance();
        View view = binding.getRoot();

//        binding.titleCreateProfile
//        binding.uploadImageBtn
//        binding.fullNameLayout
        binding.fullNameValue.setText(sessionManager.getName());
//        binding.aliasLayout
        binding.aliasValue.setText(sessionManager.getAlias());
        binding.birthdateValue.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                showDatePicker(binding.birthdateValue);
            }
        });
//        binding.birthdateValue.setText(sessionManager.get);
//        binding.autoCountryContainer
        binding.autoCountry.setText(sessionManager.getCountry());
//        binding.autoLanguageContainer
        binding.autoLanguage.setText(sessionManager.getLanguage());
//        binding.updateProfileBtn
//        binding.cancelBtn
//        binding.changePasswordItem
//
        changePasswordBinding = binding.changePasswordItem;
//        changePasswordBinding.inputPasswordContainer
//        changePasswordBinding.currentPasswordLayout
//        changePasswordBinding.currentPasswordValue
//        changePasswordBinding.newPasswordLayout
//        changePasswordBinding.newPasswordValue
//        changePasswordBinding.confirmPasswordLayout
//        changePasswordBinding.confirmPasswordValue
//        changePasswordBinding.savePassword


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
        //TODO:GET USER INFO
        changePasswordBinding.changePasswordCheck.setOnCheckedChangeListener((compoundButton, b) -> {
            if(b){
                changePasswordBinding.inputPasswordContainer.setVisibility(View.VISIBLE);
            }else{
                changePasswordBinding.inputPasswordContainer.setVisibility(View.GONE);
            }
        });

        setupCountryAutocomplete();
        setupLanguageAutocomplete();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
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
                    updateProfile(data, user -> {
                        sessionManager.saveExtraUserDetails(user.getAlias(),user.getRol(), user.getAge(), user.getCountry(),
                                user.getAvatarUrl(), user.getDateRegister(), user.getLastLogin(), user.getEmailVerifiedAt());

                        showFragment(new HomeFragment());

                    });


                }
            }
        });
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
            changePasswordBinding.currentPasswordValue.setError(getString(R.string.current_password_is_required));
            changePasswordBinding.currentPasswordValue.requestFocus();
            valid = false;
        }

        if (newPass.isEmpty()) {
            changePasswordBinding.newPasswordValue.setError(getString(R.string.new_password_is_required));
            changePasswordBinding.newPasswordValue.requestFocus();
            valid = false;
        }

        if (confirm.isEmpty()) {
            changePasswordBinding.confirmPasswordValue.setError(getString(R.string.confirm_password_is_required));
            changePasswordBinding.confirmPasswordValue.requestFocus();
            valid = false;
        }

        if (!newPass.isEmpty() && !confirm.isEmpty() && !newPass.equals(confirm)) {
            changePasswordBinding.confirmPasswordValue.setError(getString(R.string.passwords_do_not_match));
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
            public void onResponse(Call<ResponseUpdateProfile> call, Response<ResponseUpdateProfile> response) {
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
            public void onFailure(Call<ResponseUpdateProfile> call, Throwable throwable) {
                MessagesUtils.showErrorDialog(requireActivity(), ErrorUtils.parseError(throwable));
            }
        });
    }

    private void updatePassword(Map<String,Object> data){
        ApiServices apiServices = AppController.getApiServices();
        Call<JsonObject> call = apiServices.changePassword(data);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
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
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                MessagesUtils.showErrorDialog(requireActivity(), ErrorUtils.parseError(throwable));
            }
        });
    }

    private boolean validateInputs() {
        boolean isValid = true;

        String fullName = binding.fullNameValue.getText().toString().trim();
        if (fullName.isEmpty()) {
            binding.fullNameValue.setError(getString(R.string.please_enter_your_full_name));
            binding.fullNameValue.requestFocus();
            isValid =  false;
        }

        String alias = binding.aliasValue.getText().toString().trim();
        if (alias.isEmpty()) {
            binding.aliasValue.setError(getString(R.string.please_enter_an_alias));
            binding.aliasValue.requestFocus();
            isValid =  false;
        }

        String birthdate = binding.birthdateValue.getText().toString().trim();
        if (birthdate.isEmpty()) {
            binding.birthdateValue.setError(getString(R.string.please_enter_your_birthdate));
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


}
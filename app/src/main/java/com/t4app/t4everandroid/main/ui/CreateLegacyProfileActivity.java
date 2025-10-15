package com.t4app.t4everandroid.main.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.t4app.t4everandroid.AppController;
import com.t4app.t4everandroid.ErrorUtils;
import com.t4app.t4everandroid.MessagesUtils;
import com.t4app.t4everandroid.R;
import com.t4app.t4everandroid.SafeClickListener;
import com.t4app.t4everandroid.databinding.ActivityCreateLegacyProfileBinding;
import com.t4app.t4everandroid.main.GlobalDataCache;
import com.t4app.t4everandroid.main.Models.ProfileRequest;
import com.t4app.t4everandroid.main.Models.ResponseCreateAssistant;
import com.t4app.t4everandroid.network.ApiServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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

        binding = ActivityCreateLegacyProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initViews();

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
                            0,
                            birthdate,
                            deathdate,
                            relationship,
                            personalityList,
                            binding.activeCheckBox.isChecked(),
                            language,
                            country,
                            fullName
                    );

                    createUser(request);
                }
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

}
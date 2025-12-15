package com.t4app.t4everandroid.main.ui.chat.models;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.gson.Gson;
import com.t4app.t4everandroid.main.Models.LegacyProfile;
import com.t4app.t4everandroid.main.ui.chat.AddFilesManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class CreateMessageUtils {
    private static final String TAG = "CREATE_MESSAGE_UTILS";

    private final Gson gson = new Gson();

    public Map<String, Object> postTextMessage(
            LegacyProfile legacyProfile,
            String text) {
        Map<String, Object> body = new HashMap<>();

        List<Map<String, Object>> parts = new ArrayList<>();
        Map<String, Object> partText = new HashMap<>();
        partText.put("text", text);
        parts.add(partText);

        body.put("parts", parts);
        body.put("role", "user");

        Map<String, Object> legacyProfileMap = getLegacyProfileMap(legacyProfile);

        body.put("legacyProfile", legacyProfileMap);

        String json = gson.toJson(body);

        return body;
    }

    public RequestBody postMessageWithFile(
            LegacyProfile legacyProfile,
            String text,
            List<Uri> uriFiles,
            FragmentActivity activity) {
        List<Map<String, Object>> parts = new ArrayList<>();
        Map<String, Object> partText = new HashMap<>();
        partText.put("text", text);
        parts.add(partText);
        String partsJson = gson.toJson(parts);

        Map<String, Object> legacyProfileMap = getLegacyProfileMap(legacyProfile);
        String legacyProfileJson = gson.toJson(legacyProfileMap);

        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("parts", partsJson)
                .addFormDataPart("role", "user")
                .addFormDataPart("legacyProfile", legacyProfileJson);

        for (Uri uri : uriFiles) {
            if (uri == null) continue;

            String mime = activity.getContentResolver().getType(uri);
            if (mime == null) mime = "application/octet-stream";

            String fileName = AddFilesManager.getDisplayName(activity, uri);
            long size = AddFilesManager.getSize(activity, uri);

            RequestBody fileBody = new ContentUriRequestBody(activity, uri, mime, size);

            builder.addFormDataPart("files[]", fileName, fileBody);
        }
        RequestBody requestBody = builder.build();

        return requestBody;
    }



    @NonNull
    private static Map<String, Object> getLegacyProfileMap(LegacyProfile legacyProfile) {


        Log.d(TAG, "Building LegacyProfileMap:");
        Log.d(TAG, "id = " + legacyProfile.getId());
        Log.d(TAG, "language = " + legacyProfile.getLanguage());
        Log.d(TAG, "name = " + legacyProfile.getName());
        Log.d(TAG, "alias = " + (legacyProfile.getAlias() != null ? legacyProfile.getAlias() : ""));
        Log.d(TAG, "family_relationship = " + legacyProfile.getFamilyRelationship());
        Log.d(TAG, "age = " + legacyProfile.getAge());
        Log.d(TAG, "base_personality = " + legacyProfile.getBasePersonality());
        Log.d(TAG, "country = " + legacyProfile.getCountry());
        Log.d(TAG, "birth_date = " + legacyProfile.getBirthDate());
        Log.d(TAG, "death_date = " + legacyProfile.getDeathDate());

        Map<String, Object> legacyProfileMap = new HashMap<>();
        legacyProfileMap.put("id", legacyProfile.getId());
        legacyProfileMap.put("language", legacyProfile.getLanguage());
        legacyProfileMap.put("name", legacyProfile.getName());
        legacyProfileMap.put("alias", legacyProfile.getAlias() != null ? legacyProfile.getAlias() : "");
        legacyProfileMap.put("family_relationship", legacyProfile.getFamilyRelationship());
        legacyProfileMap.put("age", legacyProfile.getAge());
        legacyProfileMap.put("base_personality", legacyProfile.getBasePersonality());
        legacyProfileMap.put("country", legacyProfile.getCountry());
        legacyProfileMap.put("birth_date", legacyProfile.getBirthDate());
        if (legacyProfile.getDeathDate() != null){
            legacyProfileMap.put("death_date", legacyProfile.getDeathDate());
        }
        return legacyProfileMap;
    }
}

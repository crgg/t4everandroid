package com.t4app.t4everandroid;

import android.app.Activity;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.t4app.t4everandroid.Login.models.ApiError;

import java.util.List;
import java.util.Map;

public class ErrorUtils {
    private static final String TAG = "ERROR_UTILS";

    public static String parseError(Throwable throwable) {
        if (throwable == null || throwable.getMessage() == null) {
            Log.d(TAG, "THROWABLE IS NULL");
            return "";
        }

        String message = throwable.getMessage();

        if (message.contains("{")) {
            Log.d(TAG, "ENTRY CONTAINES");
            try {
                int index = message.indexOf("{");
                String json = message.substring(index);

                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(ApiError.class, new ApiError.ApiErrorDeserializer())
                        .create();

                ApiError apiError = gson.fromJson(json, ApiError.class);

                if (apiError != null) {
                    Log.d(TAG, "API ERROR IS NOT NULL: ");
                    StringBuilder sb = new StringBuilder();


                    if (apiError.getErrors() != null) {
                        Log.d(TAG, "HAS ERRORS ");
                        for (Map.Entry<String, List<String>> entry : apiError.getErrors().entrySet()) {
                            for (String msg : entry.getValue()) {
                                sb.append(msg).append("\n");
                            }
                        }
                    } else if (apiError.getDetailedMsg() != null) {
                        Log.d(TAG, "HAS DETAILS MSG ");
                        for (Map.Entry<String, List<String>> entry : apiError.getDetailedMsg().entrySet()) {
                            for (String msg : entry.getValue()) {
                                sb.append(msg).append("\n");
                            }
                        }
                    } else if (apiError.getSimpleMsg() != null) {
                        Log.d(TAG, "HAS SIMPLE ");
                        sb.append(apiError.getSimpleMsg());
                    } else if (apiError.getMessage() != null) {
                        Log.d(TAG, "HAS MSG ");
                        sb.append(apiError.getMessage());
                    }

                    Log.d(TAG, "parseError: BEFORE RETURN " + sb.toString().trim());

                    return sb.toString().trim();
                }

            } catch (Exception e) {
                Log.e("PARSE_ERROR", "Error parsing throwable: " + e.getMessage());
            }
        }

        return message;
    }

    public static String parseErrorApi(ApiError apiError) {
        if (apiError != null) {
            StringBuilder sb = new StringBuilder();


            if (apiError.getErrors() != null) {
                for (Map.Entry<String, List<String>> entry : apiError.getErrors().entrySet()) {
                    for (String msg : entry.getValue()) {
                        sb.append(msg).append("\n");
                    }
                }
            }


            else if (apiError.getDetailedMsg() != null) {
                for (Map.Entry<String, List<String>> entry : apiError.getDetailedMsg().entrySet()) {
                    for (String msg : entry.getValue()) {
                        sb.append(msg).append("\n");
                    }
                }
            }


            else if (apiError.getSimpleMsg() != null) {
                sb.append(apiError.getSimpleMsg());
            } else if (apiError.getMessage() != null) {
                sb.append(apiError.getMessage());
            }

            return sb.toString().trim();
        }

        return "";
    }

    public static String extractMsgErrors(Object msg, Activity activity) {
        if (msg == null) return activity.getString(R.string.unknown_error_contact_administrator);

        try {
            Gson gson = new Gson();
            String json = gson.toJson(msg);

            JsonElement element = JsonParser.parseString(json);
            if (element.isJsonObject()) {
                JsonObject obj = element.getAsJsonObject();
                StringBuilder sb = new StringBuilder();
                for (String key : obj.keySet()) {
                    JsonArray arr = obj.getAsJsonArray(key);
                    for (JsonElement val : arr) {
                        sb.append(val.getAsString()).append("\n");
                    }
                }
                return sb.toString().trim();
            } else if (element.isJsonPrimitive()) {
                return element.getAsString();
            }

        } catch (Exception e) {
            return msg.toString();
        }

        return activity.getString(R.string.unknown_error_contact_administrator);
    }


}

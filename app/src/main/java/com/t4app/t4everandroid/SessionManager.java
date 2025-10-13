package com.t4app.t4everandroid;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static SessionManager instance;
    private static Context context;
    private static final String SHARED_PREFS_NAME = "user_session_prefs";
    private static final String KEY_USER_ID = "id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_NAME = "name";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_TOKEN_USER = "token";
    private static final String KEY_IS_LOGGED = "is_logged";
    private static final String KEY_LANGUAGE = "lang";
    private static final String KEY_USER = "user";
    private SharedPreferences.Editor editor;

    private SessionManager(Context ctx) {
        context = ctx.getApplicationContext();
    }

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("UserSessionManager not initialized. Call initialize(context) first.");
        }
        return instance;
    }

    public static synchronized void initialize(Context ctx) {
        if (instance == null) {
            instance = new SessionManager(ctx);
        }
    }

    public void saveUserDetails(Integer id,
                                String name,
                                String userEmail,
                                String tokenKey,
                                boolean isLogged){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putInt(KEY_USER_ID, id);
        editor.putString(KEY_USER_EMAIL, userEmail);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_TOKEN_USER, tokenKey);
        editor.putBoolean(KEY_IS_LOGGED, isLogged);
        editor.apply();
    }

    public String getUserObject() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USER, null);
    }


    public String getLanguage() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_LANGUAGE, null);
    }

    public void setLanguage(String language) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(KEY_LANGUAGE , language);
        editor.apply();
    }

    public int getUserId() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(KEY_USER_ID, 0);
    }

    public String getUserName() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USER_NAME, null);
    }

    public String getName() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_NAME, null);
    }

    public String getUserEmail() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USER_EMAIL, null);
    }

    public String getTokenKey() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_TOKEN_USER, null);
    }

    public boolean getIsLogged() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(KEY_IS_LOGGED, false);
    }

    public void clearSession() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String savedLanguage = getLanguage();
        editor.clear();
        editor.apply();
        setLanguage(savedLanguage);
    }
}

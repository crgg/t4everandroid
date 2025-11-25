package com.t4app.t4everandroid;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static SessionManager instance;
    private static Context context;
    private static final String SHARED_PREFS_NAME = "user_session_prefs";
    private static final String KEY_USER_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_ALIAS = "alias";
    private static final String KEY_USER_EMAIL = "email";
    private static final String KEY_ROLE = "rol";
    private static final String KEY_AGE = "age";
    private static final String KEY_COUNTRY = "country";
    private static final String KEY_LANGUAGE = "language";
    private static final String KEY_AVATAR_URL = "avatar_url";
    private static final String KEY_DATE_REGISTER = "date_register";
    private static final String KEY_LAST_LOGIN = "last_login";
    private static final String KEY_EMAIL_VERIFIED_AT = "email_verified_at";
    private static final String KEY_TOKEN_USER = "token";
    private static final String KEY_IS_LOGGED = "is_logged";
    private static final String KEY_REMEMBER = "is_remember";
    private static final String KEY_LAST_MSG_MAP = "map_last_msg";


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

    public void saveExtraUserDetails(String alias,
                                     String role,
                                     int age,
                                     String country,
                                     String avatarUrl,
                                     String dateRegister,
                                     String lastLogin,
                                     String emailVerifiedAt) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(KEY_ALIAS, alias);
        editor.putString(KEY_ROLE, role);
        editor.putInt(KEY_AGE, age);
        editor.putString(KEY_COUNTRY, country);
        editor.putString(KEY_AVATAR_URL, avatarUrl);
        editor.putString(KEY_DATE_REGISTER, dateRegister);
        editor.putString(KEY_LAST_LOGIN, lastLogin);
        editor.putString(KEY_EMAIL_VERIFIED_AT, emailVerifiedAt);
        editor.apply();
    }

    public String getAlias() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_ALIAS, null);
    }

    public String getRole() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_ROLE, null);
    }

    public int getAge() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(KEY_AGE, 0);
    }

    public String getCountry() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_COUNTRY, null);
    }

    public String getAvatarUrl() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_AVATAR_URL, null);
    }

    public String getDateRegister() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_DATE_REGISTER, null);
    }

    public String getLastLogin() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_LAST_LOGIN, null);
    }

    public String getEmailVerifiedAt() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_EMAIL_VERIFIED_AT, null);
    }

    public void setAlias(String alias) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(KEY_ALIAS, alias);
        editor.apply();
    }

    public String getLastMsgMap() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_LAST_MSG_MAP, null);
    }

    public void setLastMsgMap(String msgMap) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(KEY_LAST_MSG_MAP , msgMap);
        editor.apply();
    }

    public void setRole(String role) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(KEY_ROLE, role);
        editor.apply();
    }

    public void setAge(int age) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putInt(KEY_AGE, age);
        editor.apply();
    }

    public void setCountry(String country) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(KEY_COUNTRY, country);
        editor.apply();
    }

    public void setAvatarUrl(String avatarUrl) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(KEY_AVATAR_URL, avatarUrl);
        editor.apply();
    }

    public void setDateRegister(String dateRegister) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(KEY_DATE_REGISTER, dateRegister);
        editor.apply();
    }

    public void setLastLogin(String lastLogin) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(KEY_LAST_LOGIN, lastLogin);
        editor.apply();
    }

    public void setEmailVerifiedAt(String emailVerifiedAt) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(KEY_EMAIL_VERIFIED_AT, emailVerifiedAt);
        editor.apply();
    }

    public void setUserEmail(String email) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(KEY_USER_EMAIL, email);
        editor.apply();
    }



    public static synchronized void initialize(Context ctx) {
        if (instance == null) {
            instance = new SessionManager(ctx);
        }
    }

    public void saveUserDetails(Integer id,
                                String name,
                                String userEmail,
                                String avatarUrl,
                                String tokenKey,
                                String emailVerifiedAt,
                                boolean isLogged,
                                boolean rememberMe){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putInt(KEY_USER_ID, id);
        editor.putString(KEY_USER_EMAIL, userEmail);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_TOKEN_USER, tokenKey);
        editor.putString(KEY_EMAIL_VERIFIED_AT, emailVerifiedAt);
        editor.putString(KEY_AVATAR_URL, avatarUrl);
        editor.putBoolean(KEY_IS_LOGGED, isLogged);
        editor.putBoolean(KEY_REMEMBER, rememberMe);
        editor.apply();
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

    public boolean getRememberMe() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(KEY_REMEMBER, false);
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

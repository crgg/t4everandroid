package com.t4app.t4everandroid.room.converters;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.t4app.t4everandroid.main.Models.Question;
import com.t4app.t4everandroid.main.Models.Session;
import com.t4app.t4everandroid.main.Models.SessionPagination;

import java.lang.reflect.Type;
import java.util.List;

public class LegacyProfileConverter {
    private static final Gson gson = new Gson();

    @TypeConverter
    public static String fromSession(Session session) {
        return session == null ? null : gson.toJson(session);
    }

    @TypeConverter
    public static List<String> basePersonalityList(String json) {
        if (json == null) return null;
        Type type = new TypeToken<List<Session>>() {}.getType();
        return gson.fromJson(json, type);
    }

    @TypeConverter
    public static String fromBasePersonality(List<String> basePersonality) {
        return basePersonality == null ? null : gson.toJson(basePersonality);
    }

    @TypeConverter
    public static Session toSession(String json) {
        if (json == null) return null;
        Type type = new TypeToken<Session>() {}.getType();
        return gson.fromJson(json, type);
    }

    @TypeConverter
    public static String fromSessionList(List<Session> list) {
        return list == null ? null : gson.toJson(list);
    }

    @TypeConverter
    public static List<Session> toSessionList(String json) {
        if (json == null) return null;
        Type type = new TypeToken<List<Session>>() {}.getType();
        return gson.fromJson(json, type);
    }

    @TypeConverter
    public static String fromSessionPagination(SessionPagination pagination) {
        return pagination == null ? null : gson.toJson(pagination);
    }

    @TypeConverter
    public static SessionPagination toSessionPagination(String json) {
        if (json == null) return null;
        Type type = new TypeToken<SessionPagination>() {}.getType();
        return gson.fromJson(json, type);
    }

    @TypeConverter
    public static String fromQuestionList(List<Question> list) {
        return list == null ? null : gson.toJson(list);
    }

    @TypeConverter
    public static List<Question> toQuestionList(String json) {
        if (json == null) return null;
        Type type = new TypeToken<List<Question>>() {}.getType();
        return gson.fromJson(json, type);
    }
}

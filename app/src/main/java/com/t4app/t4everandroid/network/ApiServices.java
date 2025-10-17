package com.t4app.t4everandroid.network;

import com.google.gson.JsonObject;
import com.t4app.t4everandroid.Login.models.LoginResponse;
import com.t4app.t4everandroid.main.Models.ProfileRequest;
import com.t4app.t4everandroid.main.Models.ResponseCreateAssistant;
import com.t4app.t4everandroid.main.Models.ResponseGetAssistantQuestions;
import com.t4app.t4everandroid.main.Models.ResponseGetAssistants;
import com.t4app.t4everandroid.main.Models.ResponseUpdateProfile;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiServices {

    @POST(ApiConfig.LOGIN_URL)
    @FormUrlEncoded
    Call<LoginResponse> login(
            @FieldMap Map<String, Object> body
    );

    @POST(ApiConfig.LOGOUT_URL)
    Call<JsonObject> logout();

    @POST(ApiConfig.REGISTER_URL)
    @FormUrlEncoded
    Call<JsonObject> register(
            @FieldMap Map<String, Object> body
    );

    @GET(ApiConfig.ASSISTANTS_URL)
    Call<ResponseGetAssistants> getAssistants();

    @DELETE(ApiConfig.ACTIONS_ASSISTANTS_URL)
    Call<JsonObject> deleteAssistant(
            @Path("uuid")String uuid
    );

    @PUT(ApiConfig.ACTIONS_ASSISTANTS_URL)
    Call<ResponseCreateAssistant> updateAssistant(
            @Path("uuid")String uuid,
            @Body ProfileRequest body
    );

    @POST(ApiConfig.ASSISTANTS_URL)
    Call<ResponseCreateAssistant> createAssistant(
            @Body ProfileRequest body
            );

    @GET(ApiConfig.GET_QUESTIONS_ASSISTANT)
    Call<ResponseGetAssistantQuestions> getQuestionsAssistant(
            @Query("assistant_id") String uuid
    );

    @POST(ApiConfig.UPDATE_PROFILE_URL)
    @FormUrlEncoded
    Call<ResponseUpdateProfile> updateProfile(
            @FieldMap Map<String, Object> body
    );

    @POST(ApiConfig.CHANGE_PASSWORD_URL)
    @FormUrlEncoded
    Call<JsonObject> changePassword(
            @FieldMap Map<String, Object> body
    );

}

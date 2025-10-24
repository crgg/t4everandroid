package com.t4app.t4everandroid.network;

import com.google.gson.JsonObject;
import com.t4app.t4everandroid.Login.models.LoginResponse;
import com.t4app.t4everandroid.main.Models.ProfileRequest;
import com.t4app.t4everandroid.main.Models.ResponseCreateAssistant;
import com.t4app.t4everandroid.main.Models.ResponseCreateMedia;
import com.t4app.t4everandroid.main.Models.ResponseGetAssistantQuestions;
import com.t4app.t4everandroid.main.Models.ResponseGetAssistants;
import com.t4app.t4everandroid.main.Models.ResponseGetMedia;
import com.t4app.t4everandroid.main.Models.ResponseGetUserInfo;
import com.t4app.t4everandroid.main.Models.ResponseUpdateProfile;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
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

    @DELETE(ApiConfig.DELETE_MEDIA)
    Call<JsonObject> deleteMedia(
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

    @GET(ApiConfig.UPLOAD_MEDIA)
    Call<ResponseGetMedia> getMediaAssistant(
            @Query("assistant_id") String uuid,
            @Query("type") String type
    );

    @POST(ApiConfig.UPDATE_PROFILE_URL)
    @FormUrlEncoded
    Call<ResponseUpdateProfile> updateProfile(
            @FieldMap Map<String, Object> body
    );

    @GET(ApiConfig.GET_USER_INFO)
    Call<ResponseGetUserInfo> getUserInfo();

    @POST(ApiConfig.UPLOAD_PROFILE_IMAGE_URL)
    @Multipart
    Call<ResponseUpdateProfile> uploadImageProfile(
            @Part MultipartBody.Part file
            );

    @POST(ApiConfig.UPLOAD_MEDIA)
    @Multipart
    Call<ResponseCreateMedia> uploadMedia(
            @Part MultipartBody.Part file,
            @Part("type") RequestBody type,
            @Part("assistant_id") RequestBody assistantId
    );

    @POST(ApiConfig.CHANGE_PASSWORD_URL)
    @FormUrlEncoded
    Call<JsonObject> changePassword(
            @FieldMap Map<String, Object> body
    );

}

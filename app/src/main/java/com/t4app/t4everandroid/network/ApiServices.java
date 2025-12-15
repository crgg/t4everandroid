package com.t4app.t4everandroid.network;

import com.google.gson.JsonObject;
import com.t4app.t4everandroid.main.Models.ProfileRequest;
import com.t4app.t4everandroid.network.responses.CreateAnswerResponse;
import com.t4app.t4everandroid.network.responses.GetAnswerResponse;
import com.t4app.t4everandroid.network.responses.LoginResponse;
import com.t4app.t4everandroid.network.responses.ResponseCreateAssistant;
import com.t4app.t4everandroid.network.responses.ResponseCreateMedia;
import com.t4app.t4everandroid.network.responses.ResponseCreateMessage;
import com.t4app.t4everandroid.network.responses.ResponseGetAssistantQuestions;
import com.t4app.t4everandroid.network.responses.ResponseGetAssistants;
import com.t4app.t4everandroid.network.responses.ResponseGetMedia;
import com.t4app.t4everandroid.network.responses.ResponseGetMessages;
import com.t4app.t4everandroid.network.responses.ResponseGetUserInfo;
import com.t4app.t4everandroid.network.responses.ResponseStartEndSession;
import com.t4app.t4everandroid.network.responses.ResponseUpdateProfile;

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

    //LOGIN AND REGISTER
    @POST(ApiConfig.LOGIN_URL)
    @FormUrlEncoded
    Call<LoginResponse> login(
            @FieldMap Map<String, Object> body
    );

    @POST(ApiConfig.LOGIN_WITH_GOOGLE_URL)
    @FormUrlEncoded
    Call<LoginResponse> loginWithGoogle(
            @FieldMap Map<String, Object> body
    );

    @POST(ApiConfig.LOGOUT_URL)
    Call<JsonObject> logout();

    @POST(ApiConfig.REGISTER_URL)
    @FormUrlEncoded
    Call<JsonObject> register(
            @FieldMap Map<String, Object> body
    );


    //ASSISTANTS URL
    @GET(ApiConfig.ASSISTANTS_URL)
    Call<ResponseGetAssistants> getAssistants();

    @DELETE(ApiConfig.ACTIONS_ASSISTANTS_URL)
    Call<JsonObject> deleteAssistant(
            @Path("uuid")String uuid
    );

    @POST(ApiConfig.UPLOAD_IMAGE_ASSISTANT_URL)
    @Multipart
    Call<ResponseCreateAssistant> uploadImageAssistant(
            @Part MultipartBody.Part assistantId,
            @Part MultipartBody.Part file
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

    @POST(ApiConfig.END_SESSION_URL)
    Call<ResponseStartEndSession> endSession(
            @Path("session_id") String clientId
    );

    @POST(ApiConfig.START_SESSION_URL)
    @FormUrlEncoded
    Call<ResponseStartEndSession> startSession(
            @FieldMap Map<String, Object> body
    );

    @POST(ApiConfig.RESEND_EMAIL_URL)
    Call<JsonObject> resendEmail();



    //MEDIA
    @GET(ApiConfig.UPLOAD_MEDIA)
    Call<ResponseGetMedia> getMediaAssistant(
            @Query("assistant_id") String uuid,
            @Query("type") String type
    );

    @DELETE(ApiConfig.DELETE_MEDIA)
    Call<JsonObject> deleteMedia(
            @Path("uuid")String uuid
    );

    @POST(ApiConfig.UPLOAD_MEDIA)
    @Multipart
    Call<ResponseCreateMedia> uploadMedia(
            @Part MultipartBody.Part file,
            @Part("type") RequestBody type,
            @Part("assistant_id") RequestBody assistantId
    );


    //UPLOAD PROFILE
    @GET(ApiConfig.GET_USER_INFO)
    Call<ResponseGetUserInfo> getUserInfo();

    @POST(ApiConfig.UPLOAD_PROFILE_IMAGE_URL)
    @Multipart
    Call<ResponseUpdateProfile> uploadImageProfile(
            @Part MultipartBody.Part file
    );

    @POST(ApiConfig.CHANGE_PASSWORD_URL)
    @FormUrlEncoded
    Call<JsonObject> changePassword(
            @FieldMap Map<String, Object> body
    );

    @POST(ApiConfig.UPDATE_PROFILE_URL)
    @FormUrlEncoded
    Call<ResponseUpdateProfile> updateProfile(
            @FieldMap Map<String, Object> body
    );

    //MESSAGES
    @DELETE(ApiConfig.DELETE_MESSAGE)
    Call<JsonObject> deleteMessage(
            @Path("uuid")String uuid
    );

    @POST(ApiConfig.GET_MESSAGES)
    Call<ResponseCreateMessage> sendMessageText(
            @Body Map<String, Object> body
    );

    @POST(ApiConfig.GET_MESSAGES)
    Call<ResponseCreateMessage> sendMessageWithFile(
            @Body RequestBody body
    );

    @GET(ApiConfig.GET_MESSAGES)
    Call<ResponseGetMessages> getMessages(
            @Query("legacyProfileId") String sessionId
    );


    @GET(ApiConfig.GET_LATEST_MESSAGES)
    Call<ResponseGetMessages> getLastMessages(
    );


    //QUESTIONS
    @GET(ApiConfig.GET_QUESTIONS_ASSISTANT)
    Call<ResponseGetAssistantQuestions> getQuestionsAssistant(
            @Query("assistant_id") String uuid
    );

    @GET(ApiConfig.GET_ANSWERS)
    Call<GetAnswerResponse> getAnswers(
            @Query("legacyProfileId") String uuid
    );

    @DELETE(ApiConfig.DELETE_ANSWER)
    Call<GetAnswerResponse> deleteAnswer(
            @Path("uuid") String uuid
    );

    @POST(ApiConfig.GET_ANSWERS)
    Call<CreateAnswerResponse> createAnswer(
            @Body Map<String, Object> body
    );

    @PUT(ApiConfig.DELETE_ANSWER)
    Call<CreateAnswerResponse> updateAnswer(
            @Path("uuid") String uuid,
            @Body Map<String, Object> body
    );

}

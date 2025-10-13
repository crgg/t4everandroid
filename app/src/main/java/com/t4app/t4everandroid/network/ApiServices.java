package com.t4app.t4everandroid.network;

import com.google.gson.JsonObject;
import com.t4app.t4everandroid.Login.models.LoginResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiServices {

    @POST(ApiConfig.LOGIN_URL)
    @FormUrlEncoded
    Call<LoginResponse> login(
            @FieldMap Map<String, Object> body
    );

    @POST(ApiConfig.REGISTER_URL)
    @FormUrlEncoded
    Call<JsonObject> register(
            @FieldMap Map<String, Object> body
    );

}

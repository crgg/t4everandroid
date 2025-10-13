package com.t4app.t4everandroid.Login.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LoginResponse implements Serializable {
    @SerializedName("status")
    public boolean status;

    @SerializedName("msg")
    public String msg;

    @SerializedName("errors")
    public ApiError error;

    @SerializedName("data")
    public Token data;

    public ApiError getError() {
        return error;
    }

    public void setError(ApiError error) {
        this.error = error;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Token getData() {
        return data;
    }

    public void setData(Token data) {
        this.data = data;
    }
}

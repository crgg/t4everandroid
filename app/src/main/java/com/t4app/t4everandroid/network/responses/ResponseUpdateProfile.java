package com.t4app.t4everandroid.network.responses;

import com.google.gson.annotations.SerializedName;
import com.t4app.t4everandroid.Login.models.User;

import java.io.Serializable;

public class ResponseUpdateProfile implements Serializable {
    @SerializedName("status")
    private boolean status;

    @SerializedName("msg")
    private String message;

    @SerializedName("data")
    private User data;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getData() {
        return data;
    }

    public void setData(User data) {
        this.data = data;
    }
}

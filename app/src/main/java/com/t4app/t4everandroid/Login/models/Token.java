package com.t4app.t4everandroid.Login.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Token implements Serializable {
    @SerializedName("token")
    public String token;

    @SerializedName("type")
    public String type;

    @SerializedName("user")
    public User user;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

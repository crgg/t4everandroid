package com.t4app.t4everandroid.main.Models;

import com.google.gson.annotations.SerializedName;
import com.t4app.t4everandroid.Login.models.User;

public class DataUser{
    @SerializedName("user")
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

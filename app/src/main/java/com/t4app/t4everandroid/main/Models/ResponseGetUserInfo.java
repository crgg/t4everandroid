package com.t4app.t4everandroid.main.Models;

import com.google.gson.annotations.SerializedName;
import com.t4app.t4everandroid.Login.models.User;

public class ResponseGetUserInfo {
    @SerializedName("status")
    private boolean status;

    @SerializedName("data")
    private DataUser data;

    @SerializedName("msg")
    private String msg;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public DataUser getData() {
        return data;
    }

    public void setData(DataUser data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }



}


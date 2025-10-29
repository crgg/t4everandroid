package com.t4app.t4everandroid.network.responses;

import com.google.gson.annotations.SerializedName;
import com.t4app.t4everandroid.main.Models.Interactions;

public class ResponseCreateInteraction {
    @SerializedName("status")
    private boolean status;

    @SerializedName("data")
    private Interactions data;

    @SerializedName("msg")
    private String msg;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Interactions getData() {
        return data;
    }

    public void setData(Interactions data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}


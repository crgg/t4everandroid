package com.t4app.t4everandroid.network.responses;

import com.google.gson.annotations.SerializedName;
import com.t4app.t4everandroid.main.ui.chat.models.Messages;

import java.util.List;

public class ResponseCreateMessage {
    @SerializedName("status")
    private boolean status;

    @SerializedName("data")
    private List<Messages> data;

    @SerializedName("msg")
    private String msg;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public List<Messages> getData() {
        return data;
    }

    public void setData(List<Messages> data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}


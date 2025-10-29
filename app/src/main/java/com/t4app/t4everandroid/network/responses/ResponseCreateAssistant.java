package com.t4app.t4everandroid.network.responses;

import com.google.gson.annotations.SerializedName;
import com.t4app.t4everandroid.main.Models.LegacyProfile;
import com.t4app.t4everandroid.main.Models.SessionPagination;

public class ResponseCreateAssistant {
    @SerializedName("status")
    private boolean status;

    @SerializedName("msg")
    private String msg;

    @SerializedName("data")
    private LegacyProfile data;

    @SerializedName("pagination")
    private SessionPagination pagination;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public LegacyProfile getData() {
        return data;
    }

    public void setData(LegacyProfile data) {
        this.data = data;
    }

    public SessionPagination getPagination() {
        return pagination;
    }

    public void setPagination(SessionPagination pagination) {
        this.pagination = pagination;
    }
}

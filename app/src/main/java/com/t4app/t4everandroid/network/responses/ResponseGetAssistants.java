package com.t4app.t4everandroid.network.responses;

import com.google.gson.annotations.SerializedName;
import com.t4app.t4everandroid.main.Models.LegacyProfile;
import com.t4app.t4everandroid.main.Models.SessionPagination;

import java.io.Serializable;
import java.util.List;

public class ResponseGetAssistants implements Serializable {
    @SerializedName("status")
    private boolean status;

    @SerializedName("data")
    private List<LegacyProfile> data;

    @SerializedName("pagination")
    private SessionPagination pagination;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public List<LegacyProfile> getData() {
        return data;
    }

    public void setData(List<LegacyProfile> data) {
        this.data = data;
    }

    public SessionPagination getPagination() {
        return pagination;
    }

    public void setPagination(SessionPagination pagination) {
        this.pagination = pagination;
    }
}

package com.t4app.t4everandroid.network.responses;

import com.google.gson.annotations.SerializedName;
import com.t4app.t4everandroid.main.Models.Media;

import java.io.Serializable;
import java.util.List;

public class ResponseGetMedia implements Serializable {
    @SerializedName("status")
    private boolean status;

    @SerializedName("data")
    private List<Media> data;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public List<Media> getData() {
        return data;
    }

    public void setData(List<Media> data) {
        this.data = data;
    }
}

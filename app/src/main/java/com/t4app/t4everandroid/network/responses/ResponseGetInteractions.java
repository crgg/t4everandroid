package com.t4app.t4everandroid.network.responses;

import com.google.gson.annotations.SerializedName;
import com.t4app.t4everandroid.main.Models.Interactions;

import java.io.Serializable;
import java.util.List;

public class ResponseGetInteractions implements Serializable {
    @SerializedName("status")
    private boolean status;

    @SerializedName("data")
    private List<Interactions> data;


    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public List<Interactions> getData() {
        return data;
    }

    public void setData(List<Interactions> data) {
        this.data = data;
    }
}

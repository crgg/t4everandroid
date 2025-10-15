package com.t4app.t4everandroid.main.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Session implements Serializable {
    @SerializedName("id")
    private String id;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("assistant_id")
    private String assistantId;

    @SerializedName("date_start")
    private String dateStart;

    @SerializedName("date_end")
    private String dateEnd;

    @SerializedName("canal")
    private String canal;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getAssistantId() {
        return assistantId;
    }

    public void setAssistantId(String assistantId) {
        this.assistantId = assistantId;
    }

    public String getDateStart() {
        return dateStart;
    }

    public void setDateStart(String dateStart) {
        this.dateStart = dateStart;
    }

    public String getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(String dateEnd) {
        this.dateEnd = dateEnd;
    }

    public String getCanal() {
        return canal;
    }

    public void setCanal(String canal) {
        this.canal = canal;
    }
}

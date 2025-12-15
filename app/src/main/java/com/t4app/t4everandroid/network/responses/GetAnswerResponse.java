package com.t4app.t4everandroid.network.responses;

import com.google.gson.annotations.SerializedName;
import com.t4app.t4everandroid.main.ui.questions.models.Answer;

import java.io.Serializable;
import java.util.List;

public class GetAnswerResponse implements Serializable {
    @SerializedName("status")
    private boolean status;

    @SerializedName("data")
    private List<Answer> data;

    @SerializedName("error")
    private String error;

    @SerializedName("message")
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public List<Answer> getData() {
        return data;
    }

    public void setData(List<Answer> data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}

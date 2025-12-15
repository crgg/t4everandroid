package com.t4app.t4everandroid.network.responses;

import com.google.gson.annotations.SerializedName;
import com.t4app.t4everandroid.main.ui.questions.models.Answer;

import java.io.Serializable;

public class CreateAnswerResponse implements Serializable {
    @SerializedName("message")
    public String message;

    @SerializedName("data")
    public Answer data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Answer getData() {
        return data;
    }

    public void setData(Answer data) {
        this.data = data;
    }
}

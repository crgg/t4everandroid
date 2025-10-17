package com.t4app.t4everandroid.main.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ResponseGetAssistantQuestions implements Serializable {
    @SerializedName("status")
    private boolean status;

    @SerializedName("assistant_id")
    private String assistantId;

    @SerializedName("instrument")
    private String instrument;

    @SerializedName("version")
    private int version;

    @SerializedName("items_expected")
    private int itemsExpected;

    @SerializedName("items_answered")
    private int itemsAnswered;

    @SerializedName("completed_at")
    private String completedAt;

    @SerializedName("has_previous_answers")
    private boolean hasPreviousAnswers;

    @SerializedName("scale")
    private Scale scale;

    @SerializedName("questions")
    private List<Question> questions;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getAssistantId() {
        return assistantId;
    }

    public void setAssistantId(String assistantId) {
        this.assistantId = assistantId;
    }

    public String getInstrument() {
        return instrument;
    }

    public void setInstrument(String instrument) {
        this.instrument = instrument;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getItemsExpected() {
        return itemsExpected;
    }

    public void setItemsExpected(int itemsExpected) {
        this.itemsExpected = itemsExpected;
    }

    public int getItemsAnswered() {
        return itemsAnswered;
    }

    public void setItemsAnswered(int itemsAnswered) {
        this.itemsAnswered = itemsAnswered;
    }

    public String getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(String completedAt) {
        this.completedAt = completedAt;
    }

    public boolean isHasPreviousAnswers() {
        return hasPreviousAnswers;
    }

    public void setHasPreviousAnswers(boolean hasPreviousAnswers) {
        this.hasPreviousAnswers = hasPreviousAnswers;
    }

    public Scale getScale() {
        return scale;
    }

    public void setScale(Scale scale) {
        this.scale = scale;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }
}

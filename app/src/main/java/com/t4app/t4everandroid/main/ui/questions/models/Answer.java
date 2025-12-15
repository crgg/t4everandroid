package com.t4app.t4everandroid.main.ui.questions.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Answer implements Serializable {
    @SerializedName("_id")
    private String id;

    @SerializedName("userId")
    private Integer userId;

    @SerializedName("legacyProfileId")
    private String legacyProfileId;

    @SerializedName("question")
    private String question;

    @SerializedName("questionExternalId")
    private Integer questionExternalId;

    @SerializedName("answer")
    private String answer;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("updatedAt")
    private String updatedAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getLegacyProfileId() {
        return legacyProfileId;
    }

    public void setLegacyProfileId(String legacyProfileId) {
        this.legacyProfileId = legacyProfileId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Integer getQuestionExternalId() {
        return questionExternalId;
    }

    public void setQuestionExternalId(Integer questionExternalId) {
        this.questionExternalId = questionExternalId;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}

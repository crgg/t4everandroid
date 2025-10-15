package com.t4app.t4everandroid.main.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Question implements Serializable {

    @SerializedName("question_id")
    private int questionId;

    @SerializedName("position")
    private int position;

    @SerializedName("dimension")
    private String dimension;

    @SerializedName("is_reverse")
    private boolean isReverse;

    @SerializedName("question")
    private String question;

    @SerializedName("raw")
    private int raw;

    @SerializedName("scored")
    private int scored;

    @SerializedName("answered_at")
    private String answeredAt;

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    public boolean isReverse() {
        return isReverse;
    }

    public void setReverse(boolean reverse) {
        isReverse = reverse;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public int getRaw() {
        return raw;
    }

    public void setRaw(int raw) {
        this.raw = raw;
    }

    public int getScored() {
        return scored;
    }

    public void setScored(int scored) {
        this.scored = scored;
    }

    public String getAnsweredAt() {
        return answeredAt;
    }

    public void setAnsweredAt(String answeredAt) {
        this.answeredAt = answeredAt;
    }
}

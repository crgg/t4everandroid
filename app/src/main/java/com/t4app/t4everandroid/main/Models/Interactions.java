package com.t4app.t4everandroid.main.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Interactions implements Serializable {
    @SerializedName("id")
    private String id;

    @SerializedName("session_id")
    private String sessionId;

    @SerializedName("text_from_user")
    private String textFromUser;

    @SerializedName("user_audio_url")
    private String userAudioUrl;

    @SerializedName("assistant_text_response")
    private String assistantTextResponse;

    @SerializedName("assistant_audio_response")
    private String assistantAudioResponse;

    @SerializedName("emotion_deteted")
    private String emotionDetected;

    @SerializedName("timestamp")
    private String timestamp;

    @SerializedName("has_response")
    private boolean hasResponse;

    @SerializedName("was_canceled")
    private boolean wasCanceled;

    @SerializedName("file_uuid")
    private boolean fileUuid;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getTextFromUser() {
        return textFromUser;
    }

    public void setTextFromUser(String textFromUser) {
        this.textFromUser = textFromUser;
    }

    public String getUserAudioUrl() {
        return userAudioUrl;
    }

    public void setUserAudioUrl(String userAudioUrl) {
        this.userAudioUrl = userAudioUrl;
    }

    public String getAssistantTextResponse() {
        return assistantTextResponse;
    }

    public void setAssistantTextResponse(String assistantTextResponse) {
        this.assistantTextResponse = assistantTextResponse;
    }

    public String getAssistantAudioResponse() {
        return assistantAudioResponse;
    }

    public void setAssistantAudioResponse(String assistantAudioResponse) {
        this.assistantAudioResponse = assistantAudioResponse;
    }

    public String getEmotionDetected() {
        return emotionDetected;
    }

    public void setEmotionDetected(String emotionDetected) {
        this.emotionDetected = emotionDetected;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isHasResponse() {
        return hasResponse;
    }

    public void setHasResponse(boolean hasResponse) {
        this.hasResponse = hasResponse;
    }

    public boolean isWasCanceled() {
        return wasCanceled;
    }

    public void setWasCanceled(boolean wasCanceled) {
        this.wasCanceled = wasCanceled;
    }

    public boolean isFileUuid() {
        return fileUuid;
    }

    public void setFileUuid(boolean fileUuid) {
        this.fileUuid = fileUuid;
    }
}
